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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/city_exercises_01_basic.sql}, against the installed
 * {@code sakila} database; a nested class per exercise, which answers it with JPQL and with the
 * Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class City_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: the cities whose name starts with Q")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(strings = {"Q", "X", "Z"})
        void __QueryLanguage(final String letter) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT c
                    FROM City c
                    WHERE c.city LIKE :pattern
                    ORDER BY c.city""", City.class)
                    .setParameter("pattern", letter + "%")
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).allSatisfy(c -> assertThat(c.getCity()).startsWith(letter));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Q", "X", "Z"})
        void __CriteriaApi(final String letter) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(City.class);
                final var c = cq.from(City.class);
                cq.select(c)
                        .where(cb.like(c.get(City_.city), cb.parameter(String.class, "pattern")))
                        .orderBy(cb.asc(c.get(City_.city)));
                return em.createQuery(cq)
                        .setParameter("pattern", letter + "%")
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).allSatisfy(c -> assertThat(c.getCity()).startsWith(letter));
        }
    }
}
