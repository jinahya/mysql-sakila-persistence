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
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * An embeddable for mapping the {@value Address#COLUMN_NAME_ADDRESS} column, the {@value Address#COLUMN_NAME_ADDRESS2}
 * column, the {@value Address#COLUMN_NAME_DISTRICT} column, the {@value Address#COLUMN_NAME_CITY_ID} column, and the
 * {@value Address#COLUMN_NAME_POSTAL_CODE} column, of the {@value Address#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * address VARCHAR(50) NOT NULL,
 * address2 VARCHAR(50) DEFAULT NULL,
 * district VARCHAR(20) NOT NULL,
 * city_id SMALLINT UNSIGNED NOT NULL,
 * postal_code VARCHAR(10) DEFAULT NULL,
 *}
 * <p>
 * The {@value #ATTRIBUTE_NAME_CITY} attribute is an association; an embeddable may hold one, and its owner is the
 * embedding entity. The {@value Address#COLUMN_NAME_PHONE} column and the {@value Address#COLUMN_NAME_LOCATION} column
 * are not parts of a postal address, and stay out.
 * <p>
 * This is a class, not a record, for a part is corrected on its own; fixing the {@value #ATTRIBUTE_NAME_POSTAL_CODE}
 * leaves the street lines as they are.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class _PostalAddress {

    /**
     * The name of the attribute which maps the {@value Address#COLUMN_NAME_ADDRESS} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ADDRESS = "address";

    /**
     * The name of the attribute which maps the {@value Address#COLUMN_NAME_ADDRESS2} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ADDRESS2 = "address2";

    /**
     * The name of the attribute which maps the {@value Address#COLUMN_NAME_DISTRICT} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_DISTRICT = "district";

    /**
     * The name of the attribute which maps the {@value Address#COLUMN_NAME_CITY_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CITY = "city";

    /**
     * The name of the attribute which maps the {@value Address#COLUMN_NAME_POSTAL_CODE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_POSTAL_CODE = "postalCode";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected _PostalAddress() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "address=" + address +
               ",address2=" + address2 +
               ",district=" + district +
               ",postalCode=" + postalCode +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by all the basic attributes and by the {@value #ATTRIBUTE_NAME_CITY}, which
     *         compares by its own {@link City#equals(Object) equals}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof _PostalAddress that)) {
            return false;
        }
        return Objects.equals(address, that.address)
               && Objects.equals(address2, that.address2)
               && Objects.equals(district, that.district)
               && Objects.equals(city, that.city)
               && Objects.equals(postalCode, that.postalCode);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the same attributes as {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(address, address2, district, city, postalCode);
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

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = Address.SIZE_MAX_ADDRESS)
    @Basic(optional = false)
    @Column(name = Address.COLUMN_NAME_ADDRESS, nullable = false, insertable = true, updatable = true,
            length = Address.COLUMN_LENGTH_ADDRESS)
    private String address;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = Address.SIZE_MAX_ADDRESS2)
    @Basic(optional = true)
    @Column(name = Address.COLUMN_NAME_ADDRESS2, nullable = true, insertable = true, updatable = true,
            length = Address.COLUMN_LENGTH_ADDRESS2)
    private String address2;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = Address.SIZE_MAX_DISTRICT)
    @Basic(optional = false)
    @Column(name = Address.COLUMN_NAME_DISTRICT, nullable = false, insertable = true, updatable = true,
            length = Address.COLUMN_LENGTH_DISTRICT)
    private String district;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Address.COLUMN_NAME_CITY_ID, referencedColumnName = City.COLUMN_NAME_CITY_ID, nullable = false,
                insertable = true, updatable = true)
    private City city;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = Address.SIZE_MAX_POSTAL_CODE)
    @Basic(optional = true)
    @Column(name = Address.COLUMN_NAME_POSTAL_CODE, nullable = true, insertable = true, updatable = true,
            length = Address.COLUMN_LENGTH_POSTAL_CODE)
    private String postalCode;
}
