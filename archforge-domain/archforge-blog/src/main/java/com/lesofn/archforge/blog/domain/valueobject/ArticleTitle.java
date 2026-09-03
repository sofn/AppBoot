package com.lesofn.archforge.blog.domain.valueobject;

import com.lesofn.archforge.blog.api.errors.BlogErrorCode;
import com.lesofn.archforge.blog.api.errors.BlogException;

/**
 * 文章标题值对象。
 *
 * <p>
 * 不可变，构造时自校验：非空、非空白、长度不超过 {@value #MAX_LENGTH}。
 * 领域内部不再用裸 {@code String} 承载标题，避免绕过校验直接持久化。
 */
public record ArticleTitle(String value) {

    public static final int MAX_LENGTH = 256;

    public ArticleTitle {
        if (value == null || value.isBlank() || value.length() > MAX_LENGTH) {
            throw new BlogException(BlogErrorCode.ARTICLE_TITLE_INVALID);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
