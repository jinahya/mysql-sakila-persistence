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
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * An entity class for mapping the {@value FilmText#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE film_text (
 *   film_id SMALLINT UNSIGNED NOT NULL,
 *   title VARCHAR(255) NOT NULL,
 *   description TEXT,
 *   PRIMARY KEY  (film_id),
 *   FULLTEXT KEY idx_title_description (title,description)
 * )
 *}
 * <p>
 * The table declares no foreign key, and nothing but the {@code ins_film}, {@code upd_film} and {@code del_film}
 * triggers on {@value Film#TABLE_NAME} relates a row to a {@link Film}. The triggers keep a row for every film, and
 * none for any other, so the {@value #ATTRIBUTE_NAME_FILM} attribute maps the {@value #COLUMN_NAME_FILM_ID} column
 * again, read-only, as a required association, which declares no foreign key either. Its {@value #COLUMN_NAME_FILM_ID}
 * is not generated, but assigned.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-film_text.html">The film_text Table</a>
 */
@NamedQuery(name = "FilmText.selectOneByFilmId",
            query = """
                    SELECT e
                    FROM FilmText e
                    WHERE e.filmId = :filmId"""
)
@NamedQuery(name = "FilmText.selectListOrderByFilmIdAsc",
            query = """
                    SELECT e
                    FROM FilmText e
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "FilmText.selectListOrderByFilmIdAscFilmIdGt",
            query = """
                    SELECT e
                    FROM FilmText e
                    WHERE e.filmId > :filmIdMinExclusive
                    ORDER BY e.filmId ASC"""
)
@NamedQuery(name = "FilmText.selectMaxFilmId",
            query = """
                    SELECT MAX(e.filmId)
                    FROM FilmText e"""
)
@NamedQuery(name = "FilmText.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM FilmText e"""
)
@Entity
@Table(name = FilmText.TABLE_NAME)
public class FilmText {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "film_text";

    // --------------------------------------------------------------------------------------------------------- film_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FILM_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_FILM_ID = "film_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FILM_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM_ID = "filmId";

    /**
     * The minimum value of the {@value #ATTRIBUTE_NAME_FILM_ID} attribute; the column is a {@code SMALLINT UNSIGNED}.
     * The value is {@value}.
     */
    public static final int ATTRIBUTE_MIN_FILM_ID = 0;

    /**
     * The maximum value of the {@value #ATTRIBUTE_NAME_FILM_ID} attribute; the column is a {@code SMALLINT UNSIGNED}.
     * The value is {@value}.
     */
    public static final int ATTRIBUTE_MAX_FILM_ID = 65535;

    // ------------------------------------------------------------------------------------------------------------ film

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FILM_ID} column, as an association to {@link Film}.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FILM = "film";

    // ----------------------------------------------------------------------------------------------------------- title

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TITLE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_TITLE = "title";

    /**
     * The length of the {@value #COLUMN_NAME_TITLE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_TITLE = 255;

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

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected FilmText() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "filmId=" + filmId +
               ",title=" + title +
               ",description=" + description +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone, and only once it is assigned; an instance without one
     *         equals nothing but itself.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FilmText that)) {
            return false;
        }
        final var thisId = getFilmId();
        return thisId != null && thisId.equals(that.getFilmId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is that of the class, the same for every instance, so that it does not change as the
     *         {@code @Id} is assigned; it is consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return FilmText.class.hashCode();
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
    void setFilmId(@Nonnull final Integer filmId) {
        this.filmId = filmId;
    }

    // ------------------------------------------------------------------------------------------------------------ film

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM} attribute.
     */
    public Film getFilm() {
        return film;
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
    void setTitle(@Nonnull final String title) {
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
    void setDescription(@Nullable final String description) {
        this.description = description;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Max(ATTRIBUTE_MAX_FILM_ID)
    @Min(ATTRIBUTE_MIN_FILM_ID)
    @NotNull
    @Id
    // insertable=true is the JPA default; it is spelled out because EclipseLink rejects insertable=false on an
    // @Id -- "There should be one non-read-only mapping defined for the primary key field". Nothing writes to this
    // table anyway; the triggers of the film table do.
    @Column(name = COLUMN_NAME_FILM_ID, nullable = false, insertable = true, updatable = false)
    private Integer filmId;

    @Nonnull
    @Valid
    @NotNull
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_FILM_ID, referencedColumnName = Film.COLUMN_NAME_FILM_ID, nullable = false,
                insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Film film;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_TITLE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TITLE, nullable = false, insertable = false, updatable = false,
            length = COLUMN_LENGTH_TITLE)
    private String title;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_DESCRIPTION, nullable = true, insertable = false, updatable = false)
    private String description;
}
