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
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/nicer_but_slower_film_list/03_advanced.sql
--


-- ------------------------------------------------------------------------------ SAKILA_NICER_BUT_SLOWER_FILM_LIST_A_01
-- how many rows differ from film_list, column by column, but for the actors.
-- NOT YET RUN
SELECT COUNT(*) AS differing
FROM nicer_but_slower_film_list n
         JOIN film_list l ON l.FID = n.FID AND l.category <=> n.category
WHERE NOT (n.title <=> l.title AND n.description <=> l.description AND n.price <=> l.price
           AND n.length <=> l.length AND n.rating <=> l.rating)
;

-- ------------------------------------------------------------------------------ SAKILA_NICER_BUT_SLOWER_FILM_LIST_A_02
-- the longest length in each category, and how many films have it.
-- NOT YET RUN
SELECT n.category, n.length AS longest, COUNT(*) AS films
FROM nicer_but_slower_film_list n
WHERE n.length = (SELECT MAX(m.length) FROM nicer_but_slower_film_list m WHERE m.category <=> n.category)
GROUP BY n.category, n.length
ORDER BY n.category
;
