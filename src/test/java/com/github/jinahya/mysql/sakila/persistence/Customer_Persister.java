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
 * A persister which persists {@link Customer} instances.
 * <p>
 * Each instance is given a newly persisted {@link Address}, and the first {@link Store} of the database; see
 * {@link Staff_Persister} for why the store is not a new one.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Customer_Persister extends AbstractEntityPersister<Customer> {

    Customer_Persister() {
        super(Customer.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Customer apply(final EntityManager entityManager, final Customer entityInstance) {
        entityInstance.setStore(Store_Persister.selectFirst(entityManager).orElseThrow(
                () -> new IllegalStateException("no store in the database; see Store_Persister#selectFirst")));
        entityInstance.setAddress(EntityPersisterUtils.newPersistedInstanceOf(entityManager, Address.class));
        return super.apply(entityManager, entityInstance);
    }
}
