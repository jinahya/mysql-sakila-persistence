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
-- Exercises at the intermediate level on the sales_by_store view: a join, a GROUP BY with HAVING, a subquery, or a NOT
-- EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/sales_by_store_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------ SAKILA_SALES_BY_STORE_I_01
-- whether the stores' sales add up to every payment with a rental.
-- +----------+---------------+
-- | by_store | with_a_rental |
-- +----------+---------------+
-- | 67406.56 |      67406.56 |
-- +----------+---------------+
SELECT (SELECT SUM(total_sales) FROM sales_by_store)                 AS by_store,
       (SELECT SUM(amount) FROM payment WHERE rental_id IS NOT NULL) AS with_a_rental
;

-- ------------------------------------------------------------------------------------------ SAKILA_SALES_BY_STORE_I_02
-- each store's sales, next to what its manager took.
-- +---------------------+--------------+-------------+------------------+
-- | store               | manager      | total_sales | taken_by_manager |
-- +---------------------+--------------+-------------+------------------+
-- | Lethbridge,Canada   | Mike Hillyer |    33679.79 |         33482.50 |
-- | Woodridge,Australia | Jon Stephens |    33726.77 |         33924.06 |
-- +---------------------+--------------+-------------+------------------+
SELECT v.*, SUM(p.amount) AS taken_by_manager
FROM sales_by_store v
         JOIN staff s ON CONCAT(s.first_name, ' ', s.last_name) = v.manager
         JOIN payment p ON p.staff_id = s.staff_id
GROUP BY v.store, v.manager, v.total_sales
ORDER BY v.store
;
