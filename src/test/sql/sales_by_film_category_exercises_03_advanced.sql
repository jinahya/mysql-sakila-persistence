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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/sales_by_film_category_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------- SAKILA-SALES_BY_FILM_CATEGORY-A-01
-- the running total of the categories' sales, and its share of all sales.
-- +-------------+-------------+---------------+-----------------+
-- | category    | total_sales | running_total | running_percent |
-- +-------------+-------------+---------------+-----------------+
-- | Sports      |     5314.21 |       5314.21 |            7.88 |
-- | Sci-Fi      |     4756.98 |      10071.19 |           14.94 |
-- | Animation   |     4656.30 |      14727.49 |           21.85 |
-- | Drama       |     4587.39 |      19314.88 |           28.65 |
-- | Comedy      |     4383.58 |      23698.46 |           35.16 |
-- | Action      |     4375.85 |      28074.31 |           41.65 |
-- | New         |     4351.62 |      32425.93 |           48.11 |
-- | Games       |     4281.33 |      36707.26 |           54.46 |
-- | Foreign     |     4270.67 |      40977.93 |           60.79 |
-- | Family      |     4226.07 |      45204.00 |           67.06 |
-- | Documentary |     4217.52 |      49421.52 |           73.32 |
-- | Horror      |     3722.54 |      53144.06 |           78.84 |
-- | Children    |     3655.55 |      56799.61 |           84.26 |
-- | Classics    |     3639.59 |      60439.20 |           89.66 |
-- | Travel      |     3549.64 |      63988.84 |           94.93 |
-- | Music       |     3417.72 |      67406.56 |          100.00 |
-- +-------------+-------------+---------------+-----------------+
SELECT category, total_sales,
       SUM(total_sales) OVER (ORDER BY total_sales DESC, category) AS running_total,
       ROUND(100 * SUM(total_sales) OVER (ORDER BY total_sales DESC, category) / SUM(total_sales) OVER (), 2)
           AS running_percent
FROM sales_by_film_category
ORDER BY total_sales DESC, category
;

-- ---------------------------------------------------------------------------------- SAKILA-SALES_BY_FILM_CATEGORY-A-02
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
