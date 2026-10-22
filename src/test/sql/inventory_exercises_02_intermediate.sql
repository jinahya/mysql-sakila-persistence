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


-- ---------------------------------------------------------------------------------------------------------- exercise 1
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
ORDER BY copies DESC, f.film_id
LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
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
