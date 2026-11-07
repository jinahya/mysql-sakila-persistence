package com.github.jinahya.mysql.sakila.persistence.sakila.sales_by_film_category;

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

import com.github.jinahya.mysql.sakila.persistence.Category_;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory_;
import com.github.jinahya.mysql.sakila.persistence.SalesByFilmCategory;
import com.github.jinahya.mysql.sakila.persistence.SalesByFilmCategory_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_SALES_BY_FILM_CATEGORY_I_03} of
 * {@code src/test/sql-exercises/sales_by_film_category/02_intermediate.sql} with JPQL and with the
 * Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_SALES_BY_FILM_CATEGORY_I_03: the five categories which sold the most per film")
@Slf4j
class SAKILA_SALES_BY_FILM_CATEGORY_I_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT s, COUNT(fc.filmId), ROUND(s.totalSales / COUNT(fc.filmId), 2) AS perFilm
                                                                                  FROM SalesByFilmCategory s, FilmCategory fc
                                                                                           JOIN fc.category c
                                                                                  WHERE c.name = s.category
                                                                                  GROUP BY s
                                                                                  ORDER BY perFilm DESC, s.category""",
                                                                          Object[].class)
                .setMaxResults(5)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                            r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                            r -> ((Number) r[1]).longValue(),
                            r -> ((Number) r[2]).doubleValue())
                .containsExactly(
                        tuple("Sci-Fi", "4756.98", 61L, 77.98d),
                        tuple("Comedy", "4383.58", 58L, 75.58d),
                        tuple("Drama", "4587.39", 62L, 73.99d),
                        tuple("Sports", "5314.21", 74L, 71.81d),
                        tuple("Animation", "4656.30", 66L, 70.55d));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var s = cq.from(SalesByFilmCategory.class);
            final var fc = cq.from(FilmCategory.class);
            final var c = fc.join(FilmCategory_.category);
            final var category = s.get(SalesByFilmCategory_.category);
            final var totalSales = s.get(SalesByFilmCategory_.totalSales);
            final var films = cb.count(fc.get(FilmCategory_.filmId));
            final var perFilm = cb.round(cb.quot(totalSales, films), 2);
            cq.select(cb.array(s, films, perFilm))
                    .where(cb.equal(c.get(Category_.name), category))
                    .groupBy(s)
                    .orderBy(cb.desc(perFilm), cb.asc(category));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((SalesByFilmCategory) r[0]).getCategory(),
                            r -> ((SalesByFilmCategory) r[0]).getTotalSales().toPlainString(),
                            r -> ((Number) r[1]).longValue(),
                            r -> ((Number) r[2]).doubleValue())
                .containsExactly(
                        tuple("Sci-Fi", "4756.98", 61L, 77.98d),
                        tuple("Comedy", "4383.58", 58L, 75.58d),
                        tuple("Drama", "4587.39", 62L, 73.99d),
                        tuple("Sports", "5314.21", 74L, 71.81d),
                        tuple("Animation", "4656.30", 66L, 70.55d));
    }

}
