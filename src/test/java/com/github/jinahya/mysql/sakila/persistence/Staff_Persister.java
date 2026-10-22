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

import java.util.Optional;

/**
 * A persister which persists {@link Staff} instances.
 * <p>
 * Each instance is given a newly persisted {@link Address}, and the first {@link Store} of the
 * database. A store cannot be persisted without a manager who already works at a store -- see
 * {@link Store} -- and {@code store_id} is a {@code TINYINT UNSIGNED} besides, so an existing one
 * is what a staff member can be given.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Staff_Persister
        extends AbstractEntityPersister<Staff> {

    Staff_Persister() {
        super(Staff.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Selects the {@link Staff} with the smallest {@code staffId} in the database.
     *
     * @param entityManager the entity manager to select with.
     * @return the {@code Staff} with the smallest {@code staffId}; {@link Optional#empty() empty}
     *         when the database holds none, as the generated, in-memory one does.
     */
    static Optional<Staff> selectFirst(final EntityManager entityManager) {
        return entityManager
                .createNamedQuery("Staff.selectListOrderByStaffIdAsc", Staff.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Staff apply(final EntityManager entityManager, final Staff entityInstance) {
        entityInstance.setAddress(
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, Address.class));
        entityInstance.setStore(Store_Persister.selectFirst(entityManager).orElseThrow(
                () -> new IllegalStateException(
                        "no store in the database; see Store_Persister#selectFirst")));
        return super.apply(entityManager, entityInstance);
    }
}
