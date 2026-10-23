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
-- Exercises at the intermediate level on the staff_list view: a join, a GROUP BY with HAVING, a subquery, or a NOT
-- EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/staff_list_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA-STAFF_LIST-I-01
-- whether the view has a row for every staff member.
-- +------------+-------+
-- | staff_list | staff |
-- +------------+-------+
-- |          2 |     2 |
-- +------------+-------+
SELECT (SELECT COUNT(*) FROM staff_list) AS staff_list, (SELECT COUNT(*) FROM staff) AS staff
;

-- ---------------------------------------------------------------------------------------------- SAKILA-STAFF_LIST-I-02
-- whether each staff member lives in the city of their store.
-- +----+--------------+------------+------------+
-- | ID | name         | lives_in   | works_in   |
-- +----+--------------+------------+------------+
-- |  1 | Mike Hillyer | Lethbridge | Lethbridge |
-- |  2 | Jon Stephens | Woodridge  | Woodridge  |
-- +----+--------------+------------+------------+
SELECT l.ID, l.name, l.city AS lives_in, ci.city AS works_in
FROM staff_list l
         JOIN store st ON st.store_id = l.SID
         JOIN address a ON a.address_id = st.address_id
         JOIN city ci ON ci.city_id = a.city_id
ORDER BY l.ID
;
