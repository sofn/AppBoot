package com.lesofn.archforge.server.web.service;

import com.lesofn.archforge.common.repository.BaseEntity_;
import com.lesofn.archforge.server.web.dto.WebNoticeResponse;
import com.lesofn.archforge.server.web.dto.WebOperationLogResponse;
import com.lesofn.archforge.user.api.dao.SysNoticeRepository;
import com.lesofn.archforge.user.api.dao.SysOperLogRepository;
import com.lesofn.archforge.user.api.domain.SysNotice_;
import com.lesofn.archforge.user.api.domain.SysOperLog_;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Latest notices and operation logs shown on the C-end dashboard.
 *
 * <p>
 * Criteria 字段名与 Sort 属性一律使用 Hibernate Static Metamodel 常量，
 * 避免字符串字面量在实体重构时静默失效。
 */
@Service
@RequiredArgsConstructor
public class WebNoticeService {

    private static final int LATEST_LIMIT = 20;

    private final SysNoticeRepository sysNoticeRepository;
    private final SysOperLogRepository sysOperLogRepository;

    public List<WebNoticeResponse> latestNotices() {
        return sysNoticeRepository
                .findAll(
                        (root, query, cb) -> cb.and(
                                cb.equal(root.get(SysNotice_.STATUS), 1),
                                cb.equal(root.get(BaseEntity_.DELETED), false)),
                        PageRequest.of(0, LATEST_LIMIT, Sort.by(BaseEntity_.CREATE_TIME).descending()))
                .getContent()
                .stream()
                .map(
                        n -> WebNoticeResponse.builder()
                                .id(n.getNoticeId())
                                .title(n.getNoticeTitle())
                                .content(n.getNoticeContent())
                                .noticeType(n.getNoticeType())
                                .createTime(n.getCreateTime())
                                .build())
                .collect(Collectors.toList());
    }

    public List<WebOperationLogResponse> latestOperationLogs() {
        return sysOperLogRepository
                .findAll(PageRequest.of(0, LATEST_LIMIT, Sort.by(SysOperLog_.OPERATING_TIME).descending()))
                .getContent()
                .stream()
                .map(
                        l -> WebOperationLogResponse.builder()
                                .id(l.getOperId())
                                .username(l.getUsername())
                                .module(l.getModule())
                                .summary(l.getSummary())
                                .operatingTime(l.getOperatingTime())
                                .build())
                .collect(Collectors.toList());
    }
}
