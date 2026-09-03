package com.lesofn.archforge.blog.infrastructure.persistence.converter;

import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import com.lesofn.archforge.blog.domain.valueobject.ArticleTitle;
import com.lesofn.archforge.blog.infrastructure.persistence.po.ArticlePO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 文章 PO 与领域聚合根的双向转换器。
 *
 * <p>
 * 领域方向（PO → 聚合根）走 {@link BlogArticle#restore}，因为聚合根不暴露 setter；
 * 持久化方向（聚合根 → PO）由 MapStruct 生成，值对象经 {@code xxx.value()} 拆箱。
 */
@Mapper(componentModel = "spring")
public interface ArticleConverter {

    @Mapping(target = "title", source = "title.value")
    @Mapping(target = "slug", source = "slug.value")
    ArticlePO toPo(BlogArticle article);

    /**
     * 由 PO 重建聚合根。
     *
     * <p>
     * 手写而非 MapStruct 生成：聚合根只能通过 {@link BlogArticle#restore} 重建，
     * 以保证标识、状态与审计字段一次性装载完成。
     */
    default BlogArticle toDomain(ArticlePO po) {
        if (po == null) {
            return null;
        }
        return BlogArticle.restore(
                po.getId(),
                new ArticleTitle(po.getTitle()),
                new ArticleSlug(po.getSlug()),
                po.getCategoryId(),
                po.getSummary(),
                po.getContent(),
                po.getCoverImageFileId(),
                po.getAuthorId(),
                po.getStatus(),
                po.getPublishTime(),
                po.getCreatorId(),
                po.getCreateTime(),
                po.getUpdaterId(),
                po.getUpdateTime(),
                Boolean.TRUE.equals(po.getDeleted()));
    }
}
