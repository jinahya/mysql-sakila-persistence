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
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/customer_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_B_01
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

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_B_02
-- how many customers each store has.
-- +----------+------------------+------------+---------------------+-----------+
-- | store_id | manager_staff_id | address_id | last_update         | customers |
-- +----------+------------------+------------+---------------------+-----------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |       326 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |       273 |
-- +----------+------------------+------------+---------------------+-----------+
SELECT st.*, COUNT(*) AS customers
FROM customer c
         JOIN store st ON st.store_id = c.store_id
GROUP BY st.store_id
ORDER BY st.store_id
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_B_03
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

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_B_04
-- the customers who are not active.
-- +-------------+----------+------------+-----------+-------------------------------------+------------+--------+---------------------+---------------------+
-- | customer_id | store_id | first_name | last_name | email                               | address_id | active | create_date         | last_update         |
-- +-------------+----------+------------+-----------+-------------------------------------+------------+--------+---------------------+---------------------+
-- |          16 |        2 | SANDRA     | MARTIN    | SANDRA.MARTIN@sakilacustomer.org    |         20 |      0 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |
-- |          64 |        2 | JUDITH     | COX       | JUDITH.COX@sakilacustomer.org       |         68 |      0 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |
-- |         124 |        1 | SHEILA     | WELLS     | SHEILA.WELLS@sakilacustomer.org     |        128 |      0 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |
-- |         169 |        2 | ERICA      | MATTHEWS  | ERICA.MATTHEWS@sakilacustomer.org   |        173 |      0 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |
-- |         241 |        2 | HEIDI      | LARSON    | HEIDI.LARSON@sakilacustomer.org     |        245 |      0 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |
-- |         271 |        1 | PENNY      | NEAL      | PENNY.NEAL@sakilacustomer.org       |        276 |      0 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |
-- |         315 |        2 | KENNETH    | GOODEN    | KENNETH.GOODEN@sakilacustomer.org   |        320 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         368 |        1 | HARRY      | ARCE      | HARRY.ARCE@sakilacustomer.org       |        373 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         406 |        1 | NATHAN     | RUNYON    | NATHAN.RUNYON@sakilacustomer.org    |        411 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         446 |        2 | THEODORE   | CULP      | THEODORE.CULP@sakilacustomer.org    |        451 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         482 |        1 | MAURICE    | CRAWLEY   | MAURICE.CRAWLEY@sakilacustomer.org  |        487 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         510 |        2 | BEN        | EASTER    | BEN.EASTER@sakilacustomer.org       |        515 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         534 |        1 | CHRISTIAN  | JUNG      | CHRISTIAN.JUNG@sakilacustomer.org   |        540 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         558 |        1 | JIMMIE     | EGGLESTON | JIMMIE.EGGLESTON@sakilacustomer.org |        564 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- |         592 |        1 | TERRANCE   | ROUSH     | TERRANCE.ROUSH@sakilacustomer.org   |        598 |      0 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |
-- +-------------+----------+------------+-----------+-------------------------------------+------------+--------+---------------------+---------------------+
SELECT *
FROM customer
WHERE active = 0
ORDER BY customer_id
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_B_05
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
