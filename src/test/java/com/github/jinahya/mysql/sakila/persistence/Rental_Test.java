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

import lombok.extern.slf4j.Slf4j;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link Rental} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Rental_Test
        extends _Entity_Test<Rental, Integer> {

    Rental_Test() {
        super(Rental.class, Integer.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link Rental#of(Inventory, Customer, Staff)}.
     */
    @DisplayName("of(inventory, customer, staff)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link Rental#of(Inventory, Customer, Staff) of} returns an instance whose
         * {@value Rental#ATTRIBUTE_NAME_INVENTORY}, {@value Rental#ATTRIBUTE_NAME_CUSTOMER} and
         * {@value Rental#ATTRIBUTE_NAME_STAFF} are the very instances it was given, and whose
         * {@value Rental#ATTRIBUTE_NAME_RENTAL_DATE} and {@value Rental#ATTRIBUTE_NAME_RETURN_DATE}
         * are left {@code null}.
         */
        @Test
        void __() {
            final var inventory = mock(Inventory.class);
            final var customer = mock(Customer.class);
            final var staff = mock(Staff.class);
            final var instance = Rental.of(inventory, customer, staff);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getInventory()).isSameAs(inventory);
                assertThat(v.getCustomer()).isSameAs(customer);
                assertThat(v.getStaff()).isSameAs(staff);
                assertThat(v.getRentalDate()).isNull();
                assertThat(v.getReturnDate()).isNull();
                assertThat(v.getRentalId()).isNull();
            });
        }
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Rental#equals(Object) equals} compares only the {@code @Id}, because
     *         the {@value Rental#ATTRIBUTE_NAME_RENTAL_DATE} of the table's unique key is set by a
     *         trigger, and every other attribute is mutable state.
     */
    @Override
    public SingleTypeEqualsVerifierApi<Rental> equals_verifier_() {
        return super.equals_verifier_()
                .suppress(Warning.SURROGATE_KEY,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
