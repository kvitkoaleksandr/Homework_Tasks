CREATE TABLE departments (
  id           SERIAL PRIMARY KEY,
  name         TEXT NOT NULL UNIQUE,
  location     TEXT
);

CREATE TABLE employees (
  id              SERIAL PRIMARY KEY,
  first_name      TEXT NOT NULL,
  last_name       TEXT NOT NULL,
  department_id   INT REFERENCES departments(id) ON DELETE SET NULL,
  manager_id      INT REFERENCES employees(id) ON DELETE SET NULL,
  hired_at        DATE NOT NULL DEFAULT CURRENT_DATE,
  salary          NUMERIC(12,2) NOT NULL CHECK (salary >= 0)
);
CREATE INDEX idx_emp_dept ON employees(department_id);

-- История зарплат для оконных функций/агрегатов
CREATE TABLE salaries_history (
  id           SERIAL PRIMARY KEY,
  employee_id  INT NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
  from_date    DATE NOT NULL,
  to_date      DATE,
  salary       NUMERIC(12,2) NOT NULL CHECK (salary >= 0)
);
CREATE INDEX idx_salhist_emp ON salaries_history(employee_id);

-- Проекты (M:N через связующую таблицу)
CREATE TABLE projects (
  id         SERIAL PRIMARY KEY,
  name       TEXT NOT NULL UNIQUE,
  start_date DATE,
  end_date   DATE,
  budget     NUMERIC(14,2)
);

CREATE TABLE employee_project (
  employee_id INT REFERENCES employees(id) ON DELETE CASCADE,
  project_id  INT REFERENCES projects(id) ON DELETE CASCADE,
  role        TEXT,
  allocation  INT CHECK (allocation BETWEEN 0 AND 100),
  PRIMARY KEY (employee_id, project_id)
);

-- Простая e-commerce модель для сложных JOIN-ов
CREATE TABLE customers (
  id     SERIAL PRIMARY KEY,
  name   TEXT NOT NULL,
  email  TEXT UNIQUE
);

CREATE TABLE products (
  id       SERIAL PRIMARY KEY,
  name     TEXT NOT NULL,
  category TEXT,
  price    NUMERIC(12,2) NOT NULL CHECK (price >= 0)
);

CREATE TABLE orders (
  id           SERIAL PRIMARY KEY,
  customer_id  INT NOT NULL REFERENCES customers(id),
  order_date   TIMESTAMP NOT NULL DEFAULT now(),
  status       TEXT NOT NULL DEFAULT 'created'
);

CREATE TABLE order_items (
  order_id    INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id  INT NOT NULL REFERENCES products(id),
  qty         INT NOT NULL CHECK (qty > 0),
  unit_price  NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
  PRIMARY KEY (order_id, product_id)
);

CREATE TABLE payments (
  id         SERIAL PRIMARY KEY,
  order_id   INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  amount     NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
  paid_at    TIMESTAMP NOT NULL DEFAULT now(),
  method     TEXT NOT NULL CHECK (method IN ('card','cash','transfer'))
);

-- Полезные представления (для тренировки вложенных запросов)
CREATE VIEW v_order_totals AS
SELECT
  o.id AS order_id,
  o.customer_id,
  SUM(oi.qty * oi.unit_price) AS order_total
FROM orders o
JOIN order_items oi ON oi.order_id = o.id
GROUP BY o.id, o.customer_id;