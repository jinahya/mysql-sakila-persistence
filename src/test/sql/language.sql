--
-- Profiling queries on language, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/language.sql
--


-- --------------------------------------------------------------------------------------------------------- language_id
-- 6 rows: English, Italian, Japanese, Mandarin, French and German.
SELECT language_id, CONCAT('[', name, ']') AS name, last_update
FROM language
ORDER BY language_id ASC
;

-- how many rows, and the smallest and the largest language_id; 6 languages, 1 to 6.
SELECT COUNT(*), MIN(language_id), MAX(language_id)
FROM language
;

-- a language_id whose next value is missing, below the largest.
-- no rows: language_id runs from 1 without a gap.
SELECT a.language_id + 1 AS missing_from
FROM language a
WHERE a.language_id < (SELECT MAX(language_id) FROM language)
  AND NOT EXISTS (SELECT 1
                  FROM language b
                  WHERE b.language_id = a.language_id + 1)
;


-- ---------------------------------------------------------------------------------------------------------------- name
-- name is a CHAR(20); MySQL strips its trailing spaces on read, so none show.

-- the shortest and the longest name; 6 and 8 characters.
SELECT MIN(CHAR_LENGTH(name)), MAX(CHAR_LENGTH(name))
FROM language
;

-- a name which is blank, or carries leading or trailing spaces.
-- no rows: every name is trimmed and not blank.
SELECT language_id, CONCAT('[', name, ']') AS name
FROM language
WHERE TRIM(name) = ''
   OR CHAR_LENGTH(name) <> CHAR_LENGTH(TRIM(name))
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 05:02:19, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM language
;


-- ---------------------------------------------------------------------------------------------------------------------
-- how many films each language is the language, or the original language, of; English is the language of all 1000,
-- and no language is an original language.
SELECT l.language_id,
       l.name,
       (SELECT COUNT(*) FROM film f WHERE f.language_id = l.language_id)          AS films,
       (SELECT COUNT(*) FROM film f WHERE f.original_language_id = l.language_id) AS original_films
FROM language l
ORDER BY l.language_id ASC
;
