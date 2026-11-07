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
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_LANGUAGE_I_03} of
 * {@code src/test/sql-exercises/language/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName(
        "SAKILA_LANGUAGE_I_03: the first five films with the names of their language and " +
        "original language")
@Slf4j
class SAKILA_LANGUAGE_I_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT f, l, ol
                                                                                  FROM Film f
                                                                                           JOIN f.language l
                                                                                           LEFT JOIN f.originalLanguage ol
                                                                                  ORDER BY f.filmId""",
                                                                          Object[].class)
                .setMaxResults(5)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                            r -> ((Language) r[1]).getName(),
                            r -> Optional.ofNullable((Language) r[2]).map(Language::getName)
                                    .orElse(null))
                .containsExactly(
                        tuple(1, "ACADEMY DINOSAUR", "English", null),
                        tuple(2, "ACE GOLDFINGER", "English", null),
                        tuple(3, "ADAPTATION HOLES", "English", null),
                        tuple(4, "AFFAIR PREJUDICE", "English", null),
                        tuple(5, "AFRICAN EGG", "English", null));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var f = cq.from(Film.class);
            final var l = f.join(Film_.language);
            final var ol = f.join(Film_.originalLanguage, JoinType.LEFT);
            cq.select(cb.array(f, l, ol))
                    .orderBy(cb.asc(f.get(Film_.filmId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Film) r[0]).getFilmId(), r -> ((Film) r[0]).getTitle(),
                            r -> ((Language) r[1]).getName(),
                            r -> Optional.ofNullable((Language) r[2]).map(Language::getName)
                                    .orElse(null))
                .containsExactly(
                        tuple(1, "ACADEMY DINOSAUR", "English", null),
                        tuple(2, "ACE GOLDFINGER", "English", null),
                        tuple(3, "ADAPTATION HOLES", "English", null),
                        tuple(4, "AFFAIR PREJUDICE", "English", null),
                        tuple(5, "AFRICAN EGG", "English", null));
    }

}
