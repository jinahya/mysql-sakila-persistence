package com.github.jinahya.mysql.sakila.persistence.sakila.film;

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

import com.github.jinahya.mysql.sakila.persistence.Film;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code SAKILA_FILM_I_01} of {@code src/test/sql-exercises/film/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_I_01: how many films have no copy in the inventory")
@Slf4j
class SAKILA_FILM_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                   SELECT COUNT(f)
                                                                                   FROM Film f
                                                                                   WHERE f.inventories IS EMPTY""",
                                                                           Long.class)
                .getSingleResult());
        log.debug("{}", value);
        assertThat(value).isEqualTo(42L);
    }

    @Test
    void __CriteriaApi() {
        final var value = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Long.class);
            final var f = cq.from(Film.class);
            cq.select(cb.count(f))
                    .where(cb.isEmpty(f.get(Film_.inventories)));
            return em.createQuery(cq)
                    .getSingleResult();
        });
        log.debug("{}", value);
        assertThat(value).isEqualTo(42L);
    }

}
