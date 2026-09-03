package com.lesofn.archforge.blog.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlogArticleStatus {
    DRAFT("草稿"),
    PUBLISHED("已发布"),
    OFFLINE("已下线");

    private final String label;

    public boolean isPublished() { return this == PUBLISHED; }

    /**
     * 判断当前状态是否可以流转到目标状态。
     *
     * <p>
     * 状态机：{@code DRAFT → PUBLISHED → OFFLINE}，单向不可逆。
     * 聚合根 {@code BlogArticle#publish()/offline()} 统一走这里校验，
     * 避免状态判断散落在应用服务里。
     */
    public boolean canTransitionTo(BlogArticleStatus target) {
        if (target == null) {
            return false;
        }
        return switch (this) {
            case DRAFT -> target == PUBLISHED;
            case PUBLISHED -> target == OFFLINE;
            case OFFLINE -> false;
        };
    }
}
