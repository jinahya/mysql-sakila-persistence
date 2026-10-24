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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers the exercises of {@code src/test/sql/category_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Category_Exercises_01_Basic_PersistenceIT {

    @DisplayName("SAKILA_CATEGORY_B_01: every category, by name")
    @Nested
    class SAKILA_CATEGORY_B_01_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c
                                            FROM Category c
                                            ORDER BY c.name""",
                                    Category.class
                            )
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).hasSize(16);
            assertThat(list).extracting(Category::getName)
                    .startsWith("Action", "Animation", "Children").endsWith("Travel");
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Category.class);
                final var c = cq.from(Category.class);
                cq.select(c)
                        .orderBy(cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).hasSize(16);
            assertThat(list).extracting(Category::getName)
                    .startsWith("Action", "Animation", "Children").endsWith("Travel");
        }
    }

    @DisplayName("SAKILA_CATEGORY_B_02: the categories whose names fall between D and H")
    @Nested
    class SAKILA_CATEGORY_B_02_Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(
                    em -> em.createQuery(
                                    """
                                            SELECT c
                                            FROM Category c
                                            WHERE c.name BETWEEN 'D' AND 'H'
                                            ORDER BY c.name""",
                                    Category.class
                            )
                            .getResultList()
            );
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Category::getName)
                    .containsExactly("Documentary", "Drama", "Family", "Foreign", "Games");
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Category.class);
                final var c = cq.from(Category.class);
                cq.select(c)
                        .where(cb.between(c.get(Category_.name), "D", "H"))
                        .orderBy(cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(e -> log.debug("{}", e));
            assertThat(list).extracting(Category::getName)
                    .containsExactly("Documentary", "Drama", "Family", "Foreign", "Games");
        }
    }
}
