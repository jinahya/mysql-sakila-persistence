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
-- Exercises at the advanced level on film_actor: several of those together: a self-join, a relational division,
-- correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows what the
-- installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/film_actor_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------- SAKILA-FILM_ACTOR-A-01
-- the three pairs of actors who appear together the most.
-- +-----------+-----------+-------+
-- | actor_id1 | actor_id2 | films |
-- +-----------+-----------+-------+
-- |        27 |        60 |     7 |
-- |        15 |       198 |     6 |
-- |        21 |       108 |     6 |
-- +-----------+-----------+-------+
SELECT fa1.actor_id AS actor_id1, fa2.actor_id AS actor_id2, COUNT(*) AS films
FROM film_actor fa1
         JOIN film_actor fa2 ON fa2.film_id = fa1.film_id AND fa2.actor_id > fa1.actor_id
GROUP BY fa1.actor_id, fa2.actor_id
ORDER BY films DESC, actor_id1, actor_id2 LIMIT 3
;

-- ---------------------------------------------------------------------------------------------- SAKILA-FILM_ACTOR-A-02
-- the actor who appears in the most films of each category.
-- +-------------+----------+------------+-----------+-------+
-- | name        | actor_id | first_name | last_name | films |
-- +-------------+----------+------------+-----------+-------+
-- | Action      |       50 | NATALIE    | HOPKINS   |     6 |
-- | Animation   |      137 | MORGAN     | WILLIAMS  |     6 |
-- | Children    |       17 | HELEN      | VOIGHT    |     7 |
-- | Classics    |       86 | GREG       | CHAPLIN   |     6 |
-- | Comedy      |      196 | BELA       | WALKEN    |     6 |
-- | Documentary |        3 | ED         | CHASE     |     6 |
-- | Drama       |        7 | GRACE      | MOSTEL    |     7 |
-- | Family      |       79 | MAE        | HOFFMAN   |     6 |
-- | Foreign     |      164 | HUMPHREY   | WILLIS    |     7 |
-- | Games       |       68 | RIP        | WINSLET   |     5 |
-- | Horror      |       27 | JULIA      | MCQUEEN   |     7 |
-- | Music       |      108 | WARREN     | NOLTE     |     5 |
-- | New         |      105 | SIDNEY     | CROWE     |     7 |
-- | Sci-Fi      |      107 | GINA       | DEGENERES |     7 |
-- | Sports      |       83 | BEN        | WILLIS    |     9 |
-- | Travel      |       44 | NICK       | STALLONE  |     5 |
-- +-------------+----------+------------+-----------+-------+
SELECT name, actor_id, first_name, last_name, films
FROM (SELECT c.name, a.actor_id, a.first_name, a.last_name, COUNT(*) AS films,
             ROW_NUMBER() OVER (PARTITION BY c.category_id ORDER BY COUNT(*) DESC, a.actor_id) AS n
      FROM film_actor fa
               JOIN film_category fc ON fc.film_id = fa.film_id
               JOIN category c ON c.category_id = fc.category_id
               JOIN actor a ON a.actor_id = fa.actor_id
      GROUP BY c.category_id, c.name, a.actor_id, a.first_name, a.last_name) t
WHERE n = 1
ORDER BY name
;

-- ---------------------------------------------------------------------------------------------- SAKILA-FILM_ACTOR-A-03
-- the pairs of films which have exactly the same actors.
-- +----------+----------+--------+
-- | film_id1 | film_id2 | actors |
-- +----------+----------+--------+
-- |       97 |      556 |      1 |
-- +----------+----------+--------+
SELECT fa1.film_id AS film_id1, fa2.film_id AS film_id2, COUNT(*) AS actors
FROM film_actor fa1
         JOIN film_actor fa2 ON fa2.actor_id = fa1.actor_id AND fa2.film_id > fa1.film_id
GROUP BY fa1.film_id, fa2.film_id
HAVING COUNT(*) = (SELECT COUNT(*) FROM film_actor x WHERE x.film_id = fa1.film_id)
   AND COUNT(*) = (SELECT COUNT(*) FROM film_actor y WHERE y.film_id = fa2.film_id)
ORDER BY film_id1, film_id2
;
