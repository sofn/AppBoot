package com.lesofn.archforge.server.admin.mapper;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.api.domain.BlogCategory;
import com.lesofn.archforge.server.admin.dto.response.AdminBlogArticleResponse;
import com.lesofn.archforge.server.admin.dto.response.AdminBlogCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminBlogConvertor {

    @Mapping(target = "statusLabel", ignore = true)
    @Mapping(target = "articleCount", ignore = true)
    AdminBlogCategoryResponse toCategoryResponse(BlogCategory category);

    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "statusLabel", ignore = true)
    @Mapping(target = "coverImageUrl", ignore = true)
    // 值对象显式拆箱：聚合根的 title/slug 是 ArticleTitle/ArticleSlug
    @Mapping(target = "title", source = "title.value")
    @Mapping(target = "slug", source = "slug.value")
    AdminBlogArticleResponse toArticleResponse(BlogArticle article);

    /**
     * {@link BlogArticleStatus} → 接口层状态值。
     *
     * <p>
     * 显式 switch 而非 {@code ordinal()}，避免枚举重排时静默破坏对外契约；
     * 语义与 {@code BlogArticleController#toStatus} 保持互逆。
     */
    default Integer toStatusValue(BlogArticleStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case DRAFT -> 0;
            case PUBLISHED -> 1;
            case OFFLINE -> 2;
        };
    }
}
