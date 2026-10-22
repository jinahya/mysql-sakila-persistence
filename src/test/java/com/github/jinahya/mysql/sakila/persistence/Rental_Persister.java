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
 * A persister which persists {@link Rental} instances.
 * <p>
 * Each instance is given a newly persisted {@link Inventory} and a newly persisted
 * {@link Customer}, and the first {@link Staff} of the database, whose {@code staff_id} is a
 * {@code TINYINT UNSIGNED}; see {@link Film_Persister}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Rental_Persister
        extends AbstractEntityPersister<Rental> {

    Rental_Persister() {
        super(Rental.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Rental apply(final EntityManager entityManager, final Rental entityInstance) {
        entityInstance.setInventory(
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, Inventory.class));
        entityInstance.setCustomer(
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, Customer.class));
        entityInstance.setStaff(Staff_Persister.selectFirst(entityManager).orElseThrow(
                () -> new IllegalStateException(
                        "no staff in the database; see Staff_Persister#selectFirst")));
        return super.apply(entityManager, entityInstance);
    }
}
