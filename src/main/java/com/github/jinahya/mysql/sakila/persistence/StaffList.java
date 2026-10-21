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
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/**
 * An entity class for mapping the {@value StaffList#TABLE_NAME} view.
 * <p>
 * {@snippet lang = "sql":
 * CREATE VIEW staff_list
 * AS
 * SELECT s.staff_id AS ID, CONCAT(s.first_name, _utf8mb4' ', s.last_name) AS name, a.address AS address,
 *     a.postal_code AS `zip code`, a.phone AS phone, city.city AS city, country.country AS country,
 *     s.store_id AS SID
 * FROM staff AS s JOIN address AS a ON s.address_id = a.address_id JOIN city ON a.city_id = city.city_id
 *     JOIN country ON city.country_id = country.country_id
 *}
 * <p>
 * The view has a row per {@link Staff}, so the {@value #COLUMN_NAME_ID} column identifies a row. The
 * {@value #COLUMN_NAME_ZIP_CODE} column has a space in its name, so it is mapped as a delimited identifier.
 * <p>
 * Every column is mapped read-only; only the identifier stays insertable, which EclipseLink requires of an
 * {@link Id @Id}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-views-staff-list.html">The staff_list View</a>
 */
@NamedQuery(name = "StaffList.selectOneById",
            query = """
                    SELECT e
                    FROM StaffList e
                    WHERE e.id = :id"""
)
@NamedQuery(name = "StaffList.selectListOrderByIdAsc",
            query = """
                    SELECT e
                    FROM StaffList e
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "StaffList.selectListOrderByIdAscIdGt",
            query = """
                    SELECT e
                    FROM StaffList e
                    WHERE e.id > :idMinExclusive
                    ORDER BY e.id ASC"""
)
@NamedQuery(name = "StaffList.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM StaffList e"""
)
@Entity
@Table(name = StaffList.TABLE_NAME)
public class StaffList {

    /**
     * The name of the database view to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "staff_list";

    // -------------------------------------------------------------------------------------------------------------- ID

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ID} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ID = "ID";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // ------------------------------------------------------------------------------------------------------------ name

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_NAME} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_NAME = "name";

    /**
     * The length of the {@value #COLUMN_NAME_NAME} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_NAME = 91;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_NAME} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_NAME = "name";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_NAME} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_NAME = COLUMN_LENGTH_NAME;

    // --------------------------------------------------------------------------------------------------------- address

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ADDRESS} attribute maps. The value is {@value}.
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

    // -------------------------------------------------------------------------------------------------------- zip_code

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ZIP_CODE} attribute maps. The value is
     * {@value}.
     * <p>
     * The name has a space in it, so the mappings use it delimited, as {@value #COLUMN_NAME_ZIP_CODE_DELIMITED}.
     */
    public static final String COLUMN_NAME_ZIP_CODE = "zip code";

    /**
     * The delimited form of the {@value #COLUMN_NAME_ZIP_CODE} column name, which the mapping uses. The value is
     * {@value}.
     */
    static final String COLUMN_NAME_ZIP_CODE_DELIMITED = "\"zip code\"";

    /**
     * The length of the {@value #COLUMN_NAME_ZIP_CODE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_ZIP_CODE = 10;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ZIP_CODE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ZIP_CODE = "zipCode";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_ZIP_CODE} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_ZIP_CODE = COLUMN_LENGTH_ZIP_CODE;

    // ----------------------------------------------------------------------------------------------------------- phone

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_PHONE} attribute maps. The value is {@value}.
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

    // ------------------------------------------------------------------------------------------------------------ city

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_CITY} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_CITY = "city";

    /**
     * The length of the {@value #COLUMN_NAME_CITY} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_CITY = 50;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CITY} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CITY = "city";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_CITY} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_CITY = COLUMN_LENGTH_CITY;

    // --------------------------------------------------------------------------------------------------------- country

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_COUNTRY} attribute maps. The value is {@value}.
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

    // ------------------------------------------------------------------------------------------------------------- SID

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_SID} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SID = "SID";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SID} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SID = "sid";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected StaffList() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
               ",name=" + name +
               ",address=" + address +
               ",zipCode=" + zipCode +
               ",phone=" + phone +
               ",city=" + city +
               ",country=" + country +
               ",sid=" + sid +
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
        if (!(obj instanceof StaffList that)) {
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
     *         {@code @Id} is assigned; it is consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return StaffList.class.hashCode();
    }

    // -------------------------------------------------------------------------------------------------------------- id

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ID} attribute.
     */
    public Integer getId() {
        return id;
    }

    // ------------------------------------------------------------------------------------------------------------ name

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_NAME} attribute.
     */
    @Nullable
    public String getName() {
        return name;
    }

    // --------------------------------------------------------------------------------------------------------- address

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ADDRESS} attribute.
     */
    public String getAddress() {
        return address;
    }

    // --------------------------------------------------------------------------------------------------------- zipCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ZIP_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ZIP_CODE} attribute.
     */
    @Nullable
    public String getZipCode() {
        return zipCode;
    }

    // ----------------------------------------------------------------------------------------------------------- phone

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PHONE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PHONE} attribute.
     */
    public String getPhone() {
        return phone;
    }

    // ------------------------------------------------------------------------------------------------------------ city

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    public String getCity() {
        return city;
    }

    // --------------------------------------------------------------------------------------------------------- country

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    public String getCountry() {
        return country;
    }

    // ------------------------------------------------------------------------------------------------------------- sid

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SID} attribute.
     */
    public Integer getSid() {
        return sid;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Id
    // insertable=true is the JPA default; it is spelled out because EclipseLink rejects insertable=false on an
    // @Id -- "There should be one non-read-only mapping defined for the primary key field". Nothing writes to a
    // view anyway.
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = true, updatable = false)
    private Integer id;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = SIZE_MAX_NAME)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_NAME, nullable = true, insertable = false, updatable = false,
            length = COLUMN_LENGTH_NAME)
    private String name;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_ADDRESS)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ADDRESS, nullable = false, insertable = false, updatable = false,
            length = COLUMN_LENGTH_ADDRESS)
    private String address;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(max = SIZE_MAX_ZIP_CODE)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ZIP_CODE_DELIMITED, nullable = true, insertable = false, updatable = false,
            length = COLUMN_LENGTH_ZIP_CODE)
    private String zipCode;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_PHONE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_PHONE, nullable = false, insertable = false, updatable = false,
            length = COLUMN_LENGTH_PHONE)
    private String phone;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_CITY)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_CITY, nullable = false, insertable = false, updatable = false,
            length = COLUMN_LENGTH_CITY)
    private String city;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Size(max = SIZE_MAX_COUNTRY)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_COUNTRY, nullable = false, insertable = false, updatable = false,
            length = COLUMN_LENGTH_COUNTRY)
    private String country;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SID, nullable = false, insertable = false, updatable = false)
    private Integer sid;
}
