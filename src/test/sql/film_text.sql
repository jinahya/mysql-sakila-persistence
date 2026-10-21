--
-- Profiling queries on film_text, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_text.sql
--


-- ------------------------------------------------------------------------------------------------------------- film_id
-- film_id has no foreign key; the triggers on film keep the two tables in step. 1000 rows, 1 to 1000.
SELECT COUNT(*), MIN(film_id), MAX(film_id)
FROM film_text
;

-- a film_text whose film is missing, and a film whose film_text is.
-- no rows: the two tables hold the same film_ids.
SELECT 'film_text only' AS side, t.film_id
FROM film_text t
WHERE NOT EXISTS (SELECT 1 FROM film f WHERE f.film_id = t.film_id)
UNION ALL
SELECT 'film only', f.film_id
FROM film f
WHERE NOT EXISTS (SELECT 1 FROM film_text t WHERE t.film_id = f.film_id)
;


-- --------------------------------------------------------------------------------------------------------------- title
-- the shortest and the longest title; 8 and 27 characters, as in film.
SELECT MIN(CHAR_LENGTH(title)), MAX(CHAR_LENGTH(title))
FROM film_text
;

-- a title which is blank, or carries leading or trailing spaces.
-- no rows: every title is trimmed and not blank.
SELECT film_id, CONCAT('[', title, ']') AS title
FROM film_text
WHERE TRIM(title) = ''
   OR CHAR_LENGTH(title) <> CHAR_LENGTH(TRIM(title))
;

-- a title which is not the title of its film.
-- no rows: every title is copied from the film.
SELECT t.film_id, t.title, f.title
FROM film_text t
         JOIN film f ON f.film_id = t.film_id
WHERE t.title <> f.title
;


-- --------------------------------------------------------------------------------------------------------- description
-- a description which is not the description of its film.
-- no rows: every description is copied from the film.
SELECT t.film_id
FROM film_text t
         JOIN film f ON f.film_id = t.film_id
WHERE NOT (t.description <=> f.description)
;
