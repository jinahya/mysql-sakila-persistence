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
 * Answers the exercises of {@code src/test/sql/exercises/customer_list_exercises_03_advanced.sql}, against
 * the installed {@code sakila} database; a nested class per exercise, which answers it with JPQL
 * and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class CustomerList_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("SAKILA_CUSTOMER_LIST_A_01: the three countries with the most customers of each store")
    @Nested
    class SAKILA_CUSTOMER_LIST_A_01_Test {

        // JPQL has no window functions, nor a subquery in the FROM clause; ROW_NUMBER() OVER
        // (PARTITION BY ...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT SID, country, customers
                                                                                                      FROM (SELECT SID, country, COUNT(*) AS customers,
                                                                                                                   ROW_NUMBER() OVER (PARTITION BY SID ORDER BY COUNT(*) DESC, country) AS n
                                                                                                            FROM customer_list
                                                                                                            GROUP BY SID, country) t
                                                                                                      WHERE n <= 3
                                                                                                      ORDER BY SID, n""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> ((Object[]) r)[1],
                                r -> ((Number) ((Object[]) r)[2]).longValue())
                    .containsExactly(
                            tuple(1, "India", 37L),
                            tuple(1, "China", 26L),
                            tuple(1, "United States", 22L),
                            tuple(2, "China", 27L),
                            tuple(2, "India", 23L),
                            tuple(2, "Mexico", 15L));
        }
    }
}
