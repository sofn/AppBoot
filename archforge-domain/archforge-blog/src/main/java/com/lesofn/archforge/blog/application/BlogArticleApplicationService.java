package com.lesofn.archforge.blog.application;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.api.errors.BlogErrorCode;
import com.lesofn.archforge.blog.api.errors.BlogException;
import com.lesofn.archforge.blog.api.service.BlogArticleService;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.domain.model.query.ArticleQuery;
import com.lesofn.archforge.blog.domain.repository.ArticleRepository;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 文章应用服务（用例编排）。
 *
 * <p>
 * 只做事务边界与用例编排，领域规则全部在 {@link BlogArticle} 聚合根内；
 * 写路径走 {@code blogTransactionManager}（blog 独立数据源）。
 * {@link #update} 仅同步内容字段——发布/下线必须走
 * {@link BlogArticle#publish()} / {@link BlogArticle#offline()} 领域方法，
 * 避免应用服务绕过状态机。
 */
@Service
@RequiredArgsConstructor
public class BlogArticleApplicationService implements BlogArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public Page<BlogArticle> pagePublished(Pageable pageable, Long categoryId, String keyword) {
        return this.articleRepository.search(ArticleQuery.published(categoryId, keyword), pageable);
    }

    @Override
    public Page<BlogArticle> pageAll(Pageable pageable, Long categoryId, String keyword, BlogArticleStatus status) {
        ArticleQuery query = new ArticleQuery();
        query.setCategoryId(categoryId);
        query.setKeyword(keyword);
        query.setStatus(status);
        return this.articleRepository.search(query, pageable);
    }

    @Override
    public Page<BlogArticle> pageByAuthorId(Pageable pageable, Long authorId) {
        ArticleQuery query = new ArticleQuery();
        query.setAuthorId(authorId);
        return this.articleRepository.search(query, pageable);
    }

    @Override
    public Optional<BlogArticle> findPublishedBySlug(String slug) {
        if (!StringUtils.hasText(slug)) {
            return Optional.empty();
        }
        return this.articleRepository
                .findBySlug(new ArticleSlug(slug))
                .filter(article -> article.getStatus() == BlogArticleStatus.PUBLISHED);
    }

    @Override
    public Optional<BlogArticle> findById(Long id) {
        return this.articleRepository.findById(id);
    }

    @Override
    @Transactional("blogTransactionManager")
    public BlogArticle create(BlogArticle article) {
        Objects.requireNonNull(article, "article must not be null");
        if (this.articleRepository.existsBySlug(article.getSlug(), null)) {
            throw new BlogException(BlogErrorCode.SLUG_EXISTS);
        }
        return this.articleRepository.save(article);
    }

    @Override
    @Transactional("blogTransactionManager")
    public BlogArticle update(BlogArticle article) {
        Objects.requireNonNull(article, "article must not be null");
        BlogArticle existing = findExisting(article.getId());
        if (this.articleRepository.existsBySlug(article.getSlug(), article.getId())) {
            throw new BlogException(BlogErrorCode.SLUG_EXISTS);
        }
        // 只同步内容字段，状态与审计由聚合根守护（见类注释）
        existing.rename(article.getTitle());
        existing.changeSlug(article.getSlug());
        existing.changeSummary(article.getSummary());
        existing.rewriteContent(article.getContent());
        existing.changeCover(article.getCoverImageFileId());
        if (article.getCategoryId() != null) {
            existing.moveToCategory(article.getCategoryId());
        }
        if (article.getAuthorId() != null) {
            existing.assignAuthor(article.getAuthorId());
        }
        return this.articleRepository.save(existing);
    }

    @Override
    @Transactional("blogTransactionManager")
    public void delete(Long id) {
        BlogArticle article = findExisting(id);
        article.markDeleted();
        this.articleRepository.save(article);
    }

    @Override
    @Transactional("blogTransactionManager")
    public BlogArticle publish(Long id) {
        BlogArticle article = findExisting(id);
        article.publish();
        return this.articleRepository.save(article);
    }

    @Override
    @Transactional("blogTransactionManager")
    public BlogArticle offline(Long id) {
        BlogArticle article = findExisting(id);
        article.offline();
        return this.articleRepository.save(article);
    }

    @Override
    public long countAll() {
        return this.articleRepository.countAll();
    }

    @Override
    public Page<BlogArticle> findRecent(Pageable pageable) {
        // 排序属性对应 ArticlePO 的 updateTime；Sort 只能携带字符串属性名
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "updateTime"));
        return this.articleRepository.findAll(sorted);
    }

    private BlogArticle findExisting(Long id) {
        return this.articleRepository
                .findById(id)
                .orElseThrow(() -> new BlogException(BlogErrorCode.ARTICLE_NOT_FOUND));
    }
}
