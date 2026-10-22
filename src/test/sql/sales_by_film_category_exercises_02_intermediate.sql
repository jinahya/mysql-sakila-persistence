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
-- Exercises at the intermediate level on the sales_by_film_category view: a join, a GROUP BY with HAVING, a subquery,
-- or a NOT EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/sales_by_film_category_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- whether the view's sales add up to every payment.
-- +-------------+--------------+
-- | by_category | all_payments |
-- +-------------+--------------+
-- |    67406.56 |     67406.56 |
-- +-------------+--------------+
SELECT (SELECT SUM(total_sales) FROM sales_by_film_category) AS by_category,
       (SELECT SUM(amount) FROM payment)                     AS all_payments
;
