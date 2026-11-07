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
import com.github.jinahya.mysql.sakila.persistence.Inventory_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_FILM_I_02} of {@code src/test/sql-exercises/film/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_I_02: the five films rented the most")
@Slf4j
class SAKILA_FILM_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT f, COUNT(r)
                                                                                  FROM Film f
                                                                                           JOIN f.inventories i
                                                                                           JOIN i.rentals r
                                                                                  GROUP BY f
                                                                                  ORDER BY COUNT(r) DESC, f.filmId""",
                                                                          Object[].class)
                .setMaxResults(5)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(103, "BUCKET BROTHERHOOD", 34L),
                        tuple(738, "ROCKETEER MOTHER", 33L),
                        tuple(331, "FORWARD TEMPLE", 32L),
                        tuple(382, "GRIT CLOCKWORK", 32L),
                        tuple(489, "JUGGLER HARDLY", 32L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var f = cq.from(Film.class);
            final var r = f.join(Film_.inventories).join(Inventory_.rentals);
            cq.select(cb.array(f, cb.count(r)))
                    .groupBy(f)
                    .orderBy(cb.desc(cb.count(r)), cb.asc(f.get(Film_.filmId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(103, "BUCKET BROTHERHOOD", 34L),
                        tuple(738, "ROCKETEER MOTHER", 33L),
                        tuple(331, "FORWARD TEMPLE", 32L),
                        tuple(382, "GRIT CLOCKWORK", 32L),
                        tuple(489, "JUGGLER HARDLY", 32L));
    }

}
