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
-- Profiling queries on actor_info, a view, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_info.sql
--


-- ---------------------------------------------------------------------------------------------------------------------
-- the view groups actor, left-joined with film_actor, film_category and category, by the actor.
-- what the view defines; a row per actor, with the films the actor plays in, by category.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = DATABASE()
  AND table_name = 'actor_info'
;


-- ------------------------------------------------------------------------------------------------------------ actor_id
-- how many rows, and the smallest and the largest actor_id; 200 rows, 1 to 200, as many as actor has.
SELECT COUNT(*), MIN(actor_id), MAX(actor_id), (SELECT COUNT(*) FROM actor) AS actors
FROM actor_info
;

-- an actor_id more than one row has; the view has no key, and the entity takes actor_id as its identifier.
-- no rows: actor_id is unique.
SELECT actor_id, COUNT(*) AS rows_
FROM actor_info
GROUP BY actor_id
HAVING COUNT(*) > 1
;

-- an actor with no row in the view.
-- no rows: every actor has a row.
SELECT a.actor_id
FROM actor a
WHERE NOT EXISTS (SELECT 1
                  FROM actor_info v
                  WHERE v.actor_id = a.actor_id)
;


-- ---------------------------------------------------------------------------------------------------------- first_name
-- the shortest and the longest first_name; 2 and 11 characters, as in actor.
SELECT MIN(CHAR_LENGTH(first_name)), MAX(CHAR_LENGTH(first_name))
FROM actor_info
;


-- ----------------------------------------------------------------------------------------------------------- last_name
-- the shortest and the longest last_name; 3 and 12 characters, as in actor.
SELECT MIN(CHAR_LENGTH(last_name)), MAX(CHAR_LENGTH(last_name))
FROM actor_info
;

-- a row whose names are not those of its actor.
-- no rows: the view copies both names.
SELECT v.actor_id, v.first_name, v.last_name, a.first_name, a.last_name
FROM actor_info v
         JOIN actor a ON a.actor_id = v.actor_id
WHERE CAST(v.first_name AS BINARY) <> CAST(a.first_name AS BINARY)
   OR CAST(v.last_name AS BINARY) <> CAST(a.last_name AS BINARY)
;


-- ----------------------------------------------------------------------------------------------------------- film_info
-- film_info is nullable, a GROUP_CONCAT of '<category>: <title>, ...' joined with '; '; how many rows have none.
-- none is NULL or empty, for every actor plays in at least one film; 292 to 830 characters.
SELECT SUM(film_info IS NULL),
       SUM(film_info = ''),
       MIN(CHAR_LENGTH(film_info)),
       MAX(CHAR_LENGTH(film_info))
FROM actor_info
;

-- GROUP_CONCAT cuts its result at group_concat_max_len; a film_info that long may have been cut.
-- no rows: with the default 1024, the longest, 830, is not cut.
SELECT actor_id, CHAR_LENGTH(film_info) AS length
FROM actor_info
WHERE CHAR_LENGTH(film_info) >= @@group_concat_max_len
;

-- a film_info whose number of categories is not that of the actor's films.
-- no rows: film_info has a part per category.
SELECT v.actor_id, v.categories, c.categories
FROM (SELECT actor_id,
             CHAR_LENGTH(film_info) - CHAR_LENGTH(REPLACE(film_info, ';', '')) + 1 AS categories
      FROM actor_info) v
         JOIN (SELECT fa.actor_id, COUNT(DISTINCT fc.category_id) AS categories
               FROM film_actor fa
                        JOIN film_category fc ON fc.film_id = fa.film_id
               GROUP BY fa.actor_id) c ON c.actor_id = v.actor_id
WHERE v.categories <> c.categories
;
