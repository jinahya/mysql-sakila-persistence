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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * An entity class for mapping the {@value BetterStoreStaff#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE better_store_staff (
 *   id BIGINT NOT NULL AUTO_INCREMENT,
 *   store_id BIGINT NOT NULL,
 *   staff_id BIGINT NOT NULL,
 *   manager BOOLEAN NULL DEFAULT NULL,
 *   PRIMARY KEY (id),
 *   UNIQUE KEY uk_better_store_staff_staff (staff_id),
 *   UNIQUE KEY uk_better_store_staff_manager (manager, store_id),
 *   CONSTRAINT ck_better_store_staff_manager CHECK (manager IS NULL OR manager = TRUE),
 *   CONSTRAINT fk_better_store_staff_store FOREIGN KEY (store_id) REFERENCES better_store (id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_better_store_staff_staff FOREIGN KEY (staff_id) REFERENCES better_staff (id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * A row links a {@link BetterStaff} to the {@link BetterStore} they work at, and flags the manager of the store. The
 * {@value #COLUMN_NAME_STAFF_ID} column is unique, so a staff member works at one store at most; the
 * {@value #COLUMN_NAME_MANAGER} column is {@code TRUE}, for the manager, or {@code NULL}, never {@code FALSE}, so the
 * unique key of it and the {@value #COLUMN_NAME_STORE_ID} column, whose {@code NULL}s do not collide, allows one
 * manager per store. The manager works at the store they manage, for the flag is on the row which says so.
 * <p>
 * What the table does not enforce is that every store has a manager, and that every staff member works at a store. The
 * table is created by {@code 99-better-store-staff.sql}, which runs after the installer's scripts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedQuery(name = "BetterStoreStaff.selectOneById",
            query = """
                    SELECT e
                    FROM BetterStoreStaff e
                    WHERE e.id = :id"""
)
@NamedQuery(name = "BetterStoreStaff.selectListOrderByIdAsc",
            query = """
                    SELECT e
                    FROM BetterStoreStaff e
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "BetterStoreStaff.selectOneByStaff",
            query = """
                    SELECT e
                    FROM BetterStoreStaff e
                    WHERE e.staff = :staff"""
)
@NamedQuery(name = "BetterStoreStaff.selectListByStoreOrderByIdAsc",
            query = """
                    SELECT e
                    FROM BetterStoreStaff e
                    WHERE e.store = :store
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "BetterStoreStaff.selectOneManagerByStore",
            query = """
                    SELECT e
                    FROM BetterStoreStaff e
                    WHERE e.store = :store
                      AND e.manager = TRUE"""
)
@NamedQuery(name = "BetterStoreStaff.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM BetterStoreStaff e"""
)
@Entity
@Table(name = BetterStoreStaff.TABLE_NAME,
       uniqueConstraints = {
               @UniqueConstraint(
                       columnNames = {
                               BetterStoreStaff.COLUMN_NAME_STAFF_ID
                       }
               ),
               @UniqueConstraint(
                       columnNames = {
                               BetterStoreStaff.COLUMN_NAME_MANAGER,
                               BetterStoreStaff.COLUMN_NAME_STORE_ID
                       }
               )
       }
)
public class BetterStoreStaff {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "better_store_staff";

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ID} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ID = "id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // -------------------------------------------------------------------------------------------------------- store_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STORE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_STORE_ID = "store_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE = "store";

    // -------------------------------------------------------------------------------------------------------- staff_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STAFF} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_STAFF_ID = "staff_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STAFF_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STAFF = "staff";

    // --------------------------------------------------------------------------------------------------------- manager

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_MANAGER} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_MANAGER = "manager";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_MANAGER} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_MANAGER = "manager";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected BetterStoreStaff() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
               ",manager=" + manager +
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
        if (!(obj instanceof BetterStoreStaff that)) {
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
        return BetterStoreStaff.class.hashCode();
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

    // ----------------------------------------------------------------------------------------------------------- store

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    public BetterStore getStore() {
        return store;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_STORE} attribute with the specified value; a transfer, when the
     * instance is already persisted.
     *
     * @param store new value for {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    public void setStore(final BetterStore store) {
        this.store = store;
    }

    // ----------------------------------------------------------------------------------------------------------- staff

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STAFF} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STAFF} attribute.
     */
    public BetterStaff getStaff() {
        return staff;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_STAFF} attribute with the specified value.
     *
     * @param staff new value for {@value #ATTRIBUTE_NAME_STAFF} attribute.
     */
    public void setStaff(final BetterStaff staff) {
        this.staff = staff;
    }

    // --------------------------------------------------------------------------------------------------------- manager

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute; {@code TRUE} for the manager of the store,
     *         {@code null} otherwise.
     */
    @Nullable
    public Boolean getManager() {
        return manager;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute with the specified value.
     *
     * @param manager new value for {@value #ATTRIBUTE_NAME_MANAGER} attribute; {@code TRUE} for the manager of the
     *                store, {@code null} otherwise; never {@code FALSE}.
     */
    public void setManager(@Nullable final Boolean manager) {
        this.manager = manager;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = true, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STORE_ID, referencedColumnName = BetterStore.COLUMN_NAME_ID, nullable = false,
                insertable = true, updatable = true)
    private BetterStore store;

    // -----------------------------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STAFF_ID, referencedColumnName = BetterStaff.COLUMN_NAME_ID, nullable = false,
                insertable = true, updatable = false)
    private BetterStaff staff;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @AssertTrue // TRUE or NULL, never FALSE, as the table's check constraint has it
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_MANAGER, nullable = true, insertable = true, updatable = true)
    private Boolean manager;
}
