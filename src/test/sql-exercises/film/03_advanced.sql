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
-- Exercises at the advanced level on film: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/film/03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------------- SAKILA_FILM_A_01
-- how many films of each rating are longer than their rating's average.
-- +--------+-------+
-- | rating | films |
-- +--------+-------+
-- | G      |    83 |
-- | PG     |    99 |
-- | PG-13  |   118 |
-- | R      |    94 |
-- | NC-17  |   103 |
-- +--------+-------+
SELECT f1.rating, COUNT(*) AS films
FROM film f1
WHERE f1.length > (SELECT AVG(f2.length) FROM film f2 WHERE f2.rating = f1.rating)
GROUP BY f1.rating
ORDER BY f1.rating
;

-- ---------------------------------------------------------------------------------------------------- SAKILA_FILM_A_02
-- how many films have each special feature.
-- +-------------------+-------+
-- | feature           | films |
-- +-------------------+-------+
-- | Commentaries      |   539 |
-- | Behind the Scenes |   538 |
-- | Trailers          |   535 |
-- | Deleted Scenes    |   503 |
-- +-------------------+-------+
SELECT sf.feature, COUNT(f.film_id) AS films
FROM (SELECT 'Trailers' AS feature
      UNION ALL
      SELECT 'Commentaries'
      UNION ALL
      SELECT 'Deleted Scenes'
      UNION ALL
      SELECT 'Behind the Scenes') sf
         LEFT JOIN film f ON FIND_IN_SET(sf.feature, f.special_features) > 0
GROUP BY sf.feature
ORDER BY films DESC, sf.feature
;
