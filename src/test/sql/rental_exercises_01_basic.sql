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
-- Exercises at the basic level on rental: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/rental_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- how many rentals are not returned yet.
-- +---------+
-- | rentals |
-- +---------+
-- |     183 |
-- +---------+
SELECT COUNT(*) AS rentals
FROM rental
WHERE return_date IS NULL
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- how many rentals were made in each month.
-- +---------+---------+
-- | month   | rentals |
-- +---------+---------+
-- | 2005-05 |    1156 |
-- | 2005-06 |    2311 |
-- | 2005-07 |    6709 |
-- | 2005-08 |    5686 |
-- | 2006-02 |     182 |
-- +---------+---------+
SELECT DATE_FORMAT(rental_date, '%Y-%m') AS month, COUNT(*) AS rentals
FROM rental
GROUP BY month
ORDER BY month
;

-- ---------------------------------------------------------------------------------------------------------- exercise 3
-- how many days a returned rental lasts, on average.
-- +--------------+
-- | average_days |
-- +--------------+
-- |         5.03 |
-- +--------------+
SELECT ROUND(AVG(DATEDIFF(return_date, rental_date)), 2) AS average_days
FROM rental
WHERE return_date IS NOT NULL
;

-- ---------------------------------------------------------------------------------------------------------- exercise 4
-- on which day of the week the most rentals were made.
-- +---------+---------+
-- | day     | rentals |
-- +---------+---------+
-- | Tuesday |    2463 |
-- +---------+---------+
SELECT DAYNAME(rental_date) AS day, COUNT(*) AS rentals
FROM rental
GROUP BY day
ORDER BY rentals DESC
LIMIT 1
;
