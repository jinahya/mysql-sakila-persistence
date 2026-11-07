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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link FilmActorId} composite primary key class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmActorId_Test
        extends _NonEntity_Test<FilmActorId> {

    FilmActorId_Test() {
        super(FilmActorId.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link FilmActorId#of(Integer, Integer)}.
     */
    @DisplayName("of(actorId, filmId)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link FilmActorId#of(Integer, Integer) of} returns an instance of the
         * values it was given.
         */
        @Test
        void __() {
            final var instance = FilmActorId.of(1, 2);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActorId()).isEqualTo(1);
                assertThat(v.getFilmId()).isEqualTo(2);
            });
        }

        /**
         * Verifies that {@link FilmActorId#of(Integer, Integer) of} takes {@code null} values.
         */
        @Test
        void __null() {
            final var instance = FilmActorId.of(null, null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActorId()).isNull();
                assertThat(v.getFilmId()).isNull();
            });
        }
    }

    /**
     * Tests {@link FilmActorId#from(Actor, Film)}.
     */
    @DisplayName("from(actor, film)")
    @Nested
    class FromActorFilmTest {

        /**
         * Verifies that {@link FilmActorId#from(Actor, Film) from} returns an instance of the
         * identifiers of the actor and the film.
         */
        @Test
        void __() {
            final var actor = mock(Actor.class);
            doReturn(1).when(actor).getActorId();
            final var film = mock(Film.class);
            doReturn(2).when(film).getFilmId();
            final var instance = FilmActorId.from(actor, film);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActorId()).isEqualTo(1);
                assertThat(v.getFilmId()).isEqualTo(2);
            });
        }

        /**
         * Verifies that {@link FilmActorId#from(Actor, Film) from} gives {@code null} values for a
         * {@code null} actor and a {@code null} film.
         */
        @Test
        void __null() {
            final var instance = FilmActorId.from(null, null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActorId()).isNull();
                assertThat(v.getFilmId()).isNull();
            });
        }
    }

    /**
     * Tests {@link FilmActorId#from(FilmActor)}.
     */
    @DisplayName("from(filmActor)")
    @Nested
    class FromFilmActorTest {

        /**
         * Verifies that {@link FilmActorId#from(FilmActor) from} returns an instance of the
         * identifiers of the actor and the film of the entity.
         */
        @Test
        void __() {
            final var actor = mock(Actor.class);
            doReturn(1).when(actor).getActorId();
            final var film = mock(Film.class);
            doReturn(2).when(film).getFilmId();
            final var filmActor = mock(FilmActor.class);
            doReturn(actor).when(filmActor).getActor();
            doReturn(film).when(filmActor).getFilm();
            final var instance = FilmActorId.from(filmActor);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActorId()).isEqualTo(1);
                assertThat(v.getFilmId()).isEqualTo(2);
            });
        }

        /**
         * Verifies that {@link FilmActorId#from(FilmActor) from} gives {@code null} values for an
         * entity whose actor and film are {@code null}.
         */
        @Test
        void __nullAssociations() {
            final var filmActor = mock(FilmActor.class);
            final var instance = FilmActorId.from(filmActor);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActorId()).isNull();
                assertThat(v.getFilmId()).isNull();
            });
        }

        /**
         * Verifies that {@link FilmActorId#from(FilmActor) from} gives {@code null} values for a
         * {@code null} entity.
         */
        @Test
        void __null() {
            final var instance = FilmActorId.from((FilmActor) null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActorId()).isNull();
                assertThat(v.getFilmId()).isNull();
            });
        }
    }
}
