package com.lesofn.archforge.blog.api.service;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文章应用服务契约（server 层唯一入口）。
 *
 * <p>
 * 阶段 2 起返回/接收领域聚合根 {@link BlogArticle}；server 层通过
 * {@link BlogArticle#create} 或仓储查询获得聚合，不再自行 new + setter 拼装，
 * 以免绕过领域不变量。
 */
public interface BlogArticleService {

    Page<BlogArticle> pagePublished(Pageable pageable, Long categoryId, String keyword);

    Page<BlogArticle> pageAll(Pageable pageable, Long categoryId, String keyword, BlogArticleStatus status);

    Page<BlogArticle> pageByAuthorId(Pageable pageable, Long authorId);

    Optional<BlogArticle> findPublishedBySlug(String slug);

    Optional<BlogArticle> findById(Long id);

    BlogArticle create(BlogArticle article);

    BlogArticle update(BlogArticle article);

    void delete(Long id);

    BlogArticle publish(Long id);

    BlogArticle offline(Long id);

    /** 未删除文章总数（仪表盘用）。 */
    long countAll();

    /** 按给定分页取最近文章（仪表盘用，默认未删除）。 */
    Page<BlogArticle> findRecent(Pageable pageable);
}
