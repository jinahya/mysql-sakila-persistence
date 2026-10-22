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
-- Exercises at the basic level on the actor_info view: one table: a filter, a sort, a count or an aggregate. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_info_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the film_info of the actor 1.
-- +----------+------------+-----------+----------------------------------------------------------------------------------+
-- | actor_id | first_name | last_name | film_info                                                                        |
-- +----------+------------+-----------+----------------------------------------------------------------------------------+
-- |        1 | PENELOPE   | GUINESS   | Animation: ANACONDA CONFESSIONS; Children: LANGUAGE COWBOY; Classics: COLOR PHIL |
-- +----------+------------+-----------+----------------------------------------------------------------------------------+
SELECT actor_id, first_name, last_name, LEFT(film_info, 80) AS film_info
FROM actor_info
WHERE actor_id = 1
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how many actors appear in a Horror film, as film_info says.
-- +--------+
-- | actors |
-- +--------+
-- |    156 |
-- +--------+
SELECT COUNT(*) AS actors
FROM actor_info
WHERE film_info LIKE '%Horror:%'
;
