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
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An embeddable for mapping the {@value Rental#COLUMN_NAME_RENTAL_DATE} column and the
 * {@value Rental#COLUMN_NAME_RETURN_DATE} column, of the {@value Rental#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * rental_date DATETIME NOT NULL,
 * return_date DATETIME DEFAULT NULL,
 *}
 * <p>
 * This is a class, not a record, for a rental starts with the {@value #ATTRIBUTE_NAME_RENTAL_DATE} alone, and the
 * {@value #ATTRIBUTE_NAME_RETURN_DATE} is set later, on its own, when the item comes back.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class EmbeddableRentalPeriod {

    /**
     * The name of the attribute which maps the {@value Rental#COLUMN_NAME_RENTAL_DATE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL_DATE = "rentalDate";

    /**
     * The name of the attribute which maps the {@value Rental#COLUMN_NAME_RETURN_DATE} column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RETURN_DATE = "returnDate";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified rental date, with no return date.
     *
     * @param rentalDate a value for the {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute.
     * @return a new instance of {@code rentalDate}.
     */
    public static EmbeddableRentalPeriod of(final LocalDateTime rentalDate) {
        final var instance = new EmbeddableRentalPeriod();
        instance.setRentalDate(rentalDate);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected EmbeddableRentalPeriod() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "rentalDate=" + rentalDate +
               ",returnDate=" + returnDate +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by both {@value #ATTRIBUTE_NAME_RENTAL_DATE} and
     *         {@value #ATTRIBUTE_NAME_RETURN_DATE}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof EmbeddableRentalPeriod that)) {
            return false;
        }
        return Objects.equals(rentalDate, that.rentalDate)
               && Objects.equals(returnDate, that.returnDate);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over both {@value #ATTRIBUTE_NAME_RENTAL_DATE} and
     *         {@value #ATTRIBUTE_NAME_RETURN_DATE}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(rentalDate, returnDate);
    }

    // ------------------------------------------------------------------------------------------------------ rentalDate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute.
     */
    public LocalDateTime getRentalDate() {
        return rentalDate;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute with the specified value.
     *
     * @param rentalDate new value for {@value #ATTRIBUTE_NAME_RENTAL_DATE} attribute.
     */
    public void setRentalDate(final LocalDateTime rentalDate) {
        this.rentalDate = rentalDate;
    }

    // ------------------------------------------------------------------------------------------------------ returnDate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute; {@code null} while the item is out.
     */
    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute with the specified value.
     *
     * @param returnDate new value for {@value #ATTRIBUTE_NAME_RETURN_DATE} attribute.
     */
    public void setReturnDate(final LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = Rental.COLUMN_NAME_RENTAL_DATE, nullable = false, insertable = true, updatable = true)
    private LocalDateTime rentalDate;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = Rental.COLUMN_NAME_RETURN_DATE, nullable = true, insertable = true, updatable = true)
    private LocalDateTime returnDate;
}
