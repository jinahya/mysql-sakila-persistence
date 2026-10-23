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
-- Exercises at the intermediate level on staff: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/staff_exercises_02_intermediate.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA-STAFF-I-01
-- how many rentals each staff member handled.
-- +----------+------------+---------+
-- | staff_id | first_name | rentals |
-- +----------+------------+---------+
-- |        1 | Mike       |    8040 |
-- |        2 | Jon        |    8004 |
-- +----------+------------+---------+
SELECT s.staff_id, s.first_name, COUNT(*) AS rentals
FROM staff s
         JOIN rental r ON r.staff_id = s.staff_id
GROUP BY s.staff_id, s.first_name
ORDER BY s.staff_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-STAFF-I-02
-- the staff members who manage the store they work at.
-- +----------+------------+-----------+----------+
-- | staff_id | first_name | last_name | store_id |
-- +----------+------------+-----------+----------+
-- |        1 | Mike       | Hillyer   |        1 |
-- |        2 | Jon        | Stephens  |        2 |
-- +----------+------------+-----------+----------+
SELECT s.staff_id, s.first_name, s.last_name, st.store_id
FROM staff s
         JOIN store st ON st.manager_staff_id = s.staff_id
WHERE st.store_id = s.store_id
ORDER BY s.staff_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-STAFF-I-03
-- how many rentals each staff member handled for their own store's copies, and for the other's.
-- +----------+------------+----------+-----------+-------------+
-- | staff_id | first_name | store_id | own_store | other_store |
-- +----------+------------+----------+-----------+-------------+
-- |        1 | Mike       |        1 |      3991 |        4049 |
-- |        2 | Jon        |        2 |      4072 |        3932 |
-- +----------+------------+----------+-----------+-------------+
SELECT s.staff_id, s.first_name, s.store_id,
       SUM(CASE WHEN i.store_id = s.store_id THEN 1 ELSE 0 END) AS own_store,
       SUM(CASE WHEN i.store_id <> s.store_id THEN 1 ELSE 0 END) AS other_store
FROM staff s
         JOIN rental r ON r.staff_id = s.staff_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
GROUP BY s.staff_id, s.first_name, s.store_id
ORDER BY s.staff_id
;
