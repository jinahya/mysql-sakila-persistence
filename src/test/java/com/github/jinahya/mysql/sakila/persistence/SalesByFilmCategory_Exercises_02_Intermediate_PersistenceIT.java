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
 * Answers the exercises of {@code
 * src/test/sql/sales_by_film_category_exercises_02_intermediate.sql}, against the installed {@code
 * sakila} database; a nested class per exercise, which answers it with JPQL and with the Criteria
 * API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SalesByFilmCategory_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: whether the view's sales add up to every payment")
    @Nested
    class Exercise1Test {

        // JPQL has no subquery in the SELECT clause; the two sums are two queries
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> List.<Object[]>of(new Object[]{
                    em.createQuery("SELECT SUM(s.totalSales) FROM SalesByFilmCategory s", BigDecimal.class).getSingleResult(),
                    em.createQuery("SELECT SUM(p.amount) FROM Payment p", BigDecimal.class).getSingleResult()}));
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(), r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple("67406.56", "67406.56"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var byCategory = cb.createQuery(BigDecimal.class);
                final var s = byCategory.from(SalesByFilmCategory.class);
                byCategory.select(cb.sum(s.get(SalesByFilmCategory_.totalSales)));
                final var all = cb.createQuery(BigDecimal.class);
                final var p = all.from(Payment.class);
                all.select(cb.sum(p.get(Payment_.amount)));
                return List.<Object[]>of(new Object[]{
                        em.createQuery(byCategory).getSingleResult(), em.createQuery(all).getSingleResult()});
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((BigDecimal) r[0]).toPlainString(), r -> ((BigDecimal) r[1]).toPlainString())
                    .containsExactly(
                            tuple("67406.56", "67406.56"));
        }
    }
}
