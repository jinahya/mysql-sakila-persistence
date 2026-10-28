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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/actor_exercises_02_intermediate.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_I_01
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

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_I_02
-- the top n actors by their number of films, the most first; here n is five.
-- +----------+------------+-----------+---------------------+-------+
-- | actor_id | first_name | last_name | last_update         | films |
-- +----------+------------+-----------+---------------------+-------+
-- |      107 | GINA       | DEGENERES | 2006-02-15 04:34:33 |    42 |
-- |      102 | WALTER     | TORN      | 2006-02-15 04:34:33 |    41 |
-- |      198 | MARY       | KEITEL    | 2006-02-15 04:34:33 |    40 |
-- |      181 | MATTHEW    | CARREY    | 2006-02-15 04:34:33 |    39 |
-- |       23 | SANDRA     | KILMER    | 2006-02-15 04:34:33 |    37 |
-- +----------+------------+-----------+---------------------+-------+
SELECT a.*, COUNT(*) AS films
FROM actor a
         JOIN film_actor fa ON fa.actor_id = a.actor_id
GROUP BY a.actor_id
ORDER BY films DESC, a.actor_id LIMIT 5
;

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_I_03
-- the actors who appear in no film.
-- no rows.
SELECT a.*
FROM actor a
WHERE NOT EXISTS (SELECT 1 FROM film_actor fa WHERE fa.actor_id = a.actor_id)
;

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_I_04
-- the five most common first names.
-- +------------+--------+
-- | first_name | actors |
-- +------------+--------+
-- | JULIA      |      4 |
-- | KENNETH    |      4 |
-- | PENELOPE   |      4 |
-- | BURT       |      3 |
-- | CAMERON    |      3 |
-- +------------+--------+
SELECT first_name, COUNT(*) AS actors
FROM actor
GROUP BY first_name
ORDER BY actors DESC, first_name LIMIT 5
;

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_I_05
-- the five most common last names.
-- +-----------+--------+
-- | last_name | actors |
-- +-----------+--------+
-- | KILMER    |      5 |
-- | NOLTE     |      4 |
-- | TEMPLE    |      4 |
-- | AKROYD    |      3 |
-- | ALLEN     |      3 |
-- +-----------+--------+
SELECT last_name, COUNT(*) AS actors
FROM actor
GROUP BY last_name
ORDER BY actors DESC, last_name LIMIT 5
;

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_I_06
-- the five actors whose films are, on average, the longest.
-- +----------+------------+-----------+---------------------+----------------+
-- | actor_id | first_name | last_name | last_update         | average_length |
-- +----------+------------+-----------+---------------------+----------------+
-- |       35 | JUDY       | DEAN      | 2006-02-15 04:34:33 |         137.73 |
-- |      156 | FAY        | WOOD      | 2006-02-15 04:34:33 |         133.82 |
-- |       16 | FRED       | COSTNER   | 2006-02-15 04:34:33 |         131.56 |
-- |       95 | DARYL      | WAHLBERG  | 2006-02-15 04:34:33 |         130.94 |
-- |        8 | MATTHEW    | JOHANSSON | 2006-02-15 04:34:33 |         130.85 |
-- +----------+------------+-----------+---------------------+----------------+
SELECT a.*, ROUND(AVG(f.length), 2) AS average_length
FROM actor a
         JOIN film_actor fa ON fa.actor_id = a.actor_id
         JOIN film f ON f.film_id = fa.film_id
GROUP BY a.actor_id
ORDER BY average_length DESC, a.actor_id
LIMIT 5
;

-- --------------------------------------------------------------------------------------------------- SAKILA_ACTOR_I_07
-- the films of the first five actors, counted by rating.
-- +----------+------------+--------------+---------------------+------+------+-------+------+-------+
-- | actor_id | first_name | last_name    | last_update         | g    | pg   | pg_13 | r    | nc_17 |
-- +----------+------------+--------------+---------------------+------+------+-------+------+-------+
-- |        1 | PENELOPE   | GUINESS      | 2006-02-15 04:34:33 |    4 |    6 |     1 |    3 |     5 |
-- |        2 | NICK       | WAHLBERG     | 2006-02-15 04:34:33 |    7 |    6 |     2 |    2 |     8 |
-- |        3 | ED         | CHASE        | 2006-02-15 04:34:33 |    2 |    5 |     5 |    5 |     5 |
-- |        4 | JENNIFER   | DAVIS        | 2006-02-15 04:34:33 |    3 |    5 |     5 |    4 |     5 |
-- |        5 | JOHNNY     | LOLLOBRIGIDA | 2006-02-15 04:34:33 |    7 |    4 |     4 |    7 |     7 |
-- +----------+------------+--------------+---------------------+------+------+-------+------+-------+
SELECT a.*,
       SUM(CASE WHEN f.rating = 'G' THEN 1 ELSE 0 END)     AS g,
       SUM(CASE WHEN f.rating = 'PG' THEN 1 ELSE 0 END)    AS pg,
       SUM(CASE WHEN f.rating = 'PG-13' THEN 1 ELSE 0 END) AS pg_13,
       SUM(CASE WHEN f.rating = 'R' THEN 1 ELSE 0 END)     AS r,
       SUM(CASE WHEN f.rating = 'NC-17' THEN 1 ELSE 0 END) AS nc_17
FROM actor a
         JOIN film_actor fa ON fa.actor_id = a.actor_id
         JOIN film f ON f.film_id = fa.film_id
WHERE a.actor_id BETWEEN 1 AND 5
GROUP BY a.actor_id
ORDER BY a.actor_id
;
