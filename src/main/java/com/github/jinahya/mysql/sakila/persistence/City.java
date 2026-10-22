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

import java.time.LocalDateTime;
import java.util.List;

/**
 * An entity class for mapping the {@value City#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE city (
 *   city_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   city VARCHAR(50) NOT NULL,
 *   country_id SMALLINT UNSIGNED NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (city_id),
 *   KEY idx_fk_country_id (country_id),
 *   CONSTRAINT `fk_city_country` FOREIGN KEY (country_id) REFERENCES country (country_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-city.html">The city
 *         Table</a>
 */
@NamedQuery(name = "City.selectOneByCityId",
            query = """
                    SELECT e
                    FROM City e
                    WHERE e.cityId = :cityId"""
)
@NamedQuery(name = "City.selectListOrderByCityIdAsc",
            query = """
                    SELECT e
                    FROM City e
                    ORDER BY e.cityId ASC"""
)
@NamedQuery(name = "City.selectListOrderByCityIdAscCityIdGt",
            query = """
                    SELECT e
                    FROM City e
                    WHERE e.cityId > :cityIdMinExclusive
                    ORDER BY e.cityId ASC"""
)
@NamedQuery(name = "City.selectListByCountryOrderByCityIdAsc",
            query = """
                    SELECT e
                    FROM City e
                    WHERE e.country = :country
                    ORDER BY e.cityId ASC"""
)
@NamedQuery(name = "City.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM City e"""
)
@NamedQuery(name = "City.countByCountry",
            query = """
                    SELECT COUNT(e)
                    FROM City e
                    WHERE e.country = :country"""
)
@Entity
@Table(name = City.TABLE_NAME)
public class City {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "city";

    // ------------------------------------------------------------------------------------- city_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CITY_ID} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_CITY_ID = "city_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CITY_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_CITY_ID = "cityId";

    // ---------------------------------------------------------------------------------------- city

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CITY} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_CITY = "city";

    /**
     * The length of the {@value #COLUMN_NAME_CITY} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_CITY = 50;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CITY} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_CITY = "city";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_CITY} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_CITY = COLUMN_LENGTH_CITY;

    // ---------------------------------------------------------------------------------- country_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_COUNTRY} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_COUNTRY_ID = "country_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_COUNTRY_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_COUNTRY = "country";

    // --------------------------------------------------------------------------------- last_update

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_LAST_UPDATE = "last_update";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_LAST_UPDATE} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_LAST_UPDATE = "lastUpdate";

    // ----------------------------------------------------------------------------------- addresses

    /**
     * The name of the attribute which maps the {@link Address addresses} of this city. The value is
     * {@value}.
     *
     * @see Address#ATTRIBUTE_NAME_CITY
     */
    public static final String ATTRIBUTE_NAME_ADDRESSES = "addresses";

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected City() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "cityId=" + cityId +
               ",city=" + city +
//               ",country=" + country +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone, and only once it is assigned; an
     *         instance without one equals nothing but itself.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof City that)) {
            return false;
        }
        final var thisId = getCityId();
        return thisId != null && thisId.equals(that.getCityId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is that of the class, the same for every instance, so that it does
     *         not change as the {@code @Id} is assigned; it is consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return City.class.hashCode();
    }

    // -------------------------------------------------------------------------------------- cityId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CITY_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CITY_ID} attribute; {@code null} until the
     *         instance is persisted.
     */
    public Integer getCityId() {
        return cityId;
    }

    // ---------------------------------------------------------------------------------------- city

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    public String getCity() {
        return city;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_CITY} attribute with the specified value.
     *
     * @param city new value for {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    public void setCity(final String city) {
        this.city = city;
    }

    // ------------------------------------------------------------------------------------- country

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute with the specified
     * value.
     *
     * @param country new value for {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    public void setCountry(final Country country) {
        this.country = country;
    }

    // ---------------------------------------------------------------------------------- lastUpdate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_LAST_UPDATE} attribute; {@code null} until
     *         the instance is read from the database.
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    // ----------------------------------------------------------------------------------- addresses
    List<Address> getAddresses() {
        return addresses;
    }

    void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_CITY_ID, nullable = false, insertable = true, updatable = false)
    private Integer cityId;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_CITY)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_CITY, nullable = false, insertable = true, updatable = true,
            length = COLUMN_LENGTH_CITY)
    private String city;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_COUNTRY_ID,
                referencedColumnName = Country.COLUMN_NAME_COUNTRY_ID, nullable = false,
                insertable = true, updatable = true)
    private Country country;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // ---------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = Address.ATTRIBUTE_NAME_CITY, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Address> addresses;
}
