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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * An entity class for mapping the {@value BetterStaff#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE better_staff (
 *   id BIGINT NOT NULL AUTO_INCREMENT,
 *   name VARCHAR(45) NOT NULL,
 *   PRIMARY KEY (id)
 * )
 *}
 * <p>
 * A staff member who refers to nothing. Unlike a {@link Staff}, it names no store; the store they work at, if any, is
 * the {@link BetterStoreStaff} row of them, so a store and a staff member can each be persisted first, without the
 * cycle of {@code store} and {@code staff}. The table is created by {@code 88-better-store-staff.sql}, which runs after
 * the installer's scripts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BetterStoreStaff
 */
@NamedQuery(name = "BetterStaff.selectOneById",
            query = """
                    SELECT e
                    FROM BetterStaff e
                    WHERE e.id = :id"""
)
@NamedQuery(name = "BetterStaff.selectListOrderByIdAsc",
            query = """
                    SELECT e
                    FROM BetterStaff e
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "BetterStaff.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM BetterStaff e"""
)
@Entity
@Table(name = BetterStaff.TABLE_NAME)
public class BetterStaff {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "better_staff";

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ID} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ID = "id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // ------------------------------------------------------------------------------------------------------------ name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_NAME} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_NAME = "name";

    /**
     * The length of the {@value #COLUMN_NAME_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_NAME = 45;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_NAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_NAME = "name";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_NAME = COLUMN_LENGTH_NAME;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected BetterStaff() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
               ",name=" + name +
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
        if (!(obj instanceof BetterStaff that)) {
            return false;
        }
        final var thisId = getId();
        return thisId != null && thisId.equals(that.getId());
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
        return BetterStaff.class.hashCode();
    }

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ID} attribute; {@code null} until the instance is persisted.
     */
    public Long getId() {
        return id;
    }

    // ------------------------------------------------------------------------------------------------------------ name

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_NAME} attribute with the specified value.
     *
     * @param name new value for {@value #ATTRIBUTE_NAME_NAME} attribute.
     */
    public void setName(final String name) {
        this.name = name;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = true, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = true, length = COLUMN_LENGTH_NAME)
    private String name;
}
