package com.lesofn.archforge.blog.domain.repository;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.domain.model.query.ArticleQuery;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * 测试中使用的内存文章仓储实现（不启动 Spring）。
 *
 * <p>
 * 与 {@code admin-user} 的 {@code InMemoryUserRepository} 同形态，用于给应用服务测试
 * 提供零框架依赖的仓储替身。
 */
public class InMemoryArticleRepository implements ArticleRepository {

    private final Map<Long, BlogArticle> store = new LinkedHashMap<>();

    private long sequence;

    @Override
    public Optional<BlogArticle> findById(Long id) {
        return Optional.ofNullable(this.store.get(id));
    }

    @Override
    public Optional<BlogArticle> findBySlug(ArticleSlug slug) {
        if (slug == null) {
            return Optional.empty();
        }
        return this.store.values().stream()
                .filter(article -> !article.isDeleted())
                .filter(article -> slug.equals(article.getSlug()))
                .findFirst();
    }

    @Override
    public boolean existsBySlug(ArticleSlug slug, Long excludeId) {
        if (slug == null) {
            return false;
        }
        return this.store.values().stream()
                .filter(article -> !article.isDeleted())
                .filter(article -> excludeId == null || !excludeId.equals(article.getId()))
                .anyMatch(article -> slug.equals(article.getSlug()));
    }

    @Override
    public Page<BlogArticle> search(ArticleQuery query, Pageable pageable) {
        List<BlogArticle> matched = this.store.values().stream()
                .filter(article -> matches(article, query))
                .toList();
        return paginate(matched, pageable);
    }

    @Override
    public List<BlogArticle> findAll() {
        return new ArrayList<>(this.store.values());
    }

    @Override
    public Page<BlogArticle> findAll(Pageable pageable) {
        return paginate(findAll(), pageable);
    }

    @Override
    public long countAll() {
        return this.store.values().stream().filter(article -> !article.isDeleted()).count();
    }

    @Override
    public long countByCategoryAndStatus(Long categoryId, BlogArticleStatus status) {
        if (categoryId == null) {
            return 0;
        }
        return this.store.values().stream()
                .filter(article -> !article.isDeleted())
                .filter(article -> categoryId.equals(article.getCategoryId()))
                .filter(article -> article.getStatus() == status)
                .count();
    }

    @Override
    public BlogArticle save(BlogArticle article) {
        if (article.getId() == null) {
            article.setId(++this.sequence);
        }
        this.store.put(article.getId(), article);
        return article;
    }

    @Override
    public void delete(BlogArticle article) {
        if (article != null && article.getId() != null) {
            this.store.remove(article.getId());
        }
    }

    private static boolean matches(BlogArticle article, ArticleQuery query) {
        if (query == null) {
            return !article.isDeleted();
        }
        if (query.isExcludeDeleted() && article.isDeleted()) {
            return false;
        }
        if (query.getCategoryId() != null && !query.getCategoryId().equals(article.getCategoryId())) {
            return false;
        }
        if (query.getStatus() != null && query.getStatus() != article.getStatus()) {
            return false;
        }
        if (query.getAuthorId() != null && !query.getAuthorId().equals(article.getAuthorId())) {
            return false;
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String keyword = query.getKeyword().toLowerCase();
            boolean hitTitle = article.getTitle().value().toLowerCase().contains(keyword);
            boolean hitSummary = article.getSummary() != null && article.getSummary().toLowerCase().contains(keyword);
            if (!hitTitle && !hitSummary) {
                return false;
            }
        }
        return true;
    }

    private static Page<BlogArticle> paginate(List<BlogArticle> articles, Pageable pageable) {
        int start = Math.min((int) pageable.getOffset(), articles.size());
        int end = Math.min(start + pageable.getPageSize(), articles.size());
        return new PageImpl<>(articles.subList(start, end), pageable, articles.size());
    }
}
