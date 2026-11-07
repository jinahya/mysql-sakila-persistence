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
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CITY_B_01} of {@code src/test/sql-exercises/city/01_basic.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CITY_B_01: the cities whose name starts with Q")
@Slf4j
class SAKILA_CITY_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c
                                                                                  FROM City c
                                                                                  WHERE c.city LIKE :pattern
                                                                                  ORDER BY c.city, c.cityId""",
                                                                          City.class)
                .setParameter("pattern", "Q%")
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(City::getCityId, City::getCity)
                .containsExactly(
                        tuple(421, "Qalyub"),
                        tuple(422, "Qinhuangdao"),
                        tuple(423, "Qomsheh"),
                        tuple(424, "Quilmes"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(City.class);
            final var c = cq.from(City.class);
            cq.select(c)
                    .where(cb.like(c.get(City_.city), cb.parameter(String.class, "pattern")))
                    .orderBy(cb.asc(c.get(City_.city)), cb.asc(c.get(City_.cityId)));
            return em.createQuery(cq)
                    .setParameter("pattern", "Q%")
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(City::getCityId, City::getCity)
                .containsExactly(
                        tuple(421, "Qalyub"),
                        tuple(422, "Qinhuangdao"),
                        tuple(423, "Qomsheh"),
                        tuple(424, "Quilmes"));
    }

}
