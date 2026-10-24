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
-- Exercises at the intermediate level on film_category: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS.
-- Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_category_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------- SAKILA_FILM_CATEGORY_I_01
-- the films with more than one category.
-- no rows.
SELECT f.*, COUNT(*) AS categories
FROM film_category fc
         JOIN film f ON f.film_id = fc.film_id
GROUP BY f.film_id
HAVING COUNT(*) > 1
;

-- ------------------------------------------------------------------------------------------- SAKILA_FILM_CATEGORY_I_02
-- how many films have no category.
-- +-------+
-- | films |
-- +-------+
-- |     0 |
-- +-------+
SELECT COUNT(*) AS films
FROM film f
WHERE NOT EXISTS (SELECT 1 FROM film_category fc WHERE fc.film_id = f.film_id)
;

-- ------------------------------------------------------------------------------------------- SAKILA_FILM_CATEGORY_I_03
-- how many films each category has, and how many of them are rated R.
-- +-------------+-------------+---------------------+-------+---------+
-- | category_id | name        | last_update         | films | r_rated |
-- +-------------+-------------+---------------------+-------+---------+
-- |           1 | Action      | 2006-02-15 04:46:27 |    64 |      14 |
-- |           2 | Animation   | 2006-02-15 04:46:27 |    66 |       8 |
-- |           3 | Children    | 2006-02-15 04:46:27 |    60 |       9 |
-- |           4 | Classics    | 2006-02-15 04:46:27 |    57 |      14 |
-- |           5 | Comedy      | 2006-02-15 04:46:27 |    58 |       8 |
-- |           6 | Documentary | 2006-02-15 04:46:27 |    68 |      13 |
-- |           7 | Drama       | 2006-02-15 04:46:27 |    62 |       6 |
-- |           8 | Family      | 2006-02-15 04:46:27 |    69 |      16 |
-- |           9 | Foreign     | 2006-02-15 04:46:27 |    73 |      15 |
-- |          10 | Games       | 2006-02-15 04:46:27 |    61 |      15 |
-- |          11 | Horror      | 2006-02-15 04:46:27 |    56 |      14 |
-- |          12 | Music       | 2006-02-15 04:46:27 |    51 |      11 |
-- |          13 | New         | 2006-02-15 04:46:27 |    63 |       9 |
-- |          14 | Sci-Fi      | 2006-02-15 04:46:27 |    61 |      17 |
-- |          15 | Sports      | 2006-02-15 04:46:27 |    74 |      16 |
-- |          16 | Travel      | 2006-02-15 04:46:27 |    57 |      10 |
-- +-------------+-------------+---------------------+-------+---------+
SELECT c.*,
       COUNT(*) AS films,
       SUM(CASE WHEN f.rating = 'R' THEN 1 ELSE 0 END) AS r_rated
FROM film_category fc
         JOIN category c ON c.category_id = fc.category_id
         JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id
ORDER BY c.name
;

-- ------------------------------------------------------------------------------------------- SAKILA_FILM_CATEGORY_I_04
-- the categories whose films cost more to replace than the average film.
-- +-------------+-----------+---------------------+--------------------------+
-- | category_id | name      | last_update         | average_replacement_cost |
-- +-------------+-----------+---------------------+--------------------------+
-- |          14 | Sci-Fi    | 2006-02-15 04:46:27 |                21.153934 |
-- |           7 | Drama     | 2006-02-15 04:46:27 |                21.086774 |
-- |           4 | Classics  | 2006-02-15 04:46:27 |                21.007544 |
-- |           1 | Action    | 2006-02-15 04:46:27 |                20.911875 |
-- |          15 | Sports    | 2006-02-15 04:46:27 |                20.395405 |
-- |          10 | Games     | 2006-02-15 04:46:27 |                20.285082 |
-- |           2 | Animation | 2006-02-15 04:46:27 |                20.126364 |
-- |           3 | Children  | 2006-02-15 04:46:27 |                20.056667 |
-- +-------------+-----------+---------------------+--------------------------+
SELECT c.*, AVG(f.replacement_cost) AS average_replacement_cost
FROM film_category fc
         JOIN category c ON c.category_id = fc.category_id
         JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id
HAVING AVG(f.replacement_cost) > (SELECT AVG(replacement_cost) FROM film)
ORDER BY average_replacement_cost DESC, c.name
;
