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
-- Exercises at the advanced level on country: several of those together: a self-join, a relational division, correlated
-- subqueries, or one result checked against another. Each poses a question, answers it, and shows what the installed
-- data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/country_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-A-01
-- the fewest countries which hold half of the customers.
-- +--------------------+-----------+---------------+-----------------+
-- | country            | customers | running_total | running_percent |
-- +--------------------+-----------+---------------+-----------------+
-- | India              |        60 |            60 |           10.02 |
-- | China              |        53 |           113 |           18.86 |
-- | United States      |        36 |           149 |           24.87 |
-- | Japan              |        31 |           180 |           30.05 |
-- | Mexico             |        30 |           210 |           35.06 |
-- | Brazil             |        28 |           238 |           39.73 |
-- | Russian Federation |        28 |           266 |           44.41 |
-- | Philippines        |        20 |           286 |           47.75 |
-- | Turkey             |        15 |           301 |           50.25 |
-- +--------------------+-----------+---------------+-----------------+
WITH counted AS (SELECT co.country, COUNT(*) AS customers
                 FROM country co
                          JOIN city ci ON ci.country_id = co.country_id
                          JOIN address a ON a.city_id = ci.city_id
                          JOIN customer cu ON cu.address_id = a.address_id
                 GROUP BY co.country_id, co.country),
     running AS (SELECT counted.*,
                        SUM(customers) OVER (ORDER BY customers DESC, country) AS running_total,
                        SUM(customers) OVER () AS total
                 FROM counted)
SELECT country, customers, running_total, ROUND(100 * running_total / total, 2) AS running_percent
FROM running
WHERE running_total - customers < total / 2
ORDER BY customers DESC, country
;

-- ------------------------------------------------------------------------------------------------- SAKILA-COUNTRY-A-02
-- the countries ranked in the top ten by customers, ties sharing a rank.
-- +--------------------+-----------+---------+
-- | country            | customers | ranking |
-- +--------------------+-----------+---------+
-- | India              |        60 |       1 |
-- | China              |        53 |       2 |
-- | United States      |        36 |       3 |
-- | Japan              |        31 |       4 |
-- | Mexico             |        30 |       5 |
-- | Brazil             |        28 |       6 |
-- | Russian Federation |        28 |       6 |
-- | Philippines        |        20 |       8 |
-- | Turkey             |        15 |       9 |
-- | Indonesia          |        14 |      10 |
-- +--------------------+-----------+---------+
SELECT country, customers, ranking
FROM (SELECT co.country, COUNT(*) AS customers, RANK() OVER (ORDER BY COUNT(*) DESC) AS ranking
      FROM country co
               JOIN city ci ON ci.country_id = co.country_id
               JOIN address a ON a.city_id = ci.city_id
               JOIN customer cu ON cu.address_id = a.address_id
      GROUP BY co.country_id, co.country) t
WHERE ranking <= 10
ORDER BY ranking, country
;
