--
-- Profiling queries on sales_by_store, a view, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i mysql-sakila mysql -uroot -ppassword -t sakila < src/test/sql/sales_by_store.sql
--


-- ---------------------------------------------------------------------------------------------------------------------
-- the view sums payment's amount, through rental and inventory, by the inventory's store.
-- the store is the one which holds the rented inventory, not the one of the staff who took the payment.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = DATABASE()
  AND table_name = 'sales_by_store'
;


-- --------------------------------------------------------------------------------------------------------------- store
-- store is nullable, CONCAT(city, ',', country) of the store's address; the rows, in the view's own order.
-- 2 rows, as many as store has; 'Woodridge,Australia' first, by country then by city.
SELECT store, manager, total_sales
FROM sales_by_store
;

-- a store more than one row has; the view groups by store_id, which it does not select, and two stores may share a
-- city and a country.
-- no rows: the two installed stores are in different cities.
SELECT store, COUNT(*) AS rows_
FROM sales_by_store
GROUP BY store
HAVING COUNT(*) > 1
;


-- ------------------------------------------------------------------------------------------------------------- manager
-- manager is nullable, CONCAT(first_name, ' ', last_name) of the store's manager_staff_id.
-- Jon Stephens of Woodridge, and Mike Hillyer of Lethbridge.
SELECT v.store, v.manager, CONCAT(m.first_name, ' ', m.last_name) AS manager_
FROM store s
         JOIN address a ON a.address_id = s.address_id
         JOIN city c ON c.city_id = a.city_id
         JOIN country cy ON cy.country_id = c.country_id
         JOIN staff m ON m.staff_id = s.manager_staff_id
         JOIN sales_by_store v ON v.store = CONCAT(c.city, ',', cy.country)
;


-- --------------------------------------------------------------------------------------------------------- total_sales
-- the sum of payment's amount by the inventory's store, against total_sales.
-- 33679.79 of store 1, Lethbridge, and 33726.77 of store 2, Woodridge; 67406.56 in all.
SELECT i.store_id, SUM(p.amount) AS amount
FROM payment p
         JOIN rental r ON r.rental_id = p.rental_id
         JOIN inventory i ON i.inventory_id = r.inventory_id
GROUP BY i.store_id
ORDER BY i.store_id ASC
;

-- the sum of payment's amount by the staff who took it; not what the view sums.
-- 33482.50 by staff 1, and 33924.06 by staff 2.
SELECT staff_id, SUM(amount) AS amount
FROM payment
GROUP BY staff_id
ORDER BY staff_id ASC
;
