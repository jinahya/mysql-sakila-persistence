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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CITY_I_04} of {@code src/test/sql-exercises/city/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CITY_I_04: the cities with more than one address")
@Slf4j
class SAKILA_CITY_I_04_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT c, COUNT(a)
                                        FROM City c
                                                 JOIN c.addresses a
                                        GROUP BY c
                                        HAVING COUNT(a) > 1
                                        ORDER BY COUNT(a) DESC, c.city, c.cityId""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((City) r[0]).getCityId(), r -> ((City) r[0]).getCity(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(42, "Aurora", 2L),
                        tuple(300, "Lethbridge", 2L),
                        tuple(312, "London", 2L),
                        tuple(576, "Woodridge", 2L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(City.class);
            final var a = c.join(City_.addresses);
            cq.select(cb.array(c, cb.count(a)))
                    .groupBy(c)
                    .having(cb.gt(cb.count(a), 1L))
                    .orderBy(cb.desc(cb.count(a)), cb.asc(c.get(City_.city)),
                             cb.asc(c.get(City_.cityId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((City) r[0]).getCityId(), r -> ((City) r[0]).getCity(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(42, "Aurora", 2L),
                        tuple(300, "Lethbridge", 2L),
                        tuple(312, "London", 2L),
                        tuple(576, "Woodridge", 2L));
    }

}
