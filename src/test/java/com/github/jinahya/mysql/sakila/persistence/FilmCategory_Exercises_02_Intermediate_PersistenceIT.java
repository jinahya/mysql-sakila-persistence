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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/film_category_exercises_02_intermediate.sql},
 * against the installed {@code sakila} database; a nested class per exercise, which answers it with
 * JPQL and with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FilmCategory_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the films with more than one category")
    @Nested
    class Exercise1Test {

        @ParameterizedTest
        @ValueSource(longs = {0, 1})
        void __QueryLanguage(final long count) {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT fc.filmId, COUNT(fc)
                    FROM FilmCategory fc
                    GROUP BY fc.filmId
                    HAVING COUNT(fc) > :count""", Object[].class)
                    .setParameter("count", count)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).allSatisfy(r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
        }

        @ParameterizedTest
        @ValueSource(longs = {0, 1})
        void __CriteriaApi(final long count) {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var fc = cq.from(FilmCategory.class);
                cq.select(cb.array(fc.get(FilmCategory_.filmId), cb.count(fc)))
                        .groupBy(fc.get(FilmCategory_.filmId))
                        .having(cb.gt(cb.count(fc), cb.parameter(Long.class, "count")));
                return em.createQuery(cq)
                        .setParameter("count", count)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).allSatisfy(r -> assertThat(((Number) r[1]).longValue()).isGreaterThan(count));
        }
    }

    @DisplayName("exercise 2: how many films have no category")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT COUNT(f)
                    FROM Film f
                    WHERE f.filmCategories IS EMPTY""", Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var f = cq.from(Film.class);
                cq.select(cb.count(f))
                        .where(cb.isEmpty(f.get(Film_.filmCategories)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }
    }
}
