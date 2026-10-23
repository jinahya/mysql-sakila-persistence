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
-- Exercises at the basic level on the film_list view: one table: a filter, a sort, a count or an aggregate. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_list_exercises_01_basic.sql
--


-- ----------------------------------------------------------------------------------------------- SAKILA-FILM_LIST-B-01
-- how many Action films there are.
-- +-------+
-- | films |
-- +-------+
-- |    64 |
-- +-------+
SELECT COUNT(*) AS films
FROM film_list
WHERE category = 'Action'
;

-- ----------------------------------------------------------------------------------------------- SAKILA-FILM_LIST-B-02
-- the actors of the film 1.
-- +-----+------------------+------------------------------------------------------------------------------------------------------------------------------------------------+
-- | FID | title            | actors                                                                                                                                         |
-- +-----+------------------+------------------------------------------------------------------------------------------------------------------------------------------------+
-- |   1 | ACADEMY DINOSAUR | PENELOPE GUINESS, CHRISTIAN GABLE, LUCILLE TRACY, SANDRA PECK, JOHNNY CAGE, MENA TEMPLE, WARREN NOLTE, OPRAH KILMER, ROCK DUKAKIS, MARY KEITEL |
-- +-----+------------------+------------------------------------------------------------------------------------------------------------------------------------------------+
SELECT FID, title, actors
FROM film_list
WHERE FID = 1
;

-- ----------------------------------------------------------------------------------------------- SAKILA-FILM_LIST-B-03
-- the Children films rated G.
-- +-----+----------------------+--------+
-- | FID | title                | length |
-- +-----+----------------------+--------+
-- | 238 | DOCTOR GRAIL         |     57 |
-- | 280 | EMPIRE MALKOVICH     |    177 |
-- | 304 | FARGO GANDHI         |    130 |
-- | 354 | GHOST GROUNDHOG      |     85 |
-- | 373 | GRADUATE LORD        |    156 |
-- | 409 | HEARTBREAKERS BRIGHT |     59 |
-- | 626 | NOON PAPI            |     57 |
-- | 853 | STRANGER STRANGERS   |    139 |
-- | 873 | SWEETHEARTS SUSPECTS |    108 |
-- | 959 | WARLOCK WEREWOLF     |     83 |
-- +-----+----------------------+--------+
SELECT FID, title, length
FROM film_list
WHERE category = 'Children'
  AND rating = 'G'
ORDER BY title, FID
;

-- ----------------------------------------------------------------------------------------------- SAKILA-FILM_LIST-B-04
-- the films with no actor.
-- +-----+------------------+----------+
-- | FID | title            | category |
-- +-----+------------------+----------+
-- | 257 | DRUMLINE CYCLONE | Travel   |
-- | 323 | FLIGHT LIES      | Sports   |
-- | 803 | SLACKER LIAISONS | Drama    |
-- +-----+------------------+----------+
SELECT FID, title, category
FROM film_list
WHERE actors IS NULL
ORDER BY FID
;
