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
import com.github.jinahya.mysql.sakila.persistence.City_;
import com.github.jinahya.mysql.sakila.persistence.Country_;
import com.github.jinahya.mysql.sakila.persistence.Customer_;
import com.github.jinahya.mysql.sakila.persistence.Rental;
import com.github.jinahya.mysql.sakila.persistence.Rental_;
import com.github.jinahya.mysql.sakila.persistence.StaffList;
import com.github.jinahya.mysql.sakila.persistence.StaffList_;
import com.github.jinahya.mysql.sakila.persistence.Staff_;
import com.github.jinahya.mysql.sakila.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code SAKILA_STAFF_LIST_A_01} of
 * {@code src/test/sql-exercises/staff_list/03_advanced.sql} with JPQL and with the Criteria API, or
 * with a native query where neither can.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName(
        "SAKILA_STAFF_LIST_A_01: the staff members who served a customer living outside their " +
        "own country")
@Slf4j
class SAKILA_STAFF_LIST_A_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                  SELECT l
                                                                                  FROM StaffList l
                                                                                  WHERE EXISTS (SELECT r
                                                                                                FROM Rental r
                                                                                                         JOIN r.customer cu
                                                                                                         JOIN cu.address a
                                                                                                         JOIN a.city ci
                                                                                                         JOIN ci.country co
                                                                                                WHERE r.staff.staffId = l.id
                                                                                                  AND co.country <> l.country)
                                                                                  ORDER BY l.id""",
                                                                          StaffList.class)
                .getResultList());
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(StaffList::getId, StaffList::getName, StaffList::getCountry)
                .containsExactly(
                        tuple(1, "Mike Hillyer", "Canada"),
                        tuple(2, "Jon Stephens", "Australia"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(StaffList.class);
            final var l = cq.from(StaffList.class);
            final var rentals = cq.subquery(Rental.class);
            final var r = rentals.from(Rental.class);
            final var co = r.join(Rental_.customer)
                    .join(Customer_.address)
                    .join(Address_.city)
                    .join(City_.country);
            rentals.select(r)
                    .where(cb.equal(r.get(Rental_.staff).get(Staff_.staffId),
                                    l.get(StaffList_.id)),
                           cb.notEqual(co.get(Country_.country),
                                       l.get(StaffList_.country)));
            cq.select(l)
                    .where(cb.exists(rentals))
                    .orderBy(cb.asc(l.get(StaffList_.id)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(StaffList::getId, StaffList::getName, StaffList::getCountry)
                .containsExactly(
                        tuple(1, "Mike Hillyer", "Canada"),
                        tuple(2, "Jon Stephens", "Australia"));
    }

}
