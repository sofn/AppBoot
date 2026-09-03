package com.lesofn.archforge.blog.domain.model.aggregate;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.api.errors.BlogErrorCode;
import com.lesofn.archforge.blog.api.errors.BlogException;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import com.lesofn.archforge.blog.domain.valueobject.ArticleTitle;
import com.lesofn.archforge.common.domain.BaseDomainEntity;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * 文章聚合根。
 *
 * <p>
 * 纯 POJO：不含 JPA / Spring 注解，可脱离框架做单元测试（形态对齐 {@code admin-user} 的
 * {@code UserAggregate}）。状态变更一律走业务方法，不暴露 public setter，
 * 由 {@link BlogArticleStatus#canTransitionTo(BlogArticleStatus)} 守护状态机。
 */
@Getter
public class BlogArticle extends BaseDomainEntity<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    private ArticleTitle title;
    private ArticleSlug slug;
    private Long categoryId;
    private String summary;
    private String content;
    private Long coverImageFileId;
    private Long authorId;
    private BlogArticleStatus status;
    private LocalDateTime publishTime;
    private Long creatorId;
    private LocalDateTime createTime;
    private Long updaterId;
    private LocalDateTime updateTime;
    private boolean deleted;

    public BlogArticle(
            ArticleTitle title,
            ArticleSlug slug,
            Long categoryId,
            String summary,
            String content,
            Long coverImageFileId,
            Long authorId,
            BlogArticleStatus status) {
        this.title = requireTitle(title);
        this.slug = requireSlug(slug);
        this.categoryId = categoryId;
        this.summary = summary;
        this.content = content;
        this.coverImageFileId = coverImageFileId;
        this.authorId = authorId;
        this.status = status == null ? BlogArticleStatus.DRAFT : status;
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
        if (this.status == BlogArticleStatus.PUBLISHED) {
            this.publishTime = now;
        }
    }

    /** 新建文章（默认草稿）。 */
    public static BlogArticle create(
            ArticleTitle title,
            ArticleSlug slug,
            Long categoryId,
            String summary,
            String content,
            Long coverImageFileId) {
        return new BlogArticle(title, slug, categoryId, summary, content, coverImageFileId, null, BlogArticleStatus.DRAFT);
    }

    /**
     * 由基础设施层从持久化数据重建聚合。
     *
     * <p>
     * 仅应在仓储实现 / PO 转换器中使用；业务侧一律走 {@link #create} 或仓储查询。
     */
    public static BlogArticle restore(
            Long id,
            ArticleTitle title,
            ArticleSlug slug,
            Long categoryId,
            String summary,
            String content,
            Long coverImageFileId,
            Long authorId,
            BlogArticleStatus status,
            LocalDateTime publishTime,
            Long creatorId,
            LocalDateTime createTime,
            Long updaterId,
            LocalDateTime updateTime,
            boolean deleted) {
        BlogArticle article = new BlogArticle(title, slug, categoryId, summary, content, coverImageFileId, authorId, status);
        article.setId(id);
        article.publishTime = publishTime;
        article.creatorId = creatorId;
        article.createTime = createTime;
        article.updaterId = updaterId;
        article.updateTime = updateTime;
        article.deleted = deleted;
        return article;
    }

    /** 修改标题。 */
    public void rename(ArticleTitle title) {
        this.title = requireTitle(title);
        touch();
    }

    /** 修改 URL 标识。 */
    public void changeSlug(ArticleSlug slug) {
        this.slug = requireSlug(slug);
        touch();
    }

    public void changeSummary(@Nullable String summary) {
        this.summary = summary;
        touch();
    }

    public void rewriteContent(String content) {
        this.content = content;
        touch();
    }

    public void changeCover(@Nullable Long coverImageFileId) {
        this.coverImageFileId = coverImageFileId;
        touch();
    }

    public void moveToCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BlogException(BlogErrorCode.CATEGORY_NOT_FOUND);
        }
        this.categoryId = categoryId;
        touch();
    }

    public void assignAuthor(@Nullable Long authorId) {
        this.authorId = authorId;
        touch();
    }

    /**
     * 发布文章。
     *
     * @throws BlogException 当前状态不允许发布时抛出
     *         {@link BlogErrorCode#STATUS_TRANSITION_INVALID}
     */
    public void publish() {
        if (!this.status.canTransitionTo(BlogArticleStatus.PUBLISHED)) {
            throw new BlogException(BlogErrorCode.STATUS_TRANSITION_INVALID);
        }
        this.status = BlogArticleStatus.PUBLISHED;
        this.publishTime = LocalDateTime.now();
        touch();
    }

    /**
     * 下线文章。
     *
     * @throws BlogException 当前状态不允许下线时抛出
     *         {@link BlogErrorCode#STATUS_TRANSITION_INVALID}
     */
    public void offline() {
        if (!this.status.canTransitionTo(BlogArticleStatus.OFFLINE)) {
            throw new BlogException(BlogErrorCode.STATUS_TRANSITION_INVALID);
        }
        this.status = BlogArticleStatus.OFFLINE;
        touch();
    }

    public void markDeleted() {
        this.deleted = true;
        touch();
    }

    public boolean isDeleted() { return this.deleted; }

    /** 回填审计字段（由基础设施层在重建聚合时调用）。 */
    public void replaceAudit(
            Long creatorId, LocalDateTime createTime, Long updaterId, LocalDateTime updateTime) {
        this.creatorId = creatorId;
        this.createTime = createTime;
        this.updaterId = updaterId;
        this.updateTime = updateTime;
    }

    private void touch() {
        this.updateTime = LocalDateTime.now();
    }

    private static ArticleTitle requireTitle(ArticleTitle title) {
        if (title == null) {
            throw new BlogException(BlogErrorCode.ARTICLE_TITLE_INVALID);
        }
        return title;
    }

    private static ArticleSlug requireSlug(ArticleSlug slug) {
        if (slug == null) {
            throw new BlogException(BlogErrorCode.ARTICLE_SLUG_INVALID);
        }
        return slug;
    }
}
