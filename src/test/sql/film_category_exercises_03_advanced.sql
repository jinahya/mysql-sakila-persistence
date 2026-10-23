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
-- Exercises at the advanced level on film_category: several of those together: a self-join, a relational division,
-- correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows what the
-- installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_category_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-A-01
-- the longest film of each category.
-- +-------------+---------+--------------------+--------+
-- | name        | film_id | title              | length |
-- +-------------+---------+--------------------+--------+
-- | Action      |     212 | DARN FORRESTER     |    185 |
-- | Animation   |     349 | GANGS PRIDE        |    185 |
-- | Children    |     344 | FURY MURDER        |    178 |
-- | Classics    |     180 | CONSPIRACY SPIRIT  |    184 |
-- | Comedy      |     182 | CONTROL ANTHEM     |    185 |
-- | Documentary |     973 | WIFE TURN          |    183 |
-- | Drama       |     473 | JACKET FRISCO      |    181 |
-- | Family      |     499 | KING EVOLUTION     |    184 |
-- | Foreign     |     198 | CRYSTAL BREAKING   |    184 |
-- | Games       |     141 | CHICAGO NORTH      |    185 |
-- | Horror      |      24 | ANALYZE HOOSIERS   |    181 |
-- | Music       |     426 | HOME PITY          |    185 |
-- | New         |     340 | FRONTIER CABIN     |    183 |
-- | Sci-Fi      |     817 | SOLDIERS EVOLUTION |    185 |
-- | Sports      |     813 | SMOOCHY CONTROL    |    184 |
-- | Travel      |     609 | MUSCLE BRIGHT      |    185 |
-- +-------------+---------+--------------------+--------+
SELECT name, film_id, title, length
FROM (SELECT c.name, f.film_id, f.title, f.length,
             ROW_NUMBER() OVER (PARTITION BY c.category_id ORDER BY f.length DESC, f.film_id) AS rn
      FROM film_category fc
               JOIN category c ON c.category_id = fc.category_id
               JOIN film f ON f.film_id = fc.film_id) t
WHERE rn = 1
ORDER BY name
;

-- ------------------------------------------------------------------------------------------- SAKILA-FILM_CATEGORY-A-02
-- the pairs of categories which have the same number of films.
-- +-----------+-----------+-------+
-- | category1 | category2 | films |
-- +-----------+-----------+-------+
-- | Games     | Sci-Fi    |    61 |
-- | Classics  | Travel    |    57 |
-- +-----------+-----------+-------+
SELECT c1.name AS category1, c2.name AS category2,
       (SELECT COUNT(*) FROM film_category fc WHERE fc.category_id = c1.category_id) AS films
FROM category c1
         JOIN category c2 ON c1.name < c2.name
WHERE (SELECT COUNT(*) FROM film_category fc WHERE fc.category_id = c1.category_id)
          = (SELECT COUNT(*) FROM film_category fc WHERE fc.category_id = c2.category_id)
ORDER BY films DESC, category1, category2
;
