package com.lesofn.archforge.server.admin.service.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.lesofn.archforge.blog.api.service.BlogArticleService;
import com.lesofn.archforge.meta.table.api.dao.MetaTableRepository;
import com.lesofn.archforge.user.api.dao.SysUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardMetricsTest {

    @Mock
    private SysUserRepository userRepository;

    @Mock
    private BlogArticleService articleService;

    @Mock
    private MetaTableRepository metaTableRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void metricsAggregatesExistingCounts() {
        when(userRepository.countByDeletedFalse()).thenReturn(12L);
        when(articleService.countAll()).thenReturn(4L);
        when(metaTableRepository.countByDeletedFalse()).thenReturn(3L);

        DashboardMetricsResponse metrics = dashboardService.metrics();

        assertEquals(12L, metrics.userCount());
        assertEquals(4L, metrics.articleCount());
        assertEquals(3L, metrics.metaTableCount());
        assertEquals(0L, metrics.taskCount());
    }
}
