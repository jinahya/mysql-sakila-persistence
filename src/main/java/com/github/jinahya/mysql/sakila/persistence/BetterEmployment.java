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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * An entity class for mapping the {@value BetterEmployment#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE better_employment (
 *   id BIGINT NOT NULL AUTO_INCREMENT,
 *   store_id BIGINT NOT NULL,
 *   staff_id BIGINT NOT NULL,
 *   manager BOOLEAN NULL DEFAULT NULL,
 *   PRIMARY KEY (id),
 *   UNIQUE KEY uk_better_employment_staff (staff_id),
 *   UNIQUE KEY uk_better_employment_manager (manager, store_id),
 *   CONSTRAINT ck_better_employment_manager CHECK (manager IS NULL OR manager = TRUE),
 *   CONSTRAINT fk_better_employment_store FOREIGN KEY (store_id) REFERENCES better_store (id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_better_employment_staff FOREIGN KEY (staff_id) REFERENCES better_staff (id)
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
 * table is created by {@code 88-better-employment.sql}, which runs after the installer's scripts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedQuery(name = "BetterEmployment.selectOneById",
            query = """
                    SELECT e
                    FROM BetterEmployment e
                    WHERE e.id = :id"""
)
@NamedQuery(name = "BetterEmployment.selectListOrderByIdAsc",
            query = """
                    SELECT e
                    FROM BetterEmployment e
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "BetterEmployment.selectOneByStaff",
            query = """
                    SELECT e
                    FROM BetterEmployment e
                    WHERE e.staff = :staff"""
)
@NamedQuery(name = "BetterEmployment.selectListByStoreOrderByIdAsc",
            query = """
                    SELECT e
                    FROM BetterEmployment e
                    WHERE e.store = :store
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "BetterEmployment.selectOneManagerByStore",
            query = """
                    SELECT e
                    FROM BetterEmployment e
                    WHERE e.store = :store
                      AND e.manager = TRUE"""
)
@NamedQuery(name = "BetterEmployment.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM BetterEmployment e"""
)
@Entity
@Table(name = BetterEmployment.TABLE_NAME,
       uniqueConstraints = {
               @UniqueConstraint(
                       columnNames = {
                               BetterEmployment.COLUMN_NAME_STAFF_ID
                       }
               ),
               @UniqueConstraint(
                       columnNames = {
                               BetterEmployment.COLUMN_NAME_MANAGER,
                               BetterEmployment.COLUMN_NAME_STORE_ID
                       }
               )
       }
)
public class BetterEmployment {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "better_employment";

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

    /**
     * The name of the read-only attribute which also maps the {@value #COLUMN_NAME_STORE_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE_ID = "storeId";

    // -------------------------------------------------------------------------------------------------------- staff_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STAFF} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_STAFF_ID = "staff_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STAFF_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STAFF = "staff";

    /**
     * The name of the read-only attribute which also maps the {@value #COLUMN_NAME_STAFF_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STAFF_ID = "staffId";

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
    protected BetterEmployment() {
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
     * @implSpec Equality is by the business key, the unique {@value #COLUMN_NAME_STAFF_ID} column, rather than
     *         by the generated {@code @Id}. The column is never updated, so the key does not change under a
     *         {@link java.util.HashSet}; the {@value #COLUMN_NAME_STORE_ID} column is, so it is no part of the key. It
     *         is read through its read-only getter, which a lazy proxy answers, rather than off the field, which a
     *         proxy leaves unset.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BetterEmployment that)) {
            return false;
        }
        return Objects.equals(getStaffId(), that.getStaffId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the business key, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getStaffId());
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

    // --------------------------------------------------------------------------------------------------------- storeId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STORE_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STORE_ID} attribute; {@code null} until a persisted store is
     *         set, or the instance is loaded from the database.
     */
    Long getStoreId() {
        return storeId;
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
     * Replaces current value of {@value #ATTRIBUTE_NAME_STORE} attribute with the specified value.
     * <p>
     * A transfer, when the instance is already persisted. The identifier of the {@code store} is copied to the
     * read-only {@value #ATTRIBUTE_NAME_STORE_ID}, so the store has to be persisted first, and, under a provider which
     * assigns an {@code IDENTITY} value only at the flush, flushed.
     *
     * @param store new value for {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    public void setStore(final BetterStore store) {
        this.store = store;
        this.storeId = this.store == null ? null : this.store.getId();
    }

    // --------------------------------------------------------------------------------------------------------- staffId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STAFF_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STAFF_ID} attribute; {@code null} until a persisted staff member
     *         is set, or the instance is loaded from the database.
     */
    Long getStaffId() {
        return staffId;
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
     * @implNote The identifier of the {@code staff} is copied to the read-only {@value #ATTRIBUTE_NAME_STAFF_ID}, so
     *         the staff member has to be persisted first, and, under a provider which assigns an {@code IDENTITY}
     *         value only at the flush, flushed.
     */
    public void setStaff(final BetterStaff staff) {
        this.staff = staff;
        this.staffId = this.staff == null ? null : this.staff.getId();
    }

    // --------------------------------------------------------------------------------------------------------- manager

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute; {@code TRUE} for the manager of the store,
     *         {@code null} otherwise.
     */
    public Boolean getManager() {
        return manager;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute with the specified value.
     *
     * @param manager new value for {@value #ATTRIBUTE_NAME_MANAGER} attribute; {@code TRUE} for the manager of the
     *                store, {@code null} otherwise; never {@code FALSE}.
     */
    public void setManager(final Boolean manager) {
        this.manager = manager;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = true, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Column(name = COLUMN_NAME_STORE_ID, nullable = false, insertable = false, updatable = false)
    private Long storeId;

    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STORE_ID, referencedColumnName = BetterStore.COLUMN_NAME_ID, nullable = false,
                insertable = true, updatable = true)
    private BetterStore store;

    // -----------------------------------------------------------------------------------------------------------------
    @Column(name = COLUMN_NAME_STAFF_ID, nullable = false, insertable = false, updatable = false)
    private Long staffId;

    @Valid
    @NotNull
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STAFF_ID, referencedColumnName = BetterStaff.COLUMN_NAME_ID, nullable = false,
                insertable = true, updatable = false)
    private BetterStaff staff;

    // -----------------------------------------------------------------------------------------------------------------
    @AssertTrue // TRUE or NULL, never FALSE, as the table's check constraint has it
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_MANAGER, nullable = true, insertable = true, updatable = true)
    private Boolean manager;
}
