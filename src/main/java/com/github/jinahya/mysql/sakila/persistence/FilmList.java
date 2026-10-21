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
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * A class for the {@value FilmList#TABLE_NAME} view.
 * <p>
 * {@snippet lang = "sql":
 * CREATE VIEW film_list
 * AS
 * SELECT film.film_id AS FID, film.title AS title, film.description AS description, category.name AS category,
 *     film.rental_rate AS price, film.length AS length, film.rating AS rating,
 *     GROUP_CONCAT(CONCAT(actor.first_name, _utf8mb4' ', actor.last_name) SEPARATOR ', ') AS actors
 * FROM film LEFT JOIN film_category ON film_category.film_id = film.film_id
 * LEFT JOIN category ON category.category_id = film_category.category_id LEFT
 * JOIN film_actor ON film.film_id = film_actor.film_id LEFT JOIN actor ON
 *   film_actor.actor_id = actor.actor_id
 * GROUP BY film.film_id, category.name
 *}
 * <p>
 * No column, and no combination of columns, identifies a row of this view. It groups by {@code film.film_id} and
 * {@code category.name}, so a film of more than one category has a row for each, and the
 * {@value #COLUMN_NAME_CATEGORY}, which comes from a {@code LEFT JOIN}, is nullable; that no installed film has more
 * than one category is a property of the data, not of the view. So this is not an
 * {@link jakarta.persistence.Entity @Entity}: the columns it projects are written out here, and every one of them is
 * read-only.
 * <p>
 * The public constructor takes every attribute in the order the view selects its columns, which is the order a
 * name-based row mapper, or a {@link jakarta.persistence.ConstructorResult @ConstructorResult}, hands them over.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class FilmList {

    /**
     * The name of the database view to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "film_list";

    // ------------------------------------------------------------------------------------------------------------- FID

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_FID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_FID = "FID";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FID = "fid";

    // ----------------------------------------------------------------------------------------------------------- title

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_TITLE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_TITLE = "title";

    /**
     * The length of the {@value #COLUMN_NAME_TITLE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_TITLE = 128;

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
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_DESCRIPTION = "description";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_DESCRIPTION} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_DESCRIPTION = "description";

    // -------------------------------------------------------------------------------------------------------- category

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_CATEGORY} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_CATEGORY = "category";

    /**
     * The length of the {@value #COLUMN_NAME_CATEGORY} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_CATEGORY = 25;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CATEGORY} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CATEGORY = "category";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_CATEGORY} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_CATEGORY = COLUMN_LENGTH_CATEGORY;

    // ----------------------------------------------------------------------------------------------------------- price

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_PRICE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_PRICE = "price";

    /**
     * The precision of the {@value #COLUMN_NAME_PRICE} column. The value is {@value}.
     */
    public static final int COLUMN_PRECISION_PRICE = 4;

    /**
     * The scale of the {@value #COLUMN_NAME_PRICE} column. The value is {@value}.
     */
    public static final int COLUMN_SCALE_PRICE = 2;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PRICE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PRICE = "price";

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_LENGTH} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LENGTH = "length";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LENGTH} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LENGTH = "length";

    // ---------------------------------------------------------------------------------------------------------- rating

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_RATING} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_RATING = "rating";

    /**
     * The length of the {@value #COLUMN_NAME_RATING} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_RATING = 5;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RATING} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RATING = "rating";

    // ---------------------------------------------------------------------------------------------------------- actors

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ACTORS} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_ACTORS = "actors";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTORS} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTORS = "actors";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected FilmList() {
        super();
    }

    /**
     * Creates a new instance with the specified attribute values.
     *
     * @param fid         a value for the {@value #ATTRIBUTE_NAME_FID} attribute.
     * @param title       a value for the {@value #ATTRIBUTE_NAME_TITLE} attribute.
     * @param description a value for the {@value #ATTRIBUTE_NAME_DESCRIPTION} attribute.
     * @param category    a value for the {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     * @param price       a value for the {@value #ATTRIBUTE_NAME_PRICE} attribute.
     * @param length      a value for the {@value #ATTRIBUTE_NAME_LENGTH} attribute.
     * @param rating      a value for the {@value #ATTRIBUTE_NAME_RATING} attribute.
     * @param actors      a value for the {@value #ATTRIBUTE_NAME_ACTORS} attribute.
     */
    public FilmList(final Integer fid,
                    final String title,
                    final String description,
                    final String category,
                    final BigDecimal price,
                    final Integer length,
                    final Film.Rating rating,
                    final String actors) {
        super();
        this.fid = fid;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.length = length;
        this.rating = rating;
        this.actors = actors;
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "fid=" + fid +
               ",title=" + title +
               ",description=" + description +
               ",category=" + category +
               ",price=" + price +
               ",length=" + length +
               ",rating=" + rating +
               ",actors=" + actors +
               '}';
    }

    // ------------------------------------------------------------------------------------------------------------- fid

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FID} attribute.
     */
    @Nonnull
    public Integer getFid() {
        return fid;
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

    // -------------------------------------------------------------------------------------------------------- category

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     */
    @Nullable
    public String getCategory() {
        return category;
    }

    // ----------------------------------------------------------------------------------------------------------- price

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PRICE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PRICE} attribute.
     */
    @Nonnull
    public BigDecimal getPrice() {
        return price;
    }

    // ---------------------------------------------------------------------------------------------------------- length

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LENGTH} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LENGTH} attribute.
     */
    @Nullable
    public Integer getLength() {
        return length;
    }

    // ---------------------------------------------------------------------------------------------------------- rating

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RATING} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RATING} attribute.
     */
    @Nullable
    public Film.Rating getRating() {
        return rating;
    }

    // ---------------------------------------------------------------------------------------------------------- actors

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTORS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTORS} attribute.
     */
    @Nullable
    public String getActors() {
        return actors;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FID, nullable = false, insertable = false, updatable = false)
    private Integer fid;

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

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = SIZE_MAX_CATEGORY)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_CATEGORY, nullable = true, insertable = false, updatable = false,
            length = COLUMN_LENGTH_CATEGORY)
    private String category;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Digits(integer = COLUMN_PRECISION_PRICE - COLUMN_SCALE_PRICE, fraction = COLUMN_SCALE_PRICE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_PRICE, nullable = false, insertable = false, updatable = false,
            precision = COLUMN_PRECISION_PRICE, scale = COLUMN_SCALE_PRICE)
    private BigDecimal price;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_LENGTH, nullable = true, insertable = false, updatable = false)
    private Integer length;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_RATING, nullable = true, insertable = false, updatable = false,
            length = COLUMN_LENGTH_RATING)
    private Film.Rating rating;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ACTORS, nullable = true, insertable = false, updatable = false)
    private String actors;
}
