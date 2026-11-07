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
import org.junit.jupiter.api.Test;

import static com.github.jinahya.mysql.sakila.persistence.___PersistenceIT_Utils.applyEntityManagerFactory;
import static com.github.jinahya.persistence.test.util.EntityPersisterUtils.newPersistedInstanceOf;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerFactoryUtils.applyEntityManager;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerUtils.applyInTransactionAndRollback;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Verifies the mappings of {@link Film} against the installed {@code sakila} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Film_PersistenceIT
        extends _Entity_PersistenceIT<Film, Integer> {

    Film_PersistenceIT() {
        super(Film.class, Integer.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@code ins_film} trigger inserted a {@link FilmText} for the persisted
     * film.
     *
     * @param entityManager  the entity manager the film was persisted with.
     * @param entityInstance the persisted, and flushed, film.
     * @implNote The {@link FilmText} has the film's {@code filmId}, and its title and
     *         description are the film's.
     */
    @Override
    protected void randomizedEntityInstancePersisted__(
            final EntityManagerFactory entityManagerFactory,
            final EntityManager entityManager,
            final Film entityInstance) {
        final var filmText = entityManager.find(FilmText.class, entityInstance.getFilmId());
        assertThat(filmText).as("film text of %s", entityInstance).isNotNull();
        assertThat(filmText.getTitle()).as("title of %s", filmText)
                .isEqualTo(entityInstance.getTitle());
        assertThat(filmText.getDescription()).as("description of %s", filmText)
                .isEqualTo(entityInstance.getDescription());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote Reading the instance at all already proves that
     *         {@link Film.RatingConverter} knows the installed rating; this also checks that the
     *         installed special features are members {@link Film.SpecialFeature} knows, and that
     *         the language, a lazy association, resolves.
     */
    @Override
    protected void randomEntityInstanceSelected__(final EntityManagerFactory entityManagerFactory,
                                                  final EntityManager entityManager,
                                                  final Film instance) {
        assertThatCode(() -> Film.SpecialFeature.parse(instance.getSpecialFeatures()))
                .as("special features of %s", instance)
                .doesNotThrowAnyException();
        assertThat(instance.getLanguage().getName())
                .as("name of the language of %s", instance)
                .isNotBlank();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@code upd_film} trigger updates the {@link FilmText} of an updated film.
     *
     * @implNote The {@link FilmText} is read only after the update is flushed, so it comes
     *         from the database, not from the persistence context.
     */
    @Test
    void _update_FilmTextUpdated() {
        applyEntityManagerFactory(emf -> applyEntityManager(
                emf,
                m -> applyInTransactionAndRollback(m, em -> {
                    final var v = newPersistedInstanceOf(em, entityClass);
                    em.flush();
                    v.setTitle("UPDATED " + v.getFilmId());
                    v.setDescription("updated description of " + v.getFilmId());
                    em.flush();
                    final var filmText = em.find(FilmText.class, v.getFilmId());
                    assertThat(filmText).as("film text of %s", v).isNotNull();
                    assertThat(filmText.getTitle()).as("title of %s", filmText)
                            .isEqualTo(v.getTitle());
                    assertThat(filmText.getDescription()).as("description of %s", filmText)
                            .isEqualTo(v.getDescription());
                    return null;
                })
        ));
    }

    /**
     * Verifies that the {@code del_film} trigger deletes the {@link FilmText} of a deleted film.
     */
    @Test
    void _delete_FilmTextDeleted() {
        applyEntityManagerFactory(emf -> applyEntityManager(
                emf,
                m -> applyInTransactionAndRollback(m, em -> {
                    final var v = newPersistedInstanceOf(em, entityClass);
                    em.flush();
                    final var filmId = v.getFilmId();
                    em.remove(v);
                    em.flush();
                    assertThat(em.find(FilmText.class, filmId))
                            .as("film text of the deleted film %s", filmId)
                            .isNull();
                    return null;
                })
        ));
    }
}
