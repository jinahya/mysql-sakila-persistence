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
-- Exercises at the intermediate level on payment: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/payment_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_I_01
-- the largest payment, and how many payments are that large.
-- +---------+----------+
-- | largest | payments |
-- +---------+----------+
-- |   11.99 |       10 |
-- +---------+----------+
SELECT MAX(amount)                                                                     AS largest,
       (SELECT COUNT(*) FROM payment WHERE amount = (SELECT MAX(amount) FROM payment)) AS payments
FROM payment
;

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_I_02
-- the customers who paid more than 180 in all.
-- +-------------+----------+------------+-----------+-----------------------------------+------------+--------+---------------------+---------------------+--------+
-- | customer_id | store_id | first_name | last_name | email                             | address_id | active | create_date         | last_update         | total  |
-- +-------------+----------+------------+-----------+-----------------------------------+------------+--------+---------------------+---------------------+--------+
-- |         526 |        2 | KARL       | SEAL      | KARL.SEAL@sakilacustomer.org      |        532 |      1 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 | 221.55 |
-- |         148 |        1 | ELEANOR    | HUNT      | ELEANOR.HUNT@sakilacustomer.org   |        152 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 216.54 |
-- |         144 |        1 | CLARA      | SHAW      | CLARA.SHAW@sakilacustomer.org     |        148 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 195.58 |
-- |         137 |        2 | RHONDA     | KENNEDY   | RHONDA.KENNEDY@sakilacustomer.org |        141 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 194.61 |
-- |         178 |        2 | MARION     | SNYDER    | MARION.SNYDER@sakilacustomer.org  |        182 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 | 194.61 |
-- |         459 |        1 | TOMMY      | COLLAZO   | TOMMY.COLLAZO@sakilacustomer.org  |        464 |      1 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 | 186.62 |
-- +-------------+----------+------------+-----------+-----------------------------------+------------+--------+---------------------+---------------------+--------+
SELECT c.*, SUM(p.amount) AS total
FROM payment p
         JOIN customer c ON c.customer_id = p.customer_id
GROUP BY c.customer_id
HAVING SUM(p.amount) > 180
ORDER BY total DESC, c.customer_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA_PAYMENT_I_03
-- how many payments were less than, equal to, or more than the film's rental rate.
-- +----------+----------+
-- | paid     | payments |
-- +----------+----------+
-- | less     |       24 |
-- | more     |     7293 |
-- | the rate |     8727 |
-- +----------+----------+
SELECT CASE
           WHEN p.amount < f.rental_rate THEN 'less'
           WHEN p.amount = f.rental_rate THEN 'the rate'
           ELSE 'more' END AS paid,
       COUNT(*) AS payments
FROM payment p
         JOIN rental r ON r.rental_id = p.rental_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
         JOIN film f ON f.film_id = i.film_id
GROUP BY paid
ORDER BY paid
;
