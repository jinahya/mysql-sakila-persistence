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
-- Profiling queries on sales_by_film_category, a view, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/sales_by_film_category.sql
--


-- ---------------------------------------------------------------------------------------------------------------------
-- the view sums payment's amount, through rental, inventory, film and film_category, by the category's name.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = DATABASE()
  AND table_name = 'sales_by_film_category'
;


-- ------------------------------------------------------------------------------------------------------------ category
-- how many rows; 16, as many as category has.
SELECT COUNT(*), (SELECT COUNT(*) FROM category) AS categories
FROM sales_by_film_category
;

-- a category more than one row has; the view has no key, and the entity takes category as its identifier.
-- no rows: category is unique.
SELECT category, COUNT(*) AS rows_
FROM sales_by_film_category
GROUP BY category
HAVING COUNT(*) > 1
;


-- --------------------------------------------------------------------------------------------------------- total_sales
-- the rows, in the view's own order, by total_sales, the largest first.
-- Sports, 5314.21, first, and Music, 3417.72, last.
SELECT category, total_sales
FROM sales_by_film_category
;

-- the sum of total_sales against the sum of payment's amount.
-- 67406.56 both; no payment is left out, for none lacks its rental.
SELECT (SELECT SUM(total_sales) FROM sales_by_film_category)  AS total_sales,
       (SELECT SUM(amount) FROM payment)                      AS amount,
       (SELECT COUNT(*) FROM payment WHERE rental_id IS NULL) AS payments_without_rental
;
