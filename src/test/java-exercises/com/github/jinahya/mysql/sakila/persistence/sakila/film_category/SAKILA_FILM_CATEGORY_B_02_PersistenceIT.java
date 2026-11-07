package com.github.jinahya.mysql.sakila.persistence.sakila.film_category;

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

import com.github.jinahya.mysql.sakila.persistence.FilmCategory;
import com.github.jinahya.mysql.sakila.persistence.FilmCategoryId;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_FILM_CATEGORY_B_02} of
 * {@code src/test/sql-exercises/film_category/01_basic.sql} with JPQL and with the Criteria API, or
 * with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_CATEGORY_B_02: the categories of the films 1 to 10")
@Slf4j
class SAKILA_FILM_CATEGORY_B_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT fc
                                                                                  FROM FilmCategory fc
                                                                                  WHERE fc.filmId BETWEEN :min AND :max
                                                                                  ORDER BY fc.filmId""",
                                                                          FilmCategory.class)
                .setParameter("min", 1)
                .setParameter("max", 10)
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(FilmCategoryId::from)
                .extracting(FilmCategoryId::filmId, FilmCategoryId::category)
                .containsExactly(
                        tuple(1, 6),
                        tuple(2, 11),
                        tuple(3, 6),
                        tuple(4, 11),
                        tuple(5, 8),
                        tuple(6, 9),
                        tuple(7, 5),
                        tuple(8, 11),
                        tuple(9, 11),
                        tuple(10, 15));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(FilmCategory.class);
            final var fc = cq.from(FilmCategory.class);
            cq.select(fc)
                    .where(cb.between(fc.get(FilmCategory_.filmId),
                                      cb.parameter(Integer.class, "min"),
                                      cb.parameter(Integer.class, "max")))
                    .orderBy(cb.asc(fc.get(FilmCategory_.filmId)));
            return em.createQuery(cq)
                    .setParameter("min", 1)
                    .setParameter("max", 10)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(FilmCategoryId::from)
                .extracting(FilmCategoryId::filmId, FilmCategoryId::category)
                .containsExactly(
                        tuple(1, 6),
                        tuple(2, 11),
                        tuple(3, 6),
                        tuple(4, 11),
                        tuple(5, 8),
                        tuple(6, 9),
                        tuple(7, 5),
                        tuple(8, 11),
                        tuple(9, 11),
                        tuple(10, 15));
    }

}
