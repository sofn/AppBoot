package com.lesofn.archforge.blog.domain.model.query;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import lombok.Data;

/**
 * 文章查询条件（领域层查询对象）。
 *
 * <p>
 * 形态对齐 {@code admin-user} 的 {@code UserQuery}：{@code @Data} + 可选条件字段，
 * 具体查询技术（JPA Specification / 内存过滤）由仓储实现负责翻译。
 */
@Data
public class ArticleQuery {

    private Long categoryId;

    private String keyword;

    private BlogArticleStatus status;

    private Long authorId;

    /** 是否排除已删除文章，默认 {@code true}。 */
    private boolean excludeDeleted = true;

    /** 前台已发布列表的常用查询（按分类 + 关键字）。 */
    public static ArticleQuery published(Long categoryId, String keyword) {
        ArticleQuery query = new ArticleQuery();
        query.setCategoryId(categoryId);
        query.setKeyword(keyword);
        query.setStatus(BlogArticleStatus.PUBLISHED);
        return query;
    }
}
