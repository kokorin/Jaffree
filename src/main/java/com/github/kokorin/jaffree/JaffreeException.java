/*
 *    Copyright 2021 Dan-Mikkel
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree;

/**
 * Common exception used in this library.
 */
public class JaffreeException extends RuntimeException {

    /**
     * Constructs a new {@link JaffreeException} with null as its detail message.
     */
    public JaffreeException() {
    }

    /**
     * Constructs a new {@link JaffreeException} with the specified detail message.
     *
     * @param message message
     */
    public JaffreeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@link JaffreeException} with the specified detail message and cause.
     *
     * @param message message
     * @param cause   cause
     */
    public JaffreeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@link JaffreeException} with the specified cause.
     *
     * @param cause cause
     */
    public JaffreeException(final Throwable cause) {
        super(cause);
    }
}
