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
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * An entity class for mapping the {@value Country#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE country (
 *   country_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   country VARCHAR(50) NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (country_id)
 * )
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedQuery(name = "Country.selectOneByCountryId",
            query = """
                    SELECT e
                    FROM Country e
                    WHERE e.countryId = :countryId"""
)
@NamedQuery(name = "Country.selectListOrderByCountryIdAsc",
            query = """
                    SELECT e
                    FROM Country e
                    ORDER BY e.countryId ASC"""
)
@NamedQuery(name = "Country.selectListOrderByCountryIdAscCountryIdGt",
            query = """
                    SELECT e
                    FROM Country e
                    WHERE e.countryId > :countryIdMinExclusive
                    ORDER BY e.countryId ASC"""
)
@NamedQuery(name = "Country.selectListByCountryOrderByCountryIdAsc",
            query = """
                    SELECT e
                    FROM Country e
                    WHERE e.country = :country
                    ORDER BY e.countryId ASC"""
)
@NamedQuery(name = "Country.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Country e"""
)
@Entity
@Table(name = Country.TABLE_NAME)
public class Country {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "country";

    // ------------------------------------------------------------------------------------------------------ country_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_COUNTRY_ID} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_COUNTRY_ID = "country_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_COUNTRY_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_COUNTRY_ID = "countryId";

    // --------------------------------------------------------------------------------------------------------- country

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_COUNTRY} attribute maps. The value is
     * {@value}.
     */
    public static final String COLUMN_NAME_COUNTRY = "country";

    /**
     * The length of the {@value #COLUMN_NAME_COUNTRY} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_COUNTRY = 50;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_COUNTRY} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_COUNTRY = "country";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_COUNTRY} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_COUNTRY = COLUMN_LENGTH_COUNTRY;

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

    // ---------------------------------------------------------------------------------------------------------- cities

    /**
     * The name of the attribute which maps the {@link City cities} of this country. The value is {@value}.
     *
     * @see City#ATTRIBUTE_NAME_COUNTRY
     */
    public static final String ATTRIBUTE_NAME_CITIES = "cities";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Country() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "countryId=" + countryId +
               ",country=" + country +
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
        if (!(obj instanceof Country that)) {
            return false;
        }
        final var thisId = getCountryId();
        return thisId != null && thisId.equals(that.getCountryId());
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
        return Country.class.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------- countryId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COUNTRY_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COUNTRY_ID} attribute; {@code null} until the instance is
     *         persisted.
     */
    public Integer getCountryId() {
        return countryId;
    }

    // --------------------------------------------------------------------------------------------------------- country

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    @Nonnull
    public String getCountry() {
        return country;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute with the specified value.
     *
     * @param country new value for {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    public void setCountry(@Nonnull final String country) {
        this.country = country;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute with the display name of the country of the
     * specified locale, in {@link Locale#ENGLISH English}.
     *
     * @param locale the locale whose display country, in {@link Locale#ENGLISH English}, is set; {@code null} sets
     *               {@code null}.
     * @apiNote A locale without a country, {@link Locale#ENGLISH}, for one, sets an empty string.
     * @see Locale#getDisplayCountry(Locale)
     * @see #setCountry(String)
     */
    @Transient
    public void setCountryFrom(final Locale locale) {
        setCountry(Optional.ofNullable(locale).map(v -> v.getDisplayCountry(Locale.ENGLISH)).orElse(null));
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

    // ---------------------------------------------------------------------------------------------------------- cities
    List<City> getCities() {
        return cities;
    }

    void setCities(final List<City> cities) {
        this.cities = cities;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // insertable=true is the JPA default; it is spelled out because EclipseLink rejects insertable=false on a
    // @GeneratedValue @Id -- it treats the mapping as read-only and fails descriptor initialisation.
    @Column(name = COLUMN_NAME_COUNTRY_ID, nullable = false, insertable = true, updatable = false)
    private Integer countryId;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Size(max = SIZE_MAX_COUNTRY)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_COUNTRY, nullable = false, length = COLUMN_LENGTH_COUNTRY)
    private String country;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = __Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // -----------------------------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = City.ATTRIBUTE_NAME_COUNTRY, fetch = FetchType.LAZY)
    private List<@Valid @NotNull City> cities;
}
