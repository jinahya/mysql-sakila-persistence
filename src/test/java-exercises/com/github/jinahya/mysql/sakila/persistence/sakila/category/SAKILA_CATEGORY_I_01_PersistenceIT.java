package com.github.jinahya.mysql.sakila.persistence.sakila.category;

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

import com.github.jinahya.mysql.sakila.persistence.Category;
import com.github.jinahya.mysql.sakila.persistence.Category_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CATEGORY_I_01} of
 * {@code src/test/sql-exercises/category/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CATEGORY_I_01: how many films each category has, the most first")
@Slf4j
class SAKILA_CATEGORY_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT c, COUNT(fc)
                                        FROM Category c
                                                 JOIN c.filmCategories fc
                                        GROUP BY c
                                        ORDER BY COUNT(fc) DESC, c.name""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list).hasSize(16);
        assertThat(list)
                .extracting(r -> ((Category) r[0]).getName(), r -> ((Number) r[1]).longValue())
                .startsWith(tuple("Sports", 74L), tuple("Foreign", 73L), tuple("Family", 69L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(Category.class);
            final var fc = c.join(Category_.filmCategories);
            cq.select(cb.array(c, cb.count(fc)))
                    .groupBy(c)
                    .orderBy(cb.desc(cb.count(fc)), cb.asc(c.get(Category_.name)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list).hasSize(16);
        assertThat(list)
                .extracting(r -> ((Category) r[0]).getName(), r -> ((Number) r[1]).longValue())
                .startsWith(tuple("Sports", 74L), tuple("Foreign", 73L), tuple("Family", 69L));
    }

}
