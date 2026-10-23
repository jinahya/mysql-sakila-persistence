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
-- Exercises at the advanced level on the nicer_but_slower_film_list view: several of those together: a self-join, a
-- relational division, correlated subqueries, or one result checked against another. Each poses a question, answers it,
-- and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/nicer_but_slower_film_list_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------ SAKILA-NICER_BUT_SLOWER_FILM_LIST-A-01
-- how many rows differ from film_list, column by column, but for the actors.
-- +-----------+
-- | differing |
-- +-----------+
-- |         0 |
-- +-----------+
SELECT COUNT(*) AS differing
FROM nicer_but_slower_film_list n
         JOIN film_list l ON l.FID = n.FID AND l.category = n.category
WHERE NOT (n.title <=> l.title AND n.description <=> l.description AND n.price <=> l.price
           AND n.length <=> l.length AND n.rating <=> l.rating)
;

-- ------------------------------------------------------------------------------ SAKILA-NICER_BUT_SLOWER_FILM_LIST-A-02
-- the longest length in each category, and how many films have it.
-- +-------------+---------+-------+
-- | category    | longest | films |
-- +-------------+---------+-------+
-- | Action      |     185 |     2 |
-- | Animation   |     185 |     2 |
-- | Children    |     178 |     2 |
-- | Classics    |     184 |     1 |
-- | Comedy      |     185 |     1 |
-- | Documentary |     183 |     2 |
-- | Drama       |     181 |     1 |
-- | Family      |     184 |     1 |
-- | Foreign     |     184 |     2 |
-- | Games       |     185 |     1 |
-- | Horror      |     181 |     2 |
-- | Music       |     185 |     1 |
-- | New         |     183 |     1 |
-- | Sci-Fi      |     185 |     1 |
-- | Sports      |     184 |     1 |
-- | Travel      |     185 |     2 |
-- +-------------+---------+-------+
SELECT n.category, n.length AS longest, COUNT(*) AS films
FROM nicer_but_slower_film_list n
WHERE n.length = (SELECT MAX(m.length) FROM nicer_but_slower_film_list m WHERE m.category = n.category)
GROUP BY n.category, n.length
ORDER BY n.category
;

-- ------------------------------------------------------------------------------ SAKILA-NICER_BUT_SLOWER_FILM_LIST-A-03
-- whether the actors string names as many actors as film_actor has.
-- +------------+
-- | mismatched |
-- +------------+
-- |          0 |
-- +------------+
SELECT COUNT(*) AS mismatched
FROM nicer_but_slower_film_list n
WHERE n.actors IS NOT NULL
  AND CHAR_LENGTH(n.actors) - CHAR_LENGTH(REPLACE(n.actors, ',', '')) + 1
      <> (SELECT COUNT(*) FROM film_actor fa WHERE fa.film_id = n.FID)
;
