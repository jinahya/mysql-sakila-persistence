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
 * An embeddable for mapping the {@value #COLUMN_NAME_TITLE} column and the {@value #COLUMN_NAME_DESCRIPTION} column,
 * which the {@value Film#TABLE_NAME} table and the {@value FilmText#TABLE_NAME} table both declare.
 * <p>
 * {@snippet lang = "sql":
 * -- film
 * title VARCHAR(128) NOT NULL,
 * description TEXT DEFAULT NULL,
 * -- film_text
 * title VARCHAR(255) NOT NULL,
 * description TEXT,
 *}
 * <p>
 * The lengths of the {@value #COLUMN_NAME_TITLE} column differ. The mapping here takes the {@value FilmText#TABLE_NAME}
 * table's {@value #COLUMN_LENGTH_TITLE}, and an embedding {@link Film} would override it with an
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride}. The override reaches the column only; the
 * {@link Size @Size} here stays at {@value #SIZE_MAX_TITLE}, so the shorter limit has to be checked elsewhere.
 * <p>
 * This is a class, not a record, for either part is edited on its own.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class _TitleDescription {

    // ----------------------------------------------------------------------------------------------------------- title

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TITLE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_TITLE = "title";

    /**
     * The length of the {@value #COLUMN_NAME_TITLE} column, as the {@value FilmText#TABLE_NAME} table declares. The
     * value is {@value}.
     */
    public static final int COLUMN_LENGTH_TITLE = FilmText.COLUMN_LENGTH_TITLE;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_TITLE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_TITLE = "title";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_TITLE} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_TITLE = COLUMN_LENGTH_TITLE;

    // ----------------------------------------------------------------------------------------------------- description

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_DESCRIPTION = "description";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_DESCRIPTION} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_DESCRIPTION = "description";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified values.
     *
     * @param title       a value for the {@value #ATTRIBUTE_NAME_TITLE} attribute.
     * @param description a value for the {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     * @return a new instance of {@code title} and {@code description}.
     */
    public static _TitleDescription of(@Nonnull final String title, @Nullable final String description) {
        final var instance = new _TitleDescription();
        instance.setTitle(title);
        instance.setDescription(description);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected _TitleDescription() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "title=" + title +
               ",description=" + description +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by both {@value #ATTRIBUTE_NAME_TITLE} and {@value #ATTRIBUTE_NAME_DESCRIPTION}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof _TitleDescription that)) {
            return false;
        }
        return Objects.equals(title, that.title)
               && Objects.equals(description, that.description);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over both {@value #ATTRIBUTE_NAME_TITLE} and {@value #ATTRIBUTE_NAME_DESCRIPTION},
     *         consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(title, description);
    }

    // ----------------------------------------------------------------------------------------------------------- title

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TITLE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TITLE} attribute.
     */
    @Nonnull
    public String getTitle() {
        return title;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_TITLE} attribute with the specified value.
     *
     * @param title new value for {@value #ATTRIBUTE_NAME_TITLE} attribute.
     */
    public void setTitle(@Nonnull final String title) {
        this.title = title;
    }

    // ----------------------------------------------------------------------------------------------------- description

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute with the specified value.
     *
     * @param description new value for {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     */
    public void setDescription(@Nullable final String description) {
        this.description = description;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_TITLE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TITLE, nullable = false, insertable = true, updatable = true,
            length = COLUMN_LENGTH_TITLE)
    private String title;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_DESCRIPTION, nullable = true, insertable = true, updatable = true)
    private String description;
}
