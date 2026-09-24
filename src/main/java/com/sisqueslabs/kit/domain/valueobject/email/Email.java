package com.sisqueslabs.kit.domain.valueobject.email;

import com.sisqueslabs.kit.domain.valueobject.ValueObject;
import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) implements ValueObject<String> {

    private static final int MAX_LENGTH = 254;
    private static final int MAX_LOCAL_PART_LENGTH = 64;

    // RFC 5322 compliant email regex
    private static final Pattern PATTERN =
            Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?"
                    + "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

    public Email {
        value = normalize(value);
        validate(value);
    }

    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }

    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static void validate(String value) {
        if (value.isEmpty()) {
            throw new InvalidEmailException("Email cannot be empty");
        }

        if (!PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }

        if (value.length() > MAX_LENGTH) {
            throw new InvalidEmailException("Email is too long (max 254 characters)");
        }

        if (value.indexOf('@') > MAX_LOCAL_PART_LENGTH) {
            throw new InvalidEmailException("Local part of email is too long (max 64 characters)");
        }
    }
}
