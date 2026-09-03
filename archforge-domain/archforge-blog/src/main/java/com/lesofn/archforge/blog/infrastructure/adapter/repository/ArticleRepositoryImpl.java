package com.lesofn.archforge.blog.infrastructure.adapter.repository;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.domain.model.query.ArticleQuery;
import com.lesofn.archforge.blog.domain.repository.ArticleRepository;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import com.lesofn.archforge.blog.infrastructure.persistence.converter.ArticleConverter;
import com.lesofn.archforge.blog.infrastructure.persistence.dao.ArticleDao;
import com.lesofn.archforge.blog.infrastructure.persistence.po.ArticlePO;
import com.lesofn.archforge.blog.infrastructure.persistence.po.ArticlePO_;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 文章聚合仓储实现（JPA）。
 *
 * <p>
 * 把领域查询条件翻译成 JPA Specification；字段名一律使用静态元模型
 * {@link ArticlePO_}，避免字符串字面量在重构时失效。
 */
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleDao articleDao;
    private final ArticleConverter converter;

    @Override
    public Optional<BlogArticle> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return this.articleDao.findById(id).map(this.converter::toDomain);
    }

    @Override
    public Optional<BlogArticle> findBySlug(ArticleSlug slug) {
        if (slug == null) {
            return Optional.empty();
        }
        return this.articleDao.findOne(notDeleted().and(bySlug(slug))).map(this.converter::toDomain);
    }

    @Override
    public boolean existsBySlug(ArticleSlug slug, Long excludeId) {
        if (slug == null) {
            return false;
        }
        Specification<ArticlePO> spec = notDeleted().and(bySlug(slug));
        if (excludeId != null) {
            spec = spec.and((root, query, cb) -> cb.notEqual(root.get(ArticlePO_.id), excludeId));
        }
        return this.articleDao.count(spec) > 0;
    }

    @Override
    public Page<BlogArticle> search(ArticleQuery query, Pageable pageable) {
        return this.articleDao.findAll(toSpecification(query), pageable).map(this.converter::toDomain);
    }

    @Override
    public List<BlogArticle> findAll() {
        return this.articleDao.findAll(notDeleted()).stream().map(this.converter::toDomain).toList();
    }

    @Override
    public Page<BlogArticle> findAll(Pageable pageable) {
        return this.articleDao.findAll(notDeleted(), pageable).map(this.converter::toDomain);
    }

    @Override
    public long countAll() {
        return this.articleDao.countByDeletedFalse();
    }

    @Override
    public long countByCategoryAndStatus(Long categoryId, BlogArticleStatus status) {
        if (categoryId == null) {
            return 0;
        }
        return this.articleDao.countByCategoryIdAndStatusAndDeletedFalse(categoryId, status);
    }

    @Override
    public BlogArticle save(BlogArticle article) {
        ArticlePO po = this.converter.toPo(article);
        if (po.getDeleted() == null) {
            po.setDeleted(false);
        }
        return this.converter.toDomain(this.articleDao.save(po));
    }

    @Override
    public void delete(BlogArticle article) {
        if (article == null || article.getId() == null) {
            return;
        }
        this.articleDao.deleteById(article.getId());
    }

    private static Specification<ArticlePO> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get(ArticlePO_.deleted));
    }

    private static Specification<ArticlePO> bySlug(ArticleSlug slug) {
        return (root, query, cb) -> cb.equal(root.get(ArticlePO_.slug), slug.value());
    }

    private static Specification<ArticlePO> toSpecification(ArticleQuery query) {
        return (root, jpaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query == null || query.isExcludeDeleted()) {
                predicates.add(cb.isFalse(root.get(ArticlePO_.deleted)));
            }
            if (query != null) {
                if (query.getCategoryId() != null) {
                    predicates.add(cb.equal(root.get(ArticlePO_.categoryId), query.getCategoryId()));
                }
                if (query.getStatus() != null) {
                    predicates.add(cb.equal(root.get(ArticlePO_.status), query.getStatus()));
                }
                if (query.getAuthorId() != null) {
                    predicates.add(cb.equal(root.get(ArticlePO_.authorId), query.getAuthorId()));
                }
                if (StringUtils.hasText(query.getKeyword())) {
                    String pattern = "%" + query.getKeyword().toLowerCase() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get(ArticlePO_.title)), pattern),
                            cb.like(cb.lower(root.get(ArticlePO_.summary)), pattern)));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
