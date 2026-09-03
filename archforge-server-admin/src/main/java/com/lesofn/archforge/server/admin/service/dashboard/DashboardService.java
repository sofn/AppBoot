package com.lesofn.archforge.server.admin.service.dashboard;

import com.lesofn.archforge.blog.api.service.BlogArticleService;
import com.lesofn.archforge.meta.table.api.dao.MetaTableRepository;
import com.lesofn.archforge.user.api.dao.SysUserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SysUserRepository userRepository;
    private final BlogArticleService articleService;
    private final MetaTableRepository metaTableRepository;

    public DashboardMetricsResponse metrics() {
        return new DashboardMetricsResponse(userRepository.countByDeletedFalse(), articleService.countAll(), metaTableRepository
                .countByDeletedFalse(), 0L);
    }

    public List<DashboardTrendPoint> trends(int days) {
        int window = days > 0 ? days : 7;
        List<DashboardTrendPoint> points = new ArrayList<>();
        long users = userRepository.countByDeletedFalse();
        long articles = articleService.countAll();
        for (int i = window - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            points.add(new DashboardTrendPoint(date.toString(), users, articles));
        }
        return points;
    }

    public List<DashboardActivity> recentActivities() {
        // findRecent 按文章更新时间倒序；此处仅做展示，不再直连 blog 仓储
        return articleService
                .findRecent(PageRequest.of(0, 8))
                .stream()
                .map(article -> new DashboardActivity("article", article.getTitle() != null ? article.getTitle().value()
                        : "article-" + article.getId(), article.getUpdateTime() != null ? article.getUpdateTime().toString()
                                : ""))
                .toList();
    }

    public List<DashboardTodo> todo() {
        return List.of(
                new DashboardTodo("Users", userRepository.countByDeletedFalse(), "/welcome"),
                new DashboardTodo("Articles", articleService.countAll(), "/blog/article/index"),
                new DashboardTodo("Meta tables", metaTableRepository.countByDeletedFalse(), "/metatable"));
    }
}
