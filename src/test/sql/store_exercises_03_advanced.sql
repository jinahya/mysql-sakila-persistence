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
-- Exercises at the advanced level on store: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/store_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- how many films each store has a copy of, and how many of them only that store has.
-- +----------+-------+-----------------+
-- | store_id | films | films_only_here |
-- +----------+-------+-----------------+
-- |        1 |   759 |             196 |
-- |        2 |   762 |             199 |
-- +----------+-------+-----------------+
SELECT st.store_id,
       COUNT(DISTINCT i.film_id) AS films,
       COUNT(DISTINCT CASE
                          WHEN NOT EXISTS (SELECT 1 FROM inventory o WHERE o.film_id = i.film_id AND o.store_id <> st.store_id)
                              THEN i.film_id END) AS films_only_here
FROM store st
         JOIN inventory i ON i.store_id = st.store_id
GROUP BY st.store_id
ORDER BY st.store_id
;
