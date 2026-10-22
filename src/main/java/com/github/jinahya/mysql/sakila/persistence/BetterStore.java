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
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * An entity class for mapping the {@value BetterStore#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE better_store (
 *   id BIGINT NOT NULL AUTO_INCREMENT,
 *   name VARCHAR(45) NOT NULL,
 *   PRIMARY KEY (id)
 * )
 *}
 * <p>
 * A store which refers to nothing. Unlike a {@link Store}, it names no manager; its staff, and its manager among them,
 * are the {@link BetterStoreStaff} rows of it, so a store and a staff member can each be persisted first, without the
 * cycle of {@code store} and {@code staff}. The table is created by {@code 88-better-store-staff.sql}, which runs after
 * the installer's scripts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BetterStoreStaff
 */
@NamedQuery(name = "BetterStore.selectOneById",
            query = """
                    SELECT e
                    FROM BetterStore e
                    WHERE e.id = :id"""
)
@NamedQuery(name = "BetterStore.selectListOrderByIdAsc",
            query = """
                    SELECT e
                    FROM BetterStore e
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "BetterStore.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM BetterStore e"""
)
@Entity
@Table(name = BetterStore.TABLE_NAME)
public class BetterStore
        extends Better {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "better_store";

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

    // ---------------------------------------------------------------------------------------------------------- staffs

    /**
     * The name of the attribute which maps the {@link BetterStoreStaff store-staffs} of this store. The value is
     * {@value}.
     *
     * @see BetterStoreStaff#ATTRIBUTE_NAME_STORE
     */
    public static final String ATTRIBUTE_NAME_STORE_STAFFS = "storeStaffs";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected BetterStore() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "name=" + name +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equal only to another {@code BetterStore}, and then as {@link Better#equals(Object)} has it: by
     *         the {@code @Id} alone, and only once it is assigned.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof BetterStore)) {
            return false;
        }
        return super.equals(obj);
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
        return BetterStore.class.hashCode();
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

    // ---------------------------------------------------------------------------------------------------------- staffs
    List<BetterStoreStaff> getStoreStaffs() {
        return storeStaffs;
    }

    void setStoreStaffs(final List<BetterStoreStaff> staffs) {
        this.storeStaffs = staffs;
    }

    public void addStaff(final BetterStaff staff) {
        // the row which links the staff to their store; null when they work at none
        final var storeStaff = staff.getStoreStaff();
        if (storeStaff != null) {
            if (equals(storeStaff.getStore())) {
                // works at this store already
                return;
            }
            // works at another store; fire them there first
            storeStaff.getStore().removeStaff(staff);
        }
        // hire them with a new row, which the cascade persists along with this store
        final var created = new BetterStoreStaff();
        created.setStore(this);
        created.setStaff(staff);
        staff.setStoreStaff(created);
        storeStaffs.add(created);
    }

    public void removeStaff(final BetterStaff staff) {
        // the row which links the staff to their store; null when they work at none
        final var storeStaff = staff.getStoreStaff();
        if (storeStaff == null || !equals(storeStaff.getStore())) {
            // works at no store, or at another
            return;
        }
        // fire them; out of this store's staffs, the orphan removal deletes the row
        final var removed = storeStaffs.remove(storeStaff);
        staff.setStoreStaff(null);
    }

    /**
     * Returns the manager of this store.
     *
     * @return the staff member whose {@link BetterStoreStaff} row of this store is flagged as the manager's;
     *         {@code null} when the store has no manager, or is not yet loaded from the database.
     */
    public BetterStaff getManager() {
        if (storeStaffs == null) {
            return null;
        }
        return storeStaffs.stream()
                .filter(v -> Boolean.TRUE.equals(v.getManager()))
                .map(BetterStoreStaff::getStaff)
                .findFirst()
                .orElse(null);
    }

    public void setManager(final BetterStaff staff) {
        // unflag the current manager's row, if any; first, before a transferred row could add a second flag
        final var manager = getManager();
        if (manager != null) {
            manager.getStoreStaff().setManager(null);
        }
        // hire them, from another store or from none
        addStaff(staff);
        // flag the new manager's row
        staff.getStoreStaff().setManager(Boolean.TRUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = true, length = COLUMN_LENGTH_NAME)
    private String name;

    // -----------------------------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = BetterStoreStaff.ATTRIBUTE_NAME_STORE, fetch = FetchType.LAZY, cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<@Valid @NotNull BetterStoreStaff> storeStaffs = new ArrayList<>();

    // -----------------------------------------------------------------------------------------------------------------
}
