package com.lesofn.archforge.blog.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lesofn.archforge.blog.api.errors.BlogException;
import org.junit.jupiter.api.Test;

class ArticleSlugTest {

    @Test
    void acceptsLowercaseAlphanumericAndHyphen() {
        ArticleSlug slug = new ArticleSlug("hello-world-2");

        assertEquals("hello-world-2", slug.value());
    }

    @Test
    void acceptsNonAsciiForLegacyData() {
        // 存量库中存在中文 slug，严格 [a-z0-9-] 会让聚合重建直接失败
        assertEquals("测试文章", new ArticleSlug("测试文章").value());
    }

    @Test
    void rejectsBlank() {
        assertThrows(BlogException.class, () -> new ArticleSlug(""));
        assertThrows(BlogException.class, () -> new ArticleSlug("   "));
        assertThrows(BlogException.class, () -> new ArticleSlug(null));
    }

    @Test
    void rejectsWhitespaceAndUrlReservedCharacters() {
        assertThrows(BlogException.class, () -> new ArticleSlug("hello world"));
        assertThrows(BlogException.class, () -> new ArticleSlug("blog/article"));
        assertThrows(BlogException.class, () -> new ArticleSlug("a?b#c"));
    }

    @Test
    void rejectsTooLongValue() {
        String tooLong = "a".repeat(ArticleSlug.MAX_LENGTH + 1);

        assertThrows(BlogException.class, () -> new ArticleSlug(tooLong));
    }

    @Test
    void equalityIsValueBased() {
        assertTrue(new ArticleSlug("same-slug").equals(new ArticleSlug("same-slug")));
    }
}
