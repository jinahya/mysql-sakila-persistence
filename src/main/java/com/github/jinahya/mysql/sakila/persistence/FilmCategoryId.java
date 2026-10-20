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

import java.util.Objects;

/**
 * A composite primary key class for mapping the {@value FilmCategory#COLUMN_NAME_FILM_ID} column and the
 * {@value FilmCategory#COLUMN_NAME_CATEGORY_ID} column, of the {@value FilmCategory#TABLE_NAME} table; the
 * {@link jakarta.persistence.IdClass @IdClass} of {@link FilmCategory}.
 * <p>
 * The attributes are named, and typed, as the {@link jakarta.persistence.Id @Id} attributes of {@link FilmCategory}
 * are, which is what the specification requires of an id class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilmCategory
 * @see <a
 * href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#composite-primary-keys">2.4.1.
 * Composite primary keys</a> (Jakarta Persistence 3.2 Specification Document)
 */
public class FilmCategoryId {

    /**
     * The name of the attribute which corresponds to the {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute of
     * {@link FilmCategory}. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_ID = FilmCategory.ATTRIBUTE_NAME_FILM_ID;

    /**
     * The name of the attribute which corresponds to the {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID} attribute of
     * {@link FilmCategory}. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CATEGORY_ID = FilmCategory.ATTRIBUTE_NAME_CATEGORY_ID;

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified values.
     *
     * @param filmId     a value for the {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     * @param categoryId a value for the {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute.
     * @return a new instance of {@code filmId} and {@code categoryId}.
     */
    public static FilmCategoryId of(@Nonnull final Integer filmId, @Nonnull final Integer categoryId) {
        final var instance = new FilmCategoryId();
        instance.setFilmId(filmId);
        instance.setCategoryId(categoryId);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     * <p>
     * The constructor is {@code public}, rather than {@code protected}, because EclipseLink instantiates an id class
     * through {@link Class#getConstructor(Class[])}, which sees public constructors only.
     */
    public FilmCategoryId() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "filmId=" + filmId +
               ",categoryId=" + categoryId +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by both {@value #ATTRIBUTE_NAME_FILM_ID} and {@value #ATTRIBUTE_NAME_CATEGORY_ID}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof FilmCategoryId that)) {
            return false;
        }
        return Objects.equals(filmId, that.filmId)
               && Objects.equals(categoryId, that.categoryId);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over both {@value #ATTRIBUTE_NAME_FILM_ID} and {@value #ATTRIBUTE_NAME_CATEGORY_ID},
     * consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(filmId, categoryId);
    }

    // ---------------------------------------------------------------------------------------------------------- filmId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     */
    @Nonnull
    public Integer getFilmId() {
        return filmId;
    }

    void setFilmId(@Nonnull final Integer filmId) {
        this.filmId = filmId;
    }

    // ------------------------------------------------------------------------------------------------------ categoryId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute.
     */
    @Nonnull
    public Integer getCategoryId() {
        return categoryId;
    }

    void setCategoryId(@Nonnull final Integer categoryId) {
        this.categoryId = categoryId;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Integer filmId;

    private Integer categoryId;
}
