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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An entity class for mapping the {@value Rental#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE rental (
 *   rental_id INT NOT NULL AUTO_INCREMENT,
 *   rental_date DATETIME NOT NULL,
 *   inventory_id MEDIUMINT UNSIGNED NOT NULL,
 *   customer_id SMALLINT UNSIGNED NOT NULL,
 *   return_date DATETIME DEFAULT NULL,
 *   staff_id TINYINT UNSIGNED NOT NULL,
 *   last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY (rental_id),
 *   UNIQUE KEY  (rental_date,inventory_id,customer_id),
 *   KEY idx_fk_inventory_id (inventory_id),
 *   KEY idx_fk_customer_id (customer_id),
 *   KEY idx_fk_staff_id (staff_id),
 *   CONSTRAINT fk_rental_staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_rental_inventory FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_rental_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * A {@code null} {@value #COLUMN_NAME_RETURN_DATE} means the copy is still out; the installed
 * {@code inventory_in_stock} function reads it that way.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-rental.html">The
 *         rental Table</a>
 */
@NamedQuery(name = "Rental.selectOneByRentalId",
            query = """
                    SELECT e
                    FROM Rental e
                    WHERE e.rentalId = :rentalId"""
)
@NamedQuery(name = "Rental.selectListOrderByRentalIdAsc",
            query = """
                    SELECT e
                    FROM Rental e
                    ORDER BY e.rentalId ASC"""
)
@NamedQuery(name = "Rental.selectListOrderByRentalIdAscRentalIdGt",
            query = """
                    SELECT e
                    FROM Rental e
                    WHERE e.rentalId > :rentalIdMinExclusive
                    ORDER BY e.rentalId ASC"""
)
@NamedQuery(name = "Rental.selectOneByRentalDateAndInventoryAndCustomer",
            query = """
                    SELECT e
                    FROM Rental e
                    WHERE e.rentalDate = :rentalDate
                      AND e.inventory = :inventory
                      AND e.customer = :customer"""
)
@NamedQuery(name = "Rental.selectListByCustomerOrderByRentalDateDesc",
            query = """
                    SELECT e
                    FROM Rental e
                    WHERE e.customer = :customer
                    ORDER BY e.rentalDate DESC"""
)
@NamedQuery(name = "Rental.selectListByInventoryOrderByRentalDateDesc",
            query = """
                    SELECT e
                    FROM Rental e
                    WHERE e.inventory = :inventory
                    ORDER BY e.rentalDate DESC"""
)
@NamedQuery(name = "Rental.selectListWhereReturnDateIsNullOrderByRentalDateAsc",
            query = """
                    SELECT e
                    FROM Rental e
                    WHERE e.returnDate IS NULL
                    ORDER BY e.rentalDate ASC"""
)
@NamedQuery(name = "Rental.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Rental e"""
)
@NamedQuery(name = "Rental.countByCustomer",
            query = """
                    SELECT COUNT(e)
                    FROM Rental e
                    WHERE e.customer = :customer"""
)
@Entity
@Table(name = Rental.TABLE_NAME,
       uniqueConstraints = {
               @UniqueConstraint(
                       columnNames = {
                               Rental.COLUMN_NAME_RENTAL_DATE,
                               Rental.COLUMN_NAME_INVENTORY_ID,
                               Rental.COLUMN_NAME_CUSTOMER_ID
                       }
               )
       }
)
public class Rental {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "rental";

    // ----------------------------------------------------------------------------------- rental_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RENTAL_ID} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_RENTAL_ID = "rental_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RENTAL_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL_ID = "rentalId";

    // --------------------------------------------------------------------------------- rental_date

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_RENTAL_DATE = "rental_date";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RENTAL_DATE} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL_DATE = "rentalDate";

    // -------------------------------------------------------------------------------- inventory_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_INVENTORY} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_INVENTORY_ID = "inventory_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_INVENTORY_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_INVENTORY = "inventory";

    // --------------------------------------------------------------------------------- customer_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CUSTOMER} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CUSTOMER_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_CUSTOMER = "customer";

    // --------------------------------------------------------------------------------- return_date

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_RETURN_DATE = "return_date";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RETURN_DATE} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RETURN_DATE = "returnDate";

    // ------------------------------------------------------------------------------------ staff_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STAFF} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_STAFF_ID = "staff_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STAFF_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STAFF = "staff";

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

    // ------------------------------------------------------------------------------------ payments

    /**
     * The name of the attribute which maps the {@link Payment payments} of this rental. The value
     * is {@value}.
     *
     * @see Payment#ATTRIBUTE_NAME_RENTAL
     */
    public static final String ATTRIBUTE_NAME_PAYMENTS = "payments";

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Rental() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "rentalId=" + rentalId +
               ",rentalDate=" + rentalDate +
               ",returnDate=" + returnDate +
               ",lastUpdate=" + lastUpdate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@code @Id} alone, and only once it is assigned; an
     *         instance without one equals nothing but itself. The table's unique key, which
     *         includes the {@value #ATTRIBUTE_NAME_RENTAL_DATE}, is not used, for the
     *         {@value #ATTRIBUTE_NAME_RENTAL_DATE} is set by a trigger, and is {@code null} until
     *         the instance is read from the database.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rental that)) {
            return false;
        }
        final var thisId = getRentalId();
        return thisId != null && thisId.equals(that.getRentalId());
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
        return Rental.class.hashCode();
    }

    // ----------------------------------------------------------------------------- Bean-Validation

    /**
     * Checks that the {@value #ATTRIBUTE_NAME_RETURN_DATE}, when there is one, is not before the
     * {@value #ATTRIBUTE_NAME_RENTAL_DATE}.
     *
     * @return {@code true} when either date is {@code null}, or when the return is not before the
     *         rental; {@code false} otherwise.
     * @implNote The database does not check this; a copy cannot come back before it went
     *         out, though, and every installed row agrees.
     */
    @AssertTrue
    protected boolean isReturnDateNotBeforeRentalDate() {
        if (rentalDate == null || returnDate == null) {
            return true;
        }
        return !returnDate.isBefore(rentalDate);
    }

    // ------------------------------------------------------------------------------------ rentalId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL_ID} attribute; {@code null} until the
     *         instance is persisted.
     */
    public Integer getRentalId() {
        return rentalId;
    }

    // ---------------------------------------------------------------------------------- rentalDate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute; {@code null} until
     *         the instance is read from the database.
     */
    public LocalDateTime getRentalDate() {
        return rentalDate;
    }

    // ----------------------------------------------------------------------------------- inventory

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_INVENTORY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_INVENTORY} attribute.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_INVENTORY} attribute with the specified
     * value.
     *
     * @param inventory new value for {@value #ATTRIBUTE_NAME_INVENTORY} attribute.
     */
    public void setInventory(final Inventory inventory) {
        this.inventory = inventory;
    }

    // ------------------------------------------------------------------------------------ customer

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CUSTOMER} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CUSTOMER} attribute.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_CUSTOMER} attribute with the specified
     * value.
     *
     * @param customer new value for {@value #ATTRIBUTE_NAME_CUSTOMER} attribute.
     */
    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    // ---------------------------------------------------------------------------------- returnDate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute; {@code null} while
     *         the copy is out.
     */
    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute with the specified
     * value.
     *
     * @param returnDate new value for {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute.
     */
    public void setReturnDate(final LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    // --------------------------------------------------------------------------------------- staff

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STAFF} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STAFF} attribute.
     */
    public Staff getStaff() {
        return staff;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_STAFF} attribute with the specified value.
     *
     * @param staff new value for {@value #ATTRIBUTE_NAME_STAFF} attribute.
     */
    public void setStaff(final Staff staff) {
        this.staff = staff;
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

    // ------------------------------------------------------------------------------------ payments
    List<Payment> getPayments() {
        return payments;
    }

    void setPayments(final List<Payment> payments) {
        this.payments = payments;
    }

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_RENTAL_ID, nullable = false, insertable = true, updatable = false)
    private Integer rentalId;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_RENTAL_DATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_DATETIME_CURRENT_TIMESTAMP)
    private LocalDateTime rentalDate;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_INVENTORY_ID,
                referencedColumnName = Inventory.COLUMN_NAME_INVENTORY_ID,
                nullable = false, insertable = true, updatable = true)
    private Inventory inventory;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_CUSTOMER_ID,
                referencedColumnName = Customer.COLUMN_NAME_CUSTOMER_ID,
                nullable = false, insertable = true, updatable = true)
    private Customer customer;

    // ---------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_RETURN_DATE, nullable = true, insertable = true, updatable = true)
    private LocalDateTime returnDate;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STAFF_ID, referencedColumnName = Staff.COLUMN_NAME_STAFF_ID,
                nullable = false,
                insertable = true, updatable = true)
    private Staff staff;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = false, insertable = false, updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;

    // ---------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = Payment.ATTRIBUTE_NAME_RENTAL, fetch = FetchType.LAZY)
    private List<@Valid @NotNull Payment> payments;
}
