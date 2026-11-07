package com.github.jinahya.mysql.sakila.persistence.sakila.city;

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

import com.github.jinahya.mysql.sakila.persistence.City;
import com.github.jinahya.mysql.sakila.persistence.City_;
import com.github.jinahya.mysql.sakila.persistence.Country;
import com.github.jinahya.mysql.sakila.persistence.Country_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_CITY_A_01} of {@code src/test/sql-exercises/city/03_advanced.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_CITY_A_01: the first ten cities which are the only city of their country")
@Slf4j
class SAKILA_CITY_A_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT c, co
                                                                                  FROM City c
                                                                                           JOIN c.country co
                                                                                  WHERE NOT EXISTS (SELECT o FROM City o WHERE o.country = c.country AND o.cityId <> c.cityId)
                                                                                  ORDER BY co.country, c.cityId""",
                                                                          Object[].class)
                .setMaxResults(10)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((City) r[0]).getCityId(), r -> ((City) r[0]).getCity(),
                            r -> ((Country) r[1]).getCountry())
                .containsExactly(
                        tuple(251, "Kabul", "Afghanistan"),
                        tuple(516, "Tafuna", "American Samoa"),
                        tuple(493, "South Hill", "Anguilla"),
                        tuple(586, "Yerevan", "Armenia"),
                        tuple(576, "Woodridge", "Australia"),
                        tuple(14, "al-Manama", "Bahrain"),
                        tuple(53, "Bandar Seri Begawan", "Brunei"),
                        tuple(363, "NDjamna", "Chad"),
                        tuple(378, "Olomouc", "Czech Republic"),
                        tuple(530, "Tartu", "Estonia"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var c = cq.from(City.class);
            final var co = c.join(City_.country);
            final var others = cq.subquery(City.class);
            final var o = others.from(City.class);
            others.select(o).where(cb.equal(o.get(City_.country), c.get(City_.country)),
                                   cb.notEqual(o.get(City_.cityId), c.get(City_.cityId)));
            cq.select(cb.array(c, co))
                    .where(cb.not(cb.exists(others)))
                    .orderBy(cb.asc(co.get(Country_.country)), cb.asc(c.get(City_.cityId)));
            return em.createQuery(cq)
                    .setMaxResults(10)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((City) r[0]).getCityId(), r -> ((City) r[0]).getCity(),
                            r -> ((Country) r[1]).getCountry())
                .containsExactly(
                        tuple(251, "Kabul", "Afghanistan"),
                        tuple(516, "Tafuna", "American Samoa"),
                        tuple(493, "South Hill", "Anguilla"),
                        tuple(586, "Yerevan", "Armenia"),
                        tuple(576, "Woodridge", "Australia"),
                        tuple(14, "al-Manama", "Bahrain"),
                        tuple(53, "Bandar Seri Begawan", "Brunei"),
                        tuple(363, "NDjamna", "Chad"),
                        tuple(378, "Olomouc", "Czech Republic"),
                        tuple(530, "Tartu", "Estonia"));
    }

}
