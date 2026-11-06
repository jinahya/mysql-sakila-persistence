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
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/sales_by_film_category/02_intermediate.sql
--


-- ---------------------------------------------------------------------------------- SAKILA_SALES_BY_FILM_CATEGORY_I_01
-- whether the view's sales add up to every payment.
-- +-------------+--------------+
-- | by_category | all_payments |
-- +-------------+--------------+
-- |    67406.56 |     67406.56 |
-- +-------------+--------------+
SELECT (SELECT SUM(total_sales) FROM sales_by_film_category) AS by_category,
       (SELECT SUM(amount) FROM payment)                     AS all_payments
;

-- ---------------------------------------------------------------------------------- SAKILA_SALES_BY_FILM_CATEGORY_I_02
-- the five categories which sold the most, and their share of all sales.
-- +-----------+-------------+---------+
-- | category  | total_sales | percent |
-- +-----------+-------------+---------+
-- | Sports    |     5314.21 |    7.88 |
-- | Sci-Fi    |     4756.98 |    7.06 |
-- | Animation |     4656.30 |    6.91 |
-- | Drama     |     4587.39 |    6.81 |
-- | Comedy    |     4383.58 |    6.50 |
-- +-----------+-------------+---------+
SELECT *,
       ROUND(100 * total_sales / (SELECT SUM(total_sales) FROM sales_by_film_category),
             2) AS percent
FROM sales_by_film_category
ORDER BY total_sales DESC, category
LIMIT 5
;

-- ---------------------------------------------------------------------------------- SAKILA_SALES_BY_FILM_CATEGORY_I_03
-- the five categories which sold the most per film.
-- +-----------+-------------+-------+----------+
-- | category  | total_sales | films | per_film |
-- +-----------+-------------+-------+----------+
-- | Sci-Fi    |     4756.98 |    61 |    77.98 |
-- | Comedy    |     4383.58 |    58 |    75.58 |
-- | Drama     |     4587.39 |    62 |    73.99 |
-- | Sports    |     5314.21 |    74 |    71.81 |
-- | Animation |     4656.30 |    66 |    70.55 |
-- +-----------+-------------+-------+----------+
SELECT s.*, COUNT(*) AS films, ROUND(s.total_sales / COUNT(*), 2) AS per_film
FROM sales_by_film_category s
         JOIN category c ON c.name = s.category
         JOIN film_category fc ON fc.category_id = c.category_id
GROUP BY s.category, s.total_sales
ORDER BY per_film DESC, s.category
LIMIT 5
;

-- ---------------------------------------------------------------------------------- SAKILA_SALES_BY_FILM_CATEGORY_I_04
-- the payments which reach no category, or more than one.
-- NOT YET RUN
SELECT p.payment_id, p.amount, COUNT(fc.category_id) AS categories
FROM payment p
         LEFT JOIN rental r ON r.rental_id = p.rental_id
         LEFT JOIN inventory i ON i.inventory_id = r.inventory_id
         LEFT JOIN film_category fc ON fc.film_id = i.film_id
GROUP BY p.payment_id
HAVING COUNT(fc.category_id) <> 1
ORDER BY p.payment_id
;
