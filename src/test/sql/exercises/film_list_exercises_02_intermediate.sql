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
-- Exercises at the intermediate level on the film_list view: a join, a GROUP BY with HAVING, a subquery, or a NOT
-- EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/film_list_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_I_01
-- whether the view has a row for every film which has a category.
-- +-----------+-----------------------+
-- | film_list | films_with_a_category |
-- +-----------+-----------------------+
-- |      1000 |                  1000 |
-- +-----------+-----------------------+
SELECT (SELECT COUNT(*) FROM film_list)                    AS film_list,
       (SELECT COUNT(DISTINCT film_id) FROM film_category) AS films_with_a_category
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_I_02
-- how many films of each rating there are at each price.
-- +--------+------------+------------+------------+
-- | rating | price_0_99 | price_2_99 | price_4_99 |
-- +--------+------------+------------+------------+
-- | G      |         64 |         59 |         55 |
-- | PG     |         62 |         64 |         68 |
-- | PG-13  |         72 |         74 |         77 |
-- | R      |         70 |         60 |         65 |
-- | NC-17  |         73 |         66 |         71 |
-- +--------+------------+------------+------------+
SELECT rating,
       SUM(CASE WHEN price = 0.99 THEN 1 ELSE 0 END) AS price_0_99,
       SUM(CASE WHEN price = 2.99 THEN 1 ELSE 0 END) AS price_2_99,
       SUM(CASE WHEN price = 4.99 THEN 1 ELSE 0 END) AS price_4_99
FROM film_list
GROUP BY rating
ORDER BY rating
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_I_03
-- the films whose actors string lists a different number of actors than film_actor.
-- no rows.
SELECT l.*
FROM film_list l
WHERE l.actors IS NOT NULL
  AND LENGTH(l.actors) - LENGTH(REPLACE(l.actors, ',', '')) + 1
          <> (SELECT COUNT(*) FROM film_actor fa WHERE fa.film_id = l.FID)
ORDER BY l.FID
;
