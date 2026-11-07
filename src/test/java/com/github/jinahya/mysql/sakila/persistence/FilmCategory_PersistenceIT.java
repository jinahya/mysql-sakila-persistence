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
 * Verifies the mappings of {@link FilmCategory} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmCategory_PersistenceIT
        extends _Entity_PersistenceIT<FilmCategory, FilmCategoryId> {

    FilmCategory_PersistenceIT() {
        super(FilmCategory.class, FilmCategoryId.class);
    }

    // ---------------------------------------------------------------------------------------------

//    /**
//     * Does nothing; {@link FilmCategory} is mapped with an
//     * {@link jakarta.persistence.IdClass IdClass}, which the generic test does not assemble.
//     */
//    @Disabled("mapped with an @IdClass, which the generic test does not assemble")
//    @Override
//    @Test
//    void find_RandomlySelected_ContextCleared() {
//        // empty
//    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The two basic attributes, one written and one read-only, are the
     *         identifiers of the film and of the category, lazy associations which resolve; and
     *         the {@link FilmCategoryId} built from them, a record, is the identifier the provider
     *         gives, and finds the same row with an entity manager of its own.
     */
    @Override
    protected void randomEntityInstanceSelected__(final EntityManagerFactory entityManagerFactory,
                                                  final EntityManager entityManager,
                                                  final FilmCategory instance) {
        assertThat(instance.getFilmId())
                .as("film id of %s", instance)
                .isEqualTo(instance.getFilm().getFilmId());
        assertThat(instance.getCategoryId())
                .as("category id of %s", instance)
                .isEqualTo(instance.getCategory().getCategoryId());
        final var id = FilmCategoryId.from(instance.getFilm(), instance.getCategory());
        assertThat(entityManagerFactory.getPersistenceUnitUtil().getIdentifier(instance))
                .as("identifier of %s", instance)
                .isEqualTo(id);
        final var other = entityManagerFactory.createEntityManager();
        try {
            final var found = other.find(FilmCategory.class, id);
            assertThat(found)
                    .as("found by %s", id)
                    .isNotNull()
                    .isNotSameAs(instance);
            assertThat(found.getFilmId()).isEqualTo(id.filmId());
            assertThat(found.getCategoryId()).isEqualTo(id.category());
        } finally {
            other.close();
        }
    }
}
