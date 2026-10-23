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
-- Exercises at the basic level on film_text: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_text_exercises_01_basic.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA-FILM_TEXT-B-01
-- the films whose description has both a Crocodile and a Shark.
-- +---------+-------------------+
-- | film_id | title             |
-- +---------+-------------------+
-- |     155 | CLEOPATRA DEVIL   |
-- |     177 | CONNECTICUT TRAMP |
-- |     206 | DANCING FEVER     |
-- |     292 | EXCITEMENT EVE    |
-- |     429 | HONEY TIES        |
-- |     543 | MADIGAN DORADO    |
-- |     790 | SHOOTIST SUPERFLY |
-- |     803 | SLACKER LIAISONS  |
-- |     832 | SPLASH GUMP       |
-- |     959 | WARLOCK WEREWOLF  |
-- +---------+-------------------+
SELECT film_id, title
FROM film_text
WHERE description LIKE '%Crocodile%'
  AND description LIKE '%Shark%'
ORDER BY film_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA-FILM_TEXT-B-02
-- the five longest descriptions.
-- +---------+---------------------+------------+
-- | film_id | title               | characters |
-- +---------+---------------------+------------+
-- |     116 | CANDIDATE PERDITION |        130 |
-- |     217 | DAZED PUNK          |        130 |
-- |     274 | EGG IGBY            |        129 |
-- |     569 | MENAGERIE RUSHMORE  |        128 |
-- |      51 | BALLOON HOMEWARD    |        127 |
-- +---------+---------------------+------------+
SELECT film_id, title, LENGTH(description) AS characters
FROM film_text
ORDER BY characters DESC, film_id
LIMIT 5
;
