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
-- Exercises at the intermediate level on address: a join, a GROUP BY with HAVING, a subquery, or a NOT EXISTS. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/address_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-I-01
-- the first five addresses, with their city and country.
-- +------------+----------------------+----------+----------+---------+-------------+-------------+---------------------+---------+------------+------------+---------------------+------------+-----------+---------------------+
-- | address_id | address              | address2 | district | city_id | postal_code | phone       | last_update         | city_id | city       | country_id | last_update         | country_id | country   | last_update         |
-- +------------+----------------------+----------+----------+---------+-------------+-------------+---------------------+---------+------------+------------+---------------------+------------+-----------+---------------------+
-- |          1 | 47 MySakila Drive    | NULL     | Alberta  |     300 |             |             | 2014-09-25 22:30:27 |     300 | Lethbridge |         20 | 2006-02-15 04:45:25 |         20 | Canada    | 2006-02-15 04:44:00 |
-- |          2 | 28 MySQL Boulevard   | NULL     | QLD      |     576 |             |             | 2014-09-25 22:30:09 |     576 | Woodridge  |          8 | 2006-02-15 04:45:25 |          8 | Australia | 2006-02-15 04:44:00 |
-- |          3 | 23 Workhaven Lane    | NULL     | Alberta  |     300 |             | 14033335568 | 2014-09-25 22:30:27 |     300 | Lethbridge |         20 | 2006-02-15 04:45:25 |         20 | Canada    | 2006-02-15 04:44:00 |
-- |          4 | 1411 Lillydale Drive | NULL     | QLD      |     576 |             | 6172235589  | 2014-09-25 22:30:09 |     576 | Woodridge  |          8 | 2006-02-15 04:45:25 |          8 | Australia | 2006-02-15 04:44:00 |
-- |          5 | 1913 Hanoi Way       |          | Nagasaki |     463 | 35200       | 28303384290 | 2014-09-25 22:31:53 |     463 | Sasebo     |         50 | 2006-02-15 04:45:25 |         50 | Japan     | 2006-02-15 04:44:00 |
-- +------------+----------------------+----------+----------+---------+-------------+-------------+---------------------+---------+------------+------------+---------------------+------------+-----------+---------------------+
SELECT a.address_id, a.address, a.address2, a.district, a.city_id, a.postal_code, a.phone, a.last_update, ci.*, co.* -- every column but location, a GEOMETRY which does not print
FROM address a
         JOIN city ci ON ci.city_id = a.city_id
         JOIN country co ON co.country_id = ci.country_id
ORDER BY a.address_id LIMIT 5
;

-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-I-02
-- the district names found in more than one country.
-- +------------------+-----------+
-- | district         | countries |
-- +------------------+-----------+
-- | Central          |         3 |
-- | al-Sharqiya      |         2 |
-- | Distrito Federal |         2 |
-- | Ontario          |         2 |
-- | Punjab           |         2 |
-- | Sucre            |         2 |
-- +------------------+-----------+
SELECT a.district, COUNT(DISTINCT ci.country_id) AS countries
FROM address a
         JOIN city ci ON ci.city_id = a.city_id
WHERE a.district <> ''
GROUP BY a.district
HAVING COUNT(DISTINCT ci.country_id) > 1
ORDER BY countries DESC, a.district
;

-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-I-03
-- how many addresses belong to a store, a staff member or a customer.
-- +----------+-----------+
-- | used_by  | addresses |
-- +----------+-----------+
-- | customer |       599 |
-- | staff    |         2 |
-- | store    |         2 |
-- +----------+-----------+
SELECT CASE
           WHEN EXISTS (SELECT 1 FROM store s WHERE s.address_id = a.address_id) THEN 'store'
           WHEN EXISTS (SELECT 1 FROM staff s WHERE s.address_id = a.address_id) THEN 'staff'
           WHEN EXISTS (SELECT 1 FROM customer c WHERE c.address_id = a.address_id) THEN 'customer'
           ELSE 'none'
           END  AS used_by,
       COUNT(*) AS addresses
FROM address a
GROUP BY used_by
ORDER BY addresses DESC, used_by
;
