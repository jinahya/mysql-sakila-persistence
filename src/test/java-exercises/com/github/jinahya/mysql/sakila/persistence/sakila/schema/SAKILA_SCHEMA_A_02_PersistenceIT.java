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

import com.github.jinahya.mysql.sakila.persistence.Category;
import com.github.jinahya.mysql.sakila.persistence.Customer;
import com.github.jinahya.mysql.sakila.persistence.Customer_;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory_;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.Inventory_;
import com.github.jinahya.mysql.sakila.persistence.Rental_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_SCHEMA_A_02} of {@code src/test/sql-exercises/__/03_advanced.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_SCHEMA_A_02: the customers who rented a film of every category")
@Slf4j
class SAKILA_SCHEMA_A_02_PersistenceIT {

    // both keep the columns: eclipselink drops GROUP BY/HAVING from a Criteria entity select
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c.customerId, c.firstName, c.lastName
                                                                                  FROM Customer c
                                                                                           JOIN c.rentals r
                                                                                           JOIN r.inventory i
                                                                                           JOIN i.film f
                                                                                           JOIN f.filmCategories fc
                                                                                  GROUP BY c.customerId, c.firstName, c.lastName
                                                                                  HAVING COUNT(DISTINCT fc.categoryId) = (SELECT COUNT(ca) FROM Category ca)
                                                                                  ORDER BY c.customerId""",
                                                                          Object[].class)
                .setMaxResults(5)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2])
                .containsExactly(
                        tuple(28, "CYNTHIA", "YOUNG"),
                        tuple(42, "CAROLYN", "PEREZ"),
                        tuple(75, "TAMMY", "SANDERS"),
                        tuple(113, "CINDY", "FISHER"),
                        tuple(144, "CLARA", "SHAW"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(Customer.class);
            final var fc =
                    c.join(Customer_.rentals).join(Rental_.inventory).join(Inventory_.film)
                            .join(Film_.filmCategories);
            final var categories = cq.subquery(Long.class);
            final var ca = categories.from(Category.class);
            categories.select(cb.count(ca));
            cq.select(cb.array(c.get(Customer_.customerId), c.get(Customer_.firstName),
                               c.get(Customer_.lastName)))
                    .groupBy(c.get(Customer_.customerId), c.get(Customer_.firstName),
                             c.get(Customer_.lastName))
                    .having(cb.equal(cb.countDistinct(fc.get(FilmCategory_.categoryId)),
                                     categories))
                    .orderBy(cb.asc(c.get(Customer_.customerId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2])
                .containsExactly(
                        tuple(28, "CYNTHIA", "YOUNG"),
                        tuple(42, "CAROLYN", "PEREZ"),
                        tuple(75, "TAMMY", "SANDERS"),
                        tuple(113, "CINDY", "FISHER"),
                        tuple(144, "CLARA", "SHAW"));
    }

}
