package com.github.jinahya.mysql.sakila.persistence.sakila.rental;

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
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code SAKILA_RENTAL_B_01} of {@code src/test/sql-exercises/rental/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_RENTAL_B_01: how many rentals are not returned yet")
@Slf4j
class SAKILA_RENTAL_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                   SELECT COUNT(r)
                                                                                   FROM Rental r
                                                                                   WHERE r.returnDate IS NULL""",
                                                                           Long.class)
                .getSingleResult());
        log.debug("{}", value);
        assertThat(value).isEqualTo(183L);
    }

    @Test
    void __CriteriaApi() {
        final var value = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Long.class);
            final var r = cq.from(Rental.class);
            cq.select(cb.count(r))
                    .where(cb.isNull(r.get(Rental_.returnDate)));
            return em.createQuery(cq)
                    .getSingleResult();
        });
        log.debug("{}", value);
        assertThat(value).isEqualTo(183L);
    }

}
