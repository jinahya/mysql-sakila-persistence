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
-- Exercises at the intermediate level on address: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/address_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the first five addresses, with their city and country.
-- +------------+----------------------+------------+-----------+
-- | address_id | address              | city       | country   |
-- +------------+----------------------+------------+-----------+
-- |          1 | 47 MySakila Drive    | Lethbridge | Canada    |
-- |          2 | 28 MySQL Boulevard   | Woodridge  | Australia |
-- |          3 | 23 Workhaven Lane    | Lethbridge | Canada    |
-- |          4 | 1411 Lillydale Drive | Woodridge  | Australia |
-- |          5 | 1913 Hanoi Way       | Sasebo     | Japan     |
-- +------------+----------------------+------------+-----------+
SELECT a.address_id, a.address, ci.city, co.country
FROM address a
         JOIN city ci ON ci.city_id = a.city_id
         JOIN country co ON co.country_id = ci.country_id
ORDER BY a.address_id
LIMIT 5
;
