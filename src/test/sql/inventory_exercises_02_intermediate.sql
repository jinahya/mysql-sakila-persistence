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
-- Exercises at the intermediate level on inventory: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/inventory_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-I-01
-- the five films with the most copies.
-- +---------+------------------+--------+
-- | film_id | title            | copies |
-- +---------+------------------+--------+
-- |       1 | ACADEMY DINOSAUR |      8 |
-- |      31 | APACHE DIVINE    |      8 |
-- |      69 | BEVERLY OUTLAW   |      8 |
-- |      73 | BINGO TALENTED   |      8 |
-- |      86 | BOOGIE AMELIE    |      8 |
-- +---------+------------------+--------+
SELECT f.film_id, f.title, COUNT(*) AS copies
FROM film f
         JOIN inventory i ON i.film_id = f.film_id
GROUP BY f.film_id, f.title
ORDER BY copies DESC, f.film_id LIMIT 5
;

-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-I-02
-- how many copies were never rented.
-- +--------+
-- | copies |
-- +--------+
-- |      1 |
-- +--------+
SELECT COUNT(*) AS copies
FROM inventory i
WHERE NOT EXISTS (SELECT 1 FROM rental r WHERE r.inventory_id = i.inventory_id)
;

-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-I-03
-- how many copies each store has out right now.
-- +----------+------------+
-- | store_id | copies_out |
-- +----------+------------+
-- |        1 |         92 |
-- |        2 |         91 |
-- +----------+------------+
SELECT i.store_id, COUNT(*) AS copies_out
FROM inventory i
         JOIN rental r ON r.inventory_id = i.inventory_id
WHERE r.return_date IS NULL
GROUP BY i.store_id
ORDER BY i.store_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-I-04
-- how many copies of each rating each store has.
-- +----------+--------+--------+
-- | store_id | rating | copies |
-- +----------+--------+--------+
-- |        1 | G      |    394 |
-- |        1 | PG     |    444 |
-- |        1 | PG-13  |    525 |
-- |        1 | R      |    442 |
-- |        1 | NC-17  |    465 |
-- |        2 | G      |    397 |
-- |        2 | PG     |    480 |
-- |        2 | PG-13  |    493 |
-- |        2 | R      |    462 |
-- |        2 | NC-17  |    479 |
-- +----------+--------+--------+
SELECT i.store_id, f.rating, COUNT(*) AS copies
FROM inventory i
         JOIN film f ON f.film_id = i.film_id
GROUP BY i.store_id, f.rating
ORDER BY i.store_id, f.rating
;
