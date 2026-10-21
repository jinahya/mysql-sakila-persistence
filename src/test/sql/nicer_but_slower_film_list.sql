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
---
--
-- Profiling queries on nicer_but_slower_film_list, a view, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/nicer_but_slower_film_list.sql
--


-- ---------------------------------------------------------------------------------------------------------------------
-- the view groups film, left-joined with film_category, category, film_actor and actor, by the film and the category.
-- what the view defines; a row per film and category, with the film's actors.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = DATABASE()
  AND table_name = 'nicer_but_slower_film_list'
;


-- ----------------------------------------------------------------------------------------------------------------- FID
-- how many rows, and the smallest and the largest FID; 1000 rows, 1 to 1000, as many as film has.
SELECT COUNT(*), MIN(FID), MAX(FID), (SELECT COUNT(*) FROM film) AS films
FROM nicer_but_slower_film_list
;

-- an FID more than one row has; the view groups by the film and the category, so a film of two categories has two.
-- no rows: every installed film has a single category, a property of the data, not of the view.
SELECT FID, COUNT(*) AS rows_
FROM nicer_but_slower_film_list
GROUP BY FID
HAVING COUNT(*) > 1
;


-- --------------------------------------------------------------------------------------------------------------- title
-- the shortest and the longest title; 8 and 27 characters.
SELECT MIN(CHAR_LENGTH(title)), MAX(CHAR_LENGTH(title))
FROM nicer_but_slower_film_list
;

-- a row whose columns, but actors, are not those of film_list.
-- no rows: the two views differ only in actors.
SELECT n.FID
FROM nicer_but_slower_film_list n
         JOIN film_list f ON f.FID = n.FID
WHERE NOT (n.title <=> f.title
    AND n.description <=> f.description
    AND n.category <=> f.category
    AND n.price = f.price
    AND n.length <=> f.length
    AND n.rating <=> f.rating)
;


-- --------------------------------------------------------------------------------------------------------- description
-- description is nullable; how many are NULL, and the shortest and the longest.
-- none is NULL; 70 and 130 characters.
SELECT SUM(description IS NULL), MIN(CHAR_LENGTH(description)), MAX(CHAR_LENGTH(description))
FROM nicer_but_slower_film_list
;


-- ------------------------------------------------------------------------------------------------------------ category
-- category is nullable, for the view left-joins category; how many are NULL, and how many distinct.
-- none is NULL; 16 categories.
SELECT SUM(category IS NULL), COUNT(DISTINCT category)
FROM nicer_but_slower_film_list
;


-- --------------------------------------------------------------------------------------------------------------- price
-- price is the film's rental_rate; how many films each has.
-- 341 of 0.99, 323 of 2.99, and 336 of 4.99.
SELECT price, COUNT(*) AS films
FROM nicer_but_slower_film_list
GROUP BY price
ORDER BY price ASC
;


-- -------------------------------------------------------------------------------------------------------------- length
-- length is nullable; how many are NULL, and the shortest and the longest.
-- none is NULL; 46 to 185 minutes.
SELECT SUM(length IS NULL), MIN(length), MAX(length)
FROM nicer_but_slower_film_list
;


-- -------------------------------------------------------------------------------------------------------------- rating
-- rating is nullable; how many films each has.
-- 178 G, 194 PG, 223 PG-13, 195 R, and 210 NC-17; none is NULL.
SELECT rating, COUNT(*) AS films
FROM nicer_but_slower_film_list
GROUP BY rating
ORDER BY rating ASC
;


-- -------------------------------------------------------------------------------------------------------------- actors
-- actors is nullable, a GROUP_CONCAT of the names joined with ', '; how many are NULL, and the shortest and the longest.
-- 3 are NULL; 8 and 216 characters.
SELECT SUM(actors IS NULL), MIN(CHAR_LENGTH(actors)), MAX(CHAR_LENGTH(actors))
FROM nicer_but_slower_film_list
;

-- a film with no actors.
-- 3 rows, 257, 323 and 803: no actor plays in any of them.
SELECT FID, title, category
FROM nicer_but_slower_film_list
WHERE actors IS NULL
ORDER BY FID ASC
;

-- an actors whose number of names is not that of the film's actors.
-- no rows: actors has a name per actor.
SELECT v.FID, v.actors_, c.actors_
FROM (SELECT FID, CHAR_LENGTH(actors) - CHAR_LENGTH(REPLACE(actors, ',', '')) + 1 AS actors_
      FROM nicer_but_slower_film_list) v
         JOIN (SELECT film_id, COUNT(*) AS actors_
               FROM film_actor
               GROUP BY film_id) c ON c.film_id = v.FID
WHERE v.actors_ <> c.actors_
;

-- the view writes each actor's names with the first letter in upper case, and the rest in lower case.
-- 997 rows differ, case-sensitively, from film_list, and none does once both are in upper case; the other 3 are NULL.
SELECT SUM(CAST(n.actors AS BINARY) <> CAST(f.actors AS BINARY))                AS differ,
       SUM(CAST(UPPER(n.actors) AS BINARY) <> CAST(f.actors AS BINARY))         AS differ_in_upper_case
FROM nicer_but_slower_film_list n
         JOIN film_list f ON f.FID = n.FID
;
