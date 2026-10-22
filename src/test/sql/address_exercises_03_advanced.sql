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


-- ---------------------------------------------------------------------------------------------------------- exercise 1
-- the addresses no customer, staff member or store has.
-- no rows.
SELECT a.*
FROM address a
WHERE NOT EXISTS (SELECT 1 FROM customer c WHERE c.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM staff s WHERE s.address_id = a.address_id)
  AND NOT EXISTS (SELECT 1 FROM store s WHERE s.address_id = a.address_id)
ORDER BY a.address_id
;
