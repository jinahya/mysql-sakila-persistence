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

import com.github.jinahya.mysql.sakila.persistence.Address_;
import com.github.jinahya.mysql.sakila.persistence.City_;
import com.github.jinahya.mysql.sakila.persistence.Country;
import com.github.jinahya.mysql.sakila.persistence.Country_;
import com.github.jinahya.mysql.sakila.persistence.Customer_;
import com.github.jinahya.mysql.sakila.persistence.Payment;
import com.github.jinahya.mysql.sakila.persistence.Payment_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_SCHEMA_I_03} of {@code src/test/sql-exercises/__/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_SCHEMA_I_03: the five countries whose customers paid the most")
@Slf4j
class SAKILA_SCHEMA_I_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT co, COUNT(DISTINCT cu), SUM(p.amount)
                                                                                  FROM Payment p
                                                                                           JOIN p.customer cu
                                                                                           JOIN cu.address a
                                                                                           JOIN a.city ci
                                                                                           JOIN ci.country co
                                                                                  GROUP BY co
                                                                                  ORDER BY SUM(p.amount) DESC, co.countryId ASC""",
                                                                          Object[].class)
                .setMaxResults(5)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Country) r[0]).getCountry(),
                            r -> ((Number) r[1]).longValue(),
                            r -> ((BigDecimal) r[2]).toPlainString())
                .containsExactly(
                        tuple("India", 60L, "6628.28"),
                        tuple("China", 53L, "5798.74"),
                        tuple("United States", 36L, "4110.32"),
                        tuple("Japan", 31L, "3470.75"),
                        tuple("Mexico", 30L, "3307.04"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var p = cq.from(Payment.class);
            final var cu = p.join(Payment_.customer);
            final var co = cu.join(Customer_.address).join(Address_.city).join(City_.country);
            cq.select(cb.array(co, cb.countDistinct(cu), cb.sum(p.get(Payment_.amount))))
                    .groupBy(co)
                    .orderBy(cb.desc(cb.sum(p.get(Payment_.amount))),
                             cb.asc(co.get(Country_.countryId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Country) r[0]).getCountry(),
                            r -> ((Number) r[1]).longValue(),
                            r -> ((BigDecimal) r[2]).toPlainString())
                .containsExactly(
                        tuple("India", 60L, "6628.28"),
                        tuple("China", 53L, "5798.74"),
                        tuple("United States", 36L, "4110.32"),
                        tuple("Japan", 31L, "3470.75"),
                        tuple("Mexico", 30L, "3307.04"));
    }

}
