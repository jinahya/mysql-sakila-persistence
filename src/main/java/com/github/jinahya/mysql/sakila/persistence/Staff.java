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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * An entity class for mapping the {@value Staff#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE staff (
 *   staff_id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   first_name VARCHAR(45) NOT NULL,
 *   last_name VARCHAR(45) NOT NULL,
 *   address_id SMALLINT UNSIGNED NOT NULL,
 *   picture BLOB DEFAULT NULL,
 *   email VARCHAR(50) DEFAULT NULL,
 *   store_id TINYINT UNSIGNED NOT NULL,
 *   active BOOLEAN NOT NULL DEFAULT TRUE,
 *   username VARCHAR(16) NOT NULL,
 *   password VARCHAR(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (staff_id),
 *   KEY idx_fk_store_id (store_id),
 *   KEY idx_fk_address_id (address_id),
 *   CONSTRAINT fk_staff_store FOREIGN KEY (store_id) REFERENCES store (store_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_staff_address FOREIGN KEY (address_id) REFERENCES address (address_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * See {@link Store} for how a staff member and a store refer to each other.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-staff.html">The staff Table</a>
 */
@NamedQuery(name = "Staff.selectOneByStaffId",
            query = """
                    SELECT e
                    FROM Staff e
                    WHERE e.staffId = :staffId"""
)
@NamedQuery(name = "Staff.selectListOrderByStaffIdAsc",
            query = """
                    SELECT e
                    FROM Staff e
                    ORDER BY e.staffId ASC"""
)
@NamedQuery(name = "Staff.selectListByStoreOrderByStaffIdAsc",
            query = """
                    SELECT e
                    FROM Staff e
                    WHERE e.store = :store
                    ORDER BY e.staffId ASC"""
)
@NamedQuery(name = "Staff.selectListByUsername",
            query = """
                    SELECT e
                    FROM Staff e
                    WHERE e.username = :username"""
)
@NamedQuery(name = "Staff.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Staff e"""
)
@Entity
@Table(name = Staff.TABLE_NAME)
public class Staff {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "staff";

    // -------------------------------------------------------------------------------------------------------- staff_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STAFF_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_STAFF_ID = "staff_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STAFF_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STAFF_ID = "staffId";

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

    // --------------------------------------------------------------------------------------------------------- picture

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PICTURE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_PICTURE = "picture";

    /**
     * The length of the {@value #COLUMN_NAME_PICTURE} column; the largest {@code BLOB}. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_PICTURE = 65535;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PICTURE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PICTURE = "picture";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_PICTURE} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_PICTURE = COLUMN_LENGTH_PICTURE;

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

    // -------------------------------------------------------------------------------------------------------- store_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STORE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_STORE_ID = "store_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE = "store";

    // ---------------------------------------------------------------------------------------------------------- active

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ACTIVE} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTIVE = "active";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTIVE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTIVE = "active";

    // -------------------------------------------------------------------------------------------------------- username

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_USERNAME} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_USERNAME = "username";

    /**
     * The length of the {@value #COLUMN_NAME_USERNAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_USERNAME = 16;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_USERNAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_USERNAME = "username";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_USERNAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_USERNAME = COLUMN_LENGTH_USERNAME;

    // -------------------------------------------------------------------------------------------------------- password

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PASSWORD} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_PASSWORD = "password";

    /**
     * The length of the {@value #COLUMN_NAME_PASSWORD} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_PASSWORD = 40;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PASSWORD} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PASSWORD = "password";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_PASSWORD} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_PASSWORD = COLUMN_LENGTH_PASSWORD;

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

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Staff() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "staffId=" + staffId +
               ",firstName=" + firstName +
               ",lastName=" + lastName +
//               ",address=" + address +
//               ",picture=" + picture +
               ",email=" + email +
//               ",store=" + store +
               ",active=" + active +
               ",username=" + username +
//               ",password=" + password +
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
        if (!(obj instanceof Staff that)) {
            return false;
        }
        final var thisId = getStaffId();
        return thisId != null && thisId.equals(that.getStaffId());
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
        return Staff.class.hashCode();
    }

    // --------------------------------------------------------------------------------------------------------- staffId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STAFF_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STAFF_ID} attribute; {@code null} until the instance is
     *         persisted.
     */
    public Integer getStaffId() {
        return staffId;
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

    // --------------------------------------------------------------------------------------------------------- picture

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PICTURE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PICTURE} attribute.
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_PICTURE} attribute with the specified value.
     *
     * @param picture new value for {@value #ATTRIBUTE_NAME_PICTURE} attribute.
     */
    public void setPicture(final byte[] picture) {
        this.picture = picture;
    }

    // ----------------------------------------------------------------------------------------------------------- email

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_EMAIL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_EMAIL} attribute.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_EMAIL} attribute with the specified value.
     *
     * @param email new value for {@value #ATTRIBUTE_NAME_EMAIL} attribute.
     */
    public void setEmail(final String email) {
        this.email = email;
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

    // -------------------------------------------------------------------------------------------------------- username

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_USERNAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_USERNAME} attribute.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_USERNAME} attribute with the specified value.
     *
     * @param username new value for {@value #ATTRIBUTE_NAME_USERNAME} attribute.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    // -------------------------------------------------------------------------------------------------------- password

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PASSWORD} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PASSWORD} attribute; the installed rows hold a {@code SHA-1}
     *         hash in hexadecimal.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_PASSWORD} attribute with the specified value.
     *
     * @param password new value for {@value #ATTRIBUTE_NAME_PASSWORD} attribute.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    // ------------------------------------------------------------------------------------------------------ lastUpdate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute; {@code null} until the instance is read
     *         from the database.
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_STAFF_ID, nullable = false, insertable = true, updatable = false)
    private Integer staffId;

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
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_ADDRESS_ID, referencedColumnName = Address.COLUMN_NAME_ADDRESS_ID, nullable = false,
                insertable = true, updatable = true)
    private Address address;

    // -----------------------------------------------------------------------------------------------------------------
    @Size(max = SIZE_MAX_PICTURE)
    @Lob
    @Basic(optional = true, fetch = FetchType.LAZY)
    @Column(name = COLUMN_NAME_PICTURE, nullable = true, insertable = true, updatable = true,
            length = COLUMN_LENGTH_PICTURE)
    private byte[] picture;

    // -----------------------------------------------------------------------------------------------------------------
    @Size(max = SIZE_MAX_EMAIL)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_EMAIL, nullable = true, insertable = true, updatable = true,
            length = COLUMN_LENGTH_EMAIL)
    private String email;

    // -----------------------------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STORE_ID, referencedColumnName = Store.COLUMN_NAME_STORE_ID, nullable = false,
                insertable = true, updatable = true)
    private Store store;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ACTIVE, nullable = false, insertable = true, updatable = true)
    private Boolean active;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_USERNAME)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_USERNAME, nullable = false, insertable = true, updatable = true,
            length = COLUMN_LENGTH_USERNAME)
    private String username;

    // -----------------------------------------------------------------------------------------------------------------
    @Size(max = SIZE_MAX_PASSWORD)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_PASSWORD, nullable = true, insertable = true, updatable = true,
            length = COLUMN_LENGTH_PASSWORD)
    private String password;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;
}
