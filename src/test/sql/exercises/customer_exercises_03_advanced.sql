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
-- Exercises at the advanced level on customer: several of those together: a self-join, a relational division,
-- correlated subqueries, or one result checked against another. Each poses a question, answers it, and shows what the
-- installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/customer_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_A_01
-- the three customers who paid the most at each store.
-- +----------+-------------+------------+-----------+--------+
-- | store_id | customer_id | first_name | last_name | paid   |
-- +----------+-------------+------------+-----------+--------+
-- |        1 |         148 | ELEANOR    | HUNT      | 216.54 |
-- |        1 |         144 | CLARA      | SHAW      | 195.58 |
-- |        1 |         459 | TOMMY      | COLLAZO   | 186.62 |
-- |        2 |         526 | KARL       | SEAL      | 221.55 |
-- |        2 |         137 | RHONDA     | KENNEDY   | 194.61 |
-- |        2 |         178 | MARION     | SNYDER    | 194.61 |
-- +----------+-------------+------------+-----------+--------+
SELECT store_id, customer_id, first_name, last_name, paid
FROM (SELECT c.store_id, c.customer_id, c.first_name, c.last_name, SUM(p.amount) AS paid,
             ROW_NUMBER() OVER (PARTITION BY c.store_id ORDER BY SUM(p.amount) DESC, c.customer_id) AS n
      FROM customer c
               JOIN payment p ON p.customer_id = c.customer_id
      GROUP BY c.store_id, c.customer_id, c.first_name, c.last_name) t
WHERE n <= 3
ORDER BY store_id, n
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_A_02
-- how many customers of each store paid more than that store's average customer.
-- +----------+-----------+--------------+
-- | store_id | customers | average_paid |
-- +----------+-----------+--------------+
-- |        1 |       154 |       113.49 |
-- |        2 |       126 |       111.39 |
-- +----------+-----------+--------------+
WITH paid AS (SELECT c.customer_id, c.store_id, SUM(p.amount) AS paid
              FROM customer c
                       JOIN payment p ON p.customer_id = c.customer_id
              GROUP BY c.customer_id, c.store_id)
SELECT p1.store_id, COUNT(*) AS customers,
       (SELECT ROUND(AVG(p2.paid), 2) FROM paid p2 WHERE p2.store_id = p1.store_id) AS average_paid
FROM paid p1
WHERE p1.paid > (SELECT AVG(p2.paid) FROM paid p2 WHERE p2.store_id = p1.store_id)
GROUP BY p1.store_id
ORDER BY p1.store_id
;
