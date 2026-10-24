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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/language_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Language_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_LANGUAGE_B_01: every language")
    @Nested
    class SAKILA_LANGUAGE_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT l
                                                                                      FROM Language l
                                                                                      ORDER BY l.languageId""",
                                                                              Language.class)
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Language::getName)
                    .containsExactly("English", "Italian", "Japanese", "Mandarin", "French",
                                     "German");
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Language.class);
                final var l = cq.from(Language.class);
                cq.select(l)
                        .orderBy(cb.asc(l.get(Language_.languageId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Language::getName)
                    .containsExactly("English", "Italian", "Japanese", "Mandarin", "French",
                                     "German");
        }
    }

    @DisplayName("SAKILA_LANGUAGE_B_02: how many films have an original language")
    @Nested
    class SAKILA_LANGUAGE_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(f)
                                                                                       FROM Film f
                                                                                       WHERE f.originalLanguage IS NOT NULL""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var f = cq.from(Film.class);
                cq.select(cb.count(f))
                        .where(cb.isNotNull(f.get(Film_.originalLanguage)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(0L);
        }
    }

    @DisplayName("SAKILA_LANGUAGE_B_03: which of four languages the table has")
    @Nested
    class SAKILA_LANGUAGE_B_03_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT l
                                                                                      FROM Language l
                                                                                      WHERE l.name IN :names
                                                                                      ORDER BY l.name""",
                                                                              Language.class)
                    .setParameter("names", List.of("English", "French", "German", "Spanish"))
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Language::getName)
                    .containsExactly("English", "French", "German");
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Language.class);
                final var l = cq.from(Language.class);
                cq.select(l)
                        .where(l.get(Language_.name).in("English", "French", "German", "Spanish"))
                        .orderBy(cb.asc(l.get(Language_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Language::getName)
                    .containsExactly("English", "French", "German");
        }
    }

    @DisplayName("SAKILA_LANGUAGE_B_04: the languages whose name ends with an or ese")
    @Nested
    class SAKILA_LANGUAGE_B_04_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT l
                                                                                      FROM Language l
                                                                                      WHERE l.name LIKE :an
                                                                                         OR l.name LIKE :ese
                                                                                      ORDER BY l.name""",
                                                                              Language.class)
                    .setParameter("an", "%an")
                    .setParameter("ese", "%ese")
                    .getResultList());
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Language::getLanguageId, Language::getName)
                    .containsExactly(
                            tuple(6, "German"),
                            tuple(2, "Italian"),
                            tuple(3, "Japanese"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Language.class);
                final var l = cq.from(Language.class);
                cq.select(l)
                        .where(cb.or(cb.like(l.get(Language_.name),
                                             cb.parameter(String.class, "an")),
                                     cb.like(l.get(Language_.name),
                                             cb.parameter(String.class, "ese"))))
                        .orderBy(cb.asc(l.get(Language_.name)));
                return em.createQuery(cq)
                        .setParameter("an", "%an")
                        .setParameter("ese", "%ese")
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list)
                    .extracting(Language::getLanguageId, Language::getName)
                    .containsExactly(
                            tuple(6, "German"),
                            tuple(2, "Italian"),
                            tuple(3, "Japanese"));
        }
    }
}
