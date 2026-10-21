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

/**
 * An entity class for mapping the {@value FilmCategory#TABLE_NAME} table, whose composite primary key is mapped with an
 * {@link IdClass @IdClass}.
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
 * Each column of the key is mapped twice, by a basic attribute and by a {@link ManyToOne @ManyToOne} association, and
 * the two columns show the two ways of doing it. For the {@value #COLUMN_NAME_FILM_ID} column, the basic attribute is
 * the {@link Id @Id}, which writes it, and the association is read-only. For the {@value #COLUMN_NAME_CATEGORY_ID}
 * column, the association itself is the {@link Id @Id}, which writes it (a derived identity), and the basic attribute
 * is read-only. So {@link #setFilm(Film)} sets the {@value #ATTRIBUTE_NAME_FILM_ID} along with the film, for nothing
 * else writes its column, and {@link #setCategory(Category)} sets the category alone; the
 * {@value #ATTRIBUTE_NAME_CATEGORY_ID} is only ever read from the database. {@link #equals(Object)} and
 * {@link #toString()} read the two basic attributes, never the associations. {@link FilmActor}, the other link table of
 * the database, maps its key with an {@link jakarta.persistence.EmbeddedId @EmbeddedId} instead.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-film_category.html">
 *         The film_category Table</a>
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
     * The name of the {@link Id @Id} attribute which maps, and writes, the {@value #COLUMN_NAME_FILM_ID} column. The
     * value is {@value}.
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
     * The name of the read-only attribute which maps the {@value #COLUMN_NAME_CATEGORY_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_CATEGORY_ID = "categoryId";

    /**
     * The name of the {@link Id @Id} association which also maps, and writes, the {@value #COLUMN_NAME_CATEGORY_ID}
     * column. The value is {@value}.
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

    // ------------------------------------------------------------------------------------------ STATIC FACTORY METHODS

    /**
     * Returns a new instance which links the specified film and the specified category.
     * <p>
     * This is the only way to set the {@value #ATTRIBUTE_NAME_FILM} and the {@value #ATTRIBUTE_NAME_CATEGORY} from
     * outside the package. The {@value #ATTRIBUTE_NAME_FILM_ID} and the {@value #ATTRIBUTE_NAME_CATEGORY_ID} are taken
     * from the specified film and category, so both should already have their identifiers, and changing either
     * association would change the primary key; to link another film, or another category, remove the instance and
     * persist a new one.
     *
     * @param film     a value for the {@value #ATTRIBUTE_NAME_FILM} attribute.
     * @param category a value for the {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     * @return a new instance of {@code film} and {@code category}.
     */
    public static FilmCategory of(final Film film, final Category category) {
        final var instance = new FilmCategory();
        instance.setFilm(film);
        instance.setCategory(category);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected FilmCategory() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "filmId=" + filmId +
               ",categoryId=" + categoryId +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@value #ATTRIBUTE_NAME_FILM_ID} attribute and the
     *         {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute, the values of the composite key, and only once both are
     *         there; an instance without them equals nothing but itself. The {@value #ATTRIBUTE_NAME_CATEGORY_ID} is
     *         there only once the instance is read from the database, so an instance just persisted equals nothing but
     *         itself until then.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FilmCategory that)) {
            return false;
        }
        final var thisFilmId = getFilmId();
        final var thisCategoryId = getCategoryId();
        return thisFilmId != null && thisCategoryId != null
               && thisFilmId.equals(that.getFilmId())
               && thisCategoryId.equals(that.getCategoryId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is that of the class, the same for every instance, so that it does not change as the
     *         {@value #ATTRIBUTE_NAME_FILM_ID} and the {@value #ATTRIBUTE_NAME_CATEGORY_ID} are set; it is consistent
     *         with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return FilmCategory.class.hashCode();
    }

    // ---------------------------------------------------------------------------------------------------------- filmId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FILM_ID} attribute.
     * @implNote This method is package-private, not {@code private}, because {@link #equals(Object)} calls it,
     *         and the instance it calls it on -- this one, or the other one -- may be a lazy proxy: a subclass, under
     *         Hibernate ORM, whose own fields are never populated. The proxy can intercept a package-private method,
     *         and delegate it to the loaded instance; it cannot intercept a {@code private} one, which would read the
     *         proxy's {@code null} instead. {@code _Entity_Persistence_Test#_persist_EqualsReference()} and
     *         {@code _Entity_Persistence_Test#_persist_ReferenceEquals()} fail when this method is {@code private}.
     */
    Integer getFilmId() {
        return filmId;
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
    void setFilm(final Film film) {
        this.film = film;
        this.filmId = film == null ? null : film.getFilmId();
    }

    // ------------------------------------------------------------------------------------------------------ categoryId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute.
     * @implNote This method is package-private, not {@code private}, for the reason {@link #getFilmId()}
     *         gives.
     */
    Integer getCategoryId() {
        return categoryId;
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
     * Replaces current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute, which is what writes the column, with the
     * specified value. The read-only {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute is not touched; it is read from the
     * database.
     *
     * @param category new value for {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     */
    void setCategory(final Category category) {
        this.category = category;
    }

    // ------------------------------------------------------------------------------------------------------ lastUpdate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute; {@code null} until the instance is read
     *         from the database.
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
    // no @NotNull: read-only, null until the instance is read from the database; the category carries the constraint
    @Column(name = COLUMN_NAME_CATEGORY_ID, nullable = false, insertable = false, updatable = false)
    private Integer categoryId;

    @Nonnull
    @Valid
    @NotNull
    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_CATEGORY_ID, referencedColumnName = Category.COLUMN_NAME_CATEGORY_ID,
                nullable = false, insertable = true, updatable = false)
    private Category category;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;
}
