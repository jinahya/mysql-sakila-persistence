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

/**
 * A persister which persists {@link Payment} instances.
 * <p>
 * Each instance is given a newly persisted {@link Rental}, and the customer and the staff member of that rental, as an
 * installed payment has.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Payment_Persister extends AbstractEntityPersister<Payment> {

    Payment_Persister() {
        super(Payment.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Payment apply(final EntityManager entityManager, final Payment entityInstance) {
        final var rental = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Rental.class);
        entityInstance.setRental(rental);
        entityInstance.setCustomer(rental.getCustomer());
        entityInstance.setStaff(rental.getStaff());
        return super.apply(entityManager, entityInstance);
    }
}
