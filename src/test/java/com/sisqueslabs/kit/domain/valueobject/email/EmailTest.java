package com.sisqueslabs.kit.domain.valueobject.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_EMAIL_WITH_SUBDOMAIN = "user@mail.example.com";
    private static final String VALID_EMAIL_WITH_PLUS = "user+tag@example.com";

    @Nested
    class Constructor {

        @Test
        @DisplayName("should create an email value object with a valid email")
        void createsWithValidEmail() {
            var email = new Email(VALID_EMAIL);

            assertThat(email.value()).isEqualTo(VALID_EMAIL);
        }

        @Test
        @DisplayName("should normalize email to lowercase")
        void normalizesToLowercase() {
            var email = new Email("TEST@EXAMPLE.COM");

            assertThat(email.value()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("should trim whitespace from email")
        void trimsWhitespace() {
            var email = new Email("  test@example.com  ");

            assertThat(email.value()).isEqualTo("test@example.com");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "   "})
        @DisplayName("should throw InvalidEmailException for null, empty or blank email")
        void rejectsEmpty(String input) {
            assertThatThrownBy(() -> new Email(input))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessage("Email cannot be empty");
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid", "@example.com", "test@", "test@.com"})
        @DisplayName("should throw InvalidEmailException for invalid email format")
        void rejectsInvalidFormat(String input) {
            assertThatThrownBy(() -> new Email(input))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessage("Invalid email format");
        }

        @Test
        @DisplayName("should throw InvalidEmailException for email longer than 254 characters")
        void rejectsTooLongEmail() {
            var longEmail = "a".repeat(250) + "@example.com";

            assertThatThrownBy(() -> new Email(longEmail))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("254");
        }

        @Test
        @DisplayName("should throw InvalidEmailException for local part longer than 64 characters")
        void rejectsTooLongLocalPart() {
            var longLocalPart = "a".repeat(65) + "@example.com";

            assertThatThrownBy(() -> new Email(longLocalPart))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("64");
        }

        @Test
        @DisplayName("should accept local part of exactly 64 characters")
        void acceptsMaxLengthLocalPart() {
            var maxLocalPart = "a".repeat(64) + "@example.com";

            assertThat(new Email(maxLocalPart).localPart()).hasSize(64);
        }
    }

    @Nested
    class Equality {

        @Test
        @DisplayName("should be equal for equal emails")
        void equalForSameEmail() {
            assertThat(new Email(VALID_EMAIL))
                    .isEqualTo(new Email(VALID_EMAIL))
                    .hasSameHashCodeAs(new Email(VALID_EMAIL));
        }

        @Test
        @DisplayName("should be equal for emails that differ only in case")
        void equalIgnoringCase() {
            assertThat(new Email("TEST@EXAMPLE.COM")).isEqualTo(new Email("test@example.com"));
        }

        @Test
        @DisplayName("should not be equal for different emails")
        void notEqualForDifferentEmails() {
            assertThat(new Email("user1@example.com")).isNotEqualTo(new Email("user2@example.com"));
        }
    }

    @Nested
    class LocalPart {

        @Test
        @DisplayName("should return the local part of the email")
        void returnsLocalPart() {
            assertThat(new Email(VALID_EMAIL).localPart()).isEqualTo("test");
        }

        @Test
        @DisplayName("should return the local part with plus sign")
        void returnsLocalPartWithPlus() {
            assertThat(new Email(VALID_EMAIL_WITH_PLUS).localPart()).isEqualTo("user+tag");
        }
    }

    @Nested
    class Domain {

        @Test
        @DisplayName("should return the domain part of the email")
        void returnsDomain() {
            assertThat(new Email(VALID_EMAIL).domain()).isEqualTo("example.com");
        }

        @Test
        @DisplayName("should return the domain with subdomain")
        void returnsDomainWithSubdomain() {
            assertThat(new Email(VALID_EMAIL_WITH_SUBDOMAIN).domain()).isEqualTo("mail.example.com");
        }
    }
}
