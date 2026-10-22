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
 * A persister which persists {@link Store} instances.
 * <p>
 * Each instance is given a newly persisted {@link Address}, and a newly persisted {@link Staff} as
 * its manager, who works at the first store of the database; the manager has to be a new one, for
 * {@code manager_staff_id} is unique.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Store_Persister
        extends AbstractEntityPersister<Store> {

    Store_Persister() {
        super(Store.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Selects the {@link Store} with the smallest {@code storeId} in the database.
     *
     * @param entityManager the entity manager to select with.
     * @return the {@code Store} with the smallest {@code storeId}; {@link Optional#empty() empty}
     *         when the database holds none, as the generated, in-memory one does.
     */
    static Optional<Store> selectFirst(final EntityManager entityManager) {
        return entityManager
                .createNamedQuery("Store.selectListOrderByStoreIdAsc", Store.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Store apply(final EntityManager entityManager, final Store entityInstance) {
        entityInstance.setAddress(
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, Address.class));
        entityInstance.setManagerStaff(
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, Staff.class));
        return super.apply(entityManager, entityInstance);
    }
}
