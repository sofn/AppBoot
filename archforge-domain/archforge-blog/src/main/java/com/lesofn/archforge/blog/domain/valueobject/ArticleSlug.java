package com.lesofn.archforge.blog.domain.valueobject;

import com.lesofn.archforge.blog.api.errors.BlogErrorCode;
import com.lesofn.archforge.blog.api.errors.BlogException;
import java.util.regex.Pattern;

/**
 * 文章 URL 标识（slug）值对象。
 *
 * <p>
 * 不可变，构造时自校验：非空、长度不超过 {@value #MAX_LENGTH}，且不含空白与 URL 保留字符
 * （{@code / \ ? #}）。
 *
 * <p>
 * 这里刻意<b>不</b>限制为 {@code [a-z0-9-]}：存量的 {@code blog_article} 表中存在中文等
 * 非 ASCII slug，严格校验会让仓储在重建聚合时抛异常、进而拖垮所有列表查询。
 * URL 友好的命名由创建方（server 层）负责，领域只保证"可用于 URL"。
 */
public record ArticleSlug(String value) {

    public static final int MAX_LENGTH = 256;

    /** 不允许空白与 URL 路径/查询保留字符。 */
    private static final Pattern FORBIDDEN = Pattern.compile("[\\s/\\\\?#]");

    public ArticleSlug {
        if (value == null || value.isBlank() || value.length() > MAX_LENGTH || FORBIDDEN.matcher(value).find()) {
            throw new BlogException(BlogErrorCode.ARTICLE_SLUG_INVALID);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
