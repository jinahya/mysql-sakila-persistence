package com.github.jinahya.mysql.sakila.persistence.sakila.schema;

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

import com.github.jinahya.mysql.sakila.persistence.Customer_;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.Inventory_;
import com.github.jinahya.mysql.sakila.persistence.Rental;
import com.github.jinahya.mysql.sakila.persistence.Rental_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_SCHEMA_I_13} of {@code src/test/sql-exercises/__/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_SCHEMA_I_13: the five customer and film pairs rented the most often")
@Slf4j
class SAKILA_SCHEMA_I_13_PersistenceIT {

    // both keep the columns: eclipselink drops GROUP BY/HAVING from a Criteria entity select
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c.customerId, c.firstName, c.lastName, f.filmId, f.title, COUNT(r)
                                                                                  FROM Rental r
                                                                                           JOIN r.customer c
                                                                                           JOIN r.inventory i
                                                                                           JOIN i.film f
                                                                                  GROUP BY c.customerId, c.firstName, c.lastName, f.filmId, f.title
                                                                                  HAVING COUNT(r) > 1
                                                                                  ORDER BY COUNT(r) DESC, c.customerId, f.filmId""",
                                                                          Object[].class)
                .setMaxResults(5)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertRows(list);
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var r = cq.from(Rental.class);
            final var c = r.join(Rental_.customer);
            final var f = r.join(Rental_.inventory).join(Inventory_.film);
            cq.select(cb.array(c.get(Customer_.customerId), c.get(Customer_.firstName),
                               c.get(Customer_.lastName), f.get(Film_.filmId),
                               f.get(Film_.title), cb.count(r)))
                    .groupBy(c.get(Customer_.customerId), c.get(Customer_.firstName),
                             c.get(Customer_.lastName), f.get(Film_.filmId),
                             f.get(Film_.title))
                    .having(cb.gt(cb.count(r), 1L))
                    .orderBy(cb.desc(cb.count(r)), cb.asc(c.get(Customer_.customerId)),
                             cb.asc(f.get(Film_.filmId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertRows(list);
    }

    private static void assertRows(final List<Object[]> list) {
        assertThat(list)
                .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                            r -> ((Number) r[3]).intValue(), r -> r[4],
                            r -> ((Number) r[5]).longValue())
                .containsExactly(
                        tuple(122, "THELMA", "MURRAY", 233, "DISCIPLE MOTHER", 3L),
                        tuple(314, "GEORGE", "LINTON", 111, "CADDYSHACK JEDI", 3L),
                        tuple(317, "EDWARD", "BAUGH", 322, "FLATLINERS KILLER", 3L),
                        tuple(376, "RANDY", "GAITHER", 228, "DETECTIVE VISION", 3L),
                        tuple(1, "MARY", "SMITH", 317, "FIREBALL PHILADELPHIA", 2L));
    }

}
