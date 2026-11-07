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
 * Answers {@code SAKILA_FILM_B_03} of {@code src/test/sql-exercises/film/01_basic.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_B_03: the average rental rate of each rating")
@Slf4j
class SAKILA_FILM_B_03_PersistenceIT {

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
