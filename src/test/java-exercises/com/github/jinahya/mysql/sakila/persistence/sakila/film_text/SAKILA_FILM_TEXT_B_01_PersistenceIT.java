package com.github.jinahya.mysql.sakila.persistence.sakila.film_text;

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

import com.github.jinahya.mysql.sakila.persistence.FilmText;
import com.github.jinahya.mysql.sakila.persistence.FilmText_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_FILM_TEXT_B_01} of {@code src/test/sql-exercises/film_text/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_FILM_TEXT_B_01: the films whose description has both a Crocodile and a Shark")
@Slf4j
class SAKILA_FILM_TEXT_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT ft
                                                                                  FROM FilmText ft
                                                                                  WHERE ft.description LIKE :crocodile
                                                                                    AND ft.description LIKE :shark
                                                                                  ORDER BY ft.filmId""",
                                                                          FilmText.class)
                .setParameter("crocodile", "%Crocodile%")
                .setParameter("shark", "%Shark%")
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(FilmText::getFilmId, FilmText::getTitle)
                .containsExactly(
                        tuple(155, "CLEOPATRA DEVIL"),
                        tuple(177, "CONNECTICUT TRAMP"),
                        tuple(206, "DANCING FEVER"),
                        tuple(292, "EXCITEMENT EVE"),
                        tuple(429, "HONEY TIES"),
                        tuple(543, "MADIGAN DORADO"),
                        tuple(790, "SHOOTIST SUPERFLY"),
                        tuple(803, "SLACKER LIAISONS"),
                        tuple(832, "SPLASH GUMP"),
                        tuple(959, "WARLOCK WEREWOLF"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(FilmText.class);
            final var ft = cq.from(FilmText.class);
            cq.select(ft)
                    .where(cb.like(ft.get(FilmText_.description),
                                   cb.parameter(String.class, "crocodile")),
                           cb.like(ft.get(FilmText_.description),
                                   cb.parameter(String.class, "shark")))
                    .orderBy(cb.asc(ft.get(FilmText_.filmId)));
            return em.createQuery(cq)
                    .setParameter("crocodile", "%Crocodile%")
                    .setParameter("shark", "%Shark%")
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(FilmText::getFilmId, FilmText::getTitle)
                .containsExactly(
                        tuple(155, "CLEOPATRA DEVIL"),
                        tuple(177, "CONNECTICUT TRAMP"),
                        tuple(206, "DANCING FEVER"),
                        tuple(292, "EXCITEMENT EVE"),
                        tuple(429, "HONEY TIES"),
                        tuple(543, "MADIGAN DORADO"),
                        tuple(790, "SHOOTIST SUPERFLY"),
                        tuple(803, "SLACKER LIAISONS"),
                        tuple(832, "SPLASH GUMP"),
                        tuple(959, "WARLOCK WEREWOLF"));
    }

}
