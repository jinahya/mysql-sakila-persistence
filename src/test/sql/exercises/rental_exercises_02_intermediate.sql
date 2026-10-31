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
-- Exercises at the intermediate level on rental: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/rental_exercises_02_intermediate.sql
--


-- -------------------------------------------------------------------------------------------------- SAKILA_RENTAL_I_02
-- on which days the rentals not returned yet were made.
-- +------------+---------+
-- | day        | rentals |
-- +------------+---------+
-- | 2005-08-21 |       1 |
-- | 2006-02-14 |     182 |
-- +------------+---------+
SELECT DATE(rental_date) AS day, COUNT(*) AS rentals
FROM rental
WHERE return_date IS NULL
GROUP BY day
ORDER BY day
;

-- -------------------------------------------------------------------------------------------------- SAKILA_RENTAL_I_03
-- the rentals overdue at the end of the data.
-- +-----------+---------------------+--------------+-------------+-------------+----------+---------------------+------------------+-----------------+--------------+
-- | rental_id | rental_date         | inventory_id | customer_id | return_date | staff_id | last_update         | title            | rental_duration | days_overdue |
-- +-----------+---------------------+--------------+-------------+-------------+----------+---------------------+------------------+-----------------+--------------+
-- |     14098 | 2005-08-21 00:30:32 |            6 |         554 | NULL        |        2 | 2006-02-23 04:12:08 | ACADEMY DINOSAUR |               6 |          172 |
-- +-----------+---------------------+--------------+-------------+-------------+----------+---------------------+------------------+-----------------+--------------+
SELECT r.*, f.title, f.rental_duration, DATEDIFF('2006-02-15', r.rental_date) - f.rental_duration AS days_overdue
FROM rental r
         JOIN inventory i ON i.inventory_id = r.inventory_id
         JOIN film f ON f.film_id = i.film_id
WHERE r.return_date IS NULL
  AND DATEDIFF('2006-02-15', r.rental_date) > f.rental_duration
ORDER BY days_overdue DESC, r.rental_id
;
