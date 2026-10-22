USE sakila;

--
-- A store and its staff without the cycle.
--
-- better_store and better_staff refer to nothing; better_employment links a staff member to the store they work at,
-- and flags the store's manager. Rows go in as better_store, better_staff, then better_employment, with foreign keys
-- checked throughout.
--
-- Enforced: a staff member works at one store at most (staff_id is unique); a store has one manager at most (the
-- unique (manager, store_id), whose NULLs do not collide, and the check which keeps the flag TRUE or NULL); the manager
-- works at the store they manage (the flag is on the employment). Not enforced: that every store has a manager, and
-- that every staff member works at a store.
--

CREATE TABLE better_store (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE better_staff (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE better_employment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  staff_id BIGINT NOT NULL,
  manager BOOLEAN NULL DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_better_employment_staff (staff_id),
  -- manager first: with store_id first, MySQL 9.7.2 skips the foreign key check of store_id for a row whose manager
  -- is NULL, the index being the one the foreign key uses
  UNIQUE KEY uk_better_employment_manager (manager, store_id),
  CONSTRAINT ck_better_employment_manager CHECK (manager IS NULL OR manager = TRUE),
  CONSTRAINT fk_better_employment_store FOREIGN KEY (store_id) REFERENCES better_store (id)
      ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_better_employment_staff FOREIGN KEY (staff_id) REFERENCES better_staff (id)
      ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
