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
-- Exercises at the basic level on the sales_by_film_category view: one table: a filter, a sort, a count or an
-- aggregate. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/sales_by_film_category_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the three categories which sold the most.
-- +-----------+-------------+
-- | category  | total_sales |
-- +-----------+-------------+
-- | Sports    |     5314.21 |
-- | Sci-Fi    |     4756.98 |
-- | Animation |     4656.30 |
-- +-----------+-------------+
SELECT category, total_sales
FROM sales_by_film_category
LIMIT 3
;
