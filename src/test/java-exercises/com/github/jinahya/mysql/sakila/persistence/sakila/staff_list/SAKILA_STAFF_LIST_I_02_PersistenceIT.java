package com.github.jinahya.mysql.sakila.persistence.sakila.staff_list;

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
import com.github.jinahya.mysql.sakila.persistence.City;
import com.github.jinahya.mysql.sakila.persistence.StaffList;
import com.github.jinahya.mysql.sakila.persistence.StaffList_;
import com.github.jinahya.mysql.sakila.persistence.Store;
import com.github.jinahya.mysql.sakila.persistence.Store_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_STAFF_LIST_I_02} of
 * {@code src/test/sql-exercises/staff_list/02_intermediate.sql} with JPQL and with the Criteria
 * API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_STAFF_LIST_I_02: whether each staff member lives in the city of their store")
@Slf4j
class SAKILA_STAFF_LIST_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT l, ci
                                                                                  FROM StaffList l, Store st
                                                                                           JOIN st.address a
                                                                                           JOIN a.city ci
                                                                                  WHERE st.storeId = l.sid
                                                                                  ORDER BY l.id""",
                                                                          Object[].class)
                .getResultList());
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((StaffList) r[0]).getId(), r -> ((StaffList) r[0]).getName(),
                            r -> ((StaffList) r[0]).getCity(), r -> ((City) r[1]).getCity())
                .containsExactly(
                        tuple(1, "Mike Hillyer", "Lethbridge", "Lethbridge"),
                        tuple(2, "Jon Stephens", "Woodridge", "Woodridge"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var l = cq.from(StaffList.class);
            final var st = cq.from(Store.class);
            final var ci = st.join(Store_.address).join(Address_.city);
            cq.select(cb.array(l, ci))
                    .where(cb.equal(st.get(Store_.storeId), l.get(StaffList_.sid)))
                    .orderBy(cb.asc(l.get(StaffList_.id)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((StaffList) r[0]).getId(), r -> ((StaffList) r[0]).getName(),
                            r -> ((StaffList) r[0]).getCity(), r -> ((City) r[1]).getCity())
                .containsExactly(
                        tuple(1, "Mike Hillyer", "Lethbridge", "Lethbridge"),
                        tuple(2, "Jon Stephens", "Woodridge", "Woodridge"));
    }

}
