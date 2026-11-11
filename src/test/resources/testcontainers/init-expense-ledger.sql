-- Debe reflejar repos/tracker-infra/postgres/init/02-services.sh (parte de expense_ledger).
-- Corre como superusuario desde /docker-entrypoint-initdb.d antes de que el contenedor quede listo.
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE "ExpenseTracker" FROM PUBLIC;

CREATE ROLE expense_ledger LOGIN PASSWORD 'expense-ledger-it';
GRANT CONNECT ON DATABASE "ExpenseTracker" TO expense_ledger;
CREATE SCHEMA expense_ledger AUTHORIZATION expense_ledger;
ALTER ROLE expense_ledger IN DATABASE "ExpenseTracker" SET search_path = expense_ledger;

-- Presupuesto de conexiones (ADR-013): límite = pool del servicio + 2 (psql o debug con su rol).
ALTER ROLE expense_ledger CONNECTION LIMIT 12;

-- Schema ajeno (del superusuario) para probar el aislamiento
CREATE SCHEMA foreign_service;
CREATE TABLE foreign_service.secret (id int);
