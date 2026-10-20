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
 * See the License for the specific category governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * An entity class for mapping the {@value Category#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE category (
 *   category_id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   name VARCHAR(25) NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY (category_id)
 * )
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedQuery(name = "Category.selectOneByCategoryId",
            query = """
                    SELECT e
                    FROM Category e
                    WHERE e.categoryId = :categoryId"""
)
@NamedQuery(name = "Category.selectListOrderByCategoryIdAsc",
            query = """
                    SELECT e
                    FROM Category e
                    ORDER BY e.categoryId ASC"""
)
@NamedQuery(name = "Category.selectListByNameOrderByCategoryIdAsc",
            query = """
                    SELECT e
                    FROM Category e
                    WHERE e.name = :name
                    ORDER BY e.categoryId ASC"""
)
@NamedQuery(name = "Category.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Category e"""
)
@Entity
@Table(name = Category.TABLE_NAME)
public class Category {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "category";

    // ----------------------------------------------------------------------------------------------------- category_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_CATEGORY_ID = "category_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CATEGORY_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CATEGORY_ID = "categoryId";

    // ------------------------------------------------------------------------------------------------------------ name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_NAME} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_NAME = "name";

    /**
     * The length of the {@value #COLUMN_NAME_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_NAME = 25;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_NAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_NAME = "name";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_NAME = COLUMN_LENGTH_NAME;

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

    // ------------------------------------------------------------------------------------------------- filmCategories

    /**
     * The name of the attribute which maps the {@link FilmCategory film-categories} of this category. The value is
     * {@value}.
     *
     * @see FilmCategory#ATTRIBUTE_NAME_CATEGORY
     */
    public static final String ATTRIBUTE_NAME_FILM_CATEGORIES = "filmCategories";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Category() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "categoryId=" + categoryId +
               ",name=" + name +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof Category that)) {
            return false;
        }
        return Objects.equals(getCategoryId(), that.getCategoryId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the {@code @Id}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getCategoryId());
    }

    // ------------------------------------------------------------------------------------------------------ categoryId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CATEGORY_ID} attribute; {@code null} until the instance is
     * persisted.
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    // ------------------------------------------------------------------------------------------------------------ name

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_NAME} attribute with the specified value.
     *
     * @param name new value for {@value #ATTRIBUTE_NAME_NAME} attribute.
     */
    public void setName(@Nonnull final String name) {
        this.name = name;
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

    // ------------------------------------------------------------------------------------------------- filmCategories
    List<FilmCategory> getFilmCategories() {
        return filmCategories;
    }

    void setFilmCategories(final List<FilmCategory> filmCategories) {
        this.filmCategories = filmCategories;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_CATEGORY_ID, nullable = false, insertable = true, updatable = false)
    private Integer categoryId;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_NAME, nullable = false, length = COLUMN_LENGTH_NAME)
    private String name;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // -----------------------------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = FilmCategory.ATTRIBUTE_NAME_CATEGORY, fetch = FetchType.LAZY)
    private List<@Valid @NotNull FilmCategory> filmCategories;
}
