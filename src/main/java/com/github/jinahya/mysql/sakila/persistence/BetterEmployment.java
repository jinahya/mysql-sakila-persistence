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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * An entity class for mapping the {@value BetterEmployment#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE better_employment (
 *   store_id BIGINT NOT NULL,
 *   staff_id BIGINT NOT NULL,
 *   manager BOOLEAN NULL DEFAULT NULL,
 *   PRIMARY KEY (store_id, staff_id),
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
 * primary key is the two foreign keys, as an {@link EmbeddedId @EmbeddedId}; each association is
 * {@link MapsId @MapsId} to its component of the {@value #ATTRIBUTE_NAME_ID}. The {@value #COLUMN_NAME_MANAGER} column
 * is {@code TRUE}, for the manager, or {@code NULL}, never {@code FALSE}, so the unique key of it and the
 * {@value #COLUMN_NAME_STORE_ID} column, whose {@code NULL}s do not collide, allows one manager per store.
 * <p>
 * That a staff member works at one store at most is a rule of {@link BetterStore#addStaff(BetterStaff)}, not of the
 * table. The table is created by {@code 88-better-employment.sql}, which runs after the installer's scripts.
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
                    ORDER BY e.id.storeId ASC, e.id.staffId ASC"""
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
                    ORDER BY e.id.staffId ASC"""
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

    // -------------------------------------------------------------------------------------------------------- store_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STORE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_STORE_ID = "store_id";

    /**
     * The path of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} column, inside the
     * {@link EmbeddedId @EmbeddedId}. The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_STORE_ID = "id.storeId";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} column. The association is
     * {@link MapsId @MapsId} to the {@value #ATTRIBUTE_PATH_ID_STORE_ID} attribute. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE = "store";

    // -------------------------------------------------------------------------------------------------------- staff_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STAFF} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_STAFF_ID = "staff_id";

    /**
     * The path of the attribute which maps the {@value #COLUMN_NAME_STAFF_ID} column, inside the
     * {@link EmbeddedId @EmbeddedId}. The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_STAFF_ID = "id.staffId";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STAFF_ID} column. The association is
     * {@link MapsId @MapsId} to the {@value #ATTRIBUTE_PATH_ID_STAFF_ID} attribute. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STAFF = "staff";

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} and {@value #COLUMN_NAME_STAFF_ID}
     * columns, as an {@link EmbeddedId @EmbeddedId}. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

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

    // ------------------------------------------------------------------------------------------ STATIC FACTORY METHODS

    /**
     * Returns a new instance which links the specified staff to the specified store.
     *
     * @param store the store.
     * @param staff the staff.
     * @return a new instance of {@code store} and {@code staff}.
     */
    public static BetterEmployment of(final BetterStore store, final BetterStaff staff) {
        final var instance = new BetterEmployment();
        instance.id = BetterEmploymentId.of(store.getId(), staff.getId());
        instance.store = store;
        instance.staff = staff;
        return instance;
    }

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
     * @implSpec Equality is by the {@link EmbeddedId @EmbeddedId} alone, and only once it is assigned; an
     *         instance without one equals nothing but itself.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BetterEmployment that)) {
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
     *         {@link EmbeddedId @EmbeddedId} is assigned; it is consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return BetterEmployment.class.hashCode();
    }

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     */
    public BetterEmploymentId getId() {
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

    // ----------------------------------------------------------------------------------------------------------- staff

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STAFF} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STAFF} attribute.
     */
    public BetterStaff getStaff() {
        return staff;
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
    @EmbeddedId
    private BetterEmploymentId id;

    // -----------------------------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @MapsId(BetterEmploymentId.ATTRIBUTE_NAME_STORE_ID)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STORE_ID, referencedColumnName = BetterStore.COLUMN_NAME_ID, nullable = false,
                insertable = true, updatable = false)
    private BetterStore store;

    // -----------------------------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @MapsId(BetterEmploymentId.ATTRIBUTE_NAME_STAFF_ID)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STAFF_ID, referencedColumnName = BetterStaff.COLUMN_NAME_ID, nullable = false,
                insertable = true, updatable = false)
    private BetterStaff staff;

    // -----------------------------------------------------------------------------------------------------------------
    @AssertTrue // TRUE or NULL, never FALSE, as the table's check constraint has it
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_MANAGER, nullable = true, insertable = true, updatable = true)
    private Boolean manager;
}
