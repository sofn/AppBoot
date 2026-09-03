package com.lesofn.archforge.blog.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lesofn.archforge.blog.api.errors.BlogException;
import org.junit.jupiter.api.Test;

class ArticleTitleTest {

    @Test
    void acceptsValidTitle() {
        assertEquals("架构改造记录", new ArticleTitle("架构改造记录").value());
    }

    @Test
    void rejectsBlank() {
        assertThrows(BlogException.class, () -> new ArticleTitle(""));
        assertThrows(BlogException.class, () -> new ArticleTitle("   "));
        assertThrows(BlogException.class, () -> new ArticleTitle(null));
    }

    @Test
    void rejectsTooLongTitle() {
        String tooLong = "标".repeat(ArticleTitle.MAX_LENGTH + 1);

        assertThrows(BlogException.class, () -> new ArticleTitle(tooLong));
    }
}
