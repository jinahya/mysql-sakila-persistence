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

import java.util.Objects;

/**
 * A composite primary key class for mapping the {@value FilmCategory#COLUMN_NAME_FILM_ID} column
 * and the {@value FilmCategory#COLUMN_NAME_CATEGORY_ID} column, of the
 * {@value FilmCategory#TABLE_NAME} table; the {@link jakarta.persistence.IdClass @IdClass} of
 * {@link FilmCategory}.
 * <p>
 * The attributes are named as the {@link jakarta.persistence.Id @Id} attributes of
 * {@link FilmCategory} are, which is what the specification requires of an id class. The
 * {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute is typed as the basic
 * {@link jakarta.persistence.Id @Id} attribute is, and the
 * {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} attribute, which corresponds to an
 * {@link jakarta.persistence.Id @Id} association, is typed as the primary key of
 * {@link Category} is.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilmCategory
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3
 *         .2#composite-primary-keys"> 2.4.1. Composite primary keys</a> (Jakarta Persistence 3.2
 *         Specification Document)
 */
public class FilmCategoryId {

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified values.
     * <p>
     * An application needs this to look up a {@link FilmCategory} by its primary key, as with
     * {@link jakarta.persistence.EntityManager#find(Class, Object)}; the class has no other way to
     * set the values.
     *
     * @param filmId   a value for the {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute.
     * @param category a value for the {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} attribute.
     * @return a new instance of {@code filmId} and {@code category}.
     */
    public static FilmCategoryId of(final Integer filmId, final Integer category) {
        final var instance = new FilmCategoryId();
        instance.filmId = filmId;
        instance.category = category;
        return instance;
    }

    /**
     * Returns a new instance of the identifiers of the specified entities.
     *
     * @param film     the film whose {@link Film#getFilmId() filmId} is the
     *                 {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute.
     * @param category the category whose {@link Category#getCategoryId() categoryId} is the
     *                 {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} attribute.
     * @return a new instance of {@code film}'s and {@code category}'s identifiers.
     * @see #of(Integer, Integer)
     */
    public static FilmCategoryId from(final Film film, final Category category) {
        Objects.requireNonNull(film, "film is null");
        Objects.requireNonNull(category, "category is null");
        return of(film.getFilmId(), category.getCategoryId());
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     * <p>
     * The constructor is {@code public}, rather than {@code protected}, because EclipseLink
     * instantiates an id class through {@link Class#getConstructor(Class[])}, which sees public
     * constructors only.
     */
    public FilmCategoryId() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object
    @Override
    public final String toString() {
        return super.toString() + '{' +
               "filmId=" + filmId +
               ",category=" + category +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by both {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} and
     *         {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof FilmCategoryId that)) {
            return false;
        }
        return Objects.equals(filmId, that.filmId)
               && Objects.equals(category, that.category);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over both {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} and
     *         {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY}, consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(filmId, category);
    }

    // -------------------------------------------------------------------------------------- filmId

    /**
     * Returns current value of {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute.
     *
     * @return current value of {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute.
     */
    public Integer getFilmId() {
        return filmId;
    }

    // ------------------------------------------------------------------------------------ category

    /**
     * Returns current value of {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} attribute, the
     * primary key of a {@link Category}.
     *
     * @return current value of {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} attribute.
     */
    public Integer getCategory() {
        return category;
    }

    // ---------------------------------------------------------------------------------------------
    private Integer filmId;

    private Integer category;
}
