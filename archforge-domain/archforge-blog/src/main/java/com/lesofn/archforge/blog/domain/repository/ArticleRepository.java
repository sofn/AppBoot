package com.lesofn.archforge.blog.domain.repository;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.domain.model.query.ArticleQuery;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文章聚合仓储（领域层端口）。
 *
 * <p>
 * 纯接口：由 infrastructure 提供 JPA 实现（{@code ArticleRepositoryImpl}），
 * test 源集提供内存实现（{@code InMemoryArticleRepository}），应用服务只依赖本接口。
 */
public interface ArticleRepository {

    Optional<BlogArticle> findById(Long id);

    Optional<BlogArticle> findBySlug(ArticleSlug slug);

    /**
     * 判断 slug 是否已被未删除文章占用。
     *
     * @param excludeId 更新场景排除自身，可为 {@code null}
     */
    boolean existsBySlug(ArticleSlug slug, Long excludeId);

    /** 按查询条件分页检索（排序由 {@code pageable} 携带，属性名对应 PO 字段）。 */
    Page<BlogArticle> search(ArticleQuery query, Pageable pageable);

    List<BlogArticle> findAll();

    Page<BlogArticle> findAll(Pageable pageable);

    /** 未删除文章总数。 */
    long countAll();

    /**
     * 指定分类下处于给定状态且未删除的文章数（分类删除校验用）。
     *
     * <p>
     * 状态需逐一枚举传入（调用方对三种状态求和），以便状态机新增状态时编译期可见。
     */
    long countByCategoryAndStatus(Long categoryId, BlogArticleStatus status);

    BlogArticle save(BlogArticle article);

    void delete(BlogArticle article);
}
