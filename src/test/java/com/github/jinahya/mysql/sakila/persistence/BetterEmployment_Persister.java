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
 * A persister which persists {@link BetterEmployment} instances.
 * <p>
 * Each instance is given a newly persisted {@link BetterStore} and a newly persisted {@link BetterStaff}; the store
 * being a new one, the {@code manager} which {@link BetterEmployment_Randomizer} gave it, {@link Boolean#TRUE} or
 * {@code null}, collides with no other manager of it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BetterEmployment_Persister
        extends AbstractEntityPersister<BetterEmployment> {

    BetterEmployment_Persister() {
        super(BetterEmployment.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public BetterEmployment apply(final EntityManager entityManager, final BetterEmployment entityInstance) {
        final var store = EntityPersisterUtils.newPersistedInstanceOf(entityManager, BetterStore.class);
        final var staff = EntityPersisterUtils.newPersistedInstanceOf(entityManager, BetterStaff.class);
        entityManager.flush(); // EclipseLink assigns an IDENTITY value only at the flush; the setters copy it
        entityInstance.setStore(store);
        entityInstance.setStaff(staff);
        return super.apply(entityManager, entityInstance);
    }
}
