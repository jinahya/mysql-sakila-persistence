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


-- ---------------------------------------------------------------------------------------------------------- exercise 1
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
