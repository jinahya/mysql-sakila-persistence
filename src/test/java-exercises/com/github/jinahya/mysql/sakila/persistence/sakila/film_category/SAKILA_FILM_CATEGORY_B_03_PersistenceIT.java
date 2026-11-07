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

import com.github.jinahya.mysql.sakila.persistence.Category_;
import com.github.jinahya.mysql.sakila.persistence.Film;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory_;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_FILM_CATEGORY_B_03} of
 * {@code src/test/sql-exercises/film_category/01_basic.sql} with JPQL and with the Criteria API, or
 * with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_CATEGORY_B_03: the films of the Comedy category")
@Slf4j
class SAKILA_FILM_CATEGORY_B_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT f
                                                                                  FROM FilmCategory fc
                                                                                           JOIN fc.film f
                                                                                           JOIN fc.category c
                                                                                  WHERE c.name = :name
                                                                                  ORDER BY f.title, f.filmId""",
                                                                          Film.class)
                .setParameter("name", "Comedy")
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list).hasSize(58)
                .extracting(Film::getFilmId, Film::getTitle)
                .startsWith(
                        tuple(7, "AIRPLANE SIERRA"),
                        tuple(28, "ANTHEM LUKE"),
                        tuple(99, "BRINGING HYSTERICAL"))
                .endsWith(
                        tuple(1000, "ZORRO ARK"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Film.class);
            final var fc = cq.from(FilmCategory.class);
            final var f = fc.join(FilmCategory_.film);
            final var c = fc.join(FilmCategory_.category);
            cq.select(f)
                    .where(cb.equal(c.get(Category_.name), cb.parameter(String.class, "name")))
                    .orderBy(cb.asc(f.get(Film_.title)), cb.asc(f.get(Film_.filmId)));
            return em.createQuery(cq)
                    .setParameter("name", "Comedy")
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list).hasSize(58)
                .extracting(Film::getFilmId, Film::getTitle)
                .startsWith(
                        tuple(7, "AIRPLANE SIERRA"),
                        tuple(28, "ANTHEM LUKE"),
                        tuple(99, "BRINGING HYSTERICAL"))
                .endsWith(
                        tuple(1000, "ZORRO ARK"));
    }

}
