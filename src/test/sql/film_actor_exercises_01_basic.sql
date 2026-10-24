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
-- Exercises at the basic level on film_actor: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_actor_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA_FILM_ACTOR_B_01
-- how many films the actor 1 appears in.
-- +-------+
-- | films |
-- +-------+
-- |    19 |
-- +-------+
SELECT COUNT(*) AS films
FROM film_actor
WHERE actor_id = 1
;

-- ---------------------------------------------------------------------------------------------- SAKILA_FILM_ACTOR_B_02
-- how many castings, actors and films the table has.
-- +----------+--------+-------+
-- | castings | actors | films |
-- +----------+--------+-------+
-- |     5462 |    200 |   997 |
-- +----------+--------+-------+
SELECT COUNT(*) AS castings, COUNT(DISTINCT actor_id) AS actors, COUNT(DISTINCT film_id) AS films
FROM film_actor
;
