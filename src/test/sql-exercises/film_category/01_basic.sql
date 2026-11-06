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
-- Exercises at the basic level on film_category: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/film_category/01_basic.sql
--


-- ------------------------------------------------------------------------------------------- SAKILA_FILM_CATEGORY_B_01
-- how many categories the films are in.
-- +------------+
-- | categories |
-- +------------+
-- |         16 |
-- +------------+
SELECT COUNT(DISTINCT category_id) AS categories
FROM film_category
;

-- ------------------------------------------------------------------------------------------- SAKILA_FILM_CATEGORY_B_02
-- the categories of the films 1 to 10.
-- +---------+-------------+---------------------+
-- | film_id | category_id | last_update         |
-- +---------+-------------+---------------------+
-- |       1 |           6 | 2006-02-15 05:07:09 |
-- |       2 |          11 | 2006-02-15 05:07:09 |
-- |       3 |           6 | 2006-02-15 05:07:09 |
-- |       4 |          11 | 2006-02-15 05:07:09 |
-- |       5 |           8 | 2006-02-15 05:07:09 |
-- |       6 |           9 | 2006-02-15 05:07:09 |
-- |       7 |           5 | 2006-02-15 05:07:09 |
-- |       8 |          11 | 2006-02-15 05:07:09 |
-- |       9 |          11 | 2006-02-15 05:07:09 |
-- |      10 |          15 | 2006-02-15 05:07:09 |
-- +---------+-------------+---------------------+
SELECT *
FROM film_category
WHERE film_id BETWEEN 1 AND 10
ORDER BY film_id
;

-- ------------------------------------------------------------------------------------------- SAKILA_FILM_CATEGORY_B_03
-- the films of the Comedy category.
-- +---------+-------------------------+--------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+--------------------------------------------------------+---------------------+
-- | film_id | title                   | description                                                                                                              | release_year | language_id | original_language_id | rental_duration | rental_rate | length | replacement_cost | rating | special_features                                       | last_update         |
-- +---------+-------------------------+--------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+--------------------------------------------------------+---------------------+
-- |       7 | AIRPLANE SIERRA         | A Touching Saga of a Hunter And a Butler who must Discover a Butler in A Jet Boat                                        |         2006 |           1 |                 NULL |               6 |        4.99 |     62 |            28.99 | PG-13  | Trailers,Deleted Scenes                                | 2006-02-15 05:03:42 |
-- |      28 | ANTHEM LUKE             | A Touching Panorama of a Waitress And a Woman who must Outrace a Dog in An Abandoned Amusement Park                      |         2006 |           1 |                 NULL |               5 |        4.99 |     91 |            16.99 | PG-13  | Deleted Scenes,Behind the Scenes                       | 2006-02-15 05:03:42 |
-- |      99 | BRINGING HYSTERICAL     | A Fateful Saga of a A Shark And a Technical Writer who must Find a Woman in A Jet Boat                                   |         2006 |           1 |                 NULL |               7 |        2.99 |    136 |            14.99 | PG     | Trailers                                               | 2006-02-15 05:03:42 |
-- |     119 | CAPER MOTIONS           | A Fateful Saga of a Moose And a Car who must Pursue a Woman in A MySQL Convention                                        |         2006 |           1 |                 NULL |               6 |        0.99 |    176 |            22.99 | G      | Trailers,Commentaries,Deleted Scenes                   | 2006-02-15 05:03:42 |
-- |     127 | CAT CONEHEADS           | A Fast-Paced Panorama of a Girl And a A Shark who must Confront a Boy in Ancient India                                   |         2006 |           1 |                 NULL |               5 |        4.99 |    112 |            14.99 | G      | Commentaries,Deleted Scenes                            | 2006-02-15 05:03:42 |
-- +---------+-------------------------+--------------------------------------------------------------------------------------------------------------------------+--------------+-------------+----------------------+-----------------+-------------+--------+------------------+--------+--------------------------------------------------------+---------------------+
-- ... 58 rows in all.
SELECT f.*
FROM film f
         JOIN film_category fc ON fc.film_id = f.film_id
         JOIN category c ON c.category_id = fc.category_id
WHERE c.name = 'Comedy'
ORDER BY f.title, f.film_id
;
