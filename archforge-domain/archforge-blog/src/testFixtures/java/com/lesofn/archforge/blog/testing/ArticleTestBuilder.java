package com.lesofn.archforge.blog.testing;

import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import com.lesofn.archforge.blog.domain.valueobject.ArticleTitle;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test data builder for {@link BlogArticle}. Produces valid-by-default draft articles.
 *
 * <p>
 * 聚合根不再暴露 setter，状态变更必须走领域方法：{@link #published()} 与 {@link #offline()}
 * 在 build 时依次调用 {@code publish()} / {@code offline()}，非法组合会立即暴露。
 */
public final class ArticleTestBuilder {

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private Long id;
    private ArticleTitle title;
    private ArticleSlug slug;
    private String summary;
    private String content;
    private Long categoryId;
    private Long authorId;
    private Long coverImageFileId;
    private boolean published;
    private boolean offline;

    private ArticleTestBuilder() {
    }

    public static ArticleTestBuilder anArticle() {
        return new ArticleTestBuilder();
    }

    public ArticleTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ArticleTestBuilder withTitle(String title) {
        this.title = new ArticleTitle(title);
        return this;
    }

    public ArticleTestBuilder withSlug(String slug) {
        this.slug = new ArticleSlug(slug);
        return this;
    }

    public ArticleTestBuilder withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public ArticleTestBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public ArticleTestBuilder withCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public ArticleTestBuilder withAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    public ArticleTestBuilder withCoverImageFileId(Long coverImageFileId) {
        this.coverImageFileId = coverImageFileId;
        return this;
    }

    /** Builds an article that went through {@code publish()}. */
    public ArticleTestBuilder published() {
        this.published = true;
        return this;
    }

    /** Builds an article that went through {@code publish()} then {@code offline()}. */
    public ArticleTestBuilder offline() {
        this.published = true;
        this.offline = true;
        return this;
    }

    public BlogArticle build() {
        long seq = SEQUENCE.incrementAndGet();
        BlogArticle article = BlogArticle.create(
                this.title != null ? this.title : new ArticleTitle("测试文章" + seq),
                this.slug != null ? this.slug : new ArticleSlug("article-" + seq),
                this.categoryId != null ? this.categoryId : 1L,
                this.summary != null ? this.summary : "测试摘要" + seq,
                this.content != null ? this.content : "<p>测试内容" + seq + "</p>",
                this.coverImageFileId);
        if (this.authorId != null) {
            article.assignAuthor(this.authorId);
        }
        if (this.published) {
            article.publish();
        }
        if (this.offline) {
            article.offline();
        }
        if (this.id != null) {
            article.setId(this.id);
        }
        return article;
    }
}
