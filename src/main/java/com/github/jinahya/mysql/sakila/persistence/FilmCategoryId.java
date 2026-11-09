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

import java.util.Optional;

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
 * {@link jakarta.persistence.Id @Id} association, is typed as the primary key of {@link Category}
 * is.
 * <p>
 * The class is a record, which the specification permits of a primary key class (Jakarta
 * Persistence 3.2, §2.4.1); the record defines {@code equals} and {@code hashCode} over both
 * attributes, and needs no constructor with no parameters.
 *
 * @param filmId   the value of the {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute.
 * @param category the value of the {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} attribute, the
 *                 primary key of a {@link Category}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilmCategory
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#composite-primary-keys"> 2.4.1. Composite primary keys</a> (Jakarta Persistence 3.2
 *         Specification Document)
 */
public record FilmCategoryId(Integer filmId, Integer category) {

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified values.
     * <p>
     * An application needs this, or the canonical constructor, to look up a {@link FilmCategory} by
     * its primary key, as with {@link jakarta.persistence.EntityManager#find(Class, Object)}.
     *
     * @param filmId   a value for the {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} attribute.
     * @param category a value for the {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} attribute.
     * @return a new instance of {@code filmId} and {@code category}.
     */
    public static FilmCategoryId of(final Integer filmId, final Integer category) {
        return new FilmCategoryId(filmId, category);
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
        return of(
                Optional.ofNullable(film).map(Film::getFilmId).orElse(null),
                Optional.ofNullable(category).map(Category::getCategoryId).orElse(null)
        );
    }

    /**
     * Returns a new instance of the identifiers of the film and the category of the specified
     * entity.
     * <p>
     * The values are taken through the {@value FilmCategory#ATTRIBUTE_NAME_FILM} and the
     * {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} associations, as
     * {@link #from(Film, Category)} takes them; a {@code null} entity, or a {@code null}
     * association, gives a {@code null} value.
     *
     * @param filmCategory the entity whose {@link FilmCategory#getFilm() film} and
     *                     {@link FilmCategory#getCategory() category} give the values; may be
     *                     {@code null}.
     * @return a new instance of {@code filmCategory}'s film's and category's identifiers.
     * @see #from(Film, Category)
     */
    public static FilmCategoryId from(final FilmCategory filmCategory) {
        return from(
                Optional.ofNullable(filmCategory).map(FilmCategory::getFilm).orElse(null),
                Optional.ofNullable(filmCategory).map(FilmCategory::getCategory).orElse(null)
        );
    }
}
