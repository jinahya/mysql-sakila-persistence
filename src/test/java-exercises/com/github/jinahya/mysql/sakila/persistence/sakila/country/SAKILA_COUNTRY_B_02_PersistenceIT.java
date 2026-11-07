package com.github.jinahya.mysql.sakila.persistence.sakila.country;

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

import com.github.jinahya.mysql.sakila.persistence.Country;
import com.github.jinahya.mysql.sakila.persistence.Country_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code SAKILA_COUNTRY_B_02} of {@code src/test/sql-exercises/country/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_COUNTRY_B_02: the countries whose name starts with S, ordered by name")
@Slf4j
class SAKILA_COUNTRY_B_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT c
                                        FROM Country c
                                        WHERE c.country LIKE :pattern
                                        ORDER BY c.country""",
                                Country.class
                        )
                        .setParameter("pattern", "S%")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Country::getCountryId)
                .containsExactly(81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91);
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Country.class);
            final var c = cq.from(Country.class);
            cq.select(c)
                    .where(cb.like(c.get(Country_.country),
                                   cb.parameter(String.class, "pattern")))
                    .orderBy(cb.asc(c.get(Country_.country)));
            return em.createQuery(cq)
                    .setParameter("pattern", "S%")
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Country::getCountryId)
                .containsExactly(81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91);
    }

}
