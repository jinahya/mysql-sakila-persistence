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
-- Exercises at the basic level on city: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/city_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the cities whose name starts with Q.
-- +---------+-------------+------------+---------------------+
-- | city_id | city        | country_id | last_update         |
-- +---------+-------------+------------+---------------------+
-- |     421 | Qalyub      |         29 | 2006-02-15 04:45:25 |
-- |     422 | Qinhuangdao |         23 | 2006-02-15 04:45:25 |
-- |     423 | Qomsheh     |         46 | 2006-02-15 04:45:25 |
-- |     424 | Quilmes     |          6 | 2006-02-15 04:45:25 |
-- +---------+-------------+------------+---------------------+
SELECT *
FROM city
WHERE city LIKE 'Q%'
ORDER BY city
;
