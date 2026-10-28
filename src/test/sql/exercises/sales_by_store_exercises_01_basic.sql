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
-- Exercises at the basic level on the sales_by_store view: one table: a filter, a sort, a count or an aggregate. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/exercises/sales_by_store_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------ SAKILA_SALES_BY_STORE_B_01
-- every store's sales.
-- +---------------------+--------------+-------------+
-- | store               | manager      | total_sales |
-- +---------------------+--------------+-------------+
-- | Woodridge,Australia | Jon Stephens |    33726.77 |
-- | Lethbridge,Canada   | Mike Hillyer |    33679.79 |
-- +---------------------+--------------+-------------+
SELECT *
FROM sales_by_store
;

-- ------------------------------------------------------------------------------------------ SAKILA_SALES_BY_STORE_B_02
-- how far apart the two stores' sales are.
-- +------------+
-- | difference |
-- +------------+
-- |      46.98 |
-- +------------+
SELECT MAX(total_sales) - MIN(total_sales) AS difference
FROM sales_by_store
;
