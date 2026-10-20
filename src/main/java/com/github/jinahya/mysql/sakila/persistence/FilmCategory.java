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
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An entity class for mapping the {@value FilmCategory#TABLE_NAME} table, whose composite primary key is mapped with
 * an {@link IdClass @IdClass}.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE film_category (
 *   film_id SMALLINT UNSIGNED NOT NULL,
 *   category_id TINYINT UNSIGNED NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY (film_id, category_id),
 *   CONSTRAINT fk_film_category_film FOREIGN KEY (film_id) REFERENCES film (film_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_film_category_category FOREIGN KEY (category_id) REFERENCES category (category_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * Each column is mapped twice: once by a basic {@link Id @Id} attribute, which writes it, and once by a read-only
 * {@link ManyToOne @ManyToOne} association. {@link #setFilm(Film)} and {@link #setCategory(Category)} keep the two in
 * step. {@link FilmActor}, the other link table of the database, maps its key with an
 * {@link jakarta.persistence.EmbeddedId @EmbeddedId} instead.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilmCategoryId
 */
@NamedQuery(name = "FilmCategory.selectOneByFilmIdAndCategoryId",
            query = """
                    SELECT e
                    FROM FilmCategory e
                    WHERE e.filmId = :filmId
                      AND e.categoryId = :categoryId"""
)
@NamedQuery(name = "FilmCategory.selectListOrderByFilmIdAscCategoryIdAsc",
            query = """
                    SELECT e
                    FROM FilmCategory e
                    ORDER BY e.filmId ASC, e.categoryId ASC"""
)
@NamedQuery(name = "FilmCategory.selectListByFilmOrderByCategoryIdAsc",
            query = """
                    SELECT e
                    FROM FilmCategory e
                    WHERE e.film = :film
                    ORDER BY e.categoryId ASC"""
)
@NamedQuery(name = "FilmCategory.selectListByCategoryOrderByFilmIdAsc",
            query = """
                    SELECT e
                    FROM FilmCategory e
                    WHERE e.category = :category
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "FilmCategory.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM FilmCategory e"""
)
@NamedQuery(name = "FilmCategory.countByCategory",
            query = """
                    SELECT COUNT(e)
                    FROM FilmCategory e
                    WHERE e.category = :category"""
)
@Entity
@IdClass(FilmCategoryId.class)
@Table(name = FilmCategory.TABLE_NAME)
public class FilmCategory {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "film_category";

    // --------------------------------------------------------------------------------------------------------- film_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FILM_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_FILM_ID = "film_id";

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_FILM_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_ID = "filmId";

    /**
     * The name of the read-only association which also maps the {@value #COLUMN_NAME_FILM_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM = "film";

    // ----------------------------------------------------------------------------------------------------- category_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_CATEGORY_ID = "category_id";

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_CATEGORY_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_CATEGORY_ID = "categoryId";

    /**
     * The name of the read-only association which also maps the {@value #COLUMN_NAME_CATEGORY_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CATEGORY = "category";

    // ----------------------------------------------------------------------------------------------------- last_update

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LAST_UPDATE = "last_update";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LAST_UPDATE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LAST_UPDATE = "lastUpdate";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected FilmCategory() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "filmId=" + filmId +
               ",categoryId=" + categoryId +
//               ",film=" + film +
//               ",category=" + category +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by both {@link Id @Id} attributes.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof FilmCategory that)) {
            return false;
        }
        return Objects.equals(getFilmId(), that.getFilmId())
               && Objects.equals(getCategoryId(), that.getCategoryId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over both {@link Id @Id} attributes, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getFilmId(), getCategoryId());
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

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute with the specified value.
     *
     * @param filmId new value for {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     */
    protected void setFilmId(@Nonnull final Integer filmId) {
        this.filmId = filmId;
    }

    // ------------------------------------------------------------------------------------------------------------ film

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     */
    @Nonnull
    public Film getFilm() {
        return film;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FILM} attribute with the specified value, along with the
     * {@value #ATTRIBUTE_NAME_FILM_ID} attribute, which is what writes the column.
     *
     * @param film new value for {@value #ATTRIBUTE_NAME_FILM} attribute.
     */
    public void setFilm(@Nonnull final Film film) {
        this.film = film;
        setFilmId(film == null ? null : film.getFilmId());
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

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute with the specified value.
     *
     * @param categoryId new value for {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute.
     */
    protected void setCategoryId(@Nonnull final Integer categoryId) {
        this.categoryId = categoryId;
    }

    // -------------------------------------------------------------------------------------------------------- category

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     */
    @Nonnull
    public Category getCategory() {
        return category;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute with the specified value, along with the
     * {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute, which is what writes the column.
     *
     * @param category new value for {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     */
    public void setCategory(@Nonnull final Category category) {
        this.category = category;
        setCategoryId(category == null ? null : category.getCategoryId());
    }

    // ------------------------------------------------------------------------------------------------------ lastUpdate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute; {@code null} until the instance is
     * read from the database.
     */
    @Nullable
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Id
    @Column(name = COLUMN_NAME_FILM_ID, nullable = false, insertable = true, updatable = false)
    private Integer filmId;

    @Nonnull
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_FILM_ID, referencedColumnName = Film.COLUMN_NAME_FILM_ID, nullable = false,
                insertable = false, updatable = false)
    private Film film;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Id
    @Column(name = COLUMN_NAME_CATEGORY_ID, nullable = false, insertable = true, updatable = false)
    private Integer categoryId;

    @Nonnull
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_CATEGORY_ID, referencedColumnName = Category.COLUMN_NAME_CATEGORY_ID,
                nullable = false, insertable = false, updatable = false)
    private Category category;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;
}
