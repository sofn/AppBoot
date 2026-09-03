package com.lesofn.archforge.blog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lesofn.archforge.blog.api.enums.BlogArticleStatus;
import com.lesofn.archforge.blog.api.errors.BlogErrorCode;
import com.lesofn.archforge.blog.api.errors.BlogException;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.domain.model.query.ArticleQuery;
import com.lesofn.archforge.blog.domain.repository.ArticleRepository;
import com.lesofn.archforge.blog.domain.repository.InMemoryArticleRepository;
import com.lesofn.archforge.blog.domain.valueobject.ArticleSlug;
import com.lesofn.archforge.blog.testing.ArticleTestBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 应用服务用例测试：InMemory 仓储替换 JPA 实现，不启动 Spring。
 *
 * <p>
 * 验证事务编排、slug 唯一约束与"更新不动状态"约定；状态机细节由
 * {@code BlogArticleTest} 与 {@code BlogArticleStatus} 的单元测试覆盖。
 */
class BlogArticleApplicationServiceTest {

    private static final Pageable PAGE = PageRequest.of(0, 10);

    private InMemoryArticleRepository repository;
    private BlogArticleApplicationService service;

    @BeforeEach
    void setUp() {
        this.repository = new InMemoryArticleRepository();
        this.service = new BlogArticleApplicationService(this.repository);
    }

    private static BlogArticle draft(String slug) {
        return ArticleTestBuilder.anArticle().withSlug(slug).build();
    }

    @Test
    void createPersistsNewDraftAndAssignsId() {
        BlogArticle created = this.service.create(draft("create-draft"));

        assertNotNull(created.getId());
        assertEquals(BlogArticleStatus.DRAFT, created.getStatus());
    }

    @Test
    void createRejectsDuplicateSlug() {
        this.service.create(draft("dup-slug"));

        BlogException exception = assertThrows(BlogException.class, () -> this.service.create(draft("dup-slug")));
        assertEquals(BlogErrorCode.SLUG_EXISTS.getCode(), exception.getErrorInfo().getCode());
    }

    @Test
    void updateSyncsContentFieldsWithoutTouchingStatus() {
        BlogArticle saved = this.service.create(draft("update-target"));

        BlogArticle changed = ArticleTestBuilder.anArticle()
                .withTitle("新标题")
                .withSlug("update-target")
                .withSummary("新摘要")
                .withContent("<p>新内容</p>")
                .build();
        changed.setId(saved.getId());

        BlogArticle updated = this.service.update(changed);

        assertEquals("新标题", updated.getTitle().value());
        assertEquals("新摘要", updated.getSummary());
        assertEquals("<p>新内容</p>", updated.getContent());
        assertEquals(BlogArticleStatus.DRAFT, updated.getStatus());
    }

    @Test
    void updateRejectsUnknownArticle() {
        BlogArticle ghost = draft("ghost");
        ghost.setId(999L);

        BlogException exception = assertThrows(BlogException.class, () -> this.service.update(ghost));
        assertEquals(BlogErrorCode.ARTICLE_NOT_FOUND.getCode(), exception.getErrorInfo().getCode());
    }

    @Test
    void updateRejectsSlugTakenByOtherArticle() {
        this.service.create(draft("taken-slug"));
        BlogArticle target = this.service.create(draft("update-other"));

        BlogArticle changed = draft("taken-slug");
        changed.setId(target.getId());

        BlogException exception = assertThrows(BlogException.class, () -> this.service.update(changed));
        assertEquals(BlogErrorCode.SLUG_EXISTS.getCode(), exception.getErrorInfo().getCode());
    }

    @Test
    void deleteMarksArticleAsDeletedSoftly() {
        BlogArticle saved = this.service.create(draft("soft-delete"));

        this.service.delete(saved.getId());

        assertTrue(this.repository.findById(saved.getId()).orElseThrow().isDeleted());
        assertEquals(0, this.service.countAll());
    }

    @Test
    void deleteRejectsUnknownArticle() {
        BlogException exception = assertThrows(BlogException.class, () -> this.service.delete(404L));
        assertEquals(BlogErrorCode.ARTICLE_NOT_FOUND.getCode(), exception.getErrorInfo().getCode());
    }

    @Test
    void publishMovesDraftToPublished() {
        BlogArticle saved = this.service.create(draft("publish-flow"));

        BlogArticle published = this.service.publish(saved.getId());

        assertEquals(BlogArticleStatus.PUBLISHED, published.getStatus());
        assertNotNull(published.getPublishTime());
    }

    @Test
    void publishRejectsAlreadyPublished() {
        BlogArticle saved = this.service.create(draft("double-publish"));
        this.service.publish(saved.getId());

        assertThrows(BlogException.class, () -> this.service.publish(saved.getId()));
    }

    @Test
    void offlineMovesPublishedToOffline() {
        BlogArticle saved = this.service.create(draft("offline-flow"));
        this.service.publish(saved.getId());

        BlogArticle offline = this.service.offline(saved.getId());

        assertEquals(BlogArticleStatus.OFFLINE, offline.getStatus());
    }

    @Test
    void offlineRejectsDraft() {
        BlogArticle saved = this.service.create(draft("offline-draft"));

        BlogException exception = assertThrows(BlogException.class, () -> this.service.offline(saved.getId()));
        assertEquals(
                BlogErrorCode.STATUS_TRANSITION_INVALID.getCode(),
                exception.getErrorInfo().getCode());
    }

    @Test
    void countAllCountsOnlyLiveArticles() {
        this.service.create(draft("live-1"));
        BlogArticle deleted = this.service.create(draft("dead"));
        this.service.delete(deleted.getId());

        assertEquals(1, this.service.countAll());
    }

    @Test
    void pagePublishedOnlyReturnsPublished() {
        BlogArticle published = ArticleTestBuilder.anArticle()
                .withSlug("published-only")
                .published()
                .build();
        this.repository.save(published);
        this.repository.save(draft("still-draft"));

        var page = this.service.pagePublished(PAGE, null, null);

        assertEquals(1, page.getTotalElements());
        assertEquals(BlogArticleStatus.PUBLISHED, page.getContent().get(0).getStatus());
    }

    @Test
    void pageAllHonoursQueryFilters() {
        this.repository.save(ArticleTestBuilder.anArticle().withSlug("pub-a").published().build());
        this.repository.save(draft("draft-a"));

        ArticleQuery query = new ArticleQuery();
        query.setStatus(BlogArticleStatus.DRAFT);
        var page = this.service.pageAll(PAGE, null, null, BlogArticleStatus.DRAFT);

        assertEquals(1, page.getTotalElements());
        assertEquals("draft-a", page.getContent().get(0).getSlug().value());
    }

    @Test
    void findPublishedBySlugReturnsOnlyPublished() {
        this.repository.save(ArticleTestBuilder.anArticle().withSlug("visible-post").published().build());
        this.repository.save(draft("hidden-post"));

        assertEquals("visible-post",
                this.service.findPublishedBySlug("visible-post").orElseThrow().getSlug().value());
        assertTrue(this.service.findPublishedBySlug("hidden-post").isEmpty());
        assertTrue(this.service.findPublishedBySlug("").isEmpty());
    }

    @Test
    void findRecentReturnsPagedArticles() {
        this.repository.save(draft("recent-1"));
        this.repository.save(draft("recent-2"));

        var page = this.service.findRecent(PageRequest.of(0, 1));

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
    }

    @Test
    void findByIdDelegatesToRepository() {
        BlogArticle saved = this.service.create(draft("find-me"));

        assertEquals(saved.getId(), this.service.findById(saved.getId()).orElseThrow().getId());
        assertTrue(this.service.findById(null).isEmpty());
    }

    /** 兜底确认：新建聚合 slug 拆箱来自值对象（防值对象与 PO 映射回归）。 */
    @Test
    void createKeepsSlugValueObjectUnwrapped() {
        BlogArticle created = this.service.create(draft("slug-check"));

        assertEquals(new ArticleSlug("slug-check"), created.getSlug());
        assertNull(created.getCoverImageFileId());
    }
}
