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
-- Exercises at the advanced level on sales_by_store: a window over the whole result. Each poses a question, answers it,
-- and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -p'Passw0rd!' -t sakila < src/test/sql-exercises/sales_by_store/03_advanced.sql
--


-- ------------------------------------------------------------------------------------------ SAKILA_SALES_BY_STORE_A_01
-- each store's share of all sales.
-- +---------------------+--------------+-------------+---------+
-- | store               | manager      | total_sales | percent |
-- +---------------------+--------------+-------------+---------+
-- | Woodridge,Australia | Jon Stephens |    33726.77 |   50.03 |
-- | Lethbridge,Canada   | Mike Hillyer |    33679.79 |   49.97 |
-- +---------------------+--------------+-------------+---------+
SELECT v.*, ROUND(100 * v.total_sales / SUM(v.total_sales) OVER (), 2) AS percent
FROM sales_by_store v
ORDER BY v.total_sales DESC, v.store
;
