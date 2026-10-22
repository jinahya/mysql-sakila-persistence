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


-- ---------------------------------------------------------------------------------------------------------- exercise 1
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

-- ---------------------------------------------------------------------------------------------------------- exercise 2
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

-- ---------------------------------------------------------------------------------------------------------- exercise 3
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
