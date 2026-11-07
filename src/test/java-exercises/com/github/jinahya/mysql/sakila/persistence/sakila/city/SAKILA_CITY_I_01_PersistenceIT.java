package com.github.jinahya.mysql.sakila.persistence.sakila.city;

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

import com.github.jinahya.mysql.sakila.persistence.City;
import com.github.jinahya.mysql.sakila.persistence.City_;
import com.github.jinahya.mysql.sakila.persistence.Country_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CITY_I_01} of {@code src/test/sql-exercises/city/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CITY_I_01: the cities of Canada")
@Slf4j
class SAKILA_CITY_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c
                                                                                  FROM City c
                                                                                           JOIN c.country co
                                                                                  WHERE co.country = :country
                                                                                  ORDER BY c.city""",
                                                                          City.class)
                .setParameter("country", "Canada")
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(City::getCityId, City::getCity)
                .containsExactly(
                        tuple(179, "Gatineau"),
                        tuple(196, "Halifax"),
                        tuple(300, "Lethbridge"),
                        tuple(313, "London"),
                        tuple(383, "Oshawa"),
                        tuple(430, "Richmond Hill"),
                        tuple(565, "Vancouver"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(City.class);
            final var c = cq.from(City.class);
            final var co = c.join(City_.country);
            cq.select(c)
                    .where(cb.equal(co.get(Country_.country),
                                    cb.parameter(String.class, "country")))
                    .orderBy(cb.asc(c.get(City_.city)));
            return em.createQuery(cq)
                    .setParameter("country", "Canada")
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(City::getCityId, City::getCity)
                .containsExactly(
                        tuple(179, "Gatineau"),
                        tuple(196, "Halifax"),
                        tuple(300, "Lethbridge"),
                        tuple(313, "London"),
                        tuple(383, "Oshawa"),
                        tuple(430, "Richmond Hill"),
                        tuple(565, "Vancouver"));
    }

}
