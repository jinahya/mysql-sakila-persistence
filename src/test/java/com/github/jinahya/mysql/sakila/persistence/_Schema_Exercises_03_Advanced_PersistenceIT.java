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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/_schema_exercises_03_advanced.sql},
 * against the installed {@code sakila} database; a nested class per exercise, which answers it with
 * JPQL and with the Criteria API, or with a native query where neither can, and logs what it
 * gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class _Schema_Exercises_03_Advanced_PersistenceIT {

    @DisplayName(
            "SAKILA_SCHEMA_A_01: the customer who paid the most in each of the five countries which paid " +
            "the most")
    @Nested
    class SAKILA_SCHEMA_A_01_Test {

        // JPQL has no window functions, nor a subquery in the FROM clause; RANK() OVER
        // (PARTITION BY
        // ...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH paid AS (SELECT co.country, cu.customer_id, cu.first_name, cu.last_name, SUM(p.amount) AS total
                                                                                                                    FROM payment p
                                                                                                                             JOIN customer cu ON cu.customer_id = p.customer_id
                                                                                                                             JOIN address a ON a.address_id = cu.address_id
                                                                                                                             JOIN city ci ON ci.city_id = a.city_id
                                                                                                                             JOIN country co ON co.country_id = ci.country_id
                                                                                                                    GROUP BY co.country, cu.customer_id, cu.first_name, cu.last_name),
                                                                                                           ranked AS (SELECT paid.*,
                                                                                                                             RANK() OVER (PARTITION BY country ORDER BY total DESC) AS rank_in_country,
                                                                                                                             SUM(total) OVER (PARTITION BY country) AS country_total
                                                                                                                      FROM paid)
                                                                                                      SELECT country, customer_id, first_name, last_name, total, country_total
                                                                                                      FROM ranked
                                                                                                      WHERE rank_in_country = 1
                                                                                                      ORDER BY country_total DESC
                                                                                                      LIMIT 5""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).extracting(r -> ((Object[]) r)[0])
                    .containsExactly("India", "China", "United States", "Japan", "Mexico");
        }
    }

    @DisplayName("SAKILA_SCHEMA_A_02: the customers who rented a film of every category")
    @Nested
    class SAKILA_SCHEMA_A_02_Test {

        // both keep the columns: eclipselink drops GROUP BY/HAVING from a Criteria entity select
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.customerId, c.firstName, c.lastName
                                                                                      FROM Customer c
                                                                                               JOIN c.rentals r
                                                                                               JOIN r.inventory i
                                                                                               JOIN i.film f
                                                                                               JOIN f.filmCategories fc
                                                                                      GROUP BY c.customerId, c.firstName, c.lastName
                                                                                      HAVING COUNT(DISTINCT fc.categoryId) = (SELECT COUNT(ca) FROM Category ca)
                                                                                      ORDER BY c.customerId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2])
                    .containsExactly(
                            tuple(28, "CYNTHIA", "YOUNG"),
                            tuple(42, "CAROLYN", "PEREZ"),
                            tuple(75, "TAMMY", "SANDERS"),
                            tuple(113, "CINDY", "FISHER"),
                            tuple(144, "CLARA", "SHAW"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Customer.class);
                final var fc =
                        c.join(Customer_.rentals).join(Rental_.inventory).join(Inventory_.film)
                                .join(Film_.filmCategories);
                final var categories = cq.subquery(Long.class);
                final var ca = categories.from(Category.class);
                categories.select(cb.count(ca));
                cq.select(cb.array(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                   c.get(Customer_.lastName)))
                        .groupBy(c.get(Customer_.customerId), c.get(Customer_.firstName),
                                 c.get(Customer_.lastName))
                        .having(cb.equal(cb.countDistinct(fc.get(FilmCategory_.categoryId)),
                                         categories))
                        .orderBy(cb.asc(c.get(Customer_.customerId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2])
                    .containsExactly(
                            tuple(28, "CYNTHIA", "YOUNG"),
                            tuple(42, "CAROLYN", "PEREZ"),
                            tuple(75, "TAMMY", "SANDERS"),
                            tuple(113, "CINDY", "FISHER"),
                            tuple(144, "CLARA", "SHAW"));
        }
    }

    @DisplayName("SAKILA_SCHEMA_A_03: the running total of the payments, on the first five days")
    @Nested
    class SAKILA_SCHEMA_A_03_Test {

        // JPQL has no window functions; SUM() OVER (ORDER BY ...) is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT day, total, SUM(total) OVER (ORDER BY day) AS running_total
                                                                                                      FROM (SELECT DATE(payment_date) AS day, SUM(amount) AS total FROM payment GROUP BY day) t
                                                                                                      ORDER BY day
                                                                                                      LIMIT 5""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).hasSize(5).last().satisfies(
                    r -> assertThat(((BigDecimal) ((Object[]) r)[2]).toPlainString()).isEqualTo(
                            "2846.19"));
        }
    }

    @DisplayName("SAKILA_SCHEMA_A_04: each month's payments, and the change from the month before")
    @Nested
    class SAKILA_SCHEMA_A_04_Test {

        // JPQL has no window functions; LAG() is SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT month, total, total - LAG(total) OVER (ORDER BY month) AS change_from_previous
                                                                                                      FROM (SELECT DATE_FORMAT(payment_date, '%Y-%m') AS month, SUM(amount) AS total FROM payment GROUP BY month) t
                                                                                                      ORDER BY month""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).hasSize(5).first()
                    .satisfies(r -> assertThat(((Object[]) r)[2]).isNull());
        }
    }

    @DisplayName("SAKILA_SCHEMA_A_05: the three films rented the most at each store")
    @Nested
    class SAKILA_SCHEMA_A_05_Test {

        // JPQL has no window functions; ROW_NUMBER() OVER (PARTITION BY ...) is SQL's
        @ParameterizedTest
        @ValueSource(ints = {1, 3})
        void nativeQuery(final int top) {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT store_id, film_id, title, rentals
                                                                                                      FROM (SELECT i.store_id, f.film_id, f.title, COUNT(*) AS rentals,
                                                                                                                   ROW_NUMBER() OVER (PARTITION BY i.store_id ORDER BY COUNT(*) DESC, f.film_id) AS n
                                                                                                            FROM rental r
                                                                                                                     JOIN inventory i ON i.inventory_id = r.inventory_id
                                                                                                                     JOIN film f ON f.film_id = i.film_id
                                                                                                            GROUP BY i.store_id, f.film_id, f.title) t
                                                                                                      WHERE n <= ?1
                                                                                                      ORDER BY store_id, n""")
                    .setParameter(1, top)
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).hasSize(2 * top);
        }
    }

    @DisplayName(
            "SAKILA_SCHEMA_A_06: how many actors are one film, and how many two films, away from KEVIN " +
            "BLOOM; a Bacon number, Sakila having no KEVIN BACON")
    @Nested
    class SAKILA_SCHEMA_A_06_Test {

        // JPQL has no recursive query; WITH RECURSIVE is SQL's
        @ParameterizedTest
        @CsvSource({"KEVIN, BLOOM", "PENELOPE, GUINESS"})
        void nativeQuery(final String firstName, final String lastName) {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH RECURSIVE bacon (actor_id, number) AS (
                                                                                                          SELECT actor_id, 0 FROM actor WHERE first_name = ?1 AND last_name = ?2
                                                                                                          UNION DISTINCT
                                                                                                          SELECT fa2.actor_id, b.number + 1
                                                                                                          FROM bacon b
                                                                                                                   JOIN film_actor fa1 ON fa1.actor_id = b.actor_id
                                                                                                                   JOIN film_actor fa2 ON fa2.film_id = fa1.film_id
                                                                                                          WHERE b.number < 2)
                                                                                                      SELECT number, COUNT(*) AS actors
                                                                                                      FROM (SELECT actor_id, MIN(number) AS number FROM bacon GROUP BY actor_id) t
                                                                                                      WHERE number > 0
                                                                                                      GROUP BY number
                                                                                                      ORDER BY number""")
                    .setParameter(1, firstName)
                    .setParameter(2, lastName)
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).isNotEmpty()
                    .allSatisfy(
                            r -> assertThat(((Number) ((Object[]) r)[0]).intValue()).isBetween(1,
                                                                                               2));
        }
    }

    @DisplayName("SAKILA_SCHEMA_A_07: the films none of whose copies is in, right now")
    @Nested
    class SAKILA_SCHEMA_A_07_Test {

        // JPQL sums no EXISTS; a film all of whose copies are out is one with no copy which is in
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f, COUNT(i)
                                                                                      FROM Film f
                                                                                               JOIN f.inventories i
                                                                                      WHERE NOT EXISTS (SELECT i2
                                                                                                        FROM Inventory i2
                                                                                                        WHERE i2.film = f
                                                                                                          AND NOT EXISTS (SELECT r FROM Rental r WHERE r.inventory = i2 AND r.returnDate IS NULL))
                                                                                      GROUP BY f
                                                                                      ORDER BY f.filmId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isEmpty();
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var f = cq.from(Film.class);
                final var i = f.join(Film_.inventories);
                final var in = cq.subquery(Inventory.class);
                final var i2 = in.from(Inventory.class);
                final var out = in.subquery(Rental.class);
                final var r = out.from(Rental.class);
                out.select(r).where(cb.equal(r.get(Rental_.inventory), i2),
                                    cb.isNull(r.get(Rental_.returnDate)));
                in.select(i2).where(cb.equal(i2.get(Inventory_.film), f), cb.not(cb.exists(out)));
                cq.select(cb.array(f, cb.count(i)))
                        .where(cb.not(cb.exists(in)))
                        .groupBy(f)
                        .orderBy(cb.asc(f.get(Film_.filmId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).isEmpty();
        }
    }

    @DisplayName("SAKILA_SCHEMA_A_08: the five longest runs of days on which a customer rented every day")
    @Nested
    class SAKILA_SCHEMA_A_08_Test {

        // JPQL has no window functions, nor date arithmetic; ROW_NUMBER() OVER (...) and DATE_SUB
        // are SQL's
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      WITH days AS (SELECT DISTINCT customer_id, DATE(rental_date) AS day
                                                                                                                    FROM rental),
                                                                                                           islands AS (SELECT customer_id, day,
                                                                                                                              DATE_SUB(day, INTERVAL ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY day) DAY) AS island
                                                                                                                       FROM days)
                                                                                                      SELECT c.*, MIN(i.day) AS first_day, MAX(i.day) AS last_day, COUNT(*) AS days
                                                                                                      FROM islands i
                                                                                                               JOIN customer c ON c.customer_id = i.customer_id
                                                                                                      GROUP BY c.customer_id, i.island
                                                                                                      ORDER BY days DESC, c.customer_id, first_day
                                                                                                      LIMIT 5""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Number) ((Object[]) r)[0]).intValue(),
                                r -> String.valueOf(((Object[]) r)[9]),
                                r -> String.valueOf(((Object[]) r)[10]),
                                r -> ((Number) ((Object[]) r)[11]).longValue())
                    .containsExactly(
                            tuple(42, "2005-07-26", "2005-08-02", 8L),
                            tuple(181, "2005-07-26", "2005-08-02", 8L),
                            tuple(290, "2005-07-26", "2005-08-02", 8L),
                            tuple(306, "2005-07-26", "2005-08-02", 8L),
                            tuple(373, "2005-07-26", "2005-08-02", 8L));
        }
    }

    @DisplayName("SAKILA_SCHEMA_A_09: the countries none of whose customers rented a Music film")
    @Nested
    class SAKILA_SCHEMA_A_09_Test {

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
}
