package com.github.jinahya.mysql.sakila.persistence.sakila.language;

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
import com.github.jinahya.mysql.sakila.persistence.Language;
import com.github.jinahya.mysql.sakila.persistence.Language_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_LANGUAGE_I_02} of
 * {@code src/test/sql-exercises/language/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName(
        "SAKILA_LANGUAGE_I_02: the languages no film uses, either as its language or as its " +
        "original language")
@Slf4j
class SAKILA_LANGUAGE_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT l
                                                                                  FROM Language l
                                                                                  WHERE NOT EXISTS (SELECT f
                                                                                                    FROM Film f
                                                                                                    WHERE f.language = l
                                                                                                       OR f.originalLanguage = l)
                                                                                  ORDER BY l.languageId""",
                                                                          Language.class)
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Language::getLanguageId, Language::getName)
                .containsExactly(
                        tuple(2, "Italian"),
                        tuple(3, "Japanese"),
                        tuple(4, "Mandarin"),
                        tuple(5, "French"),
                        tuple(6, "German"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Language.class);
            final var l = cq.from(Language.class);
            final var films = cq.subquery(Film.class);
            final var f = films.from(Film.class);
            films.select(f).where(cb.or(cb.equal(f.get(Film_.language), l),
                                        cb.equal(f.get(Film_.originalLanguage), l)));
            cq.select(l)
                    .where(cb.not(cb.exists(films)))
                    .orderBy(cb.asc(l.get(Language_.languageId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Language::getLanguageId, Language::getName)
                .containsExactly(
                        tuple(2, "Italian"),
                        tuple(3, "Japanese"),
                        tuple(4, "Mandarin"),
                        tuple(5, "French"),
                        tuple(6, "German"));
    }

}
