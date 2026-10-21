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
---
--
-- Profiling queries on film_actor, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_actor.sql
--


-- ------------------------------------------------------------------------------------------------------------ actor_id
-- an actor_id which refers to no actor; the foreign key forbids it.
-- no rows: every actor_id refers to a actor.
SELECT t.film_id, t.actor_id
FROM film_actor t
WHERE NOT EXISTS (SELECT 1
                  FROM actor p
                  WHERE p.actor_id = t.actor_id)
;

-- the actors in the most films; actor 107 is in 42.
SELECT actor_id, COUNT(*) AS films
FROM film_actor
GROUP BY actor_id
ORDER BY films DESC, actor_id ASC
LIMIT 5
;


-- ------------------------------------------------------------------------------------------------------------- film_id
-- a film_id which refers to no film; the foreign key forbids it.
-- no rows: every film_id refers to a film.
SELECT t.actor_id, t.film_id
FROM film_actor t
WHERE NOT EXISTS (SELECT 1
                  FROM film p
                  WHERE p.film_id = t.film_id)
;

-- the films with the most actors; film 508 has 15.
SELECT film_id, COUNT(*) AS actors
FROM film_actor
GROUP BY film_id
ORDER BY actors DESC, film_id ASC
LIMIT 5
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 05:05:03, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM film_actor
;


-- ---------------------------------------------------------------------------------------------------------------------
-- 5462 rows, of all 200 actors and 997 of the 1000 films.
SELECT COUNT(*), COUNT(DISTINCT actor_id), COUNT(DISTINCT film_id)
FROM film_actor
;
