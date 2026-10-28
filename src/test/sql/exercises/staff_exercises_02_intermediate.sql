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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/staff_exercises_02_intermediate.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA_STAFF_I_01
-- how many rentals each staff member handled.
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+---------+
-- | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password                                 | last_update         | rentals |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+---------+
-- |        1 | Mike       | Hillyer   |          3 | Mike.Hillyer@sakilastaff.com |        1 |      1 | Mike     | 8cb2237d0679ca88db6464eac60da96345513964 | 2006-02-15 03:57:16 |    8040 |
-- |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL                                     | 2006-02-15 03:57:16 |    8004 |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+---------+
SELECT s.staff_id, s.first_name, s.last_name, s.address_id, s.email, s.store_id, s.active, s.username, s.password, s.last_update, COUNT(*) AS rentals -- every column but picture, a BLOB which does not print
FROM staff s
         JOIN rental r ON r.staff_id = s.staff_id
GROUP BY s.staff_id
ORDER BY s.staff_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA_STAFF_I_02
-- the staff members who manage the store they work at.
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+------------------+------------+---------------------+
-- | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password                                 | last_update         | store_id | manager_staff_id | address_id | last_update         |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+------------------+------------+---------------------+
-- |        1 | Mike       | Hillyer   |          3 | Mike.Hillyer@sakilastaff.com |        1 |      1 | Mike     | 8cb2237d0679ca88db6464eac60da96345513964 | 2006-02-15 03:57:16 |        1 |                1 |          1 | 2006-02-15 04:57:12 |
-- |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL                                     | 2006-02-15 03:57:16 |        2 |                2 |          2 | 2006-02-15 04:57:12 |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+----------+------------------+------------+---------------------+
SELECT s.staff_id, s.first_name, s.last_name, s.address_id, s.email, s.store_id, s.active, s.username, s.password, s.last_update, st.* -- every column but picture, a BLOB which does not print
FROM staff s
         JOIN store st ON st.manager_staff_id = s.staff_id
WHERE st.store_id = s.store_id
ORDER BY s.staff_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA_STAFF_I_03
-- how many rentals each staff member handled for their own store's copies, and for the other's.
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+-----------+-------------+
-- | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password                                 | last_update         | own_store | other_store |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+-----------+-------------+
-- |        1 | Mike       | Hillyer   |          3 | Mike.Hillyer@sakilastaff.com |        1 |      1 | Mike     | 8cb2237d0679ca88db6464eac60da96345513964 | 2006-02-15 03:57:16 |      3991 |        4049 |
-- |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL                                     | 2006-02-15 03:57:16 |      4072 |        3932 |
-- +----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+-----------+-------------+
SELECT s.staff_id, s.first_name, s.last_name, s.address_id, s.email, s.store_id, s.active, s.username, s.password, s.last_update, -- every column but picture, a BLOB which does not print
       SUM(CASE WHEN i.store_id = s.store_id THEN 1 ELSE 0 END) AS own_store,
       SUM(CASE WHEN i.store_id <> s.store_id THEN 1 ELSE 0 END) AS other_store
FROM staff s
         JOIN rental r ON r.staff_id = s.staff_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
GROUP BY s.staff_id
ORDER BY s.staff_id
;
