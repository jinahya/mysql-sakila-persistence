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


-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_B_01
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

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_B_02
-- the actors of the film 1.
-- +-----+------------------+--------------------------------------------------------------------------------------------------+-------------+-------+--------+--------+------------------------------------------------------------------------------------------------------------------------------------------------+
-- | FID | title            | description                                                                                      | category    | price | length | rating | actors                                                                                                                                         |
-- +-----+------------------+--------------------------------------------------------------------------------------------------+-------------+-------+--------+--------+------------------------------------------------------------------------------------------------------------------------------------------------+
-- |   1 | ACADEMY DINOSAUR | A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies | Documentary |  0.99 |     86 | PG     | PENELOPE GUINESS, CHRISTIAN GABLE, LUCILLE TRACY, SANDRA PECK, JOHNNY CAGE, MENA TEMPLE, WARREN NOLTE, OPRAH KILMER, ROCK DUKAKIS, MARY KEITEL |
-- +-----+------------------+--------------------------------------------------------------------------------------------------+-------------+-------+--------+--------+------------------------------------------------------------------------------------------------------------------------------------------------+
SELECT *
FROM film_list
WHERE FID = 1
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_B_03
-- the Children films rated G.
-- +-----+----------------------+---------------------------------------------------------------------------------------------------------------------+----------+-------+--------+--------+-------------------------------------------------------------------------------------------------------------------------------+
-- | FID | title                | description                                                                                                         | category | price | length | rating | actors                                                                                                                        |
-- +-----+----------------------+---------------------------------------------------------------------------------------------------------------------+----------+-------+--------+--------+-------------------------------------------------------------------------------------------------------------------------------+
-- | 238 | DOCTOR GRAIL         | A Insightful Drama of a Womanizer And a Waitress who must Reach a Forensic Psychologist in The Outback              | Children |  2.99 |     57 | G      | JAMES PITT, PENELOPE MONROE, SALMA NOLTE, ALBERT NOLTE, CATE MCQUEEN, JON CHASE                                               |
-- | 280 | EMPIRE MALKOVICH     | A Amazing Story of a Feminist And a Cat who must Face a Car in An Abandoned Fun House                               | Children |  0.99 |    177 | G      | CUBA OLIVIER, FRED COSTNER, HELEN VOIGHT, DAN TORN, RAY JOHANSSON, RALPH CRUZ, ELLEN PRESLEY, SIDNEY CROWE, RICHARD PENN      |
-- | 304 | FARGO GANDHI         | A Thrilling Reflection of a Pastry Chef And a Crocodile who must Reach a Teacher in The Outback                     | Children |  2.99 |    130 | G      | MICHELLE MCCONAUGHEY, SCARLETT DAMON, KENNETH PESCI, MORGAN WILLIAMS, CHRISTOPHER WEST                                        |
-- | 354 | GHOST GROUNDHOG      | A Brilliant Panorama of a Madman And a Composer who must Succumb a Car in Ancient India                             | Children |  4.99 |     85 | G      | DAN HARRIS, KENNETH TORN, KEVIN GARLAND, RUSSELL TEMPLE, RENEE BALL                                                           |
-- | 373 | GRADUATE LORD        | A Lacklusture Epistle of a Girl And a A Shark who must Meet a Mad Scientist in Ancient China                        | Children |  2.99 |    156 | G      | EWAN GOODING, WHOOPI HURT                                                                                                     |
-- | 409 | HEARTBREAKERS BRIGHT | A Awe-Inspiring Documentary of a A Shark And a Dentist who must Outrace a Pastry Chef in The Canadian Rockies       | Children |  4.99 |     59 | G      | GINA DEGENERES, KIM ALLEN, OPRAH KILMER, CHRISTOPHER WEST, MATTHEW CARREY                                                     |
-- | 626 | NOON PAPI            | A Unbelieveable Character Study of a Mad Scientist And a Astronaut who must Find a Pioneer in A Manhattan Penthouse | Children |  2.99 |     57 | G      | CUBA OLIVIER, MILLA KEITEL, ADAM HOPPER, VIVIEN BASINGER, LAURENCE BULLOCK, WILL WILSON                                       |
-- | 853 | STRANGER STRANGERS   | A Awe-Inspiring Yarn of a Womanizer And a Explorer who must Fight a Woman in The First Manned Space Station         | Children |  4.99 |    139 | G      | HELEN VOIGHT, SANDRA KILMER, AUDREY OLIVIER, VAL BOLGER, TOM MCKELLEN, DUSTIN TAUTOU, MAE HOFFMAN, MICHAEL BENING, CUBA BIRCH |
-- | 873 | SWEETHEARTS SUSPECTS | A Brilliant Character Study of a Frisbee And a Sumo Wrestler who must Confront a Woman in The Gulf of Mexico        | Children |  0.99 |    108 | G      | JOE SWANK, HELEN VOIGHT, JULIA MCQUEEN, BEN WILLIS, JAYNE NOLTE                                                               |
-- | 959 | WARLOCK WEREWOLF     | A Astounding Yarn of a Pioneer And a Crocodile who must Defeat a A Shark in The Outback                             | Children |  2.99 |     83 | G      | GRACE MOSTEL, HELEN VOIGHT, WILL WILSON, GROUCHO WILLIAMS, HUMPHREY GARLAND, AUDREY BAILEY                                    |
-- +-----+----------------------+---------------------------------------------------------------------------------------------------------------------+----------+-------+--------+--------+-------------------------------------------------------------------------------------------------------------------------------+
SELECT *
FROM film_list
WHERE category = 'Children'
  AND rating = 'G'
ORDER BY title, FID
;

-- ----------------------------------------------------------------------------------------------- SAKILA_FILM_LIST_B_04
-- the films with no actor.
-- +-----+------------------+----------------------------------------------------------------------------------------------------------------+----------+-------+--------+--------+--------+
-- | FID | title            | description                                                                                                    | category | price | length | rating | actors |
-- +-----+------------------+----------------------------------------------------------------------------------------------------------------+----------+-------+--------+--------+--------+
-- | 257 | DRUMLINE CYCLONE | A Insightful Panorama of a Monkey And a Sumo Wrestler who must Outrace a Mad Scientist in The Canadian Rockies | Travel   |  0.99 |    110 | G      | NULL   |
-- | 323 | FLIGHT LIES      | A Stunning Character Study of a Crocodile And a Pioneer who must Pursue a Teacher in New Orleans               | Sports   |  4.99 |    179 | R      | NULL   |
-- | 803 | SLACKER LIAISONS | A Fast-Paced Tale of a A Shark And a Student who must Meet a Crocodile in Ancient China                        | Drama    |  4.99 |    179 | R      | NULL   |
-- +-----+------------------+----------------------------------------------------------------------------------------------------------------+----------+-------+--------+--------+--------+
SELECT *
FROM film_list
WHERE actors IS NULL
ORDER BY FID
;
