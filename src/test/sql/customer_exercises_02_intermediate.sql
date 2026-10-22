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
-- Exercises at the intermediate level on customer: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/customer_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the five customers who paid the most.
-- +-------------+------------+-----------+--------+
-- | customer_id | first_name | last_name | paid   |
-- +-------------+------------+-----------+--------+
-- |         526 | KARL       | SEAL      | 221.55 |
-- |         148 | ELEANOR    | HUNT      | 216.54 |
-- |         144 | CLARA      | SHAW      | 195.58 |
-- |         137 | RHONDA     | KENNEDY   | 194.61 |
-- |         178 | MARION     | SNYDER    | 194.61 |
-- +-------------+------------+-----------+--------+
SELECT c.customer_id, c.first_name, c.last_name, SUM(p.amount) AS paid
FROM customer c
         JOIN payment p ON p.customer_id = c.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY paid DESC, c.customer_id
LIMIT 5
;

-- ---------------------------------------------------------------------------------------------------------- exercise 2
-- the customers who never rented.
-- no rows.
SELECT c.*
FROM customer c
WHERE NOT EXISTS (SELECT 1 FROM rental r WHERE r.customer_id = c.customer_id)
;
