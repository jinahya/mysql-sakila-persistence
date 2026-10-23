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
 * Answers the exercises of {@code src/test/sql/film_category_exercises_03_advanced.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmCategory_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("exercise 1: the longest film of each category")
    @Nested
    class Exercise1Test {

        // JPQL has no window functions; the longest film of a category is the one which no film of
        // the category outlasts, nor equals with a smaller id
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.name, f.filmId, f.title, f.length
                                                                                      FROM FilmCategory fc
                                                                                               JOIN fc.category c
                                                                                               JOIN fc.film f
                                                                                      WHERE NOT EXISTS (SELECT fc2
                                                                                                        FROM FilmCategory fc2
                                                                                                                 JOIN fc2.film f2
                                                                                                        WHERE fc2.category = fc.category
                                                                                                          AND (f2.length > f.length
                                                                                                               OR (f2.length = f.length AND f2.filmId < f.filmId)))
                                                                                      ORDER BY c.name""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).intValue(), r -> r[2],
                                r -> ((Number) r[3]).intValue())
                    .containsExactly(
                            tuple("Action", 212, "DARN FORRESTER", 185),
                            tuple("Animation", 349, "GANGS PRIDE", 185),
                            tuple("Children", 344, "FURY MURDER", 178),
                            tuple("Classics", 180, "CONSPIRACY SPIRIT", 184),
                            tuple("Comedy", 182, "CONTROL ANTHEM", 185),
                            tuple("Documentary", 973, "WIFE TURN", 183),
                            tuple("Drama", 473, "JACKET FRISCO", 181),
                            tuple("Family", 499, "KING EVOLUTION", 184),
                            tuple("Foreign", 198, "CRYSTAL BREAKING", 184),
                            tuple("Games", 141, "CHICAGO NORTH", 185),
                            tuple("Horror", 24, "ANALYZE HOOSIERS", 181),
                            tuple("Music", 426, "HOME PITY", 185),
                            tuple("New", 340, "FRONTIER CABIN", 183),
                            tuple("Sci-Fi", 817, "SOLDIERS EVOLUTION", 185),
                            tuple("Sports", 813, "SMOOCHY CONTROL", 184),
                            tuple("Travel", 609, "MUSCLE BRIGHT", 185));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fc = cq.from(FilmCategory.class);
                final var c = fc.join(FilmCategory_.category);
                final var f = fc.join(FilmCategory_.film);
                final var longer = cq.subquery(FilmCategory.class);
                final var fc2 = longer.from(FilmCategory.class);
                final var f2 = fc2.join(FilmCategory_.film);
                longer.select(fc2)
                        .where(cb.equal(fc2.get(FilmCategory_.category),
                                        fc.get(FilmCategory_.category)),
                               cb.or(cb.gt(f2.get(Film_.length), f.get(Film_.length)),
                                     cb.and(cb.equal(f2.get(Film_.length), f.get(Film_.length)),
                                            cb.lt(f2.get(Film_.filmId), f.get(Film_.filmId)))));
                cq.select(cb.array(c.get(Category_.name), f.get(Film_.filmId), f.get(Film_.title),
                                   f.get(Film_.length)))
                        .where(cb.not(cb.exists(longer)))
                        .orderBy(cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).intValue(), r -> r[2],
                                r -> ((Number) r[3]).intValue())
                    .containsExactly(
                            tuple("Action", 212, "DARN FORRESTER", 185),
                            tuple("Animation", 349, "GANGS PRIDE", 185),
                            tuple("Children", 344, "FURY MURDER", 178),
                            tuple("Classics", 180, "CONSPIRACY SPIRIT", 184),
                            tuple("Comedy", 182, "CONTROL ANTHEM", 185),
                            tuple("Documentary", 973, "WIFE TURN", 183),
                            tuple("Drama", 473, "JACKET FRISCO", 181),
                            tuple("Family", 499, "KING EVOLUTION", 184),
                            tuple("Foreign", 198, "CRYSTAL BREAKING", 184),
                            tuple("Games", 141, "CHICAGO NORTH", 185),
                            tuple("Horror", 24, "ANALYZE HOOSIERS", 181),
                            tuple("Music", 426, "HOME PITY", 185),
                            tuple("New", 340, "FRONTIER CABIN", 183),
                            tuple("Sci-Fi", 817, "SOLDIERS EVOLUTION", 185),
                            tuple("Sports", 813, "SMOOCHY CONTROL", 184),
                            tuple("Travel", 609, "MUSCLE BRIGHT", 185));
        }
    }

    @DisplayName("exercise 2: the pairs of categories which have the same number of films")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c1.name, c2.name, COUNT(fc1)
                                                                                      FROM Category c1
                                                                                               JOIN c1.filmCategories fc1,
                                                                                           Category c2
                                                                                      WHERE c1.name < c2.name
                                                                                      GROUP BY c1.categoryId, c1.name, c2.categoryId, c2.name
                                                                                      HAVING COUNT(fc1) = (SELECT COUNT(fc2) FROM FilmCategory fc2 WHERE fc2.category = c2)
                                                                                      ORDER BY COUNT(fc1) DESC, c1.name, c2.name""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Games", "Sci-Fi", 61L),
                            tuple("Classics", "Travel", 57L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c1 = cq.from(Category.class);
                final var fc1 = c1.join(Category_.filmCategories);
                final var c2 = cq.from(Category.class);
                final var films2 = cq.subquery(Long.class);
                final var fc2 = films2.from(FilmCategory.class);
                films2.select(cb.count(fc2))
                        .where(cb.equal(fc2.get(FilmCategory_.category), c2));
                cq.select(cb.array(c1.get(Category_.name), c2.get(Category_.name), cb.count(fc1)))
                        .where(cb.lessThan(c1.get(Category_.name), c2.get(Category_.name)))
                        .groupBy(c1.get(Category_.categoryId), c1.get(Category_.name),
                                 c2.get(Category_.categoryId), c2.get(Category_.name))
                        .having(cb.equal(cb.count(fc1), films2))
                        .orderBy(cb.desc(cb.count(fc1)), cb.asc(c1.get(Category_.name)),
                                 cb.asc(c2.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Games", "Sci-Fi", 61L),
                            tuple("Classics", "Travel", 57L));
        }
    }
}
