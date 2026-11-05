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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * A class for the {@value SalesByStore#TABLE_NAME} view.
 * <p>
 * {@snippet lang = "sql":
 * CREATE VIEW sales_by_store
 * AS
 * SELECT
 * CONCAT(c.city, _utf8mb4',', cy.country) AS store
 * , CONCAT(m.first_name, _utf8mb4' ', m.last_name) AS manager
 * , SUM(p.amount) AS total_sales
 * FROM payment AS p
 * INNER JOIN rental AS r ON p.rental_id = r.rental_id
 * INNER JOIN inventory AS i ON r.inventory_id = i.inventory_id
 * INNER JOIN store AS s ON i.store_id = s.store_id
 * INNER JOIN address AS a ON s.address_id = a.address_id
 * INNER JOIN city AS c ON a.city_id = c.city_id
 * INNER JOIN country AS cy ON c.country_id = cy.country_id
 * INNER JOIN staff AS m ON s.manager_staff_id = m.staff_id
 * GROUP BY s.store_id
 * ORDER BY cy.country, c.city
 *}
 * <p>
 * No column identifies a row of this view. It groups by {@code s.store_id}, which it does not
 * select; the {@value #COLUMN_NAME_STORE} column is only the city and the country of the store,
 * which two stores may share, and the {@value #COLUMN_NAME_MANAGER} is only the name of one. So
 * this is not an {@link jakarta.persistence.Entity @Entity}: the columns it projects are written
 * out here, and every one of them is read-only.
 * <p>
 * The public constructor takes every attribute in the order the view selects its columns, which is
 * the order a name-based row mapper, or a
 * {@link jakarta.persistence.ConstructorResult @ConstructorResult}, hands them over.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-views-sales_by_store.html">
 *         The sales_by_store View</a>
 */
public class SalesByStore {

    /**
     * The name of the database view to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "sales_by_store";

    // --------------------------------------------------------------------------------------- store

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_STORE} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_STORE = "store";

    /**
     * The length of the {@value #COLUMN_NAME_STORE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_STORE = 101;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STORE} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STORE = "store";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_STORE} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_STORE = COLUMN_LENGTH_STORE;

    // ------------------------------------------------------------------------------------- manager

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_MANAGER} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_MANAGER = "manager";

    /**
     * The length of the {@value #COLUMN_NAME_MANAGER} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_MANAGER = 91;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_MANAGER} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_MANAGER = "manager";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_MANAGER} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_MANAGER = COLUMN_LENGTH_MANAGER;

    // --------------------------------------------------------------------------------- total_sales

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_TOTAL_SALES} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_TOTAL_SALES = "total_sales";

    /**
     * The precision of the {@value #COLUMN_NAME_TOTAL_SALES} column. The value is {@value}.
     */
    public static final int COLUMN_PRECISION_TOTAL_SALES = 27;

    /**
     * The scale of the {@value #COLUMN_NAME_TOTAL_SALES} column. The value is {@value}.
     */
    public static final int COLUMN_SCALE_TOTAL_SALES = 2;

    /**
     * The minimum value of the {@value #COLUMN_NAME_TOTAL_SALES} column. The value is {@value}.
     */
    public static final String COLUMN_MIN_TOTAL_SALES = "-9999999999999999999999999.99";

    /**
     * The maximum value of the {@value #COLUMN_NAME_TOTAL_SALES} column. The value is {@value}.
     */
    public static final String COLUMN_MAX_TOTAL_SALES = "9999999999999999999999999.99";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_TOTAL_SALES} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_TOTAL_SALES = "totalSales";

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected SalesByStore() {
        super();
    }

    /**
     * Creates a new instance with the specified attribute values.
     *
     * @param store      a value for the {@value #ATTRIBUTE_NAME_STORE} attribute.
     * @param manager    a value for the {@value #ATTRIBUTE_NAME_MANAGER} attribute.
     * @param totalSales a value for the {@value #ATTRIBUTE_NAME_TOTAL_SALES} attribute.
     */
    public SalesByStore(final String store,
                        final String manager,
                        final BigDecimal totalSales) {
        super();
        this.store = store;
        this.manager = manager;
        this.totalSales = totalSales;
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "store=" + store +
               ",manager=" + manager +
               ",totalSales=" + totalSales +
               '}';
    }

    // --------------------------------------------------------------------------------------- store

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STORE} attribute.
     */
    public String getStore() {
        return store;
    }

    // ------------------------------------------------------------------------------------- manager

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_MANAGER} attribute.
     */
    public String getManager() {
        return manager;
    }

    // ---------------------------------------------------------------------------------- totalSales

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TOTAL_SALES} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TOTAL_SALES} attribute.
     */
    public BigDecimal getTotalSales() {
        return totalSales;
    }

    // ---------------------------------------------------------------------------------------------
    @Size(max = SIZE_MAX_STORE)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_STORE, nullable = true, insertable = false, updatable = false,
            length = COLUMN_LENGTH_STORE)
    private String store;

    // ---------------------------------------------------------------------------------------------
    @Size(max = SIZE_MAX_MANAGER)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_MANAGER, nullable = true, insertable = false, updatable = false,
            length = COLUMN_LENGTH_MANAGER)
    private String manager;

    // ---------------------------------------------------------------------------------------------
    // @Digits counts digits differently by provider; https://github.com/jakartaee/validation-spec/issues/346
    // @Digits(integer = COLUMN_PRECISION_TOTAL_SALES - COLUMN_SCALE_TOTAL_SALES, fraction =
    // COLUMN_SCALE_TOTAL_SALES)
    @DecimalMin(COLUMN_MIN_TOTAL_SALES)
    @DecimalMax(COLUMN_MAX_TOTAL_SALES)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_TOTAL_SALES, nullable = true, insertable = false, updatable = false,
            precision = COLUMN_PRECISION_TOTAL_SALES, scale = COLUMN_SCALE_TOTAL_SALES)
    private BigDecimal totalSales;
}
