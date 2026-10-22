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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/payment_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Payment_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: how much each staff member took")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT p.staff.staffId, COUNT(p), SUM(p.amount)
                    FROM Payment p
                    GROUP BY p.staff.staffId
                    ORDER BY p.staff.staffId""", Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(), r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple(1, 8054L, "33482.50"),
                            tuple(2, 7990L, "33924.06"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var staffId = p.get(Payment_.staff).get(Staff_.staffId);
                cq.select(cb.array(staffId, cb.count(p), cb.sum(p.get(Payment_.amount))))
                        .groupBy(staffId)
                        .orderBy(cb.asc(staffId));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(), r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple(1, 8054L, "33482.50"),
                            tuple(2, 7990L, "33924.06"));
        }
    }

    @DisplayName("exercise 2: how much was paid in each month")
    @Nested
    class Exercise2Test {

        // JPQL has no DATE_FORMAT; the year and the month are EXTRACTed, and written as one in Java
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate), COUNT(p), SUM(p.amount)
                    FROM Payment p
                    GROUP BY EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate)
                    ORDER BY EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate)""", Object[].class)
                    .getResultStream()
                    // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                    .map(row -> new Object[]{String.format("%d-%02d", row[0], row[1]), row[2], row[3]})
                    .toList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(), r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1156L, "4823.44"),
                            tuple("2005-06", 2311L, "9629.89"),
                            tuple("2005-07", 6709L, "28368.91"),
                            tuple("2005-08", 5686L, "24070.14"),
                            tuple("2006-02", 182L, "514.18"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var year = cb.extract(LocalDateTimeField.YEAR, p.get(Payment_.paymentDate));
                final var month = cb.extract(LocalDateTimeField.MONTH, p.get(Payment_.paymentDate));
                cq.select(cb.array(year, month, cb.count(p), cb.sum(p.get(Payment_.amount))))
                        .groupBy(year, month)
                        .orderBy(cb.asc(year), cb.asc(month));
                return em.createQuery(cq).getResultStream()
                        // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                        .map(row -> new Object[]{String.format("%d-%02d", row[0], row[1]), row[2], row[3]})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(), r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1156L, "4823.44"),
                            tuple("2005-06", 2311L, "9629.89"),
                            tuple("2005-07", 6709L, "28368.91"),
                            tuple("2005-08", 5686L, "24070.14"),
                            tuple("2006-02", 182L, "514.18"));
        }
    }

    @DisplayName("exercise 3: how many payments have no rental")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                    SELECT COUNT(p)
                    FROM Payment p
                    WHERE p.rental IS NULL""", Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var p = cq.from(Payment.class);
                cq.select(cb.count(p))
                        .where(cb.isNull(p.get(Payment_.rental)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }
    }
}
