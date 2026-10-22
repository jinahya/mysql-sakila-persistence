-- -
-- #%L
-- mysql-sakila-persistence
-- %%
-- Copyright (C) 2026 Jinahya, Inc.
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
-- -
--
-- Exercises at the basic level on the structure of the schema, as information_schema describes it, not on a single
-- table; the exercises on each table and view are in its own {table}_exercises.sql. Each poses a question, answers it,
-- and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/_schema_01_basic_exercises.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the tables and the views of the schema.
-- +----------------------------+------------+
-- | TABLE_NAME                 | TABLE_TYPE |
-- +----------------------------+------------+
-- | actor                      | BASE TABLE |
-- | address                    | BASE TABLE |
-- | better_employment          | BASE TABLE |
-- | better_staff               | BASE TABLE |
-- | better_store               | BASE TABLE |
-- +----------------------------+------------+
-- ... 26 rows in all.
SELECT table_name, table_type
FROM information_schema.tables
WHERE table_schema = 'sakila'
ORDER BY table_type, table_name
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how many rows each table has.
-- +---------------+-----------+
-- | table_name    | row_count |
-- +---------------+-----------+
-- | actor         |       200 |
-- | address       |       603 |
-- | category      |        16 |
-- | city          |       600 |
-- | country       |       109 |
-- +---------------+-----------+
-- ... 16 rows in all.
SELECT 'actor' AS table_name, COUNT(*) AS row_count
FROM actor
UNION ALL
SELECT 'address', COUNT(*)
FROM address
UNION ALL
SELECT 'category', COUNT(*)
FROM category
UNION ALL
SELECT 'city', COUNT(*)
FROM city
UNION ALL
SELECT 'country', COUNT(*)
FROM country
UNION ALL
SELECT 'customer', COUNT(*)
FROM customer
UNION ALL
SELECT 'film', COUNT(*)
FROM film
UNION ALL
SELECT 'film_actor', COUNT(*)
FROM film_actor
UNION ALL
SELECT 'film_category', COUNT(*)
FROM film_category
UNION ALL
SELECT 'film_text', COUNT(*)
FROM film_text
UNION ALL
SELECT 'inventory', COUNT(*)
FROM inventory
UNION ALL
SELECT 'language', COUNT(*)
FROM language
UNION ALL
SELECT 'payment', COUNT(*)
FROM payment
UNION ALL
SELECT 'rental', COUNT(*)
FROM rental
UNION ALL
SELECT 'staff', COUNT(*)
FROM staff
UNION ALL
SELECT 'store', COUNT(*)
FROM store
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
-- every foreign key: the column, and the column it refers to.
-- +-------------------+----------------------+-----------------------+------------------------+
-- | TABLE_NAME        | COLUMN_NAME          | REFERENCED_TABLE_NAME | REFERENCED_COLUMN_NAME |
-- +-------------------+----------------------+-----------------------+------------------------+
-- | address           | city_id              | city                  | city_id                |
-- | better_employment | staff_id             | better_staff          | id                     |
-- | better_employment | store_id             | better_store          | id                     |
-- | city              | country_id           | country               | country_id             |
-- | customer          | address_id           | address               | address_id             |
-- +-------------------+----------------------+-----------------------+------------------------+
-- ... 24 rows in all.
SELECT table_name, column_name, referenced_table_name, referenced_column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'sakila'
  AND referenced_table_name IS NOT NULL
ORDER BY table_name, column_name
;

-- ---------------------------------------------------------------------------------------------------------- exercise 4
-- the tables which have a last_update column.
-- +---------------+
-- | TABLE_NAME    |
-- +---------------+
-- | actor         |
-- | address       |
-- | category      |
-- | city          |
-- | country       |
-- +---------------+
-- ... 15 rows in all.
SELECT table_name
FROM information_schema.columns
WHERE table_schema = 'sakila'
  AND column_name = 'last_update'
  AND table_name IN (SELECT table_name
                     FROM information_schema.tables
                     WHERE table_schema = 'sakila'
                       AND table_type = 'BASE TABLE')
ORDER BY table_name
;

-- ---------------------------------------------------------------------------------------------------------- exercise 5
-- the triggers, and the table and the event each fires on.
-- +----------------------+--------------------+---------------+--------------------+
-- | TRIGGER_NAME         | EVENT_OBJECT_TABLE | ACTION_TIMING | EVENT_MANIPULATION |
-- +----------------------+--------------------+---------------+--------------------+
-- | customer_create_date | customer           | BEFORE        | INSERT             |
-- | del_film             | film               | AFTER         | DELETE             |
-- | ins_film             | film               | AFTER         | INSERT             |
-- | upd_film             | film               | AFTER         | UPDATE             |
-- | payment_date         | payment            | BEFORE        | INSERT             |
-- | rental_date          | rental             | BEFORE        | INSERT             |
-- +----------------------+--------------------+---------------+--------------------+
SELECT trigger_name, event_object_table, action_timing, event_manipulation
FROM information_schema.triggers
WHERE trigger_schema = 'sakila'
ORDER BY event_object_table, trigger_name
;

-- ---------------------------------------------------------------------------------------------------------- exercise 6
-- the stored procedures and functions.
-- +----------------------------+--------------+
-- | ROUTINE_NAME               | ROUTINE_TYPE |
-- +----------------------------+--------------+
-- | get_customer_balance       | FUNCTION     |
-- | inventory_held_by_customer | FUNCTION     |
-- | inventory_in_stock         | FUNCTION     |
-- | film_in_stock              | PROCEDURE    |
-- | film_not_in_stock          | PROCEDURE    |
-- | rewards_report             | PROCEDURE    |
-- +----------------------------+--------------+
SELECT routine_name, routine_type
FROM information_schema.routines
WHERE routine_schema = 'sakila'
ORDER BY routine_type, routine_name
;
