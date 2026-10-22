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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Payment} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Payment_Persistence_IT
        extends _Entity_Persistence_IT<Payment> {

    Payment_Persistence_IT() {
        super(Payment.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The amount is not negative, and the customer and the staff member, lazy associations, resolve;
     *         the rental, when there is one, is of the same customer.
     */
    @Override
    void randomTargetInstanceSelected(final Payment instance) {
        assertThat(instance.getAmount()).as("amount of %s", instance).isNotNegative();
        assertThat(instance.getStaff().getUsername()).as("username of the staff of %s", instance).isNotBlank();
        final var customerId = instance.getCustomer().getCustomerId();
        final var rental = instance.getRental();
        if (rental != null) {
            assertThat(rental.getCustomer().getCustomerId())
                    .as("customer id of the rental of %s", instance)
                    .isEqualTo(customerId);
        }
    }
}
