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
-- Exercises at the basic level on address: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/address_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-B-01
-- how many addresses are in the district of California.
-- +-----------+
-- | addresses |
-- +-----------+
-- |         9 |
-- +-----------+
SELECT COUNT(*) AS addresses
FROM address
WHERE district = 'California'
;

-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-B-02
-- the five districts with the most addresses.
-- +--------------+-----------+
-- | district     | addresses |
-- +--------------+-----------+
-- | Buenos Aires |        10 |
-- | California   |         9 |
-- | Shandong     |         9 |
-- | West Bengali |         9 |
-- | São Paulo    |         8 |
-- +--------------+-----------+
SELECT district, COUNT(*) AS addresses
FROM address
GROUP BY district
ORDER BY addresses DESC, district LIMIT 5
;

-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-B-03
-- the addresses whose address2 is NULL.
-- +------------+----------------------+----------+
-- | address_id | address              | district |
-- +------------+----------------------+----------+
-- |          1 | 47 MySakila Drive    | Alberta  |
-- |          2 | 28 MySQL Boulevard   | QLD      |
-- |          3 | 23 Workhaven Lane    | Alberta  |
-- |          4 | 1411 Lillydale Drive | QLD      |
-- +------------+----------------------+----------+
SELECT address_id, address, district
FROM address
WHERE address2 IS NULL
ORDER BY address_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-B-04
-- the addresses with an empty district.
-- +------------+---------------------------+-------------+--------------+
-- | address_id | address                   | postal_code | phone        |
-- +------------+---------------------------+-------------+--------------+
-- |         30 | 18 Duisburg Boulevard     | 58327       | 998009777982 |
-- |        386 | 1368 Maracaíbo Boulevard  | 32716       | 934352415130 |
-- |        519 | 962 Tama Loop             | 65952       | 282667506728 |
-- +------------+---------------------------+-------------+--------------+
SELECT address_id, address, postal_code, phone
FROM address
WHERE district = ''
ORDER BY address_id
;
