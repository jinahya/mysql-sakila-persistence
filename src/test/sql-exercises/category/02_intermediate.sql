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
-- Exercises at the intermediate level on category: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/category/02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_I_01
-- how many films each category has, the most first.
-- +-------------+-------------+---------------------+-------+
-- | category_id | name        | last_update         | films |
-- +-------------+-------------+---------------------+-------+
-- |          15 | Sports      | 2006-02-15 04:46:27 |    74 |
-- |           9 | Foreign     | 2006-02-15 04:46:27 |    73 |
-- |           8 | Family      | 2006-02-15 04:46:27 |    69 |
-- |           6 | Documentary | 2006-02-15 04:46:27 |    68 |
-- |           2 | Animation   | 2006-02-15 04:46:27 |    66 |
-- |           1 | Action      | 2006-02-15 04:46:27 |    64 |
-- |          13 | New         | 2006-02-15 04:46:27 |    63 |
-- |           7 | Drama       | 2006-02-15 04:46:27 |    62 |
-- |          10 | Games       | 2006-02-15 04:46:27 |    61 |
-- |          14 | Sci-Fi      | 2006-02-15 04:46:27 |    61 |
-- |           3 | Children    | 2006-02-15 04:46:27 |    60 |
-- |           5 | Comedy      | 2006-02-15 04:46:27 |    58 |
-- |           4 | Classics    | 2006-02-15 04:46:27 |    57 |
-- |          16 | Travel      | 2006-02-15 04:46:27 |    57 |
-- |          11 | Horror      | 2006-02-15 04:46:27 |    56 |
-- |          12 | Music       | 2006-02-15 04:46:27 |    51 |
-- +-------------+-------------+---------------------+-------+
SELECT c.*, COUNT(fc.film_id) AS films
FROM category c
         LEFT JOIN film_category fc ON fc.category_id = c.category_id
GROUP BY c.category_id
ORDER BY films DESC, c.name
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_I_02
-- the category whose films are, on average, the longest.
-- +-------------+--------+---------------------+----------------+
-- | category_id | name   | last_update         | average_length |
-- +-------------+--------+---------------------+----------------+
-- |          15 | Sports | 2006-02-15 04:46:27 |         128.20 |
-- +-------------+--------+---------------------+----------------+
SELECT c.*, ROUND(AVG(f.length), 2) AS average_length
FROM category c
         JOIN film_category fc ON fc.category_id = c.category_id
         JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id
ORDER BY AVG(f.length) DESC, c.name
LIMIT 1
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_I_03
-- the categories with no film.
-- no rows.
SELECT c.*
FROM category c
WHERE NOT EXISTS (SELECT 1 FROM film_category fc WHERE fc.category_id = c.category_id)
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_I_04
-- the categories whose films rent for more than the average.
-- +-------------+---------+---------------------+---------------------+
-- | category_id | name    | last_update         | average_rental_rate |
-- +-------------+---------+---------------------+---------------------+
-- |          10 | Games   | 2006-02-15 04:46:27 |                3.25 |
-- |          16 | Travel  | 2006-02-15 04:46:27 |                3.24 |
-- |          14 | Sci-Fi  | 2006-02-15 04:46:27 |                3.22 |
-- |           5 | Comedy  | 2006-02-15 04:46:27 |                3.16 |
-- |          15 | Sports  | 2006-02-15 04:46:27 |                3.13 |
-- |          13 | New     | 2006-02-15 04:46:27 |                3.12 |
-- |           9 | Foreign | 2006-02-15 04:46:27 |                3.10 |
-- |          11 | Horror  | 2006-02-15 04:46:27 |                3.03 |
-- |           7 | Drama   | 2006-02-15 04:46:27 |                3.02 |
-- +-------------+---------+---------------------+---------------------+
SELECT c.*, ROUND(AVG(f.rental_rate), 2) AS average_rental_rate
FROM category c
         JOIN film_category fc ON fc.category_id = c.category_id
         JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id
HAVING AVG(f.rental_rate) > (SELECT AVG(rental_rate) FROM film)
ORDER BY average_rental_rate DESC, c.name
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_I_05
-- the five categories with the most different actors.
-- +-------------+-------------+---------------------+--------+
-- | category_id | name        | last_update         | actors |
-- +-------------+-------------+---------------------+--------+
-- |          15 | Sports      | 2006-02-15 04:46:27 |    182 |
-- |           9 | Foreign     | 2006-02-15 04:46:27 |    175 |
-- |          13 | New         | 2006-02-15 04:46:27 |    169 |
-- |           6 | Documentary | 2006-02-15 04:46:27 |    168 |
-- |          14 | Sci-Fi      | 2006-02-15 04:46:27 |    167 |
-- +-------------+-------------+---------------------+--------+
SELECT c.*, COUNT(DISTINCT fa.actor_id) AS actors
FROM category c
         JOIN film_category fc ON fc.category_id = c.category_id
         JOIN film_actor fa ON fa.film_id = fc.film_id
GROUP BY c.category_id
ORDER BY actors DESC, c.name
LIMIT 5
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CATEGORY_I_06
-- the category names which more than one category has.
-- no rows.
SELECT name, COUNT(*) AS categories
FROM category
GROUP BY name
HAVING COUNT(*) > 1
ORDER BY name
;
