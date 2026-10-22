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
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An entity class for mapping the {@value Payment#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * CREATE TABLE payment (
 *   payment_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *   customer_id SMALLINT UNSIGNED NOT NULL,
 *   staff_id TINYINT UNSIGNED NOT NULL,
 *   rental_id INT DEFAULT NULL,
 *   amount DECIMAL(5,2) NOT NULL,
 *   payment_date DATETIME NOT NULL,
 *   last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   PRIMARY KEY  (payment_id),
 *   KEY idx_fk_staff_id (staff_id),
 *   KEY idx_fk_customer_id (customer_id),
 *   CONSTRAINT fk_payment_rental FOREIGN KEY (rental_id) REFERENCES rental (rental_id)
 *       ON DELETE SET NULL ON UPDATE CASCADE,
 *   CONSTRAINT fk_payment_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE,
 *   CONSTRAINT fk_payment_staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id)
 *       ON DELETE RESTRICT ON UPDATE CASCADE
 * )
 *}
 * <p>
 * Unlike most tables of the database, the {@value #COLUMN_NAME_LAST_UPDATE} column of this one is
 * nullable.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://dev.mysql.com/doc/sakila/en/sakila-structure-tables-payment.html">The
 *         payment Table</a>
 */
@NamedQuery(name = "Payment.selectOneByPaymentId",
            query = """
                    SELECT e
                    FROM Payment e
                    WHERE e.paymentId = :paymentId"""
)
@NamedQuery(name = "Payment.selectListOrderByPaymentIdAsc",
            query = """
                    SELECT e
                    FROM Payment e
                    ORDER BY e.paymentId ASC"""
)
@NamedQuery(name = "Payment.selectListOrderByPaymentIdAscPaymentIdGt",
            query = """
                    SELECT e
                    FROM Payment e
                    WHERE e.paymentId > :paymentIdMinExclusive
                    ORDER BY e.paymentId ASC"""
)
@NamedQuery(name = "Payment.selectListByCustomerOrderByPaymentDateDesc",
            query = """
                    SELECT e
                    FROM Payment e
                    WHERE e.customer = :customer
                    ORDER BY e.paymentDate DESC"""
)
@NamedQuery(name = "Payment.selectListByRentalOrderByPaymentIdAsc",
            query = """
                    SELECT e
                    FROM Payment e
                    WHERE e.rental = :rental
                    ORDER BY e.paymentId ASC"""
)
@NamedQuery(name = "Payment.countAll",
            query = """
                    SELECT COUNT(e)
                    FROM Payment e"""
)
@NamedQuery(name = "Payment.sumAmountByCustomer",
            query = """
                    SELECT SUM(e.amount)
                    FROM Payment e
                    WHERE e.customer = :customer"""
)
@Entity
@Table(name = Payment.TABLE_NAME)
public class Payment {

    /**
     * The name of the database table to which this entity class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "payment";

    // ---------------------------------------------------------------------------------- payment_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PAYMENT_ID} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_PAYMENT_ID = "payment_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PAYMENT_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PAYMENT_ID = "paymentId";

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

    // ----------------------------------------------------------------------------------- rental_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RENTAL} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_RENTAL_ID = "rental_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RENTAL_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL = "rental";

    // -------------------------------------------------------------------------------------- amount

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_AMOUNT} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_AMOUNT = "amount";

    /**
     * The precision of the {@value #COLUMN_NAME_AMOUNT} column. The value is {@value}.
     */
    public static final int COLUMN_PRECISION_AMOUNT = 5;

    /**
     * The scale of the {@value #COLUMN_NAME_AMOUNT} column. The value is {@value}.
     */
    public static final int COLUMN_SCALE_AMOUNT = 2;

    /**
     * The minimum value of the {@value #COLUMN_NAME_AMOUNT} column. The value is {@value}.
     */
    public static final String COLUMN_MIN_AMOUNT = "-999.99";

    /**
     * The maximum value of the {@value #COLUMN_NAME_AMOUNT} column. The value is {@value}.
     */
    public static final String COLUMN_MAX_AMOUNT = "999.99";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_AMOUNT} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_AMOUNT = "amount";

    // -------------------------------------------------------------------------------- payment_date

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PAYMENT_DATE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_PAYMENT_DATE = "payment_date";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PAYMENT_DATE} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PAYMENT_DATE = "paymentDate";

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

    // ---------------------------------------------------------------------- STATIC FACTORY METHODS

    /**
     * Returns a new instance of the specified staff for the specified rental.
     * <p>
     * The {@value #ATTRIBUTE_NAME_CUSTOMER} of the new instance is the one of the rental. Along
     * with {@link #of(Staff, Customer)}, this is the only way to set the
     * {@value #ATTRIBUTE_NAME_CUSTOMER}, the {@value #ATTRIBUTE_NAME_STAFF} and the
     * {@value #ATTRIBUTE_NAME_RENTAL} from outside the package; the {@value #ATTRIBUTE_NAME_AMOUNT}
     * and the {@value #ATTRIBUTE_NAME_PAYMENT_DATE} are set on their own.
     *
     * @param staff  a value for the {@value #ATTRIBUTE_NAME_STAFF} attribute.
     * @param rental a value for the {@value #ATTRIBUTE_NAME_RENTAL} attribute.
     * @return a new instance of {@code staff} for {@code rental}.
     * @apiNote A payment is processed some time after the rental, by whichever staff member
     *         is there; the staff of the rental is just one of them, so the staff is always given.
     *         The database also sets the {@value #COLUMN_NAME_RENTAL_ID} column to {@code NULL}
     *         when the rental is deleted.
     * @see #of(Staff, Customer)
     */
    public static Payment of(final Staff staff, final Rental rental) {
        Objects.requireNonNull(rental, "rental is null");
        final var instance = new Payment();
        instance.setCustomer(rental.getCustomer());
        instance.setStaff(staff);
        instance.setRental(rental);
        return instance;
    }

    /**
     * Returns a new instance of the specified staff for the specified customer, without a rental.
     * <p>
     * A payment is not always applied to a rental; one of outstanding fees, for example, is applied
     * to the balance of the customer alone, and its {@value #ATTRIBUTE_NAME_RENTAL} is
     * {@code null}.
     *
     * @param staff    a value for the {@value #ATTRIBUTE_NAME_STAFF} attribute.
     * @param customer a value for the {@value #ATTRIBUTE_NAME_CUSTOMER} attribute.
     * @return a new instance of {@code staff} for {@code customer}.
     * @see #of(Staff, Rental)
     */
    public static Payment of(final Staff staff, final Customer customer) {
        final var instance = new Payment();
        instance.setCustomer(customer);
        instance.setStaff(staff);
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Payment() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "paymentId=" + paymentId +
               ",amount=" + amount +
               ",paymentDate=" + paymentDate +
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
        if (!(obj instanceof Payment that)) {
            return false;
        }
        final var thisId = getPaymentId();
        return thisId != null && thisId.equals(that.getPaymentId());
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
        return Payment.class.hashCode();
    }

    // ----------------------------------------------------------------------------------- paymentId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PAYMENT_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PAYMENT_ID} attribute; {@code null} until
     *         the instance is persisted.
     */
    public Integer getPaymentId() {
        return paymentId;
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
     * @apiNote This method is package-private; {@link #of(Staff, Rental)} sets the
     *         customer, as the one of the rental, from outside the package.
     */
    void setCustomer(final Customer customer) {
        this.customer = customer;
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
     * @apiNote This method is package-private; {@link #of(Staff, Rental)} sets the staff
     *         from outside the package.
     */
    void setStaff(final Staff staff) {
        this.staff = staff;
    }

    // -------------------------------------------------------------------------------------- rental

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL} attribute.
     */
    public Rental getRental() {
        return rental;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RENTAL} attribute with the specified
     * value.
     *
     * @param rental new value for {@value #ATTRIBUTE_NAME_RENTAL} attribute.
     * @apiNote This method is package-private; {@link #of(Staff, Rental)} sets the rental
     *         from outside the package. The {@value #ATTRIBUTE_NAME_CUSTOMER} is left as it is; the
     *         customer of the rental should be the customer of this payment.
     */
    void setRental(final Rental rental) {
        this.rental = rental;
    }

    // -------------------------------------------------------------------------------------- amount

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AMOUNT} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AMOUNT} attribute.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_AMOUNT} attribute with the specified
     * value.
     *
     * @param amount new value for {@value #ATTRIBUTE_NAME_AMOUNT} attribute.
     */
    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    // --------------------------------------------------------------------------------- paymentDate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PAYMENT_DATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PAYMENT_DATE} attribute; {@code null} until
     *         the instance is read from the database.
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
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

    // ---------------------------------------------------------------------------------------------
    // no @NotNull: AUTO_INCREMENT; the value is null when the provider validates at pre-persist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_PAYMENT_ID, nullable = false, insertable = true, updatable = false)
    private Integer paymentId;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_CUSTOMER_ID,
                referencedColumnName = Customer.COLUMN_NAME_CUSTOMER_ID, nullable = false,
                insertable = true, updatable = false)
    private Customer customer;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_STAFF_ID, referencedColumnName = Staff.COLUMN_NAME_STAFF_ID,
                nullable = false, insertable = true, updatable = false)
    private Staff staff;

    // ---------------------------------------------------------------------------------------------
    @Valid
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_NAME_RENTAL_ID, referencedColumnName = Rental.COLUMN_NAME_RENTAL_ID,
                nullable = true, insertable = true, updatable = false)
    private Rental rental;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    // @Digits counts digits differently by provider; https://github.com/jakartaee/validation-spec/issues/346
    // @Digits(integer = COLUMN_PRECISION_AMOUNT - COLUMN_SCALE_AMOUNT, fraction =
    // COLUMN_SCALE_AMOUNT)
    @DecimalMax(COLUMN_MAX_AMOUNT)
    @DecimalMin(COLUMN_MIN_AMOUNT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AMOUNT, nullable = false, insertable = true, updatable = true,
            precision = COLUMN_PRECISION_AMOUNT, scale = COLUMN_SCALE_AMOUNT)
    private BigDecimal amount;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_PAYMENT_DATE, nullable = false, insertable = false,
            updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_DATETIME_CURRENT_TIMESTAMP)
    private LocalDateTime paymentDate;

    // ---------------------------------------------------------------------------------------------
    @Basic
    @Column(name = COLUMN_NAME_LAST_UPDATE, nullable = true, insertable = false, updatable = false,
            columnDefinition = _Constants.COLUMN_DEFINITION_LAST_UPDATE)
    private LocalDateTime lastUpdate;
}
