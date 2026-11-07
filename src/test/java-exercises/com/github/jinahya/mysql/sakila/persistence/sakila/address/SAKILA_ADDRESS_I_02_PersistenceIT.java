package com.github.jinahya.mysql.sakila.persistence.sakila.address;

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

import com.github.jinahya.mysql.sakila.persistence.Address;
import com.github.jinahya.mysql.sakila.persistence.Address_;
import com.github.jinahya.mysql.sakila.persistence.City_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_ADDRESS_I_02} of {@code src/test/sql-exercises/address/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_ADDRESS_I_02: the district names found in more than one country")
@Slf4j
class SAKILA_ADDRESS_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT a.district, COUNT(DISTINCT c.country)
                                                                                  FROM Address a
                                                                                           JOIN a.city c
                                                                                  WHERE a.district <> ''
                                                                                  GROUP BY a.district
                                                                                  HAVING COUNT(DISTINCT c.country) > 1
                                                                                  ORDER BY COUNT(DISTINCT c.country) DESC, a.district""",
                                                                          Object[].class)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("Central", 3L),
                        tuple("al-Sharqiya", 2L),
                        tuple("Distrito Federal", 2L),
                        tuple("Ontario", 2L),
                        tuple("Punjab", 2L),
                        tuple("Sucre", 2L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(Address.class);
            final var c = a.join(Address_.city);
            final var countries = cb.countDistinct(c.get(City_.country));
            cq.select(cb.array(a.get(Address_.district), countries))
                    .where(cb.notEqual(a.get(Address_.district), ""))
                    .groupBy(a.get(Address_.district))
                    .having(cb.gt(countries, 1L))
                    .orderBy(cb.desc(countries), cb.asc(a.get(Address_.district)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("Central", 3L),
                        tuple("al-Sharqiya", 2L),
                        tuple("Distrito Federal", 2L),
                        tuple("Ontario", 2L),
                        tuple("Punjab", 2L),
                        tuple("Sucre", 2L));
    }

}
