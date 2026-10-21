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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * An entity class for mapping the {@value Store#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE store (
 *   store_id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   manager_staff_id TINYINT UNSIGNED NOT NULL,
 *   address_id SMALLINT UNSIGNED NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (store_id),
 *   UNIQUE KEY idx_unique_manager (manager_staff_id),
 *   KEY idx_fk_address_id (address_id),
 *   CONSTRAINT fk_store_staff FOREIGN KEY (manager_staff_id) REFERENCES staff (staff_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_store_address FOREIGN KEY (address_id) REFERENCES address (address_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * A store and its staff refer to each other, and neither column is nullable: a {@link Staff} works at a store, and a
 * store is managed by a {@link Staff}. Neither row can therefore be the first one, and the installed data is loaded
 * with {@code FOREIGN_KEY_CHECKS} off. Through Jakarta Persistence alone, a new store can only be given a manager who
 * already works at some other store.
 * <p>
 * The {@value #COLUMN_NAME_MANAGER_STAFF_ID} column is unique, so the {@value #ATTRIBUTE_NAME_MANAGER_STAFF}
 * association is a {@link OneToOne @OneToOne}. The unique key is declared on the table, as {@code rental}'s is on
 * {@link Rental}, and left unnamed, for the provider to name.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-store.html">The store Table</a>
 */
@NamedQuery(name = "Store.selectOneByStoreId",
            query = """
                    SELECT e
                    FROM Store e
                    WHERE e.storeId = :storeId"""
)
@NamedQuery(name = "Store.selectListOrderByStoreIdAsc",
            query = """
                    SELECT e
                    FROM Store e
                    ORDER BY e.storeId ASC"""
)
@NamedQuery(name = "Store.selectOneByManagerStaff",
            query = """
                    SELECT e
                    FROM Store e
                    WHERE e.managerStaff = :managerStaff"""
)
@NamedQuery(name = "Store.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Store e"""
)
@Entity
@Table(name = Store.TABLE_NAME,
       uniqueConstraints = {
               @UniqueConstraint(
                       columnNames = {
                               Store.COLUMN_NAME_MANAGER_STAFF_ID
                       }
               )
       }
)
public class Store {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "store";

    // -------------------------------------------------------------------------------------------------------- store_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STORE_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_STORE_ID = "store_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE_ID = "storeId";

    // ------------------------------------------------------------------------------------------------ manager_staff_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_MANAGER_STAFF} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_MANAGER_STAFF_ID = "manager_staff_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_MANAGER_STAFF_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_MANAGER_STAFF = "managerStaff";

    // ------------------------------------------------------------------------------------------------------ address_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ADDRESS} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_ADDRESS_ID = "address_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ADDRESS_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ADDRESS = "address";

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

    // ------------------------------------------------------------------------------------------------------- customers

    /**
     * The name of the attribute which maps the {@link Customer customers} of this store. The value is {@value}.
     *
     * @see Customer#ATTRIBUTE_NAME_STORE
     */
    public static final String ATTRIBUTE_NAME_CUSTOMERS = "customers";

    // ----------------------------------------------------------------------------------------------------- inventories

    /**
     * The name of the attribute which maps the {@link Inventory inventories} of this store. The value is {@value}.
     *
     * @see Inventory#ATTRIBUTE_NAME_STORE
     */
    public static final String ATTRIBUTE_NAME_INVENTORIES = "inventories";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Store() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "storeId=" + storeId +
//               ",managerStaff=" + managerStaff +
//               ",address=" + address +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the business key, the identifier of the {@value #ATTRIBUTE_NAME_MANAGER_STAFF}
     *         -- the table's unique {@value #COLUMN_NAME_MANAGER_STAFF_ID} column -- rather than by the generated
     *         {@code @Id}, which is {@code null} until the row is inserted. The association is read through its getter,
     *         and only its identifier, so a lazy proxy, on either side, compares without being initialized.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof Store that)) {
            return false;
        }
        return Objects.equals(managerStaffIdOf(this), managerStaffIdOf(that));
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the business key, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(managerStaffIdOf(this));
    }

    private static Integer managerStaffIdOf(final Store store) {
        final var managerStaff = store.getManagerStaff();
        return managerStaff == null ? null : managerStaff.getStaffId();
    }

    // --------------------------------------------------------------------------------------------------------- storeId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STORE_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STORE_ID} attribute; {@code null} until the instance is
     *         persisted.
     */
    public Integer getStoreId() {
        return storeId;
    }

    // ---------------------------------------------------------------------------------------------------- managerStaff

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_MANAGER_STAFF} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_MANAGER_STAFF} attribute.
     */
    @Nonnull
    public Staff getManagerStaff() {
        return managerStaff;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_MANAGER_STAFF} attribute with the specified value.
     *
     * @param managerStaff new value for {@value #ATTRIBUTE_NAME_MANAGER_STAFF} attribute.
     */
    public void setManagerStaff(@Nonnull final Staff managerStaff) {
        this.managerStaff = managerStaff;
    }

    // --------------------------------------------------------------------------------------------------------- address

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     */
    @Nonnull
    public Address getAddress() {
        return address;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute with the specified value.
     *
     * @param address new value for {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     */
    public void setAddress(@Nonnull final Address address) {
        this.address = address;
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

    // ------------------------------------------------------------------------------------------------------- customers
    List<Customer> getCustomers() {
        return customers;
    }

    void setCustomers(final List<Customer> customers) {
        this.customers = customers;
    }

    // ----------------------------------------------------------------------------------------------------- inventories
    List<Inventory> getInventories() {
        return inventories;
    }

    void setInventories(final List<Inventory> inventories) {
        this.inventories = inventories;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_STORE_ID, nullable = false, insertable = true, updatable = false)
    private Integer storeId;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_MANAGER_STAFF_ID, referencedColumnName = Staff.COLUMN_NAME_STAFF_ID,
                nullable = false, insertable = true, updatable = true)
    private Staff managerStaff;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_ADDRESS_ID, referencedColumnName = Address.COLUMN_NAME_ADDRESS_ID, nullable = false,
                insertable = true, updatable = true)
    private Address address;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // -----------------------------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = Customer.ATTRIBUTE_NAME_STORE, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Customer> customers;

    @OneToMany(mappedBy = Inventory.ATTRIBUTE_NAME_STORE, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Inventory> inventories;
}
