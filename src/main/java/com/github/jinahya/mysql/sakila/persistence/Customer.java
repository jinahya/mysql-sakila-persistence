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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An entity class for mapping the {@value Customer#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE customer (
 *   customer_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   store_id TINYINT UNSIGNED NOT NULL,
 *   first_name VARCHAR(45) NOT NULL,
 *   last_name VARCHAR(45) NOT NULL,
 *   email VARCHAR(50) DEFAULT NULL,
 *   address_id SMALLINT UNSIGNED NOT NULL,
 *   active BOOLEAN NOT NULL DEFAULT TRUE,
 *   create_date DATETIME NOT NULL,
 *   last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (customer_id),
 *   KEY idx_fk_store_id (store_id),
 *   KEY idx_fk_address_id (address_id),
 *   KEY idx_last_name (last_name),
 *   CONSTRAINT fk_customer_address FOREIGN KEY (address_id) REFERENCES address (address_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_customer_store FOREIGN KEY (store_id) REFERENCES store (store_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * Unlike most tables of the database, the {@value #COLUMN_NAME_LAST_UPDATE} column of this one is nullable.
 * <p>
 * The {@value #COLUMN_NAME_CREATE_DATE} column is set, on every insert, by a trigger, which replaces whatever value is
 * given with {@code NOW()}; so this class maps it read-only.
 * {@snippet lang = "sql":
 * CREATE TRIGGER customer_create_date BEFORE INSERT ON customer
 *   FOR EACH ROW SET NEW.create_date = NOW()
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-customer.html">The customer Table</a>
 */
@NamedQuery(name = "Customer.selectOneByCustomerId",
            query = """
                    SELECT e
                    FROM Customer e
                    WHERE e.customerId = :customerId"""
)
@NamedQuery(name = "Customer.selectListOrderByCustomerIdAsc",
            query = """
                    SELECT e
                    FROM Customer e
                    ORDER BY e.customerId ASC"""
)
@NamedQuery(name = "Customer.selectListOrderByCustomerIdAscCustomerIdGt",
            query = """
                    SELECT e
                    FROM Customer e
                    WHERE e.customerId > :customerIdMinExclusive
                    ORDER BY e.customerId ASC"""
)
@NamedQuery(name = "Customer.selectListByStoreOrderByCustomerIdAsc",
            query = """
                    SELECT e
                    FROM Customer e
                    WHERE e.store = :store
                    ORDER BY e.customerId ASC"""
)
@NamedQuery(name = "Customer.selectListByLastNameOrderByFirstNameAsc",
            query = """
                    SELECT e
                    FROM Customer e
                    WHERE e.lastName = :lastName
                    ORDER BY e.firstName ASC"""
)
@NamedQuery(name = "Customer.selectListByEmail",
            query = """
                    SELECT e
                    FROM Customer e
                    WHERE e.email = :email"""
)
@NamedQuery(name = "Customer.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Customer e"""
)
@NamedQuery(name = "Customer.countByStore",
            query = """
                    SELECT COUNT(e)
                    FROM Customer e
                    WHERE e.store = :store"""
)
@Entity
@Table(name = Customer.TABLE_NAME)
public class Customer {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "customer";

    // ----------------------------------------------------------------------------------------------------- customer_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CUSTOMER_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CUSTOMER_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CUSTOMER_ID = "customerId";

    // -------------------------------------------------------------------------------------------------------- store_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STORE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_STORE_ID = "store_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE = "store";

    // ------------------------------------------------------------------------------------------------------ first_name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_FIRST_NAME = "first_name";

    /**
     * The length of the {@value #COLUMN_NAME_FIRST_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_FIRST_NAME = 45;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FIRST_NAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FIRST_NAME = "firstName";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_FIRST_NAME = COLUMN_LENGTH_FIRST_NAME;

    // ------------------------------------------------------------------------------------------------------- last_name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LAST_NAME} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LAST_NAME = "last_name";

    /**
     * The length of the {@value #COLUMN_NAME_LAST_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_LAST_NAME = 45;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LAST_NAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LAST_NAME = "lastName";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_LAST_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_LAST_NAME = COLUMN_LENGTH_LAST_NAME;

    // ----------------------------------------------------------------------------------------------------------- email

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_EMAIL} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_EMAIL = "email";

    /**
     * The length of the {@value #COLUMN_NAME_EMAIL} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_EMAIL = 50;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_EMAIL} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_EMAIL = "email";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_EMAIL} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_EMAIL = COLUMN_LENGTH_EMAIL;

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

    // ---------------------------------------------------------------------------------------------------------- active

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ACTIVE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTIVE = "active";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTIVE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTIVE = "active";

    // ----------------------------------------------------------------------------------------------------- create_date

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CREATE_DATE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_CREATE_DATE = "create_date";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CREATE_DATE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CREATE_DATE = "createDate";

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

    // --------------------------------------------------------------------------------------------------------- rentals

    /**
     * The name of the attribute which maps the {@link Rental rentals} of this customer. The value is {@value}.
     *
     * @see Rental#ATTRIBUTE_NAME_CUSTOMER
     */
    public static final String ATTRIBUTE_NAME_RENTALS = "rentals";

    // -------------------------------------------------------------------------------------------------------- payments

    /**
     * The name of the attribute which maps the {@link Payment payments} of this customer. The value is {@value}.
     *
     * @see Payment#ATTRIBUTE_NAME_CUSTOMER
     */
    public static final String ATTRIBUTE_NAME_PAYMENTS = "payments";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Customer() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "customerId=" + customerId +
//               ",store=" + store +
               ",firstName=" + firstName +
               ",lastName=" + lastName +
               ",email=" + email +
//               ",address=" + address +
               ",active=" + active +
               ",createDate=" + createDate +
               ",lastUpdate=" + lastUpdate +
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
        if (!(obj instanceof Customer that)) {
            return false;
        }
        final var thisId = getCustomerId();
        return thisId != null && thisId.equals(that.getCustomerId());
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
        return Customer.class.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------ customerId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CUSTOMER_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CUSTOMER_ID} attribute; {@code null} until the instance is
     *         persisted.
     */
    public Integer getCustomerId() {
        return customerId;
    }

    // ----------------------------------------------------------------------------------------------------------- store

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    public Store getStore() {
        return store;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_STORE} attribute with the specified value.
     *
     * @param store new value for {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    public void setStore(final Store store) {
        this.store = store;
    }

    // ------------------------------------------------------------------------------------------------------- firstName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute with the specified value.
     *
     * @param firstName new value for {@value #ATTRIBUTE_NAME_FIRST_NAME} attribute.
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    // -------------------------------------------------------------------------------------------------------- lastName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_NAME} attribute.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_LAST_NAME} attribute with the specified value.
     *
     * @param lastName new value for {@value #ATTRIBUTE_NAME_LAST_NAME} attribute.
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    // ----------------------------------------------------------------------------------------------------------- email

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_EMAIL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_EMAIL} attribute.
     */
    @Nullable
    public String getEmail() {
        return email;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_EMAIL} attribute with the specified value.
     *
     * @param email new value for {@value #ATTRIBUTE_NAME_EMAIL} attribute.
     */
    public void setEmail(@Nullable final String email) {
        this.email = email;
    }

    // --------------------------------------------------------------------------------------------------------- address

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute with the specified value.
     *
     * @param address new value for {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     */
    public void setAddress(final Address address) {
        this.address = address;
    }

    // ---------------------------------------------------------------------------------------------------------- active

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTIVE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTIVE} attribute.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ACTIVE} attribute with the specified value.
     *
     * @param active new value for {@value #ATTRIBUTE_NAME_ACTIVE} attribute.
     */
    public void setActive(final Boolean active) {
        this.active = active;
    }

    // ------------------------------------------------------------------------------------------------------ createDate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CREATE_DATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CREATE_DATE} attribute; {@code null} until the instance is read
     *         from the database.
     */
    public LocalDateTime getCreateDate() {
        return createDate;
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

    // --------------------------------------------------------------------------------------------------------- rentals
    List<Rental> getRentals() {
        return rentals;
    }

    void setRentals(final List<Rental> rentals) {
        this.rentals = rentals;
    }

    // -------------------------------------------------------------------------------------------------------- payments
    List<Payment> getPayments() {
        return payments;
    }

    void setPayments(final List<Payment> payments) {
        this.payments = payments;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_CUSTOMER_ID, nullable = false, insertable = true, updatable = false)
    private Integer customerId;

    // -----------------------------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STORE_ID, referencedColumnName = Store.COLUMN_NAME_STORE_ID, nullable = false,
                insertable = true, updatable = true)
    private Store store;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_FIRST_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FIRST_NAME, nullable = false, insertable = true, updatable = true,
            length = COLUMN_LENGTH_FIRST_NAME)
    private String firstName;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_LAST_NAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LAST_NAME, nullable = false, insertable = true, updatable = true,
            length = COLUMN_LENGTH_LAST_NAME)
    private String lastName;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = SIZE_MAX_EMAIL)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_EMAIL, nullable = true, insertable = true, updatable = true,
            length = COLUMN_LENGTH_EMAIL)
    private String email;

    // -----------------------------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_ADDRESS_ID, referencedColumnName = Address.COLUMN_NAME_ADDRESS_ID, nullable = false,
                insertable = true, updatable = true)
    private Address address;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ACTIVE, nullable = false, insertable = true, updatable = true)
    private Boolean active;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_CREATE_DATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_DATETIME_CURRENT_TIMESTAMP)
    private LocalDateTime createDate;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = true, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // -----------------------------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = Rental.ATTRIBUTE_NAME_CUSTOMER, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Rental> rentals;

    @OneToMany(mappedBy = Payment.ATTRIBUTE_NAME_CUSTOMER, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Payment> payments;
}
