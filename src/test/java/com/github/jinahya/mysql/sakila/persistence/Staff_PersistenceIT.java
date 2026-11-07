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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.github.jinahya.mysql.sakila.persistence.___PersistenceIT_Utils.applyEntityManager_;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * Verifies the mappings of {@link Staff} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Staff_PersistenceIT
        extends _Entity_PersistenceIT<Staff, Integer> {

    Staff_PersistenceIT() {
        super(Staff.class, Integer.class);
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
     * @implNote Both names and the username are there, and the store, a lazy association,
     *         resolves.
     */
    @Override
    protected void randomEntityInstanceSelected__(final EntityManagerFactory entityManagerFactory,
                                                  final EntityManager entityManager,
                                                  final Staff entityInstance) {
        assertThat(entityInstance.getFirstName())
                .as("first name of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getLastName())
                .as("last name of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getUsername())
                .as("username of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getStore().getLastUpdate())
                .as("last update of the store of %s", entityInstance)
                .isNotNull();
    }

    @ValueSource(ints = {1, 2})
    @ParameterizedTest
    void find1__(final int id) {
        final var found = applyEntityManager_(emf -> em -> em.find(Staff.class, id));
        assumeThat(found).isNotNull();
        {
            final var picture = found.getPicture();
            if (picture != null) {
                try {
                    final var image = ImageIO.read(new ByteArrayInputStream(picture));
                    log.debug("picture.type: {}", image.getType());
                    log.debug("picture.width: {}", image.getWidth());
                    log.debug("picture.height: {}", image.getHeight());
                } catch (final IOException ioe) {
                    throw new RuntimeException("failed to read picture as image", ioe);
                }
            }
        }
    }
}
