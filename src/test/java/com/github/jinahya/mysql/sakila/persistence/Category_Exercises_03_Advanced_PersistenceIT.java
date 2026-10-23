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
 * Answers the exercises of {@code src/test/sql/category_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Category_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("exercise 1: the running total of the films, category by category")
    @Nested
    class Exercise1Test {

        // JPQL has no window functions; a category's running total counts the films of every
        // category at or before it, the most films first, then by name
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c,
                                                   (SELECT COUNT(fc1) FROM FilmCategory fc1 WHERE fc1.category = c),
                                                   COUNT(fc),
                                                   ROUND(COUNT(fc) * 100.0 / (SELECT COUNT(fc2) FROM FilmCategory fc2), 1)
                                            FROM Category c, FilmCategory fc
                                                     JOIN fc.category c2
                                            WHERE SIZE(c2.filmCategories) > SIZE(c.filmCategories)
                                               OR (SIZE(c2.filmCategories) = SIZE(c.filmCategories) AND c2.name <= c.name)
                                            GROUP BY c
                                            ORDER BY COUNT(fc)""",
                                    Object[].class
                            )
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue(),
                                r -> ((Number) r[3]).doubleValue())
                    .containsExactly(
                            tuple("Sports", 74L, 74L, 7.4d),
                            tuple("Foreign", 73L, 147L, 14.7d),
                            tuple("Family", 69L, 216L, 21.6d),
                            tuple("Documentary", 68L, 284L, 28.4d),
                            tuple("Animation", 66L, 350L, 35.0d),
                            tuple("Action", 64L, 414L, 41.4d),
                            tuple("New", 63L, 477L, 47.7d),
                            tuple("Drama", 62L, 539L, 53.9d),
                            tuple("Games", 61L, 600L, 60.0d),
                            tuple("Sci-Fi", 61L, 661L, 66.1d),
                            tuple("Children", 60L, 721L, 72.1d),
                            tuple("Comedy", 58L, 779L, 77.9d),
                            tuple("Classics", 57L, 836L, 83.6d),
                            tuple("Travel", 57L, 893L, 89.3d),
                            tuple("Horror", 56L, 949L, 94.9d),
                            tuple("Music", 51L, 1000L, 100.0d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Category.class);
                final var fc = cq.from(FilmCategory.class);
                final var c2 = fc.join(FilmCategory_.category);
                final var films = cq.subquery(Long.class);
                final var fc1 = films.from(FilmCategory.class);
                films.select(cb.count(fc1)).where(cb.equal(fc1.get(FilmCategory_.category), c));
                final var all = cq.subquery(Long.class);
                all.select(cb.count(all.from(FilmCategory.class)));
                final var size = cb.size(c.get(Category_.filmCategories));
                final var size2 = cb.size(c2.get(Category_.filmCategories));
                // eclipselink selects no bare subquery (a ClassCastException); it does select a
                // COALESCE of one
                cq.select(cb.array(c, cb.coalesce(films, 0L), cb.count(fc),
                                   cb.round(cb.quot(cb.prod(cb.count(fc), 100.0), all), 1)))
                        .where(cb.or(cb.gt(size2, size),
                                     cb.and(cb.equal(size2, size),
                                            cb.lessThanOrEqualTo(c2.get(Category_.name),
                                                                 c.get(Category_.name)))))
                        .groupBy(c)
                        .orderBy(cb.asc(cb.count(fc)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Number) r[1]).longValue(),
                                r -> ((Number) r[2]).longValue(),
                                r -> ((Number) r[3]).doubleValue())
                    .containsExactly(
                            tuple("Sports", 74L, 74L, 7.4d),
                            tuple("Foreign", 73L, 147L, 14.7d),
                            tuple("Family", 69L, 216L, 21.6d),
                            tuple("Documentary", 68L, 284L, 28.4d),
                            tuple("Animation", 66L, 350L, 35.0d),
                            tuple("Action", 64L, 414L, 41.4d),
                            tuple("New", 63L, 477L, 47.7d),
                            tuple("Drama", 62L, 539L, 53.9d),
                            tuple("Games", 61L, 600L, 60.0d),
                            tuple("Sci-Fi", 61L, 661L, 66.1d),
                            tuple("Children", 60L, 721L, 72.1d),
                            tuple("Comedy", 58L, 779L, 77.9d),
                            tuple("Classics", 57L, 836L, 83.6d),
                            tuple("Travel", 57L, 893L, 89.3d),
                            tuple("Horror", 56L, 949L, 94.9d),
                            tuple("Music", 51L, 1000L, 100.0d));
        }
    }

    @DisplayName("exercise 2: the film rented the most in each category")
    @Nested
    class Exercise2Test {

        // JPQL has no window functions; the film is rented no less than any other film of the
        // category, and more than any film before it by title
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c, f, COUNT(r)
                                            FROM Category c
                                                     JOIN c.filmCategories fc
                                                     JOIN fc.film f
                                                     JOIN f.inventories i
                                                     JOIN i.rentals r
                                            GROUP BY c, f
                                            HAVING COUNT(r) >= ALL (SELECT COUNT(r2)
                                                                    FROM FilmCategory fc2
                                                                             JOIN fc2.film f2
                                                                             JOIN f2.inventories i2
                                                                             JOIN i2.rentals r2
                                                                    WHERE fc2.category = c
                                                                    GROUP BY f2.filmId)
                                               AND COUNT(r) > ALL (SELECT COUNT(r3)
                                                                   FROM FilmCategory fc3
                                                                            JOIN fc3.film f3
                                                                            JOIN f3.inventories i3
                                                                            JOIN i3.rentals r3
                                                                   WHERE fc3.category = c
                                                                     AND f3.title < f.title
                                                                   GROUP BY f3.filmId)
                                            ORDER BY c.name""",
                                    Object[].class
                            )
                            .getResultList()
            );
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Film) r[1]).getTitle(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Action", "RUGRATS SHAKESPEARE", 30L),
                            tuple("Animation", "JUGGLER HARDLY", 32L),
                            tuple("Children", "ROBBERS JOON", 31L),
                            tuple("Classics", "TIMBERLAND SKY", 31L),
                            tuple("Comedy", "ZORRO ARK", 31L),
                            tuple("Documentary", "WIFE TURN", 31L),
                            tuple("Drama", "HOBBIT ALIEN", 31L),
                            tuple("Family", "APACHE DIVINE", 31L),
                            tuple("Foreign", "ROCKETEER MOTHER", 33L),
                            tuple("Games", "FORWARD TEMPLE", 32L),
                            tuple("Horror", "PULP BEVERLY", 30L),
                            tuple("Music", "SCALAWAG DUCK", 32L),
                            tuple("New", "RIDGEMONT SUBMARINE", 32L),
                            tuple("Sci-Fi", "GOODFELLAS SALUTE", 31L),
                            tuple("Sports", "GLEAMING JAWBREAKER", 29L),
                            tuple("Travel", "BUCKET BROTHERHOOD", 34L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Category.class);
                final var f = c.join(Category_.filmCategories).join(FilmCategory_.film);
                final var r = f.join(Film_.inventories).join(Inventory_.rentals);
                final var others = cq.subquery(Long.class);
                final var fc2 = others.from(FilmCategory.class);
                final var f2 = fc2.join(FilmCategory_.film);
                others.select(cb.count(f2.join(Film_.inventories).join(Inventory_.rentals)))
                        .where(cb.equal(fc2.get(FilmCategory_.category), c))
                        .groupBy(f2.get(Film_.filmId));
                final var before = cq.subquery(Long.class);
                final var fc3 = before.from(FilmCategory.class);
                final var f3 = fc3.join(FilmCategory_.film);
                before.select(cb.count(f3.join(Film_.inventories).join(Inventory_.rentals)))
                        .where(cb.equal(fc3.get(FilmCategory_.category), c),
                               cb.lessThan(f3.get(Film_.title), f.get(Film_.title)))
                        .groupBy(f3.get(Film_.filmId));
                cq.select(cb.array(c, f, cb.count(r)))
                        .groupBy(c, f)
                        .having(cb.ge(cb.count(r), cb.all(others)),
                                cb.gt(cb.count(r), cb.all(before)))
                        .orderBy(cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Category) r[0]).getName(), r -> ((Film) r[1]).getTitle(),
                                r -> ((Number) r[2]).longValue())
                    .containsExactly(
                            tuple("Action", "RUGRATS SHAKESPEARE", 30L),
                            tuple("Animation", "JUGGLER HARDLY", 32L),
                            tuple("Children", "ROBBERS JOON", 31L),
                            tuple("Classics", "TIMBERLAND SKY", 31L),
                            tuple("Comedy", "ZORRO ARK", 31L),
                            tuple("Documentary", "WIFE TURN", 31L),
                            tuple("Drama", "HOBBIT ALIEN", 31L),
                            tuple("Family", "APACHE DIVINE", 31L),
                            tuple("Foreign", "ROCKETEER MOTHER", 33L),
                            tuple("Games", "FORWARD TEMPLE", 32L),
                            tuple("Horror", "PULP BEVERLY", 30L),
                            tuple("Music", "SCALAWAG DUCK", 32L),
                            tuple("New", "RIDGEMONT SUBMARINE", 32L),
                            tuple("Sci-Fi", "GOODFELLAS SALUTE", 31L),
                            tuple("Sports", "GLEAMING JAWBREAKER", 29L),
                            tuple("Travel", "BUCKET BROTHERHOOD", 34L));
        }
    }
}
