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

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.LocalDateTimeField;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/_schema_exercises_02_intermediate.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class _Schema_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("exercise 1: how much each store took, through the copies it rented out")
    @Nested
    class Exercise1Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT i.storeId, COUNT(p), SUM(p.amount)
                                                                                      FROM Payment p
                                                                                               JOIN p.rental r
                                                                                               JOIN r.inventory i
                                                                                      GROUP BY i.storeId
                                                                                      ORDER BY i.storeId""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple(1, 7923L, "33679.79"),
                            tuple(2, 8121L, "33726.77"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var i = p.join(Payment_.rental).join(Rental_.inventory);
                cq.select(cb.array(i.get(Inventory_.storeId), cb.count(p),
                                   cb.sum(p.get(Payment_.amount))))
                        .groupBy(i.get(Inventory_.storeId))
                        .orderBy(cb.asc(i.get(Inventory_.storeId)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple(1, 7923L, "33679.79"),
                            tuple(2, 8121L, "33726.77"));
        }
    }

    @DisplayName("exercise 2: how many rentals each category had, the most first")
    @Nested
    class Exercise2Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.name, COUNT(r)
                                                                                      FROM Rental r
                                                                                               JOIN r.inventory i
                                                                                               JOIN i.film f
                                                                                               JOIN f.filmCategories fc
                                                                                               JOIN fc.category c
                                                                                      GROUP BY c.categoryId, c.name
                                                                                      ORDER BY COUNT(r) DESC, c.name""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(16);
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .startsWith(tuple("Sports", 1179L), tuple("Animation", 1166L),
                                tuple("Action", 1112L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var c =
                        r.join(Rental_.inventory).join(Inventory_.film).join(Film_.filmCategories)
                                .join(FilmCategory_.category);
                cq.select(cb.array(c.get(Category_.name), cb.count(r)))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name))
                        .orderBy(cb.desc(cb.count(r)), cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list).hasSize(16);
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .startsWith(tuple("Sports", 1179L), tuple("Animation", 1166L),
                                tuple("Action", 1112L));
        }
    }

    @DisplayName("exercise 3: the five countries whose customers paid the most")
    @Nested
    class Exercise3Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT co.country, COUNT(DISTINCT cu), SUM(p.amount)
                                                                                      FROM Payment p
                                                                                               JOIN p.customer cu
                                                                                               JOIN cu.address a
                                                                                               JOIN a.city ci
                                                                                               JOIN ci.country co
                                                                                      GROUP BY co.countryId, co.country
                                                                                      ORDER BY SUM(p.amount) DESC""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("India", 60L, "6628.28"),
                            tuple("China", 53L, "5798.74"),
                            tuple("United States", 36L, "4110.32"),
                            tuple("Japan", 31L, "3470.75"),
                            tuple("Mexico", 30L, "3307.04"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var cu = p.join(Payment_.customer);
                final var co = cu.join(Customer_.address).join(Address_.city).join(City_.country);
                cq.select(cb.array(co.get(Country_.country), cb.countDistinct(cu),
                                   cb.sum(p.get(Payment_.amount))))
                        .groupBy(co.get(Country_.countryId), co.get(Country_.country))
                        .orderBy(cb.desc(cb.sum(p.get(Payment_.amount))));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("India", 60L, "6628.28"),
                            tuple("China", 53L, "5798.74"),
                            tuple("United States", 36L, "4110.32"),
                            tuple("Japan", 31L, "3470.75"),
                            tuple("Mexico", 30L, "3307.04"));
        }
    }

    @DisplayName("exercise 4: the average payment of each rating")
    @Nested
    class Exercise4Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT f.rating, ROUND(AVG(p.amount), 2)
                                                                                      FROM Payment p
                                                                                               JOIN p.rental r
                                                                                               JOIN r.inventory i
                                                                                               JOIN i.film f
                                                                                      GROUP BY f.rating
                                                                                      ORDER BY f.rating""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 4.21d),
                            tuple(Film.Rating.PG, 4.15d),
                            tuple(Film.Rating.PG_13, 4.26d),
                            tuple(Film.Rating.R, 4.17d),
                            tuple(Film.Rating.NC_17, 4.21d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var f = p.join(Payment_.rental).join(Rental_.inventory).join(Inventory_.film);
                cq.select(
                                cb.array(f.get(Film_.rating),
                                         cb.round(cb.avg(p.get(Payment_.amount)), 2)))
                        .groupBy(f.get(Film_.rating))
                        .orderBy(cb.asc(f.get(Film_.rating)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple(Film.Rating.G, 4.21d),
                            tuple(Film.Rating.PG, 4.15d),
                            tuple(Film.Rating.PG_13, 4.26d),
                            tuple(Film.Rating.R, 4.17d),
                            tuple(Film.Rating.NC_17, 4.21d));
        }
    }

    @DisplayName("exercise 5: the five actors whose films took the most")
    @Nested
    class Exercise5Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT a.actorId, a.firstName, a.lastName, SUM(p.amount)
                                                                                      FROM Actor a
                                                                                               JOIN a.filmActors fa
                                                                                               JOIN fa.film f
                                                                                               JOIN f.inventories i
                                                                                               JOIN i.rentals r
                                                                                               JOIN r.payments p
                                                                                      GROUP BY a.actorId, a.firstName, a.lastName
                                                                                      ORDER BY SUM(p.amount) DESC, a.actorId""",
                                                                              Object[].class)
                    .setMaxResults(5)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", "3442.49"),
                            tuple(181, "MATTHEW", "CARREY", "2742.19"),
                            tuple(198, "MARY", "KEITEL", "2689.25"),
                            tuple(81, "SCARLETT", "DAMON", "2655.28"),
                            tuple(102, "WALTER", "TORN", "2620.62"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var a = cq.from(Actor.class);
                final var p =
                        a.join(Actor_.filmActors).join(FilmActor_.film).join(Film_.inventories)
                                .join(Inventory_.rentals)
                                .join(Rental_.payments);
                cq.select(cb.array(a.get(Actor_.actorId), a.get(Actor_.firstName),
                                   a.get(Actor_.lastName), cb.sum(p.get(Payment_.amount))))
                        .groupBy(a.get(Actor_.actorId), a.get(Actor_.firstName),
                                 a.get(Actor_.lastName))
                        .orderBy(cb.desc(cb.sum(p.get(Payment_.amount))),
                                 cb.asc(a.get(Actor_.actorId)));
                return em.createQuery(cq)
                        .setMaxResults(5)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> ((Number) r[0]).intValue(), r -> r[1], r -> r[2],
                                r -> ((BigDecimal) r[3]).toPlainString())
                    .containsExactly(
                            tuple(107, "GINA", "DEGENERES", "3442.49"),
                            tuple(181, "MATTHEW", "CARREY", "2742.19"),
                            tuple(198, "MARY", "KEITEL", "2689.25"),
                            tuple(81, "SCARLETT", "DAMON", "2655.28"),
                            tuple(102, "WALTER", "TORN", "2620.62"));
        }
    }

    @DisplayName("exercise 6: the customers who have returned everything they rented, how many")
    @Nested
    class Exercise6Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(c)
                                                                                       FROM Customer c
                                                                                       WHERE NOT EXISTS (SELECT r FROM Rental r WHERE r.customer = c AND r.returnDate IS NULL)""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(440L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var c = cq.from(Customer.class);
                final var out = cq.subquery(Rental.class);
                final var r = out.from(Rental.class);
                out.select(r).where(cb.equal(r.get(Rental_.customer), c),
                                    cb.isNull(r.get(Rental_.returnDate)));
                cq.select(cb.count(c))
                        .where(cb.not(cb.exists(out)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(440L);
        }
    }

    @DisplayName("exercise 7: how many rentals, and how much was paid, in each month")
    @Nested
    class Exercise7Test {

        // JPQL has no DATE_FORMAT; the year and the month are EXTRACTed, and written as one in Java
        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate), COUNT(r), SUM(p.amount)
                                                                                      FROM Rental r
                                                                                               LEFT JOIN r.payments p
                                                                                      GROUP BY EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate)
                                                                                      ORDER BY EXTRACT(YEAR FROM r.rentalDate), EXTRACT(MONTH FROM r.rentalDate)""",
                                                                              Object[].class)
                    .getResultStream()
                    // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                    .map(row -> new Object[]{String.format("%d-%02d", row[0],
                                                           row[1]), row[2], row[3]})
                    .toList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1156L, "4823.44"),
                            tuple("2005-06", 2311L, "9629.89"),
                            tuple("2005-07", 6709L, "28368.91"),
                            tuple("2005-08", 5686L, "24070.14"),
                            tuple("2006-02", 182L, "514.18"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var p = r.join(Rental_.payments, JoinType.LEFT);
                final var year = cb.extract(LocalDateTimeField.YEAR, r.get(Rental_.rentalDate));
                final var month = cb.extract(LocalDateTimeField.MONTH, r.get(Rental_.rentalDate));
                cq.select(cb.array(year, month, cb.count(r), cb.sum(p.get(Payment_.amount))))
                        .groupBy(year, month)
                        .orderBy(cb.asc(year), cb.asc(month));
                return em.createQuery(cq).getResultStream()
                        // year and month, as mysql's DATE_FORMAT(..., '%Y-%m') writes them
                        .map(row -> new Object[]{String.format("%d-%02d", row[0],
                                                               row[1]), row[2], row[3]})
                        .toList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("2005-05", 1156L, "4823.44"),
                            tuple("2005-06", 2311L, "9629.89"),
                            tuple("2005-07", 6709L, "28368.91"),
                            tuple("2005-08", 5686L, "24070.14"),
                            tuple("2006-02", 182L, "514.18"));
        }
    }

    @DisplayName("exercise 8: the customers who rented from a store other than their own, how many")
    @Nested
    class Exercise8Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(DISTINCT c)
                                                                                       FROM Customer c
                                                                                                JOIN c.rentals r
                                                                                                JOIN r.inventory i
                                                                                       WHERE i.storeId <> c.store.storeId""",
                                                                               Long.class)
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(599L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var c = cq.from(Customer.class);
                final var i = c.join(Customer_.rentals).join(Rental_.inventory);
                cq.select(cb.countDistinct(c))
                        .where(cb.notEqual(i.get(Inventory_.storeId),
                                           c.get(Customer_.store).get(Store_.storeId)));
                return em.createQuery(cq)
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(599L);
        }
    }

    @DisplayName(
            "exercise 9: how many rentals, and how much was paid, for short, medium and long " +
            "films")
    @Nested
    class Exercise9Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT CASE WHEN f.length < 60 THEN 'short' WHEN f.length <= 120 THEN 'medium' ELSE 'long' END,
                                                                                             COUNT(r), SUM(p.amount)
                                                                                      FROM Rental r
                                                                                               JOIN r.inventory i
                                                                                               JOIN i.film f
                                                                                               LEFT JOIN r.payments p
                                                                                      GROUP BY CASE WHEN f.length < 60 THEN 'short' WHEN f.length <= 120 THEN 'medium' ELSE 'long' END
                                                                                      ORDER BY MIN(f.length)""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("short", 1531L, "6191.68"),
                            tuple("medium", 7213L, "30346.87"),
                            tuple("long", 7300L, "30868.01"));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var r = cq.from(Rental.class);
                final var f = r.join(Rental_.inventory).join(Inventory_.film);
                final var p = r.join(Rental_.payments, JoinType.LEFT);
                final var group = cb.<String>selectCase()
                        .when(cb.lt(f.get(Film_.length), 60), "short")
                        .when(cb.le(f.get(Film_.length), 120), "medium")
                        .otherwise("long");
                cq.select(cb.array(group, cb.count(r), cb.sum(p.get(Payment_.amount))))
                        .groupBy(group)
                        .orderBy(cb.asc(cb.min(f.get(Film_.length))));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue(),
                                r -> ((BigDecimal) r[2]).toPlainString())
                    .containsExactly(
                            tuple("short", 1531L, "6191.68"),
                            tuple("medium", 7213L, "30346.87"),
                            tuple("long", 7300L, "30868.01"));
        }
    }

    @DisplayName("exercise 10: how many films of each category were never rented")
    @Nested
    class Exercise10Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.name, COUNT(fc)
                                                                                      FROM Category c
                                                                                               JOIN c.filmCategories fc
                                                                                      WHERE NOT EXISTS (SELECT i FROM Inventory i JOIN i.rentals r WHERE i.filmId = fc.filmId)
                                                                                      GROUP BY c.categoryId, c.name
                                                                                      ORDER BY COUNT(fc) DESC, c.name""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Foreign", 6L),
                            tuple("Documentary", 5L),
                            tuple("Travel", 4L),
                            tuple("Action", 3L),
                            tuple("Classics", 3L),
                            tuple("Games", 3L),
                            tuple("Horror", 3L),
                            tuple("New", 3L),
                            tuple("Animation", 2L),
                            tuple("Children", 2L),
                            tuple("Comedy", 2L),
                            tuple("Family", 2L),
                            tuple("Sci-Fi", 2L),
                            tuple("Drama", 1L),
                            tuple("Sports", 1L));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var c = cq.from(Category.class);
                final var fc = c.join(Category_.filmCategories);
                final var rented = cq.subquery(Inventory.class);
                final var i = rented.from(Inventory.class);
                i.join(Inventory_.rentals);
                rented.select(i)
                        .where(cb.equal(i.get(Inventory_.filmId), fc.get(FilmCategory_.filmId)));
                cq.select(cb.array(c.get(Category_.name), cb.count(fc)))
                        .where(cb.not(cb.exists(rented)))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name))
                        .orderBy(cb.desc(cb.count(fc)), cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                    .containsExactly(
                            tuple("Foreign", 6L),
                            tuple("Documentary", 5L),
                            tuple("Travel", 4L),
                            tuple("Action", 3L),
                            tuple("Classics", 3L),
                            tuple("Games", 3L),
                            tuple("Horror", 3L),
                            tuple("New", 3L),
                            tuple("Animation", 2L),
                            tuple("Children", 2L),
                            tuple("Comedy", 2L),
                            tuple("Family", 2L),
                            tuple("Sci-Fi", 2L),
                            tuple("Drama", 1L),
                            tuple("Sports", 1L));
        }
    }

    @DisplayName("exercise 11: the actors who appear in both a Comedy and a Horror film, how many")
    @Nested
    class Exercise11Test {

        @Test
        void __QueryLanguage() {
            final var value = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                       SELECT COUNT(a)
                                                                                       FROM Actor a
                                                                                       WHERE EXISTS (SELECT fa FROM FilmActor fa JOIN fa.film f JOIN f.filmCategories fc JOIN fc.category c WHERE fa.actor = a AND c.name = :first)
                                                                                         AND EXISTS (SELECT fa FROM FilmActor fa JOIN fa.film f JOIN f.filmCategories fc JOIN fc.category c WHERE fa.actor = a AND c.name = :second)""",
                                                                               Long.class)
                    .setParameter("first", "Comedy")
                    .setParameter("second", "Horror")
                    .getSingleResult());
            log.debug("{}", value);
            assertThat(value).isEqualTo(115L);
        }

        @Test
        void __CriteriaApi() {
            final var value = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Long.class);
                final var a = cq.from(Actor.class);
                final var first = cb.parameter(String.class, "first");
                final var second = cb.parameter(String.class, "second");
                final var comedy = cq.subquery(FilmActor.class);
                final var fa1 = comedy.from(FilmActor.class);
                final var c1 = fa1.join(FilmActor_.film).join(Film_.filmCategories)
                        .join(FilmCategory_.category);
                comedy.select(fa1).where(cb.equal(fa1.get(FilmActor_.actor), a),
                                         cb.equal(c1.get(Category_.name), first));
                final var horror = cq.subquery(FilmActor.class);
                final var fa2 = horror.from(FilmActor.class);
                final var c2 = fa2.join(FilmActor_.film).join(Film_.filmCategories)
                        .join(FilmCategory_.category);
                horror.select(fa2).where(cb.equal(fa2.get(FilmActor_.actor), a),
                                         cb.equal(c2.get(Category_.name), second));
                cq.select(cb.count(a))
                        .where(cb.exists(comedy), cb.exists(horror));
                return em.createQuery(cq)
                        .setParameter(first, "Comedy")
                        .setParameter(second, "Horror")
                        .getSingleResult();
            });
            log.debug("{}", value);
            assertThat(value).isEqualTo(115L);
        }
    }

    @DisplayName(
            "exercise 12: the categories whose payments are, on average, larger than all " +
            "payments")
    @Nested
    class Exercise12Test {

        @Test
        void __QueryLanguage() {
            final var list = __Exercises_TestUtils.apply(em -> em.createQuery("""
                                                                                      SELECT c.name, ROUND(AVG(p.amount), 2)
                                                                                      FROM Payment p
                                                                                               JOIN p.rental r
                                                                                               JOIN r.inventory i
                                                                                               JOIN i.film f
                                                                                               JOIN f.filmCategories fc
                                                                                               JOIN fc.category c
                                                                                      GROUP BY c.categoryId, c.name
                                                                                      HAVING AVG(p.amount) > (SELECT AVG(p2.amount) FROM Payment p2)
                                                                                      ORDER BY AVG(p.amount) DESC, c.name""",
                                                                              Object[].class)
                    .getResultList());
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Comedy", 4.66d),
                            tuple("New", 4.63d),
                            tuple("Sports", 4.51d),
                            tuple("Games", 4.42d),
                            tuple("Horror", 4.40d),
                            tuple("Drama", 4.33d),
                            tuple("Sci-Fi", 4.32d),
                            tuple("Travel", 4.24d));
        }

        @Test
        void __CriteriaApi() {
            final var list = __Exercises_TestUtils.apply(em -> {
                final var cb = em.getCriteriaBuilder();
                final var cq = cb.createQuery(Object[].class);
                final var p = cq.from(Payment.class);
                final var c = p.join(Payment_.rental).join(Rental_.inventory).join(Inventory_.film)
                        .join(Film_.filmCategories).join(FilmCategory_.category);
                final var all = cq.subquery(Double.class);
                final var p2 = all.from(Payment.class);
                all.select(cb.avg(p2.get(Payment_.amount)));
                final var average = cb.avg(p.get(Payment_.amount));
                cq.select(cb.array(c.get(Category_.name), cb.round(average, 2)))
                        .groupBy(c.get(Category_.categoryId), c.get(Category_.name))
                        .having(cb.gt(average, all))
                        .orderBy(cb.desc(average), cb.asc(c.get(Category_.name)));
                return em.createQuery(cq)
                        .getResultList();
            });
            list.forEach(r -> log.debug("{}", Arrays.toString(r)));
            assertThat(list)
                    .extracting(r -> r[0], r -> ((Number) r[1]).doubleValue())
                    .containsExactly(
                            tuple("Comedy", 4.66d),
                            tuple("New", 4.63d),
                            tuple("Sports", 4.51d),
                            tuple("Games", 4.42d),
                            tuple("Horror", 4.40d),
                            tuple("Drama", 4.33d),
                            tuple("Sci-Fi", 4.32d),
                            tuple("Travel", 4.24d));
        }
    }
}
