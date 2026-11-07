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
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

/**
 * A class for testing the {@link FilmCategoryId} composite primary key class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FilmCategoryId_Test
        extends _NonEntity_Test<FilmCategoryId> {

    FilmCategoryId_Test() {
        super(FilmCategoryId.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link FilmCategoryId#of(Integer, Integer)}.
     */
    @DisplayName("of(filmId, category)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link FilmCategoryId#of(Integer, Integer) of} returns an instance of the
         * values it was given.
         */
        @Test
        void __() {
            final var instance = FilmCategoryId.of(1, 2);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.filmId()).isEqualTo(1);
                assertThat(v.category()).isEqualTo(2);
            });
        }

        /**
         * Verifies that {@link FilmCategoryId#of(Integer, Integer) of} takes {@code null} values.
         */
        @Test
        void __null() {
            final var instance = FilmCategoryId.of(null, null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.filmId()).isNull();
                assertThat(v.category()).isNull();
            });
        }
    }

    /**
     * Tests {@link FilmCategoryId#from(Film, Category)}.
     */
    @DisplayName("from(film, category)")
    @Nested
    class FromFilmCategoryTest {

        /**
         * Verifies that {@link FilmCategoryId#from(Film, Category) from} returns an instance of the
         * identifiers of the film and the category.
         */
        @Test
        void __() {
            final var film = Mockito.mock(Film.class);
            doReturn(1).when(film).getFilmId();
            final var category = Mockito.mock(Category.class);
            doReturn(2).when(category).getCategoryId();
            final var instance = FilmCategoryId.from(film, category);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.filmId()).isEqualTo(1);
                assertThat(v.category()).isEqualTo(2);
            });
        }

        /**
         * Verifies that {@link FilmCategoryId#from(Film, Category) from} gives {@code null} values
         * for a {@code null} film and a {@code null} category.
         */
        @Test
        void __null() {
            final var instance = FilmCategoryId.from(null, null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.filmId()).isNull();
                assertThat(v.category()).isNull();
            });
        }
    }

    /**
     * Tests {@link FilmCategoryId#from(FilmCategory)}.
     */
    @DisplayName("from(filmCategory)")
    @Nested
    class FromFilmCategoryEntityTest {

        /**
         * Verifies that {@link FilmCategoryId#from(FilmCategory) from} returns an instance of the
         * identifiers of the film and the category of the entity.
         */
        @Test
        void __() {
            final var film = Mockito.mock(Film.class);
            doReturn(1).when(film).getFilmId();
            final var category = Mockito.mock(Category.class);
            doReturn(2).when(category).getCategoryId();
            final var filmCategory = Mockito.mock(FilmCategory.class);
            doReturn(film).when(filmCategory).getFilm();
            doReturn(category).when(filmCategory).getCategory();
            final var instance = FilmCategoryId.from(filmCategory);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.filmId()).isEqualTo(1);
                assertThat(v.category()).isEqualTo(2);
            });
        }

        /**
         * Verifies that {@link FilmCategoryId#from(FilmCategory) from} gives {@code null} values
         * for an entity whose film and category are {@code null}.
         */
        @Test
        void __nullAssociations() {
            final var filmCategory = Mockito.mock(FilmCategory.class);
            final var instance = FilmCategoryId.from(filmCategory);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.filmId()).isNull();
                assertThat(v.category()).isNull();
            });
        }

        /**
         * Verifies that {@link FilmCategoryId#from(FilmCategory) from} gives {@code null} values
         * for a {@code null} entity.
         */
        @Test
        void __null() {
            final var instance = FilmCategoryId.from(null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.filmId()).isNull();
                assertThat(v.category()).isNull();
            });
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implNote The record has no constructor with no parameters, so this goes through the
     *         canonical constructor.
     */
    @Override
    public FilmCategoryId newTargetInstance() {
        return new FilmCategoryId(null, null);
    }
}
