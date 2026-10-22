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
-- Exercises at the intermediate level on film_actor: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_actor_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the five films with the most actors.
-- +---------+-------------------+--------+
-- | film_id | title             | actors |
-- +---------+-------------------+--------+
-- |     508 | LAMBS CINCINATTI  |     15 |
-- |      87 | BOONDOCK BALLROOM |     13 |
-- |     146 | CHITTY LOCK       |     13 |
-- |     188 | CRAZY HOME        |     13 |
-- |     249 | DRACULA CRYSTAL   |     13 |
-- +---------+-------------------+--------+
SELECT f.film_id, f.title, COUNT(*) AS actors
FROM film f
         JOIN film_actor fa ON fa.film_id = f.film_id
GROUP BY f.film_id, f.title
ORDER BY actors DESC, f.film_id LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how many actors a film has, on average.
-- +----------------+
-- | average_actors |
-- +----------------+
-- |           5.48 |
-- +----------------+
SELECT ROUND(AVG(actors), 2) AS average_actors
FROM (SELECT COUNT(*) AS actors FROM film_actor GROUP BY film_id) t
;
