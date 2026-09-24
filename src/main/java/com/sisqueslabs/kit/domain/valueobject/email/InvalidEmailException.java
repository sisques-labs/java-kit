package com.sisqueslabs.kit.domain.valueobject.email;

import com.sisqueslabs.kit.domain.valueobject.InvalidValueObjectException;

public class InvalidEmailException extends InvalidValueObjectException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
