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
 * A class for testing the {@link FilmCategory} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmCategory_Test
        extends _Entity_Test<FilmCategory> {

    FilmCategory_Test() {
        super(FilmCategory.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link FilmCategory#of(Film, Category)}.
     */
    @DisplayName("of(film, category)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link FilmCategory#of(Film, Category) of} returns an instance whose
         * {@value FilmCategory#ATTRIBUTE_NAME_FILM} and
         * {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY} are the very instances it was given, and
         * whose {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} is the identifier of the film; the
         * read-only {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID} is left {@code null}, to be
         * read from the database.
         */
        @Test
        void __() {
            final var film = Mockito.mock(Film.class);
            Mockito.when(film.getFilmId()).thenReturn(1);
            final var category = Mockito.mock(Category.class);
            final var instance = FilmCategory.of(film, category);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getFilm()).isSameAs(film);
                assertThat(v.getCategory()).isSameAs(category);
                assertThat(v.getFilmId()).isEqualTo(1);
                assertThat(v.getCategoryId()).isNull();
            });
        }
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link FilmCategory#equals(Object) equals} compares only the two values of
     *         the composite key, the {@value FilmCategory#ATTRIBUTE_NAME_FILM_ID} and the
     *         {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID}; the latter is not an {@code @Id}
     *         attribute but the read-only mirror of the
     *         {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY}, the {@code @Id} association, so the
     *         key is a mix of {@code @Id} and non-{@code @Id} fields.
     *         <p>
     *         The {@value FilmCategory#ATTRIBUTE_NAME_CATEGORY_ID} is declared non-null to the
     *         verifier, though it is {@code null} until the instance is read from the database: an
     *         instance without the whole key equals nothing but itself, so a {@code null} there
     *         hides the field from the verifier's check of which fields equality uses.
     */
    @Override
    public SingleTypeEqualsVerifierApi<FilmCategory> equalsVerifier() {
        return super.equalsVerifier()
                .suppress(Warning.SURROGATE_OR_BUSINESS_KEY,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE)
                .withIgnoredFields(FilmCategory.ATTRIBUTE_NAME_FILM,
                                   FilmCategory.ATTRIBUTE_NAME_CATEGORY,
                                   FilmCategory.ATTRIBUTE_NAME_LAST_UPDATE)
                .withNonnullFields(FilmCategory.ATTRIBUTE_NAME_CATEGORY_ID);
    }
}
