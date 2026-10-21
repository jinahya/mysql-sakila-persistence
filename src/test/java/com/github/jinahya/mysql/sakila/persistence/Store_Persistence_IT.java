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
 * Verifies the mappings of {@link Store} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Store_Persistence_IT extends _Entity_Persistence_IT<Store> {

    Store_Persistence_IT() {
        super(Store.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The manager, a lazy association, works at the store, and the address, another, resolves.
     */
    @Override
    void randomSelected(final Store instance) {
        assertThat(instance.getManagerStaff().getStore().getStoreId())
                .as("store id of the manager of %s", instance)
                .isEqualTo(instance.getStoreId());
        assertThat(instance.getAddress().getLastUpdate()).as("last update of the address of %s", instance).isNotNull();
    }
}
