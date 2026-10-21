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
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * An entity class for mapping the {@value Address#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE address (
 *   address_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   address VARCHAR(50) NOT NULL,
 *   address2 VARCHAR(50) DEFAULT NULL,
 *   district VARCHAR(20) NOT NULL,
 *   city_id SMALLINT UNSIGNED NOT NULL,
 *   postal_code VARCHAR(10) DEFAULT NULL,
 *   phone VARCHAR(20) NOT NULL,
 *   location GEOMETRY SRID 0 NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (address_id),
 *   KEY idx_fk_city_id (city_id),
 *   SPATIAL KEY `idx_location` (location),
 *   CONSTRAINT `fk_address_city` FOREIGN KEY (city_id) REFERENCES city (city_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * The {@value #COLUMN_NAME_LOCATION} column, a {@code GEOMETRY}, has no type of its own in Jakarta Persistence; it is
 * mapped as the bytes MySQL stores it in -- a four-byte, little-endian {@code SRID} followed by the geometry's
 * well-known binary ({@code WKB}) -- which is also what MySQL accepts back. {@link #newLocation(int, double, double)}
 * builds the bytes of a point.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/refman/9.7/en/gis-data-formats.html#gis-internal-format">Internal Geometry
 * Storage Format</a>
 */
@NamedQuery(name = "Address.selectOneByAddressId",
            query = """
                    SELECT e
                    FROM Address e
                    WHERE e.addressId = :addressId"""
)
@NamedQuery(name = "Address.selectListOrderByAddressIdAsc",
            query = """
                    SELECT e
                    FROM Address e
                    ORDER BY e.addressId ASC"""
)
@NamedQuery(name = "Address.selectListOrderByAddressIdAscAddressIdGt",
            query = """
                    SELECT e
                    FROM Address e
                    WHERE e.addressId > :addressIdMinExclusive
                    ORDER BY e.addressId ASC"""
)
@NamedQuery(name = "Address.selectListByCityOrderByAddressIdAsc",
            query = """
                    SELECT e
                    FROM Address e
                    WHERE e.city = :city
                    ORDER BY e.addressId ASC"""
)
@NamedQuery(name = "Address.selectListByPostalCodeOrderByAddressIdAsc",
            query = """
                    SELECT e
                    FROM Address e
                    WHERE e.postalCode = :postalCode
                    ORDER BY e.addressId ASC"""
)
@NamedQuery(name = "Address.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Address e"""
)
@NamedQuery(name = "Address.countByCity",
            query = """
                    SELECT COUNT(e)
                    FROM Address e
                    WHERE e.city = :city"""
)
@Entity
@Table(name = Address.TABLE_NAME)
public class Address {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "address";

    // ------------------------------------------------------------------------------------------------------ address_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ADDRESS_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_ADDRESS_ID = "address_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ADDRESS_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ADDRESS_ID = "addressId";

    // --------------------------------------------------------------------------------------------------------- address

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ADDRESS} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_ADDRESS = "address";

    /**
     * The length of the {@value #COLUMN_NAME_ADDRESS} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_ADDRESS = 50;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ADDRESS} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ADDRESS = "address";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_ADDRESS} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_ADDRESS = COLUMN_LENGTH_ADDRESS;

    // -------------------------------------------------------------------------------------------------------- address2

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ADDRESS2} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_ADDRESS2 = "address2";

    /**
     * The length of the {@value #COLUMN_NAME_ADDRESS2} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_ADDRESS2 = 50;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ADDRESS2} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ADDRESS2 = "address2";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_ADDRESS2} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_ADDRESS2 = COLUMN_LENGTH_ADDRESS2;

    // -------------------------------------------------------------------------------------------------------- district

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_DISTRICT} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_DISTRICT = "district";

    /**
     * The length of the {@value #COLUMN_NAME_DISTRICT} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_DISTRICT = 20;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_DISTRICT} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_DISTRICT = "district";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_DISTRICT} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_DISTRICT = COLUMN_LENGTH_DISTRICT;

    // --------------------------------------------------------------------------------------------------------- city_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CITY} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_CITY_ID = "city_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CITY_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CITY = "city";

    // ----------------------------------------------------------------------------------------------------- postal_code

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_POSTAL_CODE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_POSTAL_CODE = "postal_code";

    /**
     * The length of the {@value #COLUMN_NAME_POSTAL_CODE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_POSTAL_CODE = 10;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_POSTAL_CODE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_POSTAL_CODE = "postalCode";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_POSTAL_CODE} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_POSTAL_CODE = COLUMN_LENGTH_POSTAL_CODE;

    // ----------------------------------------------------------------------------------------------------------- phone

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PHONE} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_PHONE = "phone";

    /**
     * The length of the {@value #COLUMN_NAME_PHONE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_PHONE = 20;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PHONE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PHONE = "phone";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_PHONE} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_PHONE = COLUMN_LENGTH_PHONE;

    // -------------------------------------------------------------------------------------------------------- location

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LOCATION} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_LOCATION = "location";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LOCATION} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LOCATION = "location";

    /**
     * The spatial reference system identifier of the {@value #COLUMN_NAME_LOCATION} column. The value is {@value}.
     */
    public static final int COLUMN_SRID_LOCATION = 0;

    /**
     * The number of bytes of a point in MySQL's internal geometry format; four of {@code SRID}, one of byte order, four
     * of geometry type, and eight for each of the two coordinates. The value is {@value}.
     */
    public static final int LOCATION_POINT_BYTES = Integer.BYTES + Byte.BYTES + Integer.BYTES + (Double.BYTES << 1);

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

    // -------------------------------------------------------------------------------------------------- STATIC_METHODS

    /**
     * Returns the bytes, in MySQL's internal geometry format, of a point of the specified spatial reference system at
     * the specified coordinates.
     *
     * @param srid the spatial reference system identifier; {@value #COLUMN_SRID_LOCATION} for the
     *             {@value #COLUMN_NAME_LOCATION} column.
     * @param x    the first coordinate of the point.
     * @param y    the second coordinate of the point.
     * @return a new array of {@value #LOCATION_POINT_BYTES} bytes.
     */
    public static byte[] newLocation(final int srid, final double x, final double y) {
        return ByteBuffer.allocate(LOCATION_POINT_BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(srid)
                .put((byte) 1)  // WKB byte order; 1 is little-endian (NDR)
                .putInt(1)      // WKB geometry type; 1 is a point
                .putDouble(x)
                .putDouble(y)
                .array();
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Address() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "addressId=" + addressId +
               ",address=" + address +
               ",address2=" + address2 +
               ",district=" + district +
//               ",city=" + city +
               ",postalCode=" + postalCode +
               ",phone=" + phone +
               ",location=" + Arrays.toString(location) +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone, and only once it is assigned; an instance without one
     * equals nothing but itself.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Address that)) {
            return false;
        }
        final var thisId = getAddressId();
        return thisId != null && thisId.equals(that.getAddressId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is that of the class, the same for every instance, so that it does not change as the
     * {@code @Id} is assigned; it is consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Address.class.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------- addressId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ADDRESS_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ADDRESS_ID} attribute; {@code null} until the instance is
     * persisted.
     */
    public Integer getAddressId() {
        return addressId;
    }

    // --------------------------------------------------------------------------------------------------------- address

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     */
    @Nonnull
    public String getAddress() {
        return address;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute with the specified value.
     *
     * @param address new value for {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     */
    public void setAddress(@Nonnull final String address) {
        this.address = address;
    }

    // -------------------------------------------------------------------------------------------------------- address2

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ADDRESS2} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ADDRESS2} attribute.
     */
    @Nullable
    public String getAddress2() {
        return address2;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ADDRESS2} attribute with the specified value.
     *
     * @param address2 new value for {@value #ATTRIBUTE_NAME_ADDRESS2} attribute.
     */
    public void setAddress2(@Nullable final String address2) {
        this.address2 = address2;
    }

    // -------------------------------------------------------------------------------------------------------- district

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DISTRICT} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DISTRICT} attribute.
     */
    @Nonnull
    public String getDistrict() {
        return district;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DISTRICT} attribute with the specified value.
     *
     * @param district new value for {@value #ATTRIBUTE_NAME_DISTRICT} attribute.
     */
    public void setDistrict(@Nonnull final String district) {
        this.district = district;
    }

    // ------------------------------------------------------------------------------------------------------------ city

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    @Nonnull
    public City getCity() {
        return city;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_CITY} attribute with the specified value.
     *
     * @param city new value for {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    public void setCity(@Nonnull final City city) {
        this.city = city;
    }

    // ------------------------------------------------------------------------------------------------------ postalCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_POSTAL_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_POSTAL_CODE} attribute.
     */
    @Nullable
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_POSTAL_CODE} attribute with the specified value.
     *
     * @param postalCode new value for {@value #ATTRIBUTE_NAME_POSTAL_CODE} attribute.
     */
    public void setPostalCode(@Nullable final String postalCode) {
        this.postalCode = postalCode;
    }

    // ----------------------------------------------------------------------------------------------------------- phone

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PHONE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PHONE} attribute.
     */
    @Nonnull
    public String getPhone() {
        return phone;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_PHONE} attribute with the specified value.
     *
     * @param phone new value for {@value #ATTRIBUTE_NAME_PHONE} attribute.
     */
    public void setPhone(@Nonnull final String phone) {
        this.phone = phone;
    }

    // -------------------------------------------------------------------------------------------------------- location

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LOCATION} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LOCATION} attribute; the bytes of the geometry in MySQL's
     * internal format.
     */
    @Nonnull
    public byte[] getLocation() {
        return location;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_LOCATION} attribute with the specified value.
     *
     * @param location new value for {@value #ATTRIBUTE_NAME_LOCATION} attribute; the bytes of a geometry in MySQL's
     *                 internal format.
     * @see #newLocation(int, double, double)
     */
    public void setLocation(@Nonnull final byte[] location) {
        this.location = location;
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

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ADDRESS_ID, nullable = false, insertable = true, updatable = false)
    private Integer addressId;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_ADDRESS)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ADDRESS, nullable = false, length = COLUMN_LENGTH_ADDRESS)
    private String address;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = SIZE_MAX_ADDRESS2)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ADDRESS2, nullable = true, length = COLUMN_LENGTH_ADDRESS2)
    private String address2;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_DISTRICT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_DISTRICT, nullable = false, length = COLUMN_LENGTH_DISTRICT)
    private String district;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_CITY_ID, referencedColumnName = City.COLUMN_NAME_CITY_ID, nullable = false)
    private City city;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = SIZE_MAX_POSTAL_CODE)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_POSTAL_CODE, nullable = true, length = COLUMN_LENGTH_POSTAL_CODE)
    private String postalCode;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_PHONE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_PHONE, nullable = false, length = COLUMN_LENGTH_PHONE)
    private String phone;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LOCATION, nullable = false)
    private byte[] location;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;
}
