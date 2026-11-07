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
import com.github.jinahya.mysql.sakila.persistence.Country;
import com.github.jinahya.mysql.sakila.persistence.Country_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CITY_I_02} of {@code src/test/sql-exercises/city/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CITY_I_02: the five countries with the most cities")
@Slf4j
class SAKILA_CITY_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT co, COUNT(c)
                                        FROM City c
                                                 JOIN c.country co
                                        GROUP BY co
                                        ORDER BY COUNT(c) DESC, co.country""",
                                Object[].class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Country) r[0]).getCountry(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("India", 60L),
                        tuple("China", 53L),
                        tuple("United States", 35L),
                        tuple("Japan", 31L),
                        tuple("Mexico", 30L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(City.class);
            final var co = c.join(City_.country);
            cq.select(cb.array(co, cb.count(c)))
                    .groupBy(co)
                    .orderBy(cb.desc(cb.count(c)), cb.asc(co.get(Country_.country)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Country) r[0]).getCountry(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("India", 60L),
                        tuple("China", 53L),
                        tuple("United States", 35L),
                        tuple("Japan", 31L),
                        tuple("Mexico", 30L));
    }

}
