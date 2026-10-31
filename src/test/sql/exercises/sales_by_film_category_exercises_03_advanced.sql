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
-- Exercises at the advanced level on the sales_by_film_category view: several of those together: a self-join, a
-- relational division, correlated subqueries, or one result checked against another. Each poses a question, answers it,
-- and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/sales_by_film_category_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------- SAKILA_SALES_BY_FILM_CATEGORY_A_02
-- the three largest gaps between a category and the next one down.
-- +-------------+-------------+---------------+--------+
-- | category    | total_sales | next_category | gap    |
-- +-------------+-------------+---------------+--------+
-- | Sports      |     5314.21 | Sci-Fi        | 557.23 |
-- | Documentary |     4217.52 | Horror        | 494.98 |
-- | Drama       |     4587.39 | Comedy        | 203.81 |
-- +-------------+-------------+---------------+--------+
SELECT category, total_sales, next_category, total_sales - next_sales AS gap
FROM (SELECT category, total_sales,
             LEAD(category) OVER (ORDER BY total_sales DESC, category) AS next_category,
             LEAD(total_sales) OVER (ORDER BY total_sales DESC, category) AS next_sales
      FROM sales_by_film_category) g
WHERE next_category IS NOT NULL
ORDER BY gap DESC, category
LIMIT 3
;

-- ---------------------------------------------------------------------------------- SAKILA_SALES_BY_FILM_CATEGORY_A_03
-- each category's rank by sales, next to its rank by rentals.
-- +-------------+-------------+------------+---------+--------------+
-- | category    | total_sales | sales_rank | rentals | rentals_rank |
-- +-------------+-------------+------------+---------+--------------+
-- | Sports      |     5314.21 |          1 |    1179 |            1 |
-- | Sci-Fi      |     4756.98 |          2 |    1101 |            4 |
-- | Animation   |     4656.30 |          3 |    1166 |            2 |
-- | Drama       |     4587.39 |          4 |    1060 |            6 |
-- | Comedy      |     4383.58 |          5 |     941 |           11 |
-- | Action      |     4375.85 |          6 |    1112 |            3 |
-- | New         |     4351.62 |          7 |     940 |           12 |
-- | Games       |     4281.33 |          8 |     969 |            9 |
-- | Foreign     |     4270.67 |          9 |    1033 |            8 |
-- | Family      |     4226.07 |         10 |    1096 |            5 |
-- | Documentary |     4217.52 |         11 |    1050 |            7 |
-- | Horror      |     3722.54 |         12 |     846 |           14 |
-- | Children    |     3655.55 |         13 |     945 |           10 |
-- | Classics    |     3639.59 |         14 |     939 |           13 |
-- | Travel      |     3549.64 |         15 |     837 |           15 |
-- | Music       |     3417.72 |         16 |     830 |           16 |
-- +-------------+-------------+------------+---------+--------------+
WITH rented AS (SELECT c.name AS category, COUNT(*) AS rentals
                FROM rental r
                         JOIN inventory i ON i.inventory_id = r.inventory_id
                         JOIN film_category fc ON fc.film_id = i.film_id
                         JOIN category c ON c.category_id = fc.category_id
                GROUP BY c.name)
SELECT s.*,
       RANK() OVER (ORDER BY s.total_sales DESC) AS sales_rank,
       rd.rentals,
       RANK() OVER (ORDER BY rd.rentals DESC)    AS rentals_rank
FROM sales_by_film_category s
         JOIN rented rd ON rd.category = s.category
ORDER BY sales_rank, s.category
;
