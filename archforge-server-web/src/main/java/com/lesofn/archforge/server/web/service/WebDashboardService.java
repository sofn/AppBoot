package com.lesofn.archforge.server.web.service;

import com.lesofn.archforge.server.web.dto.WebDashboardMetricsResponse;
import com.lesofn.archforge.user.api.dao.SysLoginLogRepository;
import com.lesofn.archforge.user.api.dao.SysOperLogRepository;
import com.lesofn.archforge.user.api.domain.SysLoginLog_;
import com.lesofn.archforge.user.api.domain.SysOperLog_;
import com.lesofn.archforge.user.domain.adapter.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Aggregates the C-end dashboard metrics.
 *
 * <p>
 * Criteria 字段名一律使用 Hibernate Static Metamodel（{@code SysLoginLog_}/
 * {@code SysOperLog_}），避免字符串字面量在实体重构时静默失效。
 */
@Service
@RequiredArgsConstructor
public class WebDashboardService {

    private final UserRepository userRepository;
    private final SysLoginLogRepository sysLoginLogRepository;
    private final SysOperLogRepository sysOperLogRepository;

    public WebDashboardMetricsResponse metrics() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long userTotal = userRepository.countActiveUsers();
        long onlineNow = userRepository.countOnlineUsers();
        long todayLogin = sysLoginLogRepository.count(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get(SysLoginLog_.STATUS), 1),
                        cb.greaterThanOrEqualTo(root.get(SysLoginLog_.LOGIN_TIME), todayStart)));
        long todayOperation = sysOperLogRepository.count(
                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(SysOperLog_.OPERATING_TIME), todayStart));
        return WebDashboardMetricsResponse.builder()
                .userTotal(userTotal)
                .onlineNow(onlineNow)
                .todayLogin(todayLogin)
                .todayOperation(todayOperation)
                .build();
    }
}
