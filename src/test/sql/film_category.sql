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
-- Profiling queries on film_category, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_category.sql
--


-- ------------------------------------------------------------------------------------------------------------- film_id
-- a film_id which refers to no film; the foreign key forbids it.
-- no rows: every film_id refers to a film.
SELECT t.category_id, t.film_id
FROM film_category t
WHERE NOT EXISTS (SELECT 1
                  FROM film p
                  WHERE p.film_id = t.film_id)
;

-- a film in more than one category; the key allows it.
-- no rows: every film is in one category alone.
SELECT film_id, COUNT(*) AS categories
FROM film_category
GROUP BY film_id
HAVING COUNT(*) > 1
;


-- --------------------------------------------------------------------------------------------------------- category_id
-- a category_id which refers to no category; the foreign key forbids it.
-- no rows: every category_id refers to a category.
SELECT t.film_id, t.category_id
FROM film_category t
WHERE NOT EXISTS (SELECT 1
                  FROM category p
                  WHERE p.category_id = t.category_id)
;

-- how many films each category_id has; every category, 51 to 74 films each.
SELECT category_id, COUNT(*) AS films
FROM film_category
GROUP BY category_id
ORDER BY category_id ASC
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 05:07:09, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM film_category
;


-- ---------------------------------------------------------------------------------------------------------------------
-- 1000 rows, one per film, over all 16 categories.
SELECT COUNT(*), COUNT(DISTINCT film_id), COUNT(DISTINCT category_id)
FROM film_category
;
