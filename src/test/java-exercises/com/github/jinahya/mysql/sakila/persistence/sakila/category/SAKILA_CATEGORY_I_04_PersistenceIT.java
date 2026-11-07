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
import com.github.jinahya.mysql.sakila.persistence.Film;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory_;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CATEGORY_I_04} of
 * {@code src/test/sql-exercises/category/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CATEGORY_I_04: the categories whose films rent for more than the average")
@Slf4j
class SAKILA_CATEGORY_I_04_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT c, ROUND(AVG(f.rentalRate), 2)
                                        FROM Category c
                                                 JOIN c.filmCategories fc
                                                 JOIN fc.film f
                                        GROUP BY c
                                        HAVING AVG(f.rentalRate) > (SELECT AVG(f2.rentalRate) FROM Film f2)
                                        ORDER BY AVG(f.rentalRate) DESC, c.name""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Category) r[0]).getName(),
                            r -> ((Number) r[1]).doubleValue())
                .containsExactly(
                        tuple("Games", 3.25d),
                        tuple("Travel", 3.24d),
                        tuple("Sci-Fi", 3.22d),
                        tuple("Comedy", 3.16d),
                        tuple("Sports", 3.13d),
                        tuple("New", 3.12d),
                        tuple("Foreign", 3.10d),
                        tuple("Horror", 3.03d),
                        tuple("Drama", 3.02d));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(Category.class);
            final var f = c.join(Category_.filmCategories).join(FilmCategory_.film);
            final var average = cq.subquery(Double.class);
            final var f2 = average.from(Film.class);
            average.select(cb.avg(f2.get(Film_.rentalRate)));
            cq.select(cb.array(c, cb.round(cb.avg(f.get(Film_.rentalRate)), 2)))
                    .groupBy(c)
                    .having(cb.gt(cb.avg(f.get(Film_.rentalRate)), average))
                    .orderBy(cb.desc(cb.avg(f.get(Film_.rentalRate))),
                             cb.asc(c.get(Category_.name)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Category) r[0]).getName(),
                            r -> ((Number) r[1]).doubleValue())
                .containsExactly(
                        tuple("Games", 3.25d),
                        tuple("Travel", 3.24d),
                        tuple("Sci-Fi", 3.22d),
                        tuple("Comedy", 3.16d),
                        tuple("Sports", 3.13d),
                        tuple("New", 3.12d),
                        tuple("Foreign", 3.10d),
                        tuple("Horror", 3.03d),
                        tuple("Drama", 3.02d));
    }

}
