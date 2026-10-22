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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * An entity class for mapping the {@value SalesByFilmCategory#TABLE_NAME} view.
 * <p>
 * {@snippet lang = "sql":
 * CREATE VIEW sales_by_film_category
 * AS
 * SELECT
 * c.name AS category
 * , SUM(p.amount) AS total_sales
 * FROM payment AS p
 * INNER JOIN rental AS r ON p.rental_id = r.rental_id
 * INNER JOIN inventory AS i ON r.inventory_id = i.inventory_id
 * INNER JOIN film AS f ON i.film_id = f.film_id
 * INNER JOIN film_category AS fc ON f.film_id = fc.film_id
 * INNER JOIN category AS c ON fc.category_id = c.category_id
 * GROUP BY c.name
 * ORDER BY total_sales DESC
 *}
 * <p>
 * The view groups by {@code c.name}, so the {@value #COLUMN_NAME_CATEGORY} column identifies a row.
 * Total sales add up to more than the sum of the payments, because a film may belong to more than
 * one category.
 * <p>
 * Every column is mapped read-only; only the identifier stays insertable, which EclipseLink
 * requires of an {@link Id @Id}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql
 *         .com/doc/sakila/en/sakila-structure-views-sales_by_film_category.html"> The
 *         sales_by_film_category View</a>
 */
@NamedQuery(name = "SalesByFilmCategory.selectOneByCategory",
            query = """
                    SELECT e
                    FROM SalesByFilmCategory e
                    WHERE e.category = :category"""
)
@NamedQuery(name = "SalesByFilmCategory.selectListOrderByCategoryAsc",
            query = """
                    SELECT e
                    FROM SalesByFilmCategory e
                    ORDER BY e.category ASC"""
)
@NamedQuery(name = "SalesByFilmCategory.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM SalesByFilmCategory e"""
)
@Entity
@Table(name = SalesByFilmCategory.TABLE_NAME)
public class SalesByFilmCategory {

    /**
     * The name of the database view to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "sales_by_film_category";

    // ------------------------------------------------------------------------------------ category

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_CATEGORY} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_CATEGORY = "category";

    /**
     * The length of the {@value #COLUMN_NAME_CATEGORY} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_CATEGORY = 25;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CATEGORY} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_CATEGORY = "category";

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_CATEGORY} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_CATEGORY = COLUMN_LENGTH_CATEGORY;

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
    protected SalesByFilmCategory() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "category=" + category +
               ",totalSales=" + totalSales +
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
        if (!(obj instanceof SalesByFilmCategory that)) {
            return false;
        }
        final var thisId = getCategory();
        return thisId != null && thisId.equals(that.getCategory());
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
        return SalesByFilmCategory.class.hashCode();
    }

    // ------------------------------------------------------------------------------------ category

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CATEGORY} attribute.
     */
    public String getCategory() {
        return category;
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
    @NotNull
    @Id
    @Size(max = SIZE_MAX_CATEGORY)
    // insertable=true is the JPA default; it is spelled out because EclipseLink rejects
    // insertable=false on an
    // @Id -- "There should be one non-read-only mapping defined for the primary key field".
    // Nothing writes to a
    // view anyway.
    @Column(name = COLUMN_NAME_CATEGORY, nullable = false, insertable = true, updatable = false,
            length = COLUMN_LENGTH_CATEGORY)
    private String category;

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
