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
-- Exercises at the intermediate level on the customer_list view: a join, a GROUP BY with HAVING, a subquery, or a NOT
-- EXISTS. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/customer_list_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_I_01
-- whether the view has a row for every customer.
-- +---------------+----------+
-- | customer_list | customer |
-- +---------------+----------+
-- |           599 |      599 |
-- +---------------+----------+
SELECT (SELECT COUNT(*) FROM customer_list) AS customer_list,
       (SELECT COUNT(*) FROM customer)      AS customer
;

-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_I_02
-- the cities with more than one customer.
-- +--------+----------------+-----------+
-- | city   | country        | customers |
-- +--------+----------------+-----------+
-- | Aurora | United States  |         2 |
-- | London | United Kingdom |         2 |
-- +--------+----------------+-----------+
SELECT city, country, COUNT(*) AS customers
FROM customer_list
GROUP BY city, country
HAVING COUNT(*) > 1
ORDER BY city, country
;

-- ------------------------------------------------------------------------------------------- SAKILA_CUSTOMER_LIST_I_03
-- the rows whose name is not the customer's first and last name.
-- no rows.
SELECT cl.*, c.*
FROM customer_list cl
         JOIN customer c ON c.customer_id = cl.ID
WHERE cl.name <> CONCAT(c.first_name, ' ', c.last_name)
;
