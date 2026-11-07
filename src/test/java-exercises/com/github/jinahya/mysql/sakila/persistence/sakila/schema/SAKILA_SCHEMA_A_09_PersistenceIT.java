package com.github.jinahya.mysql.sakila.persistence.sakila.schema;

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

import com.github.jinahya.mysql.sakila.persistence.Address_;
import com.github.jinahya.mysql.sakila.persistence.Category_;
import com.github.jinahya.mysql.sakila.persistence.City_;
import com.github.jinahya.mysql.sakila.persistence.Country;
import com.github.jinahya.mysql.sakila.persistence.Country_;
import com.github.jinahya.mysql.sakila.persistence.Customer;
import com.github.jinahya.mysql.sakila.persistence.Customer_;
import com.github.jinahya.mysql.sakila.persistence.FilmCategory_;
import com.github.jinahya.mysql.sakila.persistence.Film_;
import com.github.jinahya.mysql.sakila.persistence.Inventory_;
import com.github.jinahya.mysql.sakila.persistence.Rental;
import com.github.jinahya.mysql.sakila.persistence.Rental_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_SCHEMA_A_09} of {@code src/test/sql-exercises/__/03_advanced.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_SCHEMA_A_09: the countries none of whose customers rented a Music film")
@Slf4j
class SAKILA_SCHEMA_A_09_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT co, COUNT(cu)
                                                                                  FROM Customer cu
                                                                                           JOIN cu.address a
                                                                                           JOIN a.city ci
                                                                                           JOIN ci.country co
                                                                                  WHERE NOT EXISTS (SELECT r
                                                                                                    FROM Rental r
                                                                                                             JOIN r.customer cu2
                                                                                                             JOIN cu2.address a2
                                                                                                             JOIN a2.city ci2
                                                                                                             JOIN r.inventory i
                                                                                                             JOIN i.film f
                                                                                                             JOIN f.filmCategories fc
                                                                                                             JOIN fc.category c
                                                                                                    WHERE ci2.country = co
                                                                                                      AND c.name = :name)
                                                                                  GROUP BY co
                                                                                  ORDER BY co.country""",
                                                                          Object[].class)
                .setParameter("name", "Music")
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Country) r[0]).getCountry(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("Afghanistan", 1L),
                        tuple("Bahrain", 1L),
                        tuple("Finland", 1L),
                        tuple("French Polynesia", 2L),
                        tuple("Iraq", 1L),
                        tuple("Liechtenstein", 1L),
                        tuple("Madagascar", 1L),
                        tuple("New Zealand", 1L),
                        tuple("Slovakia", 1L),
                        tuple("Sri Lanka", 1L),
                        tuple("Turkmenistan", 1L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var cu = cq.from(Customer.class);
            final var co = cu.join(Customer_.address).join(Address_.city).join(City_.country);
            final var name = cb.parameter(String.class, "name");
            final var music = cq.subquery(Rental.class);
            final var r = music.from(Rental.class);
            final var ci2 =
                    r.join(Rental_.customer).join(Customer_.address).join(Address_.city);
            final var c = r.join(Rental_.inventory).join(Inventory_.film)
                    .join(Film_.filmCategories).join(FilmCategory_.category);
            music.select(r).where(cb.equal(ci2.get(City_.country), co),
                                  cb.equal(c.get(Category_.name), name));
            cq.select(cb.array(co, cb.count(cu)))
                    .where(cb.not(cb.exists(music)))
                    .groupBy(co)
                    .orderBy(cb.asc(co.get(Country_.country)));
            return em.createQuery(cq)
                    .setParameter(name, "Music")
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Country) r[0]).getCountry(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("Afghanistan", 1L),
                        tuple("Bahrain", 1L),
                        tuple("Finland", 1L),
                        tuple("French Polynesia", 2L),
                        tuple("Iraq", 1L),
                        tuple("Liechtenstein", 1L),
                        tuple("Madagascar", 1L),
                        tuple("New Zealand", 1L),
                        tuple("Slovakia", 1L),
                        tuple("Sri Lanka", 1L),
                        tuple("Turkmenistan", 1L));
    }

}
