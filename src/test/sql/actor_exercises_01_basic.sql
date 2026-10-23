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
-- Exercises at the basic level on actor: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/actor_exercises_01_basic.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-B-01
-- the actors whose last name is GUINESS.
-- +----------+------------+-----------+---------------------+
-- | actor_id | first_name | last_name | last_update         |
-- +----------+------------+-----------+---------------------+
-- |        1 | PENELOPE   | GUINESS   | 2006-02-15 04:34:33 |
-- |       90 | SEAN       | GUINESS   | 2006-02-15 04:34:33 |
-- |      179 | ED         | GUINESS   | 2006-02-15 04:34:33 |
-- +----------+------------+-----------+---------------------+
SELECT *
FROM actor
WHERE last_name = 'GUINESS'
ORDER BY actor_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-B-02
-- the actors whose first name starts with JU, ordered by first name.
-- +----------+------------+-----------+---------------------+
-- | actor_id | first_name | last_name | last_update         |
-- +----------+------------+-----------+---------------------+
-- |       57 | JUDE       | CRUISE    | 2006-02-15 04:34:33 |
-- |       35 | JUDY       | DEAN      | 2006-02-15 04:34:33 |
-- |       27 | JULIA      | MCQUEEN   | 2006-02-15 04:34:33 |
-- |       47 | JULIA      | BARRYMORE | 2006-02-15 04:34:33 |
-- |      186 | JULIA      | ZELLWEGER | 2006-02-15 04:34:33 |
-- |      199 | JULIA      | FAWCETT   | 2006-02-15 04:34:33 |
-- |      123 | JULIANNE   | DENCH     | 2006-02-15 04:34:33 |
-- +----------+------------+-----------+---------------------+
SELECT *
FROM actor
WHERE first_name LIKE 'JU%'
ORDER BY first_name, actor_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-B-03
-- the actors whose last name starts with WA, ordered by last name.
-- +----------+------------+-----------+---------------------+
-- | actor_id | first_name | last_name | last_update         |
-- +----------+------------+-----------+---------------------+
-- |        2 | NICK       | WAHLBERG  | 2006-02-15 04:34:33 |
-- |       95 | DARYL      | WAHLBERG  | 2006-02-15 04:34:33 |
-- |      196 | BELA       | WALKEN    | 2006-02-15 04:34:33 |
-- |       29 | ALEC       | WAYNE     | 2006-02-15 04:34:33 |
-- +----------+------------+-----------+---------------------+
SELECT *
FROM actor
WHERE last_name LIKE 'WA%'
ORDER BY last_name, actor_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-B-04
-- the actors whose first and last names start with the same letter.
-- +----------+------------+-------------+---------------------+
-- | actor_id | first_name | last_name   | last_update         |
-- +----------+------------+-------------+---------------------+
-- |       31 | SISSY      | SOBIESKI    | 2006-02-15 04:34:33 |
-- |       54 | PENELOPE   | PINKETT     | 2006-02-15 04:34:33 |
-- |       70 | MICHELLE   | MCCONAUGHEY | 2006-02-15 04:34:33 |
-- |       76 | ANGELINA   | ASTAIRE     | 2006-02-15 04:34:33 |
-- |      114 | MORGAN     | MCDORMAND   | 2006-02-15 04:34:33 |
-- |      131 | JANE       | JACKMAN     | 2006-02-15 04:34:33 |
-- |      135 | RITA       | REYNOLDS    | 2006-02-15 04:34:33 |
-- |      161 | HARVEY     | HOPE        | 2006-02-15 04:34:33 |
-- |      168 | WILL       | WILSON      | 2006-02-15 04:34:33 |
-- |      191 | GREGORY    | GOODING     | 2006-02-15 04:34:33 |
-- |      200 | THORA      | TEMPLE      | 2006-02-15 04:34:33 |
-- +----------+------------+-------------+---------------------+
SELECT *
FROM actor
WHERE LEFT(first_name, 1) = LEFT(last_name, 1)
ORDER BY actor_id
;

-- --------------------------------------------------------------------------------------------------- SAKILA-ACTOR-B-05
-- how many different first names and last names there are.
-- +-------------+------------+
-- | first_names | last_names |
-- +-------------+------------+
-- |         128 |        121 |
-- +-------------+------------+
SELECT COUNT(DISTINCT first_name) AS first_names, COUNT(DISTINCT last_name) AS last_names
FROM actor
;
