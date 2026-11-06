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
-- Exercises at the advanced level on staff_list: a correlated EXISTS across the rentals, customers and their addresses.
-- Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/staff_list/03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA_STAFF_LIST_A_01
-- the staff members who served a customer living outside their own country.
-- +----+--------------+-----------+
-- | ID | name         | country   |
-- +----+--------------+-----------+
-- |  1 | Mike Hillyer | Canada    |
-- |  2 | Jon Stephens | Australia |
-- +----+--------------+-----------+
SELECT l.ID, l.name, l.country
FROM staff_list l
WHERE EXISTS (SELECT 1
              FROM rental r
                       JOIN customer cu ON cu.customer_id = r.customer_id
                       JOIN address a ON a.address_id = cu.address_id
                       JOIN city ci ON ci.city_id = a.city_id
                       JOIN country co ON co.country_id = ci.country_id
              WHERE r.staff_id = l.ID
                AND co.country <> l.country)
ORDER BY l.ID
;
