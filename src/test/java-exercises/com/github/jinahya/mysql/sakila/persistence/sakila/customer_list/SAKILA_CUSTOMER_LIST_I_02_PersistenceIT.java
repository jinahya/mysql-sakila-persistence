package com.github.jinahya.mysql.sakila.persistence.sakila.customer_list;

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

import com.github.jinahya.mysql.sakila.persistence.CustomerList;
import com.github.jinahya.mysql.sakila.persistence.CustomerList_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CUSTOMER_LIST_I_02} of
 * {@code src/test/sql-exercises/customer_list/02_intermediate.sql} with JPQL and with the Criteria
 * API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CUSTOMER_LIST_I_02: the cities with more than one customer")
@Slf4j
class SAKILA_CUSTOMER_LIST_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT l.city, l.country, COUNT(l)
                                                                                  FROM CustomerList l
                                                                                  GROUP BY l.city, l.country
                                                                                  HAVING COUNT(l) > 1
                                                                                  ORDER BY l.city, l.country""",
                                                                          Object[].class)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple("Aurora", "United States", 2L),
                        tuple("London", "United Kingdom", 2L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var l = cq.from(CustomerList.class);
            cq.select(cb.array(l.get(CustomerList_.city), l.get(CustomerList_.country),
                               cb.count(l)))
                    .groupBy(l.get(CustomerList_.city), l.get(CustomerList_.country))
                    .having(cb.gt(cb.count(l), 1L))
                    .orderBy(cb.asc(l.get(CustomerList_.city)),
                             cb.asc(l.get(CustomerList_.country)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple("Aurora", "United States", 2L),
                        tuple("London", "United Kingdom", 2L));
    }

}
