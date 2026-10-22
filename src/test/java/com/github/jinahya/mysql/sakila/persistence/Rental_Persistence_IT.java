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
 * Verifies the mappings of {@link Rental} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Rental_Persistence_IT
        extends _Entity_Persistence_IT<Rental> {

    Rental_Persistence_IT() {
        super(Rental.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The return date, when there is one, is not before the rental date, and the
     *         inventory, the customer, and the staff member, lazy associations, resolve.
     */
    @Override
    void randomTargetInstanceSelected(final Rental instance) {
        assertThat(instance.isReturnDateNotBeforeRentalDate()).as("return date of %s", instance)
                .isTrue();
        assertThat(instance.getInventory().getLastUpdate()).as("last update of the inventory of %s",
                                                               instance)
                .isNotNull();
        assertThat(instance.getCustomer().getLastName()).as("last name of the customer of %s",
                                                            instance).isNotBlank();
        assertThat(instance.getStaff().getUsername()).as("username of the staff of %s", instance)
                .isNotBlank();
    }
}
