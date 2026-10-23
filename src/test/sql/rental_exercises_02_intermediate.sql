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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/rental_exercises_02_intermediate.sql
--


-- -------------------------------------------------------------------------------------------------- SAKILA-RENTAL-I-01
-- how many rentals are not returned yet, at each store.
-- +----------+------------------+------------+---------------------+---------+
-- | store_id | manager_staff_id | address_id | last_update         | rentals |
-- +----------+------------------+------------+---------------------+---------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |      92 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |      91 |
-- +----------+------------------+------------+---------------------+---------+
SELECT st.*, COUNT(*) AS rentals
FROM rental r
         JOIN inventory i ON i.inventory_id = r.inventory_id
         JOIN store st ON st.store_id = i.store_id
WHERE r.return_date IS NULL
GROUP BY st.store_id
ORDER BY st.store_id
;

-- -------------------------------------------------------------------------------------------------- SAKILA-RENTAL-I-02
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
