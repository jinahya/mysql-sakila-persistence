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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/category_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Category_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: how many films each category has, the most first")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c.name, COUNT(fc)
                                            FROM Category c
                                                     JOIN c.filmCategories fc
                                            GROUP BY c.categoryId, c.name
                                            ORDER BY COUNT(fc) DESC, c.name""",
                                    Object[].class
                            )
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(16);
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .startsWith(tuple("Sports", 74L), tuple("Foreign", 73L), tuple("Family", 69L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Category.class);
                final var fc = c.join(Category_.filmCategories);
                cq.select(cb.array(c.get(Category_.name), cb.count(fc)))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name))
                        .orderBy(cb.desc(cb.count(fc)), cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(16);
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .startsWith(tuple("Sports", 74L), tuple("Foreign", 73L), tuple("Family", 69L));
        }
    }

    @DisplayName("exercise 2: the category whose films are, on average, the longest")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c.name, ROUND(AVG(f.length), 2)
                                            FROM Category c
                                                     JOIN c.filmCategories fc
                                                     JOIN fc.film f
                                            GROUP BY c.categoryId, c.name
                                            ORDER BY AVG(f.length) DESC""",
                                    Object[].class
                            )
                            .setMaxResults(1)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Sports", 128.2d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Category.class);
                final var f = c.join(Category_.filmCategories).join(FilmCategory_.film);
                cq.select(cb.array(c.get(Category_.name), cb.round(cb.avg(f.get(Film_.length)), 2)))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name))
                        .orderBy(cb.desc(cb.avg(f.get(Film_.length))));
                return em.createQuery(cq)
                        .setMaxResults(1)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Sports", 128.2d));
        }
    }

    @DisplayName("exercise 3: the categories with no film")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c
                                            FROM Category c
                                            WHERE c.filmCategories IS EMPTY""",
                                    Category.class
                            )
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Category.class);
                final var c = cq.from(Category.class);
                cq.select(c)
                        .where(cb.isEmpty(c.get(Category_.filmCategories)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isEmpty();
        }
    }

    @DisplayName("exercise 4: the categories whose films rent for more than the average")
    @Nested
    class Exercise4Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c.name, ROUND(AVG(f.rentalRate), 2)
                                            FROM Category c
                                                     JOIN c.filmCategories fc
                                                     JOIN fc.film f
                                            GROUP BY c.categoryId, c.name
                                            HAVING AVG(f.rentalRate) > (SELECT AVG(f2.rentalRate) FROM Film f2)
                                            ORDER BY AVG(f.rentalRate) DESC, c.name""",
                                    Object[].class
                            )
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Games", 3.25d),
                            tuple("Travel", 3.24d),
                            tuple("Sci-Fi", 3.22d),
                            tuple("Comedy", 3.16d),
                            tuple("Sports", 3.13d),
                            tuple("New", 3.12d),
                            tuple("Foreign", 3.10d),
                            tuple("Horror", 3.03d),
                            tuple("Drama", 3.02d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Category.class);
                final var f = c.join(Category_.filmCategories).join(FilmCategory_.film);
                final var average = cq.subquery(Double.class);
                final var f2 = average.from(Film.class);
                average.select(cb.avg(f2.get(Film_.rentalRate)));
                cq.select(cb.array(c.get(Category_.name),
                                   cb.round(cb.avg(f.get(Film_.rentalRate)), 2)))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name))
                        .having(cb.gt(cb.avg(f.get(Film_.rentalRate)), average))
                        .orderBy(cb.desc(cb.avg(f.get(Film_.rentalRate))),
                                 cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Games", 3.25d),
                            tuple("Travel", 3.24d),
                            tuple("Sci-Fi", 3.22d),
                            tuple("Comedy", 3.16d),
                            tuple("Sports", 3.13d),
                            tuple("New", 3.12d),
                            tuple("Foreign", 3.10d),
                            tuple("Horror", 3.03d),
                            tuple("Drama", 3.02d));
        }
    }

    @DisplayName("exercise 5: the five categories with the most different actors")
    @Nested
    class Exercise5Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c.name, COUNT(DISTINCT fa.actor)
                                            FROM Category c
                                                     JOIN c.filmCategories fc
                                                     JOIN fc.film f
                                                     JOIN f.filmActors fa
                                            GROUP BY c.categoryId, c.name
                                            ORDER BY COUNT(DISTINCT fa.actor) DESC, c.name""",
                                    Object[].class
                            )
                            .setMaxResults(5)
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Sports", 182L),
                            tuple("Foreign", 175L),
                            tuple("New", 169L),
                            tuple("Documentary", 168L),
                            tuple("Sci-Fi", 167L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Category.class);
                final var fa = c.join(Category_.filmCategories).join(FilmCategory_.film)
                        .join(Film_.filmActors);
                final var actors = cb.countDistinct(fa.get(FilmActor_.actor));
                cq.select(cb.array(c.get(Category_.name), actors))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name))
                        .orderBy(cb.desc(actors), cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Sports", 182L),
                            tuple("Foreign", 175L),
                            tuple("New", 169L),
                            tuple("Documentary", 168L),
                            tuple("Sci-Fi", 167L));
        }
    }
}
