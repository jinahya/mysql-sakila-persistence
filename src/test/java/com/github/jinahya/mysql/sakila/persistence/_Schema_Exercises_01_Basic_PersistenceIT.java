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
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers the exercises of {@code src/test/sql/_schema_exercises_01_basic.sql}, against the
 * installed {@code sakila} database; a nested class per exercise, which answers it with JPQL and
 * with the Criteria API, or with a native query where neither can, and logs what it gives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class _Schema_Exercises_01_Basic_PersistenceIT {

    @DisplayName("exercise 1: the tables and the views of the schema")
    @Nested
    class Exercise1Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name, table_type
                                                                                                      FROM information_schema.tables
                                                                                                      WHERE table_schema = ?1
                                                                                                      ORDER BY table_type, table_name""")
                    .setParameter(1, "sakila")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).extracting(r -> ((Object[]) r)[0])
                    .contains("actor", "film", "actor_info", "staff_list");
        }
    }

    @DisplayName("exercise 2: how many rows each table has")
    @Nested
    class Exercise2Test {

        // the exercise counts the rows of the tables, by the tables' names; JPQL knows entities,
        // not
        // tables
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT 'actor' AS table_name, COUNT(*) AS row_count FROM actor
                                                                                                      UNION ALL SELECT 'address', COUNT(*) FROM address
                                                                                                      UNION ALL SELECT 'category', COUNT(*) FROM category
                                                                                                      UNION ALL SELECT 'city', COUNT(*) FROM city
                                                                                                      UNION ALL SELECT 'country', COUNT(*) FROM country
                                                                                                      UNION ALL SELECT 'customer', COUNT(*) FROM customer
                                                                                                      UNION ALL SELECT 'film', COUNT(*) FROM film
                                                                                                      UNION ALL SELECT 'film_actor', COUNT(*) FROM film_actor
                                                                                                      UNION ALL SELECT 'film_category', COUNT(*) FROM film_category
                                                                                                      UNION ALL SELECT 'film_text', COUNT(*) FROM film_text
                                                                                                      UNION ALL SELECT 'inventory', COUNT(*) FROM inventory
                                                                                                      UNION ALL SELECT 'language', COUNT(*) FROM language
                                                                                                      UNION ALL SELECT 'payment', COUNT(*) FROM payment
                                                                                                      UNION ALL SELECT 'rental', COUNT(*) FROM rental
                                                                                                      UNION ALL SELECT 'staff', COUNT(*) FROM staff
                                                                                                      UNION ALL SELECT 'store', COUNT(*) FROM store""")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).hasSize(16).first()
                    .satisfies(r -> assertThat(((Object[]) r)[0]).isEqualTo("actor"));
        }
    }

    @DisplayName("exercise 3: every foreign key: the column, and the column it refers to")
    @Nested
    class Exercise3Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name, column_name, referenced_table_name, referenced_column_name
                                                                                                      FROM information_schema.key_column_usage
                                                                                                      WHERE table_schema = ?1
                                                                                                        AND referenced_table_name IS NOT NULL
                                                                                                      ORDER BY table_name, column_name""")
                    .setParameter(1, "sakila")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).extracting(r -> ((Object[]) r)[0] + "." + ((Object[]) r)[1])
                    .contains("address.city_id", "city.country_id");
        }
    }

    @DisplayName("exercise 4: the tables which have a last_update column")
    @Nested
    class Exercise4Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @ParameterizedTest
        @ValueSource(strings = {"last_update", "name"})
        void nativeQuery(final String column) {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name
                                                                                                      FROM information_schema.columns
                                                                                                      WHERE table_schema = ?1
                                                                                                        AND column_name = ?2
                                                                                                        AND table_name IN (SELECT table_name
                                                                                                                           FROM information_schema.tables
                                                                                                                           WHERE table_schema = ?1 AND table_type = ?3)
                                                                                                      ORDER BY table_name""")
                    .setParameter(1, "sakila")
                    .setParameter(2, column)
                    .setParameter(3, "BASE TABLE")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).isNotEmpty();
        }
    }

    @DisplayName("exercise 5: the triggers, and the table and the event each fires on")
    @Nested
    class Exercise5Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT trigger_name, event_object_table, action_timing, event_manipulation
                                                                                                      FROM information_schema.triggers
                                                                                                      WHERE trigger_schema = ?1
                                                                                                      ORDER BY event_object_table, trigger_name""")
                    .setParameter(1, "sakila")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).extracting(r -> ((Object[]) r)[0]).containsExactly(
                    "customer_create_date", "del_film", "ins_film", "upd_film", "payment_date",
                    "rental_date");
        }
    }

    @DisplayName("exercise 6: the stored procedures and functions")
    @Nested
    class Exercise6Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT routine_name, routine_type
                                                                                                      FROM information_schema.routines
                                                                                                      WHERE routine_schema = ?1
                                                                                                      ORDER BY routine_type, routine_name""")
                    .setParameter(1, "sakila")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list).extracting(r -> ((Object[]) r)[0]).containsExactly(
                    "get_customer_balance", "inventory_held_by_customer", "inventory_in_stock",
                    "film_in_stock",
                    "film_not_in_stock", "rewards_report");
        }
    }

    @DisplayName("exercise 7: the views, and whether each is updatable")
    @Nested
    class Exercise7Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name, is_updatable
                                                                                                      FROM information_schema.views
                                                                                                      WHERE table_schema = ?1
                                                                                                      ORDER BY table_name""")
                    .setParameter(1, "sakila")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0], r -> ((Object[]) r)[1])
                    .containsExactly(
                            tuple("actor_info", "NO"),
                            tuple("customer_list", "YES"),
                            tuple("film_list", "NO"),
                            tuple("nicer_but_slower_film_list", "NO"),
                            tuple("sales_by_film_category", "NO"),
                            tuple("sales_by_store", "NO"),
                            tuple("staff_list", "YES"));
        }
    }

    @DisplayName("exercise 8: the tables whose primary key has more than one column")
    @Nested
    class Exercise8Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name, COUNT(*) AS columns
                                                                                                      FROM information_schema.key_column_usage
                                                                                                      WHERE table_schema = ?1
                                                                                                        AND constraint_name = ?2
                                                                                                      GROUP BY table_name
                                                                                                      HAVING COUNT(*) > 1
                                                                                                      ORDER BY table_name""")
                    .setParameter(1, "sakila")
                    .setParameter(2, "PRIMARY")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0],
                                r -> ((Number) ((Object[]) r)[1]).longValue())
                    .containsExactly(
                            tuple("better_employment", 2L),
                            tuple("film_actor", 2L),
                            tuple("film_category", 2L));
        }
    }

    @DisplayName("exercise 9: the ENUM and SET columns")
    @Nested
    class Exercise9Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name, column_name, column_type
                                                                                                      FROM information_schema.columns
                                                                                                      WHERE table_schema = ?1
                                                                                                        AND data_type IN (?2, ?3)
                                                                                                      ORDER BY table_name, column_name""")
                    .setParameter(1, "sakila")
                    .setParameter(2, "enum")
                    .setParameter(3, "set")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0], r -> ((Object[]) r)[1])
                    .containsExactly(
                            tuple("film", "rating"),
                            tuple("film", "special_features"),
                            tuple("film_list", "rating"),
                            tuple("nicer_but_slower_film_list", "rating"));
        }
    }

    @DisplayName("exercise 10: the foreign keys which do not restrict a delete")
    @Nested
    class Exercise10Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name, constraint_name, referenced_table_name, update_rule, delete_rule
                                                                                                      FROM information_schema.referential_constraints
                                                                                                      WHERE constraint_schema = ?1
                                                                                                        AND delete_rule <> ?2
                                                                                                      ORDER BY table_name, constraint_name""")
                    .setParameter(1, "sakila")
                    .setParameter(2, "RESTRICT")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0], r -> ((Object[]) r)[1],
                                r -> ((Object[]) r)[2], r -> ((Object[]) r)[3],
                                r -> ((Object[]) r)[4])
                    .containsExactly(
                            tuple("payment", "fk_payment_rental", "rental", "CASCADE",
                                  "SET NULL"));
        }
    }

    @DisplayName("exercise 11: the unique keys other than the primary keys, column by column")
    @Nested
    class Exercise11Test {

        // information_schema describes the database, not the entities; JPQL and the Criteria API
        // cannot read it
        @Test
        void nativeQuery() {
            final var list = __Exercises_TestUtils.apply(em -> (List<?>) em.createNativeQuery("""
                                                                                                      SELECT table_name, index_name, seq_in_index, column_name
                                                                                                      FROM information_schema.statistics
                                                                                                      WHERE table_schema = ?1
                                                                                                        AND non_unique = 0
                                                                                                        AND index_name <> ?2
                                                                                                      ORDER BY table_name, index_name, seq_in_index""")
                    .setParameter(1, "sakila")
                    .setParameter(2, "PRIMARY")
                    .getResultList());
            list.forEach(r -> log.debug("{}", r instanceof Object[] a ? Arrays.toString(a) : r));
            assertThat(list)
                    .extracting(r -> ((Object[]) r)[0], r -> ((Object[]) r)[1],
                                r -> ((Number) ((Object[]) r)[2]).intValue(),
                                r -> ((Object[]) r)[3])
                    .containsExactly(
                            tuple("better_employment", "uk_better_employment_manager", 1,
                                  "manager"),
                            tuple("better_employment", "uk_better_employment_manager", 2,
                                  "store_id"),
                            tuple("rental", "rental_date", 1, "rental_date"),
                            tuple("rental", "rental_date", 2, "inventory_id"),
                            tuple("rental", "rental_date", 3, "customer_id"),
                            tuple("store", "idx_unique_manager", 1, "manager_staff_id"));
        }
    }
}
