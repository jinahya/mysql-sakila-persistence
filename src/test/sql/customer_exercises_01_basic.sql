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
-- Exercises at the basic level on customer: one table: a filter, a sort, a count or an aggregate. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/customer_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA-CUSTOMER-B-01
-- how many customers are active, and how many are not.
-- +--------+-----------+
-- | active | customers |
-- +--------+-----------+
-- |      0 |        15 |
-- |      1 |       584 |
-- +--------+-----------+
SELECT active, COUNT(*) AS customers
FROM customer
GROUP BY active
ORDER BY active
;

-- ------------------------------------------------------------------------------------------------ SAKILA-CUSTOMER-B-02
-- how many customers each store has.
-- +----------+-----------+
-- | store_id | customers |
-- +----------+-----------+
-- |        1 |       326 |
-- |        2 |       273 |
-- +----------+-----------+
SELECT store_id, COUNT(*) AS customers
FROM customer
GROUP BY store_id
ORDER BY store_id
;

-- ------------------------------------------------------------------------------------------------ SAKILA-CUSTOMER-B-03
-- the domains of the customers' emails.
-- +--------------------+-----------+
-- | domain             | customers |
-- +--------------------+-----------+
-- | sakilacustomer.org |       599 |
-- +--------------------+-----------+
SELECT SUBSTRING_INDEX(email, '@', -1) AS domain, COUNT(*) AS customers
FROM customer
GROUP BY domain
;

-- ------------------------------------------------------------------------------------------------ SAKILA-CUSTOMER-B-04
-- the customers who are not active.
-- +-------------+------------+-----------+----------+
-- | customer_id | first_name | last_name | store_id |
-- +-------------+------------+-----------+----------+
-- |          16 | SANDRA     | MARTIN    |        2 |
-- |          64 | JUDITH     | COX       |        2 |
-- |         124 | SHEILA     | WELLS     |        1 |
-- |         169 | ERICA      | MATTHEWS  |        2 |
-- |         241 | HEIDI      | LARSON    |        2 |
-- |         271 | PENNY      | NEAL      |        1 |
-- |         315 | KENNETH    | GOODEN    |        2 |
-- |         368 | HARRY      | ARCE      |        1 |
-- |         406 | NATHAN     | RUNYON    |        1 |
-- |         446 | THEODORE   | CULP      |        2 |
-- |         482 | MAURICE    | CRAWLEY   |        1 |
-- |         510 | BEN        | EASTER    |        2 |
-- |         534 | CHRISTIAN  | JUNG      |        1 |
-- |         558 | JIMMIE     | EGGLESTON |        1 |
-- |         592 | TERRANCE   | ROUSH     |        1 |
-- +-------------+------------+-----------+----------+
SELECT customer_id, first_name, last_name, store_id
FROM customer
WHERE active = 0
ORDER BY customer_id
;

-- ------------------------------------------------------------------------------------------------ SAKILA-CUSTOMER-B-05
-- how many customers have the same initial in both names.
-- +-----------+
-- | customers |
-- +-----------+
-- |        30 |
-- +-----------+
SELECT COUNT(*) AS customers
FROM customer
WHERE LEFT(first_name, 1) = LEFT(last_name, 1)
;
