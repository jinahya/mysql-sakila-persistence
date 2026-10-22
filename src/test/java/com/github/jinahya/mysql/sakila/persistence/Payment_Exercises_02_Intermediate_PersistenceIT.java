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
 * Answers the exercises of {@code src/test/sql/payment_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Payment_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: the largest payment, and how many payments are that large")
    @Nested
    class Exercise1Test {

        // JPQL has no subquery in the SELECT clause; the largest amount and its count are two
        // queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT MAX(p.amount) FROM Payment p",
                                   BigDecimal.class).getSingleResult(),
                    em.createQuery("""
                                           SELECT COUNT(p)
                                           FROM Payment p
                                           WHERE p.amount = (SELECT MAX(p2.amount) FROM Payment p2)""",
                                   Long.class).getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("11.99", 10L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var largest = cb.createQuery(BigDecimal.class);
                final var p = largest.from(Payment.class);
                largest.select(cb.max(p.get(Payment_.amount)));
                final var count = cb.createQuery(Long.class);
                final var p1 = count.from(Payment.class);
                final var max = count.subquery(BigDecimal.class);
                final var p2 = max.from(Payment.class);
                max.select(cb.max(p2.get(Payment_.amount)));
                count.select(cb.count(p1)).where(cb.equal(p1.get(Payment_.amount), max));
                return List.<Object[]>of(new Object[]{
                        em.createQuery(largest).getSingleResult(), em.createQuery(
                        count).getSingleResult()});
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(),
                                r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("11.99", 10L));
        }
    }
}
