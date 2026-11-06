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
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/film_category/02_intermediate.sql
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
-- NOT YET RUN
SELECT c.*,
       COUNT(f.film_id) AS films,
       SUM(CASE WHEN f.rating = 'R' THEN 1 ELSE 0 END) AS r_rated
FROM category c
         LEFT JOIN film_category fc ON fc.category_id = c.category_id
         LEFT JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id
ORDER BY c.name
;
