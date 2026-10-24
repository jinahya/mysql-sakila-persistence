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
-- Exercises at the advanced level on the actor_info view: several of those together: a self-join, a relational
-- division, correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows
-- what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_info_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA_ACTOR_INFO_A_01
-- the actors whose film_info disagrees with the tables on the number of categories.
-- no rows.
SELECT ai.*
FROM actor_info ai
WHERE CHAR_LENGTH(ai.film_info) - CHAR_LENGTH(REPLACE(ai.film_info, ':', ''))
          <> (SELECT COUNT(DISTINCT fc.category_id)
              FROM film_actor fa
                       JOIN film_category fc ON fc.film_id = fa.film_id
              WHERE fa.actor_id = ai.actor_id)
ORDER BY ai.actor_id
;
