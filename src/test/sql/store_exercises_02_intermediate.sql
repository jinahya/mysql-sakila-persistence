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
-- Exercises at the intermediate level on store: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/store_exercises_02_intermediate.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA_STORE_I_01
-- every store, with its manager and city.
-- +----------+------------------+------------+---------------------+----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+---------+------------+------------+---------------------+
-- | store_id | manager_staff_id | address_id | last_update         | staff_id | first_name | last_name | address_id | email                        | store_id | active | username | password                                 | last_update         | city_id | city       | country_id | last_update         |
-- +----------+------------------+------------+---------------------+----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+---------+------------+------------+---------------------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |        1 | Mike       | Hillyer   |          3 | Mike.Hillyer@sakilastaff.com |        1 |      1 | Mike     | 8cb2237d0679ca88db6464eac60da96345513964 | 2006-02-15 03:57:16 |     300 | Lethbridge |         20 | 2006-02-15 04:45:25 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |        2 | Jon        | Stephens  |          4 | Jon.Stephens@sakilastaff.com |        2 |      1 | Jon      | NULL                                     | 2006-02-15 03:57:16 |     576 | Woodridge  |          8 | 2006-02-15 04:45:25 |
-- +----------+------------------+------------+---------------------+----------+------------+-----------+------------+------------------------------+----------+--------+----------+------------------------------------------+---------------------+---------+------------+------------+---------------------+
SELECT st.*, s.staff_id, s.first_name, s.last_name, s.address_id, s.email, s.store_id, s.active, s.username, s.password, s.last_update, ci.* -- every column but picture, a BLOB which does not print
FROM store st
         JOIN staff s ON s.staff_id = st.manager_staff_id
         JOIN address a ON a.address_id = st.address_id
         JOIN city ci ON ci.city_id = a.city_id
ORDER BY st.store_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA_STORE_I_02
-- how many customers, copies and staff members each store has.
-- +----------+------------------+------------+---------------------+-----------+--------+-------+
-- | store_id | manager_staff_id | address_id | last_update         | customers | copies | staff |
-- +----------+------------------+------------+---------------------+-----------+--------+-------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |       326 |   2270 |     1 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |       273 |   2311 |     1 |
-- +----------+------------------+------------+---------------------+-----------+--------+-------+
SELECT st.*,
       (SELECT COUNT(*) FROM customer c WHERE c.store_id = st.store_id)  AS customers,
       (SELECT COUNT(*) FROM inventory i WHERE i.store_id = st.store_id) AS copies,
       (SELECT COUNT(*) FROM staff s WHERE s.store_id = st.store_id)     AS staff
FROM store st
ORDER BY st.store_id
;
