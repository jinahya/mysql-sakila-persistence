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

import com.github.jinahya.persistence.test.util.AbstractEntityPersister;
import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import jakarta.persistence.EntityManager;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A persister which persists {@link Payment} instances.
 * <p>
 * Each instance is, at random, either one for a rental or one without a rental, the two kinds the
 * {@link Payment#of(Staff, Rental)} and the {@link Payment#of(Staff, Customer)} create.
 * <p>
 * One for a rental is given a newly persisted {@link Rental}, which also gives it the customer of that rental. The
 * staff member is, at random, either the one of that rental or any {@link Staff} of the database; nothing ties the two,
 * and about half of the installed payments were taken by a staff member other than the one of their rental.
 * <p>
 * One without a rental, a payment of outstanding fees, is given a newly persisted {@link Customer}, and any
 * {@link Staff} of the database.
 * <p>
 * No staff member is persisted: a {@link Store} and its manager refer to each other, and neither is persisted; see
 * {@link Store}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Payment_Persister
        extends AbstractEntityPersister<Payment> {

    Payment_Persister() {
        super(Payment.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    private static Staff selectAnyStaff(final EntityManager entityManager) {
        final var staff = entityManager
                .createNamedQuery("Staff.selectListOrderByStaffIdAsc", Staff.class)
                .getResultList();
        if (staff.isEmpty()) {
            throw new IllegalStateException("no staff in the database");
        }
        return staff.get(ThreadLocalRandom.current().nextInt(staff.size()));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Payment apply(final EntityManager entityManager, final Payment entityInstance) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            // a payment for a rental, Payment.of(Staff, Rental); the customer is the one of the rental, and the staff
            // is either the one of the rental or another
            final var rental = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Rental.class);
            entityInstance.setCustomer(rental.getCustomer());
            entityInstance.setRental(rental);
            entityInstance.setStaff(
                    ThreadLocalRandom.current().nextBoolean()
                            ? rental.getStaff()
                            : selectAnyStaff(entityManager)
            );
        } else {
            // a payment without a rental, Payment.of(Staff, Customer); outstanding fees applied to the balance of the
            // customer alone, so the rental_id is NULL
            entityInstance.setCustomer(EntityPersisterUtils.newPersistedInstanceOf(entityManager, Customer.class));
            entityInstance.setRental(null);
            entityInstance.setStaff(selectAnyStaff(entityManager));
        }
        return super.apply(entityManager, entityInstance);
    }
}
