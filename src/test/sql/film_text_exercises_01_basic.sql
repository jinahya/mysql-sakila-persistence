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


-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_B_01
-- the films whose description has both a Crocodile and a Shark.
-- +---------+-------------------+------------------------------------------------------------------------------------------------------+
-- | film_id | title             | description                                                                                          |
-- +---------+-------------------+------------------------------------------------------------------------------------------------------+
-- |     155 | CLEOPATRA DEVIL   | A Fanciful Documentary of a Crocodile And a Technical Writer who must Fight a A Shark in A Baloon    |
-- |     177 | CONNECTICUT TRAMP | A Unbelieveable Drama of a Crocodile And a Mad Cow who must Reach a Dentist in A Shark Tank          |
-- |     206 | DANCING FEVER     | A Stunning Story of a Explorer And a Forensic Psychologist who must Face a Crocodile in A Shark Tank |
-- |     292 | EXCITEMENT EVE    | A Brilliant Documentary of a Monkey And a Car who must Conquer a Crocodile in A Shark Tank           |
-- |     429 | HONEY TIES        | A Taut Story of a Waitress And a Crocodile who must Outrace a Lumberjack in A Shark Tank             |
-- |     543 | MADIGAN DORADO    | A Astounding Character Study of a A Shark And a A Shark who must Discover a Crocodile in The Outback |
-- |     790 | SHOOTIST SUPERFLY | A Fast-Paced Story of a Crocodile And a A Shark who must Sink a Pioneer in Berlin                    |
-- |     803 | SLACKER LIAISONS  | A Fast-Paced Tale of a A Shark And a Student who must Meet a Crocodile in Ancient China              |
-- |     832 | SPLASH GUMP       | A Taut Saga of a Crocodile And a Boat who must Conquer a Hunter in A Shark Tank                      |
-- |     959 | WARLOCK WEREWOLF  | A Astounding Yarn of a Pioneer And a Crocodile who must Defeat a A Shark in The Outback              |
-- +---------+-------------------+------------------------------------------------------------------------------------------------------+
SELECT *
FROM film_text
WHERE description LIKE '%Crocodile%'
  AND description LIKE '%Shark%'
ORDER BY film_id
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_TEXT_B_02
-- the five longest descriptions.
-- +---------+---------------------+------------------------------------------------------------------------------------------------------------------------------------+------------+
-- | film_id | title               | description                                                                                                                        | characters |
-- +---------+---------------------+------------------------------------------------------------------------------------------------------------------------------------+------------+
-- |     116 | CANDIDATE PERDITION | A Brilliant Epistle of a Composer And a Database Administrator who must Vanquish a Mad Scientist in The First Manned Space Station |        130 |
-- |     217 | DAZED PUNK          | A Action-Packed Story of a Pioneer And a Technical Writer who must Discover a Forensic Psychologist in An Abandoned Amusement Park |        130 |
-- |     274 | EGG IGBY            | A Beautiful Documentary of a Boat And a Sumo Wrestler who must Succumb a Database Administrator in The First Manned Space Station  |        129 |
-- |     569 | MENAGERIE RUSHMORE  | A Unbelieveable Panorama of a Composer And a Butler who must Overcome a Database Administrator in The First Manned Space Station   |        128 |
-- |      51 | BALLOON HOMEWARD    | A Insightful Panorama of a Forensic Psychologist And a Mad Cow who must Build a Mad Scientist in The First Manned Space Station    |        127 |
-- +---------+---------------------+------------------------------------------------------------------------------------------------------------------------------------+------------+
SELECT *, LENGTH(description) AS characters
FROM film_text
ORDER BY characters DESC, film_id
LIMIT 5
;
