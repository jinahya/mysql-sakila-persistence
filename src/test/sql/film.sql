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
-- Profiling queries on film, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film.sql
--


-- ------------------------------------------------------------------------------------------------------------- film_id
-- how many rows, and the smallest and the largest film_id; 1000 films, 1 to 1000.
SELECT COUNT(*), MIN(film_id), MAX(film_id)
FROM film
;

-- a film_id whose next value is missing, below the largest.
-- no rows: film_id runs from 1 without a gap.
SELECT a.film_id + 1 AS missing_from
FROM film a
WHERE a.film_id < (SELECT MAX(film_id) FROM film)
  AND NOT EXISTS (SELECT 1
                  FROM film b
                  WHERE b.film_id = a.film_id + 1)
;


-- --------------------------------------------------------------------------------------------------------------- title
-- the shortest and the longest title; 8 and 27 characters.
SELECT MIN(CHAR_LENGTH(title)), MAX(CHAR_LENGTH(title))
FROM film
;

-- a title which is blank, or carries leading or trailing spaces.
-- no rows: every title is trimmed and not blank.
SELECT film_id, CONCAT('[', title, ']') AS title
FROM film
WHERE TRIM(title) = ''
   OR CHAR_LENGTH(title) <> CHAR_LENGTH(TRIM(title))
;

-- the installed data writes title in upper case.
-- no rows: every title is upper case.
SELECT film_id, title
FROM film
WHERE NOT REGEXP_LIKE(title, '^[^a-z]*$', 'c')
;

-- the schema has no unique key on title; a title more than one film has.
-- no rows: every title is unique.
SELECT title, COUNT(*)
FROM film
GROUP BY title
HAVING COUNT(*) > 1
;


-- --------------------------------------------------------------------------------------------------------- description
-- description is a nullable TEXT; how many films have none, and how long the others are. None; 70 to 130 characters.
SELECT COUNT(*) - COUNT(description) AS without,
       MIN(CHAR_LENGTH(description)),
       MAX(CHAR_LENGTH(description))
FROM film
;


-- -------------------------------------------------------------------------------------------------------- release_year
-- release_year is a nullable YEAR.

-- how many films each release_year has; all 1000 are of 2006.
SELECT release_year, COUNT(*) AS films
FROM film
GROUP BY release_year
ORDER BY release_year ASC
;


-- --------------------------------------------------------------------------------------------------------- language_id
-- a language_id which refers to no language; the foreign key forbids it.
-- no rows: every language_id refers to a language.
SELECT t.film_id, t.language_id
FROM film t
WHERE NOT EXISTS (SELECT 1
                  FROM language p
                  WHERE p.language_id = t.language_id)
;

-- how many films each language_id has; all 1000 are in language 1, English.
SELECT language_id, COUNT(*) AS films
FROM film
GROUP BY language_id
ORDER BY language_id ASC
;


-- ------------------------------------------------------------------------------------------------ original_language_id
-- an original_language_id which refers to no language; the foreign key forbids it.
-- no rows: every original_language_id refers to a language.
SELECT t.film_id, t.original_language_id
FROM film t
WHERE NOT EXISTS (SELECT 1
                  FROM language p
                  WHERE p.language_id = t.original_language_id)
  AND t.original_language_id IS NOT NULL
;

-- original_language_id is nullable; how many films have one. None.
SELECT COUNT(*) - COUNT(original_language_id) AS without, COUNT(original_language_id) AS `with`
FROM film
;


-- ----------------------------------------------------------------------------------------------------- rental_duration
-- rental_duration is a number of days, DEFAULT 3.

-- how many films each rental_duration has; 3 to 7 days, about 200 films each.
SELECT rental_duration, COUNT(*) AS films
FROM film
GROUP BY rental_duration
ORDER BY rental_duration ASC
;


-- --------------------------------------------------------------------------------------------------------- rental_rate
-- rental_rate is a DECIMAL(4,2), DEFAULT 4.99.

-- how many films each rental_rate has; 0.99, 2.99 and 4.99, about a third each.
SELECT rental_rate, COUNT(*) AS films
FROM film
GROUP BY rental_rate
ORDER BY rental_rate ASC
;


-- -------------------------------------------------------------------------------------------------------------- length
-- length is a nullable number of minutes; none is NULL, and they run from 46 to 185.
SELECT COUNT(*) - COUNT(length) AS without, MIN(length), MAX(length)
FROM film
;


-- ---------------------------------------------------------------------------------------------------- replacement_cost
-- replacement_cost is a DECIMAL(5,2), DEFAULT 19.99; 9.99 to 29.99, in 21 values.
SELECT MIN(replacement_cost), MAX(replacement_cost), COUNT(DISTINCT replacement_cost)
FROM film
;


-- -------------------------------------------------------------------------------------------------------------- rating
-- rating is a nullable ENUM('G','PG','PG-13','R','NC-17'), DEFAULT 'G'; the Java constants spell PG-13 and NC-17 with
-- an underscore.

-- how many films each rating has; all five ratings, 178 to 223 films each, and no NULL.
SELECT rating, COUNT(*) AS films
FROM film
GROUP BY rating
ORDER BY rating ASC
;


-- ---------------------------------------------------------------------------------------------------- special_features
-- special_features is a nullable SET of four values; how many films carry each of them. 503 to 539 each.
SELECT 'Trailers' AS feature, SUM(FIND_IN_SET('Trailers', special_features) > 0) AS films
FROM film
UNION ALL
SELECT 'Commentaries', SUM(FIND_IN_SET('Commentaries', special_features) > 0)
FROM film
UNION ALL
SELECT 'Deleted Scenes', SUM(FIND_IN_SET('Deleted Scenes', special_features) > 0)
FROM film
UNION ALL
SELECT 'Behind the Scenes', SUM(FIND_IN_SET('Behind the Scenes', special_features) > 0)
FROM film
;

-- how many films carry a NULL, and how many no feature at all; none of either.
SELECT SUM(special_features IS NULL) AS `null`, SUM(special_features = '') AS `empty`
FROM film
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 05:03:42, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM film
;


-- ---------------------------------------------------------------------------------------------------------------------
-- a film with no actor; 3 rows, films 257, 323 and 803.
SELECT f.film_id, f.title
FROM film f
WHERE NOT EXISTS (SELECT 1
                  FROM film_actor fa
                  WHERE fa.film_id = f.film_id)
;

-- a film with no category.
-- no rows: every film has a category.
SELECT f.film_id, f.title
FROM film f
WHERE NOT EXISTS (SELECT 1
                  FROM film_category fc
                  WHERE fc.film_id = f.film_id)
;

-- a film no store has a copy of; 42 rows.
SELECT f.film_id, f.title
FROM film f
WHERE NOT EXISTS (SELECT 1
                  FROM inventory i
                  WHERE i.film_id = f.film_id)
;
