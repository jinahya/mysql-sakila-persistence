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
-- Profiling queries on actor, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor.sql
--


-- ------------------------------------------------------------------------------------------------------------ actor_id
-- how many rows, and the smallest and the largest actor_id; 200 actors, 1 to 200.
SELECT COUNT(*), MIN(actor_id), MAX(actor_id)
FROM actor
;

-- an actor_id whose next value is missing, below the largest.
-- no rows: actor_id runs from 1 without a gap.
SELECT a.actor_id + 1 AS missing_from
FROM actor a
WHERE a.actor_id < (SELECT MAX(actor_id) FROM actor)
  AND NOT EXISTS (SELECT 1
                  FROM actor b
                  WHERE b.actor_id = a.actor_id + 1)
;


-- ---------------------------------------------------------------------------------------------------------- first_name
-- the shortest and the longest first_name; 2 and 11 characters.
SELECT MIN(CHAR_LENGTH(first_name)), MAX(CHAR_LENGTH(first_name))
FROM actor
;

-- a first_name which is blank, or carries leading or trailing spaces.
-- no rows: every first_name is trimmed and not blank.
SELECT actor_id, CONCAT('[', first_name, ']') AS first_name
FROM actor
WHERE TRIM(first_name) = ''
   OR CHAR_LENGTH(first_name) <> CHAR_LENGTH(TRIM(first_name))
;

-- the installed data writes first_name in upper case.
-- no rows: every first_name is upper case.
SELECT actor_id, first_name
FROM actor
WHERE NOT REGEXP_LIKE(first_name, '^[^a-z]*$', 'c')
;


-- ----------------------------------------------------------------------------------------------------------- last_name
-- the shortest and the longest last_name; 3 and 12 characters.
SELECT MIN(CHAR_LENGTH(last_name)), MAX(CHAR_LENGTH(last_name))
FROM actor
;

-- a last_name which is blank, or carries leading or trailing spaces.
-- no rows: every last_name is trimmed and not blank.
SELECT actor_id, CONCAT('[', last_name, ']') AS last_name
FROM actor
WHERE TRIM(last_name) = ''
   OR CHAR_LENGTH(last_name) <> CHAR_LENGTH(TRIM(last_name))
;

-- the installed data writes last_name in upper case.
-- no rows: every last_name is upper case.
SELECT actor_id, last_name
FROM actor
WHERE NOT REGEXP_LIKE(last_name, '^[^a-z]*$', 'c')
;

-- the schema has no unique key on the names; actors who share both. One pair: two SUSAN DAVIS.
SELECT first_name, last_name, COUNT(*) AS actors
FROM actor
GROUP BY first_name, last_name
HAVING COUNT(*) > 1
ORDER BY first_name ASC, last_name ASC
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 04:34:33, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM actor
;


-- ---------------------------------------------------------------------------------------------------------------------
-- an actor who plays in no film.
-- no rows: every actor plays in at least one film.
SELECT a.actor_id, a.first_name, a.last_name
FROM actor a
WHERE NOT EXISTS (SELECT 1
                  FROM film_actor fa
                  WHERE fa.actor_id = a.actor_id)
;
