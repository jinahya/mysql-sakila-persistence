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
-- Exercises at the advanced level on the customer_list view: several of those together: a self-join, a relational
-- division, correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows
-- what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/customer_list_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_A_01
-- the three countries with the most customers of each store.
-- +-----+---------------+-----------+
-- | SID | country       | customers |
-- +-----+---------------+-----------+
-- |   1 | India         |        37 |
-- |   1 | China         |        26 |
-- |   1 | United States |        22 |
-- |   2 | China         |        27 |
-- |   2 | India         |        23 |
-- |   2 | Mexico        |        15 |
-- +-----+---------------+-----------+
SELECT SID, country, customers
FROM (SELECT SID, country, COUNT(*) AS customers,
             ROW_NUMBER() OVER (PARTITION BY SID ORDER BY COUNT(*) DESC, country) AS n
      FROM customer_list
      GROUP BY SID, country) t
WHERE n <= 3
ORDER BY SID, n
;
