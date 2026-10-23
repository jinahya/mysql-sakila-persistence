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


-- --------------------------------------------------------------------------------------------------- SAKILA-STORE-I-01
-- every store, with its manager and city.
-- +----------+------------+-----------+------------+
-- | store_id | first_name | last_name | city       |
-- +----------+------------+-----------+------------+
-- |        1 | Mike       | Hillyer   | Lethbridge |
-- |        2 | Jon        | Stephens  | Woodridge  |
-- +----------+------------+-----------+------------+
SELECT st.store_id, s.first_name, s.last_name, ci.city
FROM store st
         JOIN staff s ON s.staff_id = st.manager_staff_id
         JOIN address a ON a.address_id = st.address_id
         JOIN city ci ON ci.city_id = a.city_id
ORDER BY st.store_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-STORE-I-02
-- how many customers, copies and staff members each store has.
-- +----------+-----------+--------+-------+
-- | store_id | customers | copies | staff |
-- +----------+-----------+--------+-------+
-- |        1 |       326 |   2270 |     1 |
-- |        2 |       273 |   2311 |     1 |
-- +----------+-----------+--------+-------+
SELECT st.store_id,
       (SELECT COUNT(*) FROM customer c WHERE c.store_id = st.store_id)  AS customers,
       (SELECT COUNT(*) FROM inventory i WHERE i.store_id = st.store_id) AS copies,
       (SELECT COUNT(*) FROM staff s WHERE s.store_id = st.store_id)     AS staff
FROM store st
ORDER BY st.store_id
;
