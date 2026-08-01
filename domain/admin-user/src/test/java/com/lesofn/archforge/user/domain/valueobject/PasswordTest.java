package com.lesofn.archforge.user.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void shouldCreatePasswordFromRawAndMatch() {
        Password password = Password.ofRaw("secret123", encoder);
        assertTrue(password.matches("secret123", encoder));
    }

    @Test
    void shouldNotMatchDifferentPassword() {
        Password password = Password.ofRaw("secret123", encoder);
        assertFalse(password.matches("wrongPassword", encoder));
    }

    @Test
    void shouldRejectBlankRawPassword() {
        assertThrows(IllegalArgumentException.class, () -> Password.ofRaw("", encoder));
        assertThrows(IllegalArgumentException.class, () -> Password.ofRaw("   ", encoder));
        assertThrows(IllegalArgumentException.class, () -> Password.ofRaw(null, encoder));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345", "123456789012345678901234567890123"
    })
    void shouldRejectOutOfRangeRawPassword(String value) {
        assertThrows(IllegalArgumentException.class, () -> Password.ofRaw(value, encoder));
    }

    @Test
    void shouldCreatePasswordFromEncryptedValue() {
        String encoded = encoder.encode("myPassword");
        Password password = Password.ofEncrypted(encoded);
        assertTrue(password.matches("myPassword", encoder));
    }

    @Test
    void shouldRejectBlankEncryptedPassword() {
        assertThrows(IllegalArgumentException.class, () -> Password.ofEncrypted(""));
        assertThrows(IllegalArgumentException.class, () -> Password.ofEncrypted(null));
    }

    @Test
    void shouldReturnFalseWhenMatchingNullRawPassword() {
        Password password = Password.ofRaw("secret123", encoder);
        assertFalse(password.matches(null, encoder));
    }
}
