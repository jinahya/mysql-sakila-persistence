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
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/customer/03_advanced.sql
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
FROM (SELECT c.store_id,
             c.customer_id,
             c.first_name,
             c.last_name,
             SUM(p.amount)                                                                          AS paid,
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
SELECT p1.store_id,
       COUNT(*)                                                                     AS customers,
       (SELECT ROUND(AVG(p2.paid), 2) FROM paid p2 WHERE p2.store_id = p1.store_id) AS average_paid
FROM paid p1
WHERE p1.paid > (SELECT AVG(p2.paid) FROM paid p2 WHERE p2.store_id = p1.store_id)
GROUP BY p1.store_id
ORDER BY p1.store_id
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_A_03
-- the customers in four bands by what they paid.
-- +------+-----------+------------+-----------+----------+
-- | band | customers | least_paid | most_paid | total    |
-- +------+-----------+------------+-----------+----------+
-- |    1 |       150 |     128.71 |    221.55 | 21876.45 |
-- |    2 |       150 |     110.73 |    128.70 | 17818.77 |
-- |    3 |       150 |      94.78 |    110.73 | 15425.27 |
-- |    4 |       149 |      50.85 |     94.77 | 12286.07 |
-- +------+-----------+------------+-----------+----------+
WITH paid AS (SELECT c.customer_id, SUM(p.amount) AS paid
              FROM customer c
                       JOIN payment p ON p.customer_id = c.customer_id
              GROUP BY c.customer_id),
     banded AS (SELECT paid.*, NTILE(4) OVER (ORDER BY paid DESC, customer_id) AS band
                FROM paid)
SELECT band,
       COUNT(*)  AS customers,
       MIN(paid) AS least_paid,
       MAX(paid) AS most_paid,
       SUM(paid) AS total
FROM banded
GROUP BY band
ORDER BY band
;

-- ------------------------------------------------------------------------------------------------ SAKILA_CUSTOMER_A_04
-- the five customers with the longest gap between two rentals.
-- +-------------+----------+------------+-----------+------------------------------------+------------+--------+---------------------+---------------------+-------------+
-- | customer_id | store_id | first_name | last_name | email                              | address_id | active | create_date         | last_update         | longest_gap |
-- +-------------+----------+------------+-----------+------------------------------------+------------+--------+---------------------+---------------------+-------------+
-- |         208 |        1 | LUCY       | WHEELER   | LUCY.WHEELER@sakilacustomer.org    |        212 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |         181 |
-- |          99 |        2 | EMILY      | DIAZ      | EMILY.DIAZ@sakilacustomer.org      |        103 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |         179 |
-- |         330 |        1 | SCOTT      | SHELLEY   | SCOTT.SHELLEY@sakilacustomer.org   |        335 |      1 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |         179 |
-- |         228 |        2 | ALLISON    | STANLEY   | ALLISON.STANLEY@sakilacustomer.org |        232 |      1 | 2006-02-14 22:04:36 | 2006-02-15 04:57:20 |         178 |
-- |         479 |        1 | ZACHARY    | HITE      | ZACHARY.HITE@sakilacustomer.org    |        484 |      1 | 2006-02-14 22:04:37 | 2006-02-15 04:57:20 |         178 |
-- +-------------+----------+------------+-----------+------------------------------------+------------+--------+---------------------+---------------------+-------------+
WITH gaps AS (SELECT r.customer_id,
                     DATEDIFF(r.rental_date,
                              LAG(r.rental_date) OVER (PARTITION BY r.customer_id ORDER BY r.rental_date, r.rental_id)) AS gap_days
              FROM rental r)
SELECT c.*, MAX(g.gap_days) AS longest_gap
FROM customer c
         JOIN gaps g ON g.customer_id = c.customer_id
GROUP BY c.customer_id
ORDER BY longest_gap DESC, c.customer_id
LIMIT 5
;
