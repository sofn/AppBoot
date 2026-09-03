package com.lesofn.archforge.blog.infrastructure.persistence.po;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.common.persistence.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 文章持久化对象。
 *
 * <p>
 * 映射 {@code blog_article} 表，字段均为基本类型，不含领域值对象；
 * 与领域聚合根 {@link com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle} 的互转由
 * {@link com.lesofn.archforge.blog.infrastructure.persistence.converter.ArticleConverter} 承担。
 */
@Setter
@Getter
@Entity
@Table(name = "blog_article")
@DynamicInsert
@DynamicUpdate
public class ArticlePO extends BasePO {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 256)
    private String title;

    @Column(nullable = false, unique = true, length = 256)
    private String slug;

    @Column(length = 1024)
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image_file_id")
    private Long coverImageFileId;

    @Column(name = "author_id")
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BlogArticleStatus status = BlogArticleStatus.DRAFT;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;
}
