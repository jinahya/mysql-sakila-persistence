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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An embeddable for mapping the {@value Film#COLUMN_NAME_RENTAL_DURATION} column, the
 * {@value Film#COLUMN_NAME_RENTAL_RATE} column, and the {@value Film#COLUMN_NAME_REPLACEMENT_COST}
 * column, of the {@value Film#TABLE_NAME} table.
 * <p>
 * {@snippet lang = "sql":
 * rental_duration TINYINT UNSIGNED NOT NULL DEFAULT 3,
 * rental_rate DECIMAL(4,2) NOT NULL DEFAULT 4.99,
 * replacement_cost DECIMAL(5,2) NOT NULL DEFAULT 19.99,
 *}
 * <p>
 * This is a class, not a record, for each term is revised on its own; a new rental rate leaves the
 * duration and the replacement cost as they are.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class EmbeddableRentalTerms {

    /**
     * The name of the attribute which maps the {@value Film#COLUMN_NAME_RENTAL_DURATION} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL_DURATION = "rentalDuration";

    /**
     * The name of the attribute which maps the {@value Film#COLUMN_NAME_RENTAL_RATE} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_RENTAL_RATE = "rentalRate";

    /**
     * The name of the attribute which maps the {@value Film#COLUMN_NAME_REPLACEMENT_COST} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_REPLACEMENT_COST = "replacementCost";

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified values.
     *
     * @param rentalDuration  a value for the {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute.
     * @param rentalRate      a value for the {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute.
     * @param replacementCost a value for the {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute.
     * @return a new instance of {@code rentalDuration}, {@code rentalRate}, and
     *         {@code replacementCost}.
     */
    public static EmbeddableRentalTerms of(final Integer rentalDuration,
                                           final BigDecimal rentalRate,
                                           final BigDecimal replacementCost) {
        final var instance = new EmbeddableRentalTerms();
        instance.setRentalDuration(rentalDuration);
        instance.setRentalRate(rentalRate);
        instance.setReplacementCost(replacementCost);
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected EmbeddableRentalTerms() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "rentalDuration=" + rentalDuration +
               ",rentalRate=" + rentalRate +
               ",replacementCost=" + replacementCost +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by all three attributes. The amounts compare by
     *         {@link BigDecimal#equals(Object) BigDecimal.equals}, so {@code 4.99} and
     *         {@code 4.990} differ; both come back from the database at the column's scale.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof EmbeddableRentalTerms that)) {
            return false;
        }
        return Objects.equals(rentalDuration, that.rentalDuration)
               && Objects.equals(rentalRate, that.rentalRate)
               && Objects.equals(replacementCost, that.replacementCost);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over all three attributes, consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(rentalDuration, rentalRate, replacementCost);
    }

    // ------------------------------------------------------------------------------ rentalDuration

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute.
     */
    public Integer getRentalDuration() {
        return rentalDuration;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute with the
     * specified value.
     *
     * @param rentalDuration new value for {@value #ATTRIBUTE_NAME_RENTAL_DURATION} attribute.
     */
    public void setRentalDuration(final Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    // ---------------------------------------------------------------------------------- rentalRate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute.
     */
    public BigDecimal getRentalRate() {
        return rentalRate;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute with the specified
     * value.
     *
     * @param rentalRate new value for {@value #ATTRIBUTE_NAME_RENTAL_RATE} attribute.
     */
    public void setRentalRate(final BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    // ----------------------------------------------------------------------------- replacementCost

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute.
     */
    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute with the
     * specified value.
     *
     * @param replacementCost new value for {@value #ATTRIBUTE_NAME_REPLACEMENT_COST} attribute.
     */
    public void setReplacementCost(final BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Max(Film.ATTRIBUTE_MAX_RENTAL_DURATION)
    @Min(Film.ATTRIBUTE_MIN_RENTAL_DURATION)
    @Basic(optional = false)
    @Column(name = Film.COLUMN_NAME_RENTAL_DURATION, nullable = false, insertable = true,
            updatable = true)
    private Integer rentalDuration;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    // @Digits counts digits differently by provider; https://github.com/jakartaee/validation-spec/issues/346
    // @Digits(integer = Film.COLUMN_PRECISION_RENTAL_RATE - Film.COLUMN_SCALE_RENTAL_RATE,
    //         fraction = Film.COLUMN_SCALE_RENTAL_RATE)
    @DecimalMin(Film.COLUMN_MIN_RENTAL_RATE)
    @DecimalMax(Film.COLUMN_MAX_RENTAL_RATE)
    @Basic(optional = false)
    @Column(name = Film.COLUMN_NAME_RENTAL_RATE, nullable = false, insertable = true,
            updatable = true, precision = Film.COLUMN_PRECISION_RENTAL_RATE,
            scale = Film.COLUMN_SCALE_RENTAL_RATE)
    private BigDecimal rentalRate;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    // @Digits counts digits differently by provider; https://github.com/jakartaee/validation-spec/issues/346
    // @Digits(integer = Film.COLUMN_PRECISION_REPLACEMENT_COST - Film
    // .COLUMN_SCALE_REPLACEMENT_COST,
    //         fraction = Film.COLUMN_SCALE_REPLACEMENT_COST)
    @DecimalMin(Film.COLUMN_MIN_REPLACEMENT_COST)
    @DecimalMax(Film.COLUMN_MAX_REPLACEMENT_COST)
    @Basic(optional = false)
    @Column(name = Film.COLUMN_NAME_REPLACEMENT_COST, nullable = false, insertable = true,
            updatable = true, precision = Film.COLUMN_PRECISION_REPLACEMENT_COST,
            scale = Film.COLUMN_SCALE_REPLACEMENT_COST)
    private BigDecimal replacementCost;
}
