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
-- Exercises at the basic level on inventory: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/inventory_exercises_01_basic.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-B-01
-- how many copies each store has.
-- +----------+--------+
-- | store_id | copies |
-- +----------+--------+
-- |        1 |   2270 |
-- |        2 |   2311 |
-- +----------+--------+
SELECT store_id, COUNT(*) AS copies
FROM inventory
GROUP BY store_id
ORDER BY store_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-B-02
-- the copies of the film 1.
-- +--------------+----------+
-- | inventory_id | store_id |
-- +--------------+----------+
-- |            1 |        1 |
-- |            2 |        1 |
-- |            3 |        1 |
-- |            4 |        1 |
-- |            5 |        2 |
-- |            6 |        2 |
-- |            7 |        2 |
-- |            8 |        2 |
-- +--------------+----------+
SELECT inventory_id, store_id
FROM inventory
WHERE film_id = 1
ORDER BY inventory_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA-INVENTORY-B-03
-- how many copies of the films 1 to 5 each store has.
-- +---------+----------+--------+
-- | film_id | store_id | copies |
-- +---------+----------+--------+
-- |       1 |        1 |      4 |
-- |       1 |        2 |      4 |
-- |       2 |        2 |      3 |
-- |       3 |        2 |      4 |
-- |       4 |        1 |      4 |
-- |       4 |        2 |      3 |
-- |       5 |        2 |      3 |
-- +---------+----------+--------+
SELECT film_id, store_id, COUNT(*) AS copies
FROM inventory
WHERE film_id BETWEEN 1 AND 5
GROUP BY film_id, store_id
ORDER BY film_id, store_id
;
