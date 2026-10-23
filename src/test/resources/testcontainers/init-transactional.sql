-- Debe reflejar repos/tracker-infra/postgres/init/02-services.sh (parte de transactional).
-- Corre como superusuario desde /docker-entrypoint-initdb.d antes de que el contenedor quede listo.
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE "ExpenseTracker" FROM PUBLIC;

CREATE ROLE transactional LOGIN PASSWORD 'transactional-it';
GRANT CONNECT ON DATABASE "ExpenseTracker" TO transactional;
CREATE SCHEMA transactional AUTHORIZATION transactional;
ALTER ROLE transactional IN DATABASE "ExpenseTracker" SET search_path = transactional;

-- Schema ajeno (del superusuario) para probar el aislamiento
CREATE SCHEMA foreign_service;
CREATE TABLE foreign_service.secret (id int);
