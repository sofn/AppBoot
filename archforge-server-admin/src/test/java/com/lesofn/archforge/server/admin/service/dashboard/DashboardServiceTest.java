package com.lesofn.archforge.server.admin.service.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.lesofn.archforge.blog.api.service.BlogArticleService;
import com.lesofn.archforge.blog.domain.model.aggregate.BlogArticle;
import com.lesofn.archforge.blog.testing.ArticleTestBuilder;
import com.lesofn.archforge.meta.table.api.dao.MetaTableRepository;
import com.lesofn.archforge.user.api.dao.SysUserRepository;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/** Unit tests for {@link DashboardService}. */
@Tag("P1")
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private SysUserRepository userRepository;
    @Mock
    private BlogArticleService articleService;
    @Mock
    private MetaTableRepository metaTableRepository;

    @InjectMocks
    private DashboardService service;

    @Test
    void metricsCountsLiveRowsOnly() {
        when(userRepository.countByDeletedFalse()).thenReturn(11L);
        when(articleService.countAll()).thenReturn(22L);
        when(metaTableRepository.countByDeletedFalse()).thenReturn(33L);

        DashboardMetricsResponse metrics = service.metrics();

        assertEquals(11L, metrics.userCount());
        assertEquals(22L, metrics.articleCount());
        assertEquals(33L, metrics.metaTableCount());
        assertEquals(0L, metrics.taskCount());
    }

    @Test
    void trendsDefaultsToSevenDaysAndRepeatsCurrentTotals() {
        when(userRepository.countByDeletedFalse()).thenReturn(5L);
        when(articleService.countAll()).thenReturn(6L);

        List<DashboardTrendPoint> points = service.trends(0);

        assertEquals(7, points.size());
        points.forEach(point -> {
            assertEquals(5L, point.users());
            assertEquals(6L, point.articles());
        });
    }

    @Test
    void trendsHonorsPositiveWindow() {
        List<DashboardTrendPoint> points = service.trends(3);

        assertEquals(3, points.size());
    }

    @Test
    void recentActivitiesRendersArticleTitles() {
        // 聚合根的标题是必填值对象（无标题场景已不可能），直接验证拆箱渲染
        BlogArticle titled = ArticleTestBuilder.anArticle().withId(1L).withTitle("Hello").build();
        BlogArticle other = ArticleTestBuilder.anArticle().withId(2L).withTitle("World").build();
        when(articleService.findRecent(org.mockito.ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(titled, other)));

        List<DashboardActivity> activities = service.recentActivities();

        assertEquals(2, activities.size());
        assertEquals("article", activities.get(0).type());
        assertEquals("Hello", activities.get(0).title());
        assertEquals("World", activities.get(1).title());
    }

    @Test
    void todoListsAllCountersWithLinks() {
        when(userRepository.countByDeletedFalse()).thenReturn(1L);
        when(articleService.countAll()).thenReturn(2L);
        when(metaTableRepository.countByDeletedFalse()).thenReturn(3L);

        List<DashboardTodo> todos = service.todo();

        assertEquals(3, todos.size());
        assertEquals("/welcome", todos.get(0).href());
        assertEquals("/blog/article/index", todos.get(1).href());
        assertEquals("/metatable", todos.get(2).href());
    }
}
