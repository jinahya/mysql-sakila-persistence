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


-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-A-01
-- the addresses no customer, staff member or store has.
-- no rows.
SELECT a.*
FROM address a
WHERE NOT EXISTS (SELECT 1 FROM customer c WHERE c.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM staff s WHERE s.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM store s WHERE s.address_id = a.address_id)
ORDER BY a.address_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-A-02
-- the pairs of addresses which share a postal code.
-- +------------+------------------------------+------------------+------------------------+-------------+
-- | address_id | address                      | other_address_id | other_address          | postal_code |
-- +------------+------------------------------+------------------+------------------------+-------------+
-- |        221 | 866 Shivapuri Manor          |              595 | 1074 Sanaa Parkway     | 22474       |
-- |        304 | 1635 Kuwana Boulevard        |              603 | 1103 Quilmes Boulevard | 52137       |
-- |        313 | 1191 Sungai Petani Boulevard |              476 | 270 Tambaram Parkway   | 9668        |
-- +------------+------------------------------+------------------+------------------------+-------------+
SELECT a1.address_id,
       a1.address,
       a2.address_id AS other_address_id,
       a2.address    AS other_address,
       a1.postal_code
FROM address a1
         JOIN address a2 ON a2.postal_code = a1.postal_code AND a2.address_id > a1.address_id
WHERE a1.postal_code <> ''
ORDER BY a1.address_id, a2.address_id
;

-- ------------------------------------------------------------------------------------------------- SAKILA-ADDRESS-A-03
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
