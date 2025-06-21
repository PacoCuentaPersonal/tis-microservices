
-- Módulos básicos
INSERT INTO oauth2.modules (code, name, active)
VALUES ('SALES', 'Ventas', 1),
       ('PURCHASE', 'Compras', 1),
       ('INVENTORY', 'Inventario', 1);

-- Submódulos para el módulo SALES (id = 1)
INSERT INTO oauth2.submodules (module_id, code, name, active)
VALUES (1, 'ORDERS_SALE', 'Órdenes de venta', 1),
       (1, 'INVOICES', 'Facturas', 1);

-- Submódulos para el módulo PURCHASE (id = 2)
INSERT INTO oauth2.submodules (module_id, code, name, active)
VALUES (2, 'ORDERS_PURCHASE', 'Órdenes de compras', 1),
       (2, 'SUPPLIERS', 'Proveedores', 1);

-- Submódulos para el módulo INVENTORY (id = 3)
INSERT INTO oauth2.submodules (module_id, code, name, active)
VALUES (3, 'PRODUCTS', 'Productos', 1),
       (3, 'PRODUCTS_CATEGORY', 'Categoría de productos', 1),
       (3, 'WAREHOUSES', 'Almacenes', 1);

-- Permisos de gestión por submódulo
-- Permisos para SALES
INSERT INTO oauth2.permissions (submodule_id, code, name, active)
VALUES (1, 'PERMISSION_MANAGEMENT_ORDERS_SALE', 'Gestión de Órdenes de Venta', 1),
       (2, 'PERMISSION_MANAGEMENT_INVOICES', 'Gestión de Facturas', 1);

-- Permisos para PURCHASE
INSERT INTO oauth2.permissions (submodule_id, code, name, active)
VALUES (3, 'PERMISSION_MANAGEMENT_ORDERS_PURCHASE', 'Gestión de Órdenes de Compra', 1),
       (4, 'PERMISSION_MANAGEMENT_SUPPLIERS', 'Gestión de Proveedores', 1);

-- Permisos para INVENTORY
INSERT INTO oauth2.permissions (submodule_id, code, name, active)
VALUES (5, 'PERMISSION_MANAGEMENT_PRODUCTS', 'Gestión de Productos', 1),
       (6, 'PERMISSION_MANAGEMENT_PRODUCTS_CATEGORY', 'Gestión de Categorías de Productos', 1),
       (7, 'PERMISSION_MANAGEMENT_WAREHOUSES', 'Gestión de Almacenes', 1);

INSERT INTO oauth2.roles (name, description, active)
VALUES ('ROOT','Usuario raiz del sistema', 1),
       ('ADMIN','Usuario encargado de la gestión de cuentas', 1);


-- Asignación de TODOS los permisos al rol ROOT (id = 1)
INSERT INTO oauth2.roles_permissions (permission_id, role_id, active)
VALUES (1, 1, 1),
       (2, 1, 1),
       (3, 1, 1),
       (4, 1, 1),
       (5, 1, 1),
       (6, 1, 1),
       (7, 1, 1);


-- Usuario administrador inicial (contraseña: password123)
INSERT INTO oauth2.account (password, email, username, profile_picture_url, email_verified, role_id, employee_public_id)
VALUES ('$2a$10$iHyISe6FwNugXA5mCyWSTu04f0IGq4gaUyQTobaN.8.avDi/2/ncO', 'root@example.com','jianfrancoRoot','https://us.123rf.com/450wm/saphatthachat/saphatthachat2302/saphatthachat230200007/198075555-pixel-art-of-office-worker-umbrella.jpg?ver=6',1, 1, NEWID());



