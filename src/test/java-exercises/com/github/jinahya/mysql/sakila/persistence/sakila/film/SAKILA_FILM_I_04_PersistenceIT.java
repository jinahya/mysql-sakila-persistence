package com.github.jinahya.mysql.sakila.persistence.sakila.film;

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

import com.github.jinahya.mysql.sakila.persistence.Film;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_FILM_I_04} of {@code src/test/sql-exercises/film/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_I_04: the ratings whose films are longer than the average film")
@Slf4j
class SAKILA_FILM_I_04_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT f.rating, ROUND(AVG(f.length), 2)
                                                                                  FROM Film f
                                                                                  GROUP BY f.rating
                                                                                  HAVING AVG(f.length) > (SELECT AVG(f2.length) FROM Film f2)
                                                                                  ORDER BY f.rating""",
                                                                          Object[].class)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                .containsExactly(
                        tuple(Film.Rating.PG_13, 120.44d),
                        tuple(Film.Rating.R, 118.66d));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var f = cq.from(Film.class);
            final var average = cq.subquery(Double.class);
            final var f2 = average.from(Film.class);
            average.select(cb.avg(f2.get(Film_.length)));
            cq.select(cb.array(f.get(Film_.rating), cb.round(cb.avg(f.get(Film_.length)), 2)))
                    .groupBy(f.get(Film_.rating))
                    .having(cb.gt(cb.avg(f.get(Film_.length)), average))
                    .orderBy(cb.asc(f.get(Film_.rating)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                .containsExactly(
                        tuple(Film.Rating.PG_13, 120.44d),
                        tuple(Film.Rating.R, 118.66d));
    }

}
