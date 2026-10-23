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
-- Exercises at the basic level on store: one table: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/store_exercises_01_basic.sql
--


-- --------------------------------------------------------------------------------------------------- SAKILA-STORE-B-01
-- every store, with its manager and address ids.
-- +----------+------------------+------------+---------------------+
-- | store_id | manager_staff_id | address_id | last_update         |
-- +----------+------------------+------------+---------------------+
-- |        1 |                1 |          1 | 2006-02-15 04:57:12 |
-- |        2 |                2 |          2 | 2006-02-15 04:57:12 |
-- +----------+------------------+------------+---------------------+
SELECT *
FROM store
ORDER BY store_id
;
