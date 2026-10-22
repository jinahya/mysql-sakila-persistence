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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/category_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- how many films each category has, the most first.
-- +-------------+-------+
-- | name        | films |
-- +-------------+-------+
-- | Sports      |    74 |
-- | Foreign     |    73 |
-- | Family      |    69 |
-- | Documentary |    68 |
-- | Animation   |    66 |
-- +-------------+-------+
-- ... 16 rows in all.
SELECT c.name, COUNT(*) AS films
FROM category c
         JOIN film_category fc ON fc.category_id = c.category_id
GROUP BY c.category_id, c.name
ORDER BY films DESC, c.name
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- the category whose films are, on average, the longest.
-- +--------+----------------+
-- | name   | average_length |
-- +--------+----------------+
-- | Sports |         128.20 |
-- +--------+----------------+
SELECT c.name, ROUND(AVG(f.length), 2) AS average_length
FROM category c
         JOIN film_category fc ON fc.category_id = c.category_id
         JOIN film f ON f.film_id = fc.film_id
GROUP BY c.category_id, c.name
ORDER BY average_length DESC LIMIT 1
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
-- the categories with no film.
-- no rows.
SELECT c.*
FROM category c
WHERE NOT EXISTS (SELECT 1 FROM film_category fc WHERE fc.category_id = c.category_id)
;
