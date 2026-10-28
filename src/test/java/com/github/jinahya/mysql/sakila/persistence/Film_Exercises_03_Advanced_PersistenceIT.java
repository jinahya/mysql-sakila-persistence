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
 * Answers the exercises of {@code src/test/sql/exercises/film_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Film_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_FILM_A_01: how many films of each rating are longer than their rating's average")
    @Nested
    class SAKILA_FILM_A_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f1.rating, COUNT(f1)
                                                                                      FROM Film f1
                                                                                      WHERE f1.length > (SELECT AVG(f2.length) FROM Film f2 WHERE f2.rating = f1.rating)
                                                                                      GROUP BY f1.rating
                                                                                      ORDER BY f1.rating""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 83L),
                            tuple(Film.Rating.PG, 99L),
                            tuple(Film.Rating.PG_13, 118L),
                            tuple(Film.Rating.R, 94L),
                            tuple(Film.Rating.NC_17, 103L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f1 = cq.from(Film.class);
                final var average = cq.subquery(Double.class);
                final var f2 = average.from(Film.class);
                average.select(cb.avg(f2.get(Film_.length)))
                        .where(cb.equal(f2.get(Film_.rating), f1.get(Film_.rating)));
                cq.select(cb.array(f1.get(Film_.rating), cb.count(f1)))
                        .where(cb.gt(f1.get(Film_.length), average))
                        .groupBy(f1.get(Film_.rating))
                        .orderBy(cb.asc(f1.get(Film_.rating)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 83L),
                            tuple(Film.Rating.PG, 99L),
                            tuple(Film.Rating.PG_13, 118L),
                            tuple(Film.Rating.R, 94L),
                            tuple(Film.Rating.NC_17, 103L));
        }
    }
}
