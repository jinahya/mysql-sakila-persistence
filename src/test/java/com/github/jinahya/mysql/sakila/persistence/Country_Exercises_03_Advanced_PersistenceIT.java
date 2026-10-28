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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/exercises/country_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Country_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_COUNTRY_A_01: the fewest countries which hold half of the customers")
    @Nested
    class SAKILA_COUNTRY_A_01_Test {

        // JPQL has no window functions, nor a subquery in the FROM clause; SUM() OVER (ORDER BY
        // ...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH counted AS (SELECT co.country, COUNT(*) AS customers
                                                                                                                       FROM country co
                                                                                                                                JOIN city ci ON ci.country_id = co.country_id
                                                                                                                                JOIN address a ON a.city_id = ci.city_id
                                                                                                                                JOIN customer cu ON cu.address_id = a.address_id
                                                                                                                       GROUP BY co.country_id, co.country),
                                                                                                           running AS (SELECT counted.*,
                                                                                                                              SUM(customers) OVER (ORDER BY customers DESC, country) AS running_total,
                                                                                                                              SUM(customers) OVER () AS total
                                                                                                                       FROM counted)
                                                                                                      SELECT country, customers, running_total, ROUND(100 * running_total / total, 2) AS running_percent
                                                                                                      FROM running
                                                                                                      WHERE running_total - customers < total / 2
                                                                                                      ORDER BY customers DESC, country""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[2]).longValue())
                    .containsExactly(
                            tuple("India", 60L),
                            tuple("China", 113L),
                            tuple("United States", 149L),
                            tuple("Japan", 180L),
                            tuple("Mexico", 210L),
                            tuple("Brazil", 238L),
                            tuple("Russian Federation", 266L),
                            tuple("Philippines", 286L),
                            tuple("Turkey", 301L));
        }
    }

    @DisplayName(
            "SAKILA_COUNTRY_A_02: the countries ranked in the top ten by customers, ties sharing a rank")
    @Nested
    class SAKILA_COUNTRY_A_02_Test {

        // JPQL has no window functions, nor a subquery in the FROM clause; RANK() OVER (ORDER BY
        // ...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT country, customers, ranking
                                                                                                      FROM (SELECT co.country, COUNT(*) AS customers, RANK() OVER (ORDER BY COUNT(*) DESC) AS ranking
                                                                                                            FROM country co
                                                                                                                     JOIN city ci ON ci.country_id = co.country_id
                                                                                                                     JOIN address a ON a.city_id = ci.city_id
                                                                                                                     JOIN customer cu ON cu.address_id = a.address_id
                                                                                                            GROUP BY co.country_id, co.country) t
                                                                                                      WHERE ranking <= 10
                                                                                                      ORDER BY ranking, country""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[2]).intValue())
                    .containsExactly(
                            tuple("India", 1),
                            tuple("China", 2),
                            tuple("United States", 3),
                            tuple("Japan", 4),
                            tuple("Mexico", 5),
                            tuple("Brazil", 6),
                            tuple("Russian Federation", 6),
                            tuple("Philippines", 8),
                            tuple("Turkey", 9),
                            tuple("Indonesia", 10));
        }
    }
}
