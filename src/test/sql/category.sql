--
-- Profiling queries on category, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/category.sql
--


-- --------------------------------------------------------------------------------------------------------- category_id
-- 16 rows, Action to Travel.
SELECT category_id, name, last_update
FROM category
ORDER BY category_id ASC
;

-- how many rows, and the smallest and the largest category_id; 16 categories, 1 to 16.
SELECT COUNT(*), MIN(category_id), MAX(category_id)
FROM category
;

-- a category_id whose next value is missing, below the largest.
-- no rows: category_id runs from 1 without a gap.
SELECT a.category_id + 1 AS missing_from
FROM category a
WHERE a.category_id < (SELECT MAX(category_id) FROM category)
  AND NOT EXISTS (SELECT 1
                  FROM category b
                  WHERE b.category_id = a.category_id + 1)
;


-- ---------------------------------------------------------------------------------------------------------------- name
-- the shortest and the longest name; 3 and 11 characters.
SELECT MIN(CHAR_LENGTH(name)), MAX(CHAR_LENGTH(name))
FROM category
;

-- a name which is blank, or carries leading or trailing spaces.
-- no rows: every name is trimmed and not blank.
SELECT category_id, CONCAT('[', name, ']') AS name
FROM category
WHERE TRIM(name) = ''
   OR CHAR_LENGTH(name) <> CHAR_LENGTH(TRIM(name))
;

-- the schema has no unique key on name; a name more than one category has.
-- no rows: every name is unique.
SELECT name, COUNT(*)
FROM category
GROUP BY name
HAVING COUNT(*) > 1
;


-- --------------------------------------------------------------------------------------------------------- last_update
-- the database sets last_update on insert, and again on every update; the range the installed rows carry.
-- 2006-02-15 04:46:27, a single moment, as every row was loaded at once
SELECT MIN(last_update), MAX(last_update)
FROM category
;


-- ---------------------------------------------------------------------------------------------------------------------
-- how many films each category has; 51 to 74.
SELECT c.category_id, c.name, COUNT(fc.film_id) AS films
FROM category c
         LEFT JOIN film_category fc ON fc.category_id = c.category_id
GROUP BY c.category_id, c.name
ORDER BY c.category_id ASC
;
