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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Inventory} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Inventory_PersistenceIT
        extends _Entity_PersistenceIT<Inventory, Integer> {

    Inventory_PersistenceIT() {
        super(Inventory.class, Integer.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityInstance {@inheritDoc}
     * @implNote The film and the store, lazy associations, resolve.
     */
    @Override
    protected void randomEntityInstanceSelected__(final EntityManagerFactory entityManagerFactory,
                                                  final EntityManager entityManager,
                                                  final Inventory entityInstance) {
        assertThat(entityInstance.getFilm().getTitle())
                .as("title of the film of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getStore().getLastUpdate())
                .as("last update of the store of %s", entityInstance)
                .isNotNull();
    }
}
