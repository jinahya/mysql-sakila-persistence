package com.github.jinahya.mysql.sakila.persistence;

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

import jakarta.persistence.criteria.LocalDateTimeField;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/staff_exercises_03_advanced.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Staff_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("exercise 1: the day on which each staff member took the most")
    @Nested
    class Exercise1Test {

        // JPQL has no window functions; the day is the one whose total is not less than any other
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT s.staffId, EXTRACT(DATE FROM p.paymentDate), COUNT(p), SUM(p.amount)
                                                                                      FROM Payment p
                                                                                               JOIN p.staff s
                                                                                      GROUP BY s.staffId, EXTRACT(DATE FROM p.paymentDate)
                                                                                      HAVING SUM(p.amount) >= ALL (SELECT SUM(p2.amount)
                                                                                                                   FROM Payment p2
                                                                                                                   WHERE p2.staff.staffId = s.staffId
                                                                                                                   GROUP BY EXTRACT(DATE FROM p2.paymentDate))
                                                                                      ORDER BY s.staffId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    // the day, whichever of LocalDate, LocalDateTime and java.sql.Date it is
                    .extracting(r -> ((Number) r[0]).intValue(),
                                r -> String.valueOf(r[1]).substring(0, 10),
                                r -> ((Number) r[2]).longValue(),
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(1, "2005-08-01", 345L, "1442.55"),
                            tuple(2, "2005-07-31", 351L, "1517.49"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var s = p.join(Payment_.staff);
                final var day = cb.extract(LocalDateTimeField.DATE, p.get(Payment_.paymentDate));
                final var totals = cq.subquery(BigDecimal.class);
                final var p2 = totals.from(Payment.class);
                totals.select(cb.sum(p2.get(Payment_.amount)))
                        .where(cb.equal(p2.get(Payment_.staff).get(Staff_.staffId),
                                        s.get(Staff_.staffId)))
                        .groupBy(cb.extract(LocalDateTimeField.DATE,
                                            p2.get(Payment_.paymentDate)));
                cq.select(cb.array(s.get(Staff_.staffId), day, cb.count(p),
                                   cb.sum(p.get(Payment_.amount))))
                        .groupBy(s.get(Staff_.staffId), day)
                        .having(cb.greaterThanOrEqualTo(cb.sum(p.get(Payment_.amount)),
                                                        cb.all(totals)))
                        .orderBy(cb.asc(s.get(Staff_.staffId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    // the day, whichever of LocalDate, LocalDateTime and java.sql.Date it is
                    .extracting(r -> ((Number) r[0]).intValue(),
                                r -> String.valueOf(r[1]).substring(0, 10),
                                r -> ((Number) r[2]).longValue(),
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(1, "2005-08-01", 345L, "1442.55"),
                            tuple(2, "2005-07-31", 351L, "1517.49"));
        }
    }
}
