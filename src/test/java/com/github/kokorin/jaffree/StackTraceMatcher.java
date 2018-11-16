package com.github.kokorin.jaffree;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class StackTraceMatcher extends BaseMatcher<Object> {
    private final String messagePart;

    public StackTraceMatcher(String messagePart) {
        this.messagePart = messagePart;
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof Throwable)) {
            return false;
        }

        Throwable throwable = (Throwable) item;
        while (throwable != null) {
            String message = throwable.getMessage();
            if (message != null && message.contains(messagePart)) {
                return true;
            }
            throwable = throwable.getCause();
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {

    }
}
