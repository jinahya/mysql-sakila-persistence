package com.github.jinahya.mysql.sakila.persistence.sakila.staff;

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

import com.github.jinahya.mysql.sakila.persistence.Rental;
import com.github.jinahya.mysql.sakila.persistence.Rental_;
import com.github.jinahya.mysql.sakila.persistence.Staff;
import com.github.jinahya.mysql.sakila.persistence.Staff_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_STAFF_I_01} of {@code src/test/sql-exercises/staff/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_STAFF_I_01: how many rentals each staff member handled")
@Slf4j
class SAKILA_STAFF_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT s, COUNT(r)
                                                                                  FROM Rental r
                                                                                           JOIN r.staff s
                                                                                  GROUP BY s
                                                                                  ORDER BY s.staffId""",
                                                                          Object[].class)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Staff) r[0]).getStaffId(),
                            r -> ((Staff) r[0]).getFirstName(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(1, "Mike", 8040L),
                        tuple(2, "Jon", 8004L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var r = cq.from(Rental.class);
            final var s = r.join(Rental_.staff);
            cq.select(cb.array(s, cb.count(r)))
                    .groupBy(s)
                    .orderBy(cb.asc(s.get(Staff_.staffId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Staff) r[0]).getStaffId(),
                            r -> ((Staff) r[0]).getFirstName(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(1, "Mike", 8040L),
                        tuple(2, "Jon", 8004L));
    }

}
