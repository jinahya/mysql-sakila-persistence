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


-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-I-01
-- the films with more than one category.
-- no rows.
SELECT film_id, COUNT(*) AS categories
FROM film_category
GROUP BY film_id
HAVING COUNT(*) > 1
;

-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-I-02
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

-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-I-03
-- how many films each category has, and how many of them are rated R.
-- +-------------+-------+---------+
-- | name        | films | r_rated |
-- +-------------+-------+---------+
-- | Action      |    64 |      14 |
-- | Animation   |    66 |       8 |
-- | Children    |    60 |       9 |
-- | Classics    |    57 |      14 |
-- | Comedy      |    58 |       8 |
-- | Documentary |    68 |      13 |
-- | Drama       |    62 |       6 |
-- | Family      |    69 |      16 |
-- | Foreign     |    73 |      15 |
-- | Games       |    61 |      15 |
-- | Horror      |    56 |      14 |
-- | Music       |    51 |      11 |
-- | New         |    63 |       9 |
-- | Sci-Fi      |    61 |      17 |
-- | Sports      |    74 |      16 |
-- | Travel      |    57 |      10 |
-- +-------------+-------+---------+
SELECT c.name,
       COUNT(*) AS films,
       SUM(CASE WHEN f.rating = 'R' THEN 1 ELSE 0 END) AS r_rated
FROM film_category fc
         JOIN category c ON c.category_id = fc.category_id
         JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id, c.name
ORDER BY c.name
;

-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-I-04
-- the categories whose films cost more to replace than the average film.
-- +-----------+--------------------------+
-- | name      | average_replacement_cost |
-- +-----------+--------------------------+
-- | Sci-Fi    |                21.153934 |
-- | Drama     |                21.086774 |
-- | Classics  |                21.007544 |
-- | Action    |                20.911875 |
-- | Sports    |                20.395405 |
-- | Games     |                20.285082 |
-- | Animation |                20.126364 |
-- | Children  |                20.056667 |
-- +-----------+--------------------------+
SELECT c.name, AVG(f.replacement_cost) AS average_replacement_cost
FROM film_category fc
         JOIN category c ON c.category_id = fc.category_id
         JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id, c.name
HAVING AVG(f.replacement_cost) > (SELECT AVG(replacement_cost) FROM film)
ORDER BY average_replacement_cost DESC, c.name
;
