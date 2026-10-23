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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/film_exercises_01_basic.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Film_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: how many films each rating has")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f.rating, COUNT(f)
                                                                                      FROM Film f
                                                                                      GROUP BY f.rating
                                                                                      ORDER BY f.rating""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 178L),
                            tuple(Film.Rating.PG, 194L),
                            tuple(Film.Rating.PG_13, 223L),
                            tuple(Film.Rating.R, 195L),
                            tuple(Film.Rating.NC_17, 210L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f = cq.from(Film.class);
                cq.select(cb.array(f.get(Film_.rating), cb.count(f)))
                        .groupBy(f.get(Film_.rating))
                        .orderBy(cb.asc(f.get(Film_.rating)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 178L),
                            tuple(Film.Rating.PG, 194L),
                            tuple(Film.Rating.PG_13, 223L),
                            tuple(Film.Rating.R, 195L),
                            tuple(Film.Rating.NC_17, 210L));
        }
    }

    @DisplayName("exercise 2: the films longer than three hours, the longest first")
    @Nested
    class Exercise2Test {

        @ParameterizedTest
        @ValueSource(ints = {170, 180})
        void __QueryLanguage(final int length) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f
                                                                                      FROM Film f
                                                                                      WHERE f.length > :length
                                                                                      ORDER BY f.length DESC, f.title""",
                                                                              Film.class)
                    .setParameter("length", length)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty().hasSizeLessThanOrEqualTo(5)
                    .allSatisfy(f -> assertThat(f.getLength()).isGreaterThan(length));
            assertThat(list).extracting(Film::getLength)
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        @ParameterizedTest
        @ValueSource(ints = {170, 180})
        void __CriteriaApi(final int length) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Film.class);
                final var f = cq.from(Film.class);
                cq.select(f)
                        .where(cb.gt(f.get(Film_.length), cb.parameter(Integer.class, "length")))
                        .orderBy(cb.desc(f.get(Film_.length)), cb.asc(f.get(Film_.title)));
                return em.createQuery(cq)
                        .setParameter("length", length)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).isNotEmpty().hasSizeLessThanOrEqualTo(5)
                    .allSatisfy(f -> assertThat(f.getLength()).isGreaterThan(length));
            assertThat(list).extracting(Film::getLength)
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }
    }

    @DisplayName("exercise 3: the average rental rate of each rating")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f.rating, ROUND(AVG(f.rentalRate), 2)
                                                                                      FROM Film f
                                                                                      GROUP BY f.rating
                                                                                      ORDER BY f.rating""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 2.89d),
                            tuple(Film.Rating.PG, 3.05d),
                            tuple(Film.Rating.PG_13, 3.03d),
                            tuple(Film.Rating.R, 2.94d),
                            tuple(Film.Rating.NC_17, 2.97d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f = cq.from(Film.class);
                cq.select(
                                cb.array(f.get(Film_.rating),
                                         cb.round(cb.avg(f.get(Film_.rentalRate)), 2)))
                        .groupBy(f.get(Film_.rating))
                        .orderBy(cb.asc(f.get(Film_.rating)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 2.89d),
                            tuple(Film.Rating.PG, 3.05d),
                            tuple(Film.Rating.PG_13, 3.03d),
                            tuple(Film.Rating.R, 2.94d),
                            tuple(Film.Rating.NC_17, 2.97d));
        }
    }

    @DisplayName("exercise 4: how many films have Behind the Scenes among their special features")
    @Nested
    class Exercise4Test {

        // JPQL has no FIND_IN_SET; no member of the set contains another, so a LIKE finds the same
        // films
        @ParameterizedTest
        @ValueSource(strings = {"Behind the Scenes", "Trailers", "Commentaries", "Deleted Scenes"})
        void __QueryLanguage(final String feature) {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(f)
                                                                                       FROM Film f
                                                                                       WHERE f.specialFeatures LIKE :pattern""",
                                                                               Long.class)
                    .setParameter("pattern", "%" + feature + "%")
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isPositive();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Behind the Scenes", "Trailers", "Commentaries", "Deleted Scenes"})
        void __CriteriaApi(final String feature) {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var f = cq.from(Film.class);
                cq.select(cb.count(f))
                        .where(cb.like(f.get(Film_.specialFeatures),
                                       cb.parameter(String.class, "pattern")));
                return em.createQuery(cq)
                        .setParameter("pattern", "%" + feature + "%")
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isPositive();
        }
    }

    @DisplayName("exercise 5: the five shortest films")
    @Nested
    class Exercise5Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f.filmId, f.title, f.length
                                                                                      FROM Film f
                                                                                      ORDER BY f.length, f.title""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1],
                                r -> ((Number) r[2]).intValue())
                    .containsExactly(
                            tuple(15, "ALIEN CENTER", 46),
                            tuple(469, "IRON MOON", 46),
                            tuple(504, "KWAI HOMEWARD", 46),
                            tuple(505, "LABYRINTH LEAGUE", 46),
                            tuple(730, "RIDGEMONT SUBMARINE", 46));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f = cq.from(Film.class);
                cq.select(cb.array(f.get(Film_.filmId), f.get(Film_.title), f.get(Film_.length)))
                        .orderBy(cb.asc(f.get(Film_.length)), cb.asc(f.get(Film_.title)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1],
                                r -> ((Number) r[2]).intValue())
                    .containsExactly(
                            tuple(15, "ALIEN CENTER", 46),
                            tuple(469, "IRON MOON", 46),
                            tuple(504, "KWAI HOMEWARD", 46),
                            tuple(505, "LABYRINTH LEAGUE", 46),
                            tuple(730, "RIDGEMONT SUBMARINE", 46));
        }
    }

    @DisplayName("exercise 6: the films cheapest to rent and dearest to replace")
    @Nested
    class Exercise6Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f.filmId, f.title, f.rentalRate, f.replacementCost
                                                                                      FROM Film f
                                                                                      WHERE f.rentalRate = :rentalRate
                                                                                        AND f.replacementCost = :replacementCost
                                                                                      ORDER BY f.title""",
                                                                              Object[].class)
                    .setParameter("rentalRate", new BigDecimal("0.99"))
                    .setParameter("replacementCost", new BigDecimal("29.99"))
                    .setMaxResults(10)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(10)
                    .allSatisfy(r -> {
                        assertThat(((BigDecimal) r[2]).toPlainString()).isEqualTo("0.99");
                        assertThat(((BigDecimal) r[3]).toPlainString()).isEqualTo("29.99");
                    });
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1])
                    .containsExactly(
                            tuple(34, "ARABIA DOGMA"),
                            tuple(52, "BALLROOM MOCKINGBIRD"),
                            tuple(85, "BONNIE HOLOCAUST"),
                            tuple(157, "CLOCKWORK PARADISE"),
                            tuple(163, "CLYDE THEORY"),
                            tuple(196, "CRUELTY UNFORGIVEN"),
                            tuple(270, "EARTH VISION"),
                            tuple(290, "EVERYONE CRAFT"),
                            tuple(309, "FEUD FROGMEN"),
                            tuple(358, "GILMORE BOILED"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f = cq.from(Film.class);
                cq.select(cb.array(f.get(Film_.filmId), f.get(Film_.title), f.get(Film_.rentalRate),
                                   f.get(Film_.replacementCost)))
                        .where(cb.equal(f.get(Film_.rentalRate),
                                        cb.parameter(BigDecimal.class, "rentalRate")),
                               cb.equal(f.get(Film_.replacementCost),
                                        cb.parameter(BigDecimal.class, "replacementCost")))
                        .orderBy(cb.asc(f.get(Film_.title)));
                return em.createQuery(cq)
                        .setParameter("rentalRate", new BigDecimal("0.99"))
                        .setParameter("replacementCost", new BigDecimal("29.99"))
                        .setMaxResults(10)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(10)
                    .allSatisfy(r -> {
                        assertThat(((BigDecimal) r[2]).toPlainString()).isEqualTo("0.99");
                        assertThat(((BigDecimal) r[3]).toPlainString()).isEqualTo("29.99");
                    });
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1])
                    .containsExactly(
                            tuple(34, "ARABIA DOGMA"),
                            tuple(52, "BALLROOM MOCKINGBIRD"),
                            tuple(85, "BONNIE HOLOCAUST"),
                            tuple(157, "CLOCKWORK PARADISE"),
                            tuple(163, "CLYDE THEORY"),
                            tuple(196, "CRUELTY UNFORGIVEN"),
                            tuple(270, "EARTH VISION"),
                            tuple(290, "EVERYONE CRAFT"),
                            tuple(309, "FEUD FROGMEN"),
                            tuple(358, "GILMORE BOILED"));
        }
    }
}
