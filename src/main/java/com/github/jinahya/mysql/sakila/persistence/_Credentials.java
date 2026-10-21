package com.github.jinahya.mysql.sakila.persistence;

/*-
 * #%L
 * mysql-sakila-persistence
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * An embeddable for mapping the {@value Staff#COLUMN_NAME_USERNAME} column and the
 * {@value Staff#COLUMN_NAME_PASSWORD} column, of the {@value Staff#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * username VARCHAR(16) NOT NULL,
 * password VARCHAR(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
 *}
 * <p>
 * This is a class, not a record, for the {@value #ATTRIBUTE_NAME_PASSWORD} changes on its own, far more often than the
 * {@value #ATTRIBUTE_NAME_USERNAME} does.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class _Credentials {

    /**
     * The name of the attribute which maps the {@value Staff#COLUMN_NAME_USERNAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_USERNAME = "username";

    /**
     * The name of the attribute which maps the {@value Staff#COLUMN_NAME_PASSWORD} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PASSWORD = "password";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified username, with no password.
     *
     * @param username a value for the {@value #ATTRIBUTE_NAME_USERNAME} attribute.
     * @return a new instance of {@code username}.
     */
    public static _Credentials of(@Nonnull final String username) {
        final var instance = new _Credentials();
        instance.setUsername(username);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected _Credentials() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implNote The {@value #ATTRIBUTE_NAME_PASSWORD} is left out.
     */
    @Override
    public final String toString() {
        return super.toString() + '{' +
               "username=" + username +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by both {@value #ATTRIBUTE_NAME_USERNAME} and {@value #ATTRIBUTE_NAME_PASSWORD}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof _Credentials that)) {
            return false;
        }
        return Objects.equals(username, that.username)
               && Objects.equals(password, that.password);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over both {@value #ATTRIBUTE_NAME_USERNAME} and {@value #ATTRIBUTE_NAME_PASSWORD},
     * consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(username, password);
    }

    // -------------------------------------------------------------------------------------------------------- username

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_USERNAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_USERNAME} attribute.
     */
    @Nonnull
    public String getUsername() {
        return username;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_USERNAME} attribute with the specified value.
     *
     * @param username new value for {@value #ATTRIBUTE_NAME_USERNAME} attribute.
     */
    public void setUsername(@Nonnull final String username) {
        this.username = username;
    }

    // -------------------------------------------------------------------------------------------------------- password

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PASSWORD} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PASSWORD} attribute.
     */
    @Nullable
    public String getPassword() {
        return password;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_PASSWORD} attribute with the specified value.
     *
     * @param password new value for {@value #ATTRIBUTE_NAME_PASSWORD} attribute.
     */
    public void setPassword(@Nullable final String password) {
        this.password = password;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = Staff.SIZE_MAX_USERNAME)
    @Basic(optional = false)
    @Column(name = Staff.COLUMN_NAME_USERNAME, nullable = false, insertable = true, updatable = true,
            length = Staff.COLUMN_LENGTH_USERNAME)
    private String username;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = Staff.SIZE_MAX_PASSWORD)
    @Basic(optional = true)
    @Column(name = Staff.COLUMN_NAME_PASSWORD, nullable = true, insertable = true, updatable = true,
            length = Staff.COLUMN_LENGTH_PASSWORD)
    private String password;
}
