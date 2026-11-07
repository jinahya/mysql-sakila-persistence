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

import com.github.jinahya.mysql.sakila.persistence.StaffList;
import com.github.jinahya.mysql.sakila.persistence.StaffList_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_STAFF_LIST_B_01} of {@code src/test/sql-exercises/staff_list/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("SAKILA_STAFF_LIST_B_01: every row of the view")
@Slf4j
class SAKILA_STAFF_LIST_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT l
                                                                                  FROM StaffList l
                                                                                  ORDER BY l.id""",
                                                                          StaffList.class)
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list).extracting(StaffList::getId, StaffList::getName)
                .containsExactly(tuple(1, "Mike Hillyer"), tuple(2, "Jon Stephens"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(StaffList.class);
            final var l = cq.from(StaffList.class);
            cq.select(l)
                    .orderBy(cb.asc(l.get(StaffList_.id)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list).extracting(StaffList::getId, StaffList::getName)
                .containsExactly(tuple(1, "Mike Hillyer"), tuple(2, "Jon Stephens"));
    }

}
