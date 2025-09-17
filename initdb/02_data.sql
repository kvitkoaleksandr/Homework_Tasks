-- Departments
INSERT INTO departments (name, location) VALUES
('Engineering', 'Berlin'),
('Marketing',   'Prague'),
('Sales',       'Warsaw'),
('HR',          'Vilnius'),
('Support',     'Riga');

-- Employees
INSERT INTO employees (first_name, last_name, department_id, manager_id, hired_at, salary) VALUES
('Ivan','Petrov', 1, NULL, '2021-01-10', 2500),
('Olga','Sidorova', 1, 1, '2022-04-01', 4200),
('Max','Kuznetsov', 2, NULL, '2020-09-12', 3800),
('Anna','Smirnova', 2, 3, '2023-02-05', 5100),
('Kate','Novikova', 3, NULL, '2019-03-22', 3000),
('Pavel','Orlov',   3, 5, '2021-07-19', 4500),
('Sergey','Popov',  4, NULL, '2022-12-01', 2200),
('Nina','Lebedeva', 5, NULL, '2020-06-18', 2700),
('Dmitry','Ivanov', 1, 1, '2024-01-15', 6000),
('Alina','Morozova',2, 3, '2024-05-10', 5400);

-- Salaries history (для оконных функций)
INSERT INTO salaries_history (employee_id, from_date, to_date, salary) VALUES
(1,'2021-01-10','2022-12-31',2500),(1,'2023-01-01',NULL,2800),
(2,'2022-04-01',NULL,4200),
(3,'2020-09-12','2022-12-31',3600),(3,'2023-01-01',NULL,3800),
(4,'2023-02-05',NULL,5100),
(9,'2024-01-15',NULL,6000),
(10,'2024-05-10',NULL,5400);

-- Projects
INSERT INTO projects (name, start_date, budget) VALUES
('Phoenix','2023-01-01',150000),
('Orion','2023-06-01',80000),
('Atlas','2024-01-01',120000);

INSERT INTO employee_project (employee_id, project_id, role, allocation) VALUES
(1,1,'Dev',80),(2,1,'Dev',60),(9,1,'TechLead',50),
(3,2,'Analyst',70),(4,2,'Marketer',60),(10,3,'Analyst',50);

-- E-commerce demo
INSERT INTO customers (name,email) VALUES
('Alex K','alex@example.com'),
('Maria S','maria@example.com'),
('John D','john@example.com');

INSERT INTO products (name,category,price) VALUES
('Laptop','Electronics',1200),
('Mouse','Electronics',25),
('Book UML','Books',30),
('Coffee','Grocery',5);

INSERT INTO orders (customer_id, order_date, status) VALUES
(1,'2024-06-01','paid'),
(1,'2024-07-10','paid'),
(2,'2024-07-15','created'),
(3,'2024-08-01','paid');

INSERT INTO order_items (order_id, product_id, qty, unit_price) VALUES
(1,1,1,1200),(1,2,2,25),
(2,3,3,30),
(3,4,5,5),
(4,1,1,1150),(4,2,1,20);

INSERT INTO payments (order_id, amount, paid_at, method) VALUES
(1,1250,'2024-06-01','card'),
(2,90,'2024-07-10','transfer'),
(4,1170,'2024-08-01','card');