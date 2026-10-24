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
-- Exercises at the advanced level on address: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/address_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA_ADDRESS_A_01
-- the addresses no customer, staff member or store has.
-- no rows.
SELECT a.*
FROM address a
WHERE NOT EXISTS (SELECT 1 FROM customer c WHERE c.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM staff s WHERE s.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM store s WHERE s.address_id = a.address_id)
ORDER BY a.address_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA_ADDRESS_A_02
-- the pairs of addresses which share a postal code.
-- +------------+------------------------------+----------+---------------+---------+-------------+--------------+---------------------+------------+------------------------+----------+----------+---------+-------------+--------------+---------------------+
-- | address_id | address                      | address2 | district      | city_id | postal_code | phone        | last_update         | address_id | address                | address2 | district | city_id | postal_code | phone        | last_update         |
-- +------------+------------------------------+----------+---------------+---------+-------------+--------------+---------------------+------------+------------------------+----------+----------+---------+-------------+--------------+---------------------+
-- |        221 | 866 Shivapuri Manor          |          | Uttar Pradesh |     448 | 22474       | 778502731092 | 2014-09-25 22:31:26 |        595 | 1074 Sanaa Parkway     |          | Loja     |     311 | 22474       | 154124128457 | 2014-09-25 22:30:55 |
-- |        304 | 1635 Kuwana Boulevard        |          | Hiroshima     |     205 | 52137       | 710603868323 | 2014-09-25 22:31:46 |        603 | 1103 Quilmes Boulevard |          | Piura    |     503 | 52137       | 644021380889 | 2014-09-25 22:32:34 |
-- |        313 | 1191 Sungai Petani Boulevard |          | Missouri      |     262 | 9668        | 983259819766 | 2014-09-25 22:33:54 |        476 | 270 Tambaram Parkway   |          | Gauteng  |     244 | 9668        | 248446668735 | 2014-09-25 22:33:03 |
-- +------------+------------------------------+----------+---------------+---------+-------------+--------------+---------------------+------------+------------------------+----------+----------+---------+-------------+--------------+---------------------+
SELECT a1.address_id, a1.address, a1.address2, a1.district, a1.city_id, a1.postal_code, a1.phone, a1.last_update, -- every column but location, a GEOMETRY which does not print
       a2.address_id, a2.address, a2.address2, a2.district, a2.city_id, a2.postal_code, a2.phone, a2.last_update
FROM address a1
         JOIN address a2 ON a2.postal_code = a1.postal_code AND a2.address_id > a1.address_id
WHERE a1.postal_code <> ''
ORDER BY a1.address_id, a2.address_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA_ADDRESS_A_03
-- the three districts with the most addresses in each of India, China and the United States.
-- +---------------+---------------+-----------+
-- | country       | district      | addresses |
-- +---------------+---------------+-----------+
-- | China         | Shandong      |         9 |
-- | China         | Hubei         |         5 |
-- | China         | Guangdong     |         4 |
-- | India         | West Bengali  |         9 |
-- | India         | Uttar Pradesh |         8 |
-- | India         | Maharashtra   |         7 |
-- | United States | California    |         9 |
-- | United States | Texas         |         5 |
-- | United States | Illinois      |         4 |
-- +---------------+---------------+-----------+
SELECT country, district, addresses
FROM (SELECT co.country,
             a.district,
             COUNT(*)                                                                       AS addresses,
             ROW_NUMBER() OVER (PARTITION BY co.country_id ORDER BY COUNT(*) DESC, a.district) AS district_rank
      FROM address a
               JOIN city ci ON ci.city_id = a.city_id
               JOIN country co ON co.country_id = ci.country_id
      WHERE co.country IN ('India', 'China', 'United States')
      GROUP BY co.country_id, co.country, a.district) ranked
WHERE district_rank <= 3
ORDER BY country, district_rank
;
