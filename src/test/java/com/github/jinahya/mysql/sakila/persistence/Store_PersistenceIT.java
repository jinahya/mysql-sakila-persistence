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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Store} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Store_PersistenceIT
        extends _Entity_PersistenceIT<Store, Integer> {

    Store_PersistenceIT() {
        super(Store.class, Integer.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; a {@link Store} and its manager, a {@link Staff}, refer to each other through
     * {@code NOT NULL} columns, so neither is persisted; see {@link Store}.
     */
    @Disabled("a store and its manager refer to each other: neither is persisted")
    @Override
    @Test
    protected void persistRandomizedEntityInstance__() {
        // empty
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityInstance {@inheritDoc}
     * @implNote The manager, a lazy association, works at the store, and the address,
     *         another, resolves.
     */
    @Override
    protected void randomEntityInstanceSelected__(final EntityManagerFactory entityManagerFactory,
                                                  final EntityManager entityManager,
                                                  final Store entityInstance) {
        assertThat(entityInstance.getManagerStaff().getStore().getStoreId())
                .as("store id of the manager of %s", entityInstance)
                .isEqualTo(entityInstance.getStoreId());
        assertThat(entityInstance.getAddress().getLastUpdate())
                .as("last update of the address of %s", entityInstance)
                .isNotNull();
    }
}
