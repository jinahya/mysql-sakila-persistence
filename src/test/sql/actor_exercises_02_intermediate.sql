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
-- Exercises at the intermediate level on actor: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the last names shared by more than three actors, the most shared first.
-- +-----------+--------+
-- | last_name | actors |
-- +-----------+--------+
-- | KILMER    |      5 |
-- | NOLTE     |      4 |
-- | TEMPLE    |      4 |
-- +-----------+--------+
SELECT last_name, COUNT(*) AS actors
FROM actor
GROUP BY last_name
HAVING COUNT(*) > 3
ORDER BY actors DESC, last_name
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- the five actors who appear in the most films.
-- +----------+------------+-----------+-------+
-- | actor_id | first_name | last_name | films |
-- +----------+------------+-----------+-------+
-- |      107 | GINA       | DEGENERES |    42 |
-- |      102 | WALTER     | TORN      |    41 |
-- |      198 | MARY       | KEITEL    |    40 |
-- |      181 | MATTHEW    | CARREY    |    39 |
-- |       23 | SANDRA     | KILMER    |    37 |
-- +----------+------------+-----------+-------+
SELECT a.actor_id, a.first_name, a.last_name, COUNT(*) AS films
FROM actor a
         JOIN film_actor fa ON fa.actor_id = a.actor_id
GROUP BY a.actor_id, a.first_name, a.last_name
ORDER BY films DESC, a.actor_id
LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
-- the actors who appear in no film.
-- no rows.
SELECT a.*
FROM actor a
WHERE NOT EXISTS (SELECT 1 FROM film_actor fa WHERE fa.actor_id = a.actor_id)
;
