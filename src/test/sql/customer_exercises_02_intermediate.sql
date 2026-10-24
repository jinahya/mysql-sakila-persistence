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


-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_I_01
-- the five customers who paid the most.
-- +-------------+----------+------------+-----------+-----------------------------------+------------+--------+---------------------+---------------------+--------+
-- | customer_id | store_id | first_name | last_name | email                             | address_id | active | create_date         | last_update         | paid   |
-- +-------------+----------+------------+-----------+-----------------------------------+------------+--------+---------------------+---------------------+--------+
-- |         526 |        2 | KARL       | SEAL      | KARL.SEAL@sakilacustomer.org      |        532 |      1 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 | 221.55 |
-- |         148 |        1 | ELEANOR    | HUNT      | ELEANOR.HUNT@sakilacustomer.org   |        152 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 216.54 |
-- |         144 |        1 | CLARA      | SHAW      | CLARA.SHAW@sakilacustomer.org     |        148 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 195.58 |
-- |         137 |        2 | RHONDA     | KENNEDY   | RHONDA.KENNEDY@sakilacustomer.org |        141 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 194.61 |
-- |         178 |        2 | MARION     | SNYDER    | MARION.SNYDER@sakilacustomer.org  |        182 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 194.61 |
-- +-------------+----------+------------+-----------+-----------------------------------+------------+--------+---------------------+---------------------+--------+
SELECT c.*, SUM(p.amount) AS paid
FROM customer c
         JOIN payment p ON p.customer_id = c.customer_id
GROUP BY c.customer_id
ORDER BY paid DESC, c.customer_id LIMIT 5
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_I_02
-- the customers who never rented.
-- no rows.
SELECT c.*
FROM customer c
WHERE NOT EXISTS (SELECT 1 FROM rental r WHERE r.customer_id = c.customer_id)
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_I_03
-- the customers who rented forty times or more.
-- +-------------+----------+------------+-----------+----------------------------------+------------+--------+---------------------+---------------------+---------+
-- | customer_id | store_id | first_name | last_name | email                            | address_id | active | create_date         | last_update         | rentals |
-- +-------------+----------+------------+-----------+----------------------------------+------------+--------+---------------------+---------------------+---------+
-- |         148 |        1 | ELEANOR    | HUNT      | ELEANOR.HUNT@sakilacustomer.org  |        152 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |      46 |
-- |         526 |        2 | KARL       | SEAL      | KARL.SEAL@sakilacustomer.org     |        532 |      1 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |      45 |
-- |         144 |        1 | CLARA      | SHAW      | CLARA.SHAW@sakilacustomer.org    |        148 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |      42 |
-- |         236 |        1 | MARCIA     | DEAN      | MARCIA.DEAN@sakilacustomer.org   |        240 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |      42 |
-- |          75 |        2 | TAMMY      | SANDERS   | TAMMY.SANDERS@sakilacustomer.org |         79 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |      41 |
-- |         197 |        2 | SUE        | PETERS    | SUE.PETERS@sakilacustomer.org    |        201 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |      40 |
-- |         469 |        2 | WESLEY     | BULL      | WESLEY.BULL@sakilacustomer.org   |        474 |      1 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |      40 |
-- +-------------+----------+------------+-----------+----------------------------------+------------+--------+---------------------+---------------------+---------+
SELECT c.*, COUNT(*) AS rentals
FROM customer c
         JOIN rental r ON r.customer_id = c.customer_id
GROUP BY c.customer_id
HAVING COUNT(*) >= 40
ORDER BY rentals DESC, c.customer_id
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_I_04
-- the five customers holding the most rentals not returned yet.
-- +-------------+----------+------------+-----------+--------------------------------------+------------+--------+---------------------+---------------------+------------+
-- | customer_id | store_id | first_name | last_name | email                                | address_id | active | create_date         | last_update         | unreturned |
-- +-------------+----------+------------+-----------+--------------------------------------+------------+--------+---------------------+---------------------+------------+
-- |          75 |        2 | TAMMY      | SANDERS   | TAMMY.SANDERS@sakilacustomer.org     |         79 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |          3 |
-- |          15 |        1 | HELEN      | HARRIS    | HELEN.HARRIS@sakilacustomer.org      |         19 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |          2 |
-- |          42 |        2 | CAROLYN    | PEREZ     | CAROLYN.PEREZ@sakilacustomer.org     |         46 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |          2 |
-- |          43 |        2 | CHRISTINE  | ROBERTS   | CHRISTINE.ROBERTS@sakilacustomer.org |         47 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |          2 |
-- |          53 |        1 | HEATHER    | MORRIS    | HEATHER.MORRIS@sakilacustomer.org    |         57 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |          2 |
-- +-------------+----------+------------+-----------+--------------------------------------+------------+--------+---------------------+---------------------+------------+
SELECT c.*, COUNT(*) AS unreturned
FROM customer c
         JOIN rental r ON r.customer_id = c.customer_id
WHERE r.return_date IS NULL
GROUP BY c.customer_id
ORDER BY unreturned DESC, c.customer_id
LIMIT 5
;
