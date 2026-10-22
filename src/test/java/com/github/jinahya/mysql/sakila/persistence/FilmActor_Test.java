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

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing the {@link FilmActor} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmActor_Test
        extends _Entity_Test<FilmActor> {

    FilmActor_Test() {
        super(FilmActor.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link FilmActor#of(Actor, Film)}.
     */
    @DisplayName("of(actor, film)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link FilmActor#of(Actor, Film) of} returns an instance whose
         * {@value FilmActor#ATTRIBUTE_NAME_ACTOR} and {@value FilmActor#ATTRIBUTE_NAME_FILM} are
         * the very instances it was given.
         */
        @Test
        void __() {
            final var actor = Mockito.mock(Actor.class);
            final var film = Mockito.mock(Film.class);
            final var instance = FilmActor.of(actor, film);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getActor()).isSameAs(actor);
                assertThat(v.getFilm()).isSameAs(film);
            });
        }
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link FilmActor#equals(Object) equals} compares only the
     *         {@code @EmbeddedId}, the composite key, because every other attribute is mutable
     *         state; hence the {@link Warning#SURROGATE_KEY} is suppressed.
     */
    @Override
    public SingleTypeEqualsVerifierApi<FilmActor> equalsVerifier() {
        return super.equalsVerifier()
                .suppress(Warning.SURROGATE_KEY,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
