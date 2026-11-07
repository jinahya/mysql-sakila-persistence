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

import com.github.jinahya.mysql.sakila.persistence.Language;
import com.github.jinahya.mysql.sakila.persistence.Language_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code SAKILA_LANGUAGE_B_03} of {@code src/test/sql-exercises/language/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_LANGUAGE_B_03: which of four languages the table has")
@Slf4j
class SAKILA_LANGUAGE_B_03_PersistenceIT {

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
