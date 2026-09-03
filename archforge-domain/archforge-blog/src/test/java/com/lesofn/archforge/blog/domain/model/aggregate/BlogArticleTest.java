package com.lesofn.archforge.blog.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.api.errors.BlogException;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import com.lesofn.archforge.blog.domain.valueobject.ArticleTitle;
import org.junit.jupiter.api.Test;

class BlogArticleTest {

    private static final Long CATEGORY_ID = 7L;

    private static BlogArticle draft(String slug) {
        return BlogArticle.create(
                new ArticleTitle("标题-" + slug),
                new ArticleSlug(slug),
                CATEGORY_ID,
                "摘要",
                "<p>正文</p>",
                null);
    }

    @Test
    void newArticleStartsAsDraftWithoutPublishTime() {
        BlogArticle article = draft("draft-article");

        assertEquals(BlogArticleStatus.DRAFT, article.getStatus());
        assertNull(article.getPublishTime());
        assertFalse(article.isDeleted());
        assertNotNull(article.getCreateTime());
    }

    @Test
    void publishMovesDraftToPublishedAndStampsPublishTime() {
        BlogArticle article = draft("publish-article");

        article.publish();

        assertEquals(BlogArticleStatus.PUBLISHED, article.getStatus());
        assertNotNull(article.getPublishTime());
    }

    @Test
    void publishRejectsAlreadyPublishedArticle() {
        BlogArticle article = draft("double-publish");
        article.publish();

        assertThrows(BlogException.class, article::publish);
    }

    @Test
    void publishRejectsOfflineArticle() {
        BlogArticle article = draft("offline-then-publish");
        article.publish();
        article.offline();

        assertThrows(BlogException.class, article::publish);
    }

    @Test
    void offlineMovesPublishedToOffline() {
        BlogArticle article = draft("offline-article");
        article.publish();

        article.offline();

        assertEquals(BlogArticleStatus.OFFLINE, article.getStatus());
    }

    @Test
    void offlineRejectsDraftArticle() {
        BlogArticle article = draft("draft-offline");

        assertThrows(BlogException.class, article::offline);
    }

    @Test
    void offlineRejectsAlreadyOfflineArticle() {
        BlogArticle article = draft("offline-twice");
        article.publish();
        article.offline();

        assertThrows(BlogException.class, article::offline);
    }

    @Test
    void businessMethodsReplaceStateWithoutSetters() {
        BlogArticle article = draft("mutable-article");

        article.rename(new ArticleTitle("新标题"));
        article.changeSlug(new ArticleSlug("new-slug"));
        article.changeSummary("新摘要");
        article.rewriteContent("<p>新正文</p>");
        article.changeCover(99L);
        article.moveToCategory(3L);
        article.assignAuthor(11L);

        assertEquals("新标题", article.getTitle().value());
        assertEquals("new-slug", article.getSlug().value());
        assertEquals("新摘要", article.getSummary());
        assertEquals("<p>新正文</p>", article.getContent());
        assertEquals(99L, article.getCoverImageFileId());
        assertEquals(3L, article.getCategoryId());
        assertEquals(11L, article.getAuthorId());
    }

    @Test
    void nullTitleAndSlugAreRejected() {
        assertThrows(
                BlogException.class,
                () -> BlogArticle.create(null, new ArticleSlug("x"), CATEGORY_ID, "s", "c", null));
        assertThrows(
                BlogException.class,
                () -> BlogArticle.create(new ArticleTitle("t"), null, CATEGORY_ID, "s", "c", null));
        BlogArticle article = draft("rename-null");
        assertThrows(BlogException.class, () -> article.rename(null));
    }

    @Test
    void markDeletedFlagsAggregate() {
        BlogArticle article = draft("delete-article");

        article.markDeleted();

        assertTrue(article.isDeleted());
    }
}
