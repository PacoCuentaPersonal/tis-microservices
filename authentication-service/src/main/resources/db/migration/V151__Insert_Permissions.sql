-- Permisos para INVENTARIO/STOCK (submodule_id = 1)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(1, 1, 'CREAR_STOCK', 1, 'system'),
(1, 2, 'LEER_STOCK', 1, 'system'),
(1, 3, 'ACTUALIZAR_STOCK', 1, 'system'),
(1, 4, 'ELIMINAR_STOCK', 1, 'system'),
(1, 5, 'LISTAR_STOCK', 1, 'system'),
(1, 6, 'BUSCAR_STOCK', 1, 'system'),
(1, 7, 'IMPORTAR_STOCK', 1, 'system'),
(1, 8, 'EXPORTAR_STOCK', 1, 'system');

-- Permisos para INVENTARIO/MOVIMIENTOS (submodule_id = 2)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(2, 1, 'CREAR_MOVIMIENTOS', 1, 'system'),
(2, 2, 'LEER_MOVIMIENTOS', 1, 'system'),
(2, 3, 'ACTUALIZAR_MOVIMIENTOS', 1, 'system'),
(2, 4, 'ELIMINAR_MOVIMIENTOS', 1, 'system'),
(2, 5, 'LISTAR_MOVIMIENTOS', 1, 'system'),
(2, 6, 'BUSCAR_MOVIMIENTOS', 1, 'system'),
(2, 7, 'IMPORTAR_MOVIMIENTOS', 1, 'system'),
(2, 8, 'EXPORTAR_MOVIMIENTOS', 1, 'system');

-- Permisos para INVENTARIO/AJUSTES (submodule_id = 3)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(3, 1, 'CREAR_AJUSTES', 1, 'system'),
(3, 2, 'LEER_AJUSTES', 1, 'system'),
(3, 3, 'ACTUALIZAR_AJUSTES', 1, 'system'),
(3, 4, 'ELIMINAR_AJUSTES', 1, 'system'),
(3, 5, 'LISTAR_AJUSTES', 1, 'system'),
(3, 6, 'BUSCAR_AJUSTES', 1, 'system'),
(3, 7, 'IMPORTAR_AJUSTES', 1, 'system'),
(3, 8, 'EXPORTAR_AJUSTES', 1, 'system');

-- Permisos para INVENTARIO/CONTEO (submodule_id = 4)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(4, 1, 'CREAR_CONTEO', 1, 'system'),
(4, 2, 'LEER_CONTEO', 1, 'system'),
(4, 3, 'ACTUALIZAR_CONTEO', 1, 'system'),
(4, 4, 'ELIMINAR_CONTEO', 1, 'system'),
(4, 5, 'LISTAR_CONTEO', 1, 'system'),
(4, 6, 'BUSCAR_CONTEO', 1, 'system'),
(4, 7, 'IMPORTAR_CONTEO', 1, 'system'),
(4, 8, 'EXPORTAR_CONTEO', 1, 'system');

-- Permisos para COMPRAS/ORDENES_COMPRA (submodule_id = 5)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(5, 1, 'CREAR_ORDEN_COMPRA', 1, 'system'),
(5, 2, 'LEER_ORDEN_COMPRA', 1, 'system'),
(5, 3, 'ACTUALIZAR_ORDEN_COMPRA', 1, 'system'),
(5, 4, 'ELIMINAR_ORDEN_COMPRA', 1, 'system'),
(5, 5, 'LISTAR_ORDEN_COMPRA', 1, 'system'),
(5, 6, 'BUSCAR_ORDEN_COMPRA', 1, 'system'),
(5, 7, 'IMPORTAR_ORDEN_COMPRA', 1, 'system'),
(5, 8, 'EXPORTAR_ORDEN_COMPRA', 1, 'system');

-- Permisos para COMPRAS/REQUISICIONES (submodule_id = 6)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(6, 1, 'CREAR_REQUISICIONES', 1, 'system'),
(6, 2, 'LEER_REQUISICIONES', 1, 'system'),
(6, 3, 'ACTUALIZAR_REQUISICIONES', 1, 'system'),
(6, 4, 'ELIMINAR_REQUISICIONES', 1, 'system'),
(6, 5, 'LISTAR_REQUISICIONES', 1, 'system'),
(6, 6, 'BUSCAR_REQUISICIONES', 1, 'system'),
(6, 7, 'IMPORTAR_REQUISICIONES', 1, 'system'),
(6, 8, 'EXPORTAR_REQUISICIONES', 1, 'system');

-- Permisos para COMPRAS/RECEPCIONES (submodule_id = 7)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(7, 1, 'CREAR_RECEPCIONES', 1, 'system'),
(7, 2, 'LEER_RECEPCIONES', 1, 'system'),
(7, 3, 'ACTUALIZAR_RECEPCIONES', 1, 'system'),
(7, 4, 'ELIMINAR_RECEPCIONES', 1, 'system'),
(7, 5, 'LISTAR_RECEPCIONES', 1, 'system'),
(7, 6, 'BUSCAR_RECEPCIONES', 1, 'system'),
(7, 7, 'IMPORTAR_RECEPCIONES', 1, 'system'),
(7, 8, 'EXPORTAR_RECEPCIONES', 1, 'system');

-- Permisos para ALMACEN/UBICACIONES (submodule_id = 8)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(8, 1, 'CREAR_UBICACIONES', 1, 'system'),
(8, 2, 'LEER_UBICACIONES', 1, 'system'),
(8, 3, 'ACTUALIZAR_UBICACIONES', 1, 'system'),
(8, 4, 'ELIMINAR_UBICACIONES', 1, 'system'),
(8, 5, 'LISTAR_UBICACIONES', 1, 'system'),
(8, 6, 'BUSCAR_UBICACIONES', 1, 'system'),
(8, 7, 'IMPORTAR_UBICACIONES', 1, 'system'),
(8, 8, 'EXPORTAR_UBICACIONES', 1, 'system');

-- Permisos para ALMACEN/PICKING (submodule_id = 9)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(9, 1, 'CREAR_PICKING', 1, 'system'),
(9, 2, 'LEER_PICKING', 1, 'system'),
(9, 3, 'ACTUALIZAR_PICKING', 1, 'system'),
(9, 4, 'ELIMINAR_PICKING', 1, 'system'),
(9, 5, 'LISTAR_PICKING', 1, 'system'),
(9, 6, 'BUSCAR_PICKING', 1, 'system'),
(9, 7, 'IMPORTAR_PICKING', 1, 'system'),
(9, 8, 'EXPORTAR_PICKING', 1, 'system');

-- Permisos para ALMACEN/CROSSDOCK (submodule_id = 10)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(10, 1, 'CREAR_CROSSDOCK', 1, 'system'),
(10, 2, 'LEER_CROSSDOCK', 1, 'system'),
(10, 3, 'ACTUALIZAR_CROSSDOCK', 1, 'system'),
(10, 4, 'ELIMINAR_CROSSDOCK', 1, 'system'),
(10, 5, 'LISTAR_CROSSDOCK', 1, 'system'),
(10, 6, 'BUSCAR_CROSSDOCK', 1, 'system'),
(10, 7, 'IMPORTAR_CROSSDOCK', 1, 'system'),
(10, 8, 'EXPORTAR_CROSSDOCK', 1, 'system');

-- Permisos para DISTRIBUCION/RUTAS (submodule_id = 11)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(11, 1, 'CREAR_RUTAS', 1, 'system'),
(11, 2, 'LEER_RUTAS', 1, 'system'),
(11, 3, 'ACTUALIZAR_RUTAS', 1, 'system'),
(11, 4, 'ELIMINAR_RUTAS', 1, 'system'),
(11, 5, 'LISTAR_RUTAS', 1, 'system'),
(11, 6, 'BUSCAR_RUTAS', 1, 'system'),
(11, 7, 'IMPORTAR_RUTAS', 1, 'system'),
(11, 8, 'EXPORTAR_RUTAS', 1, 'system');

-- Permisos para DISTRIBUCION/DESPACHOS (submodule_id = 12)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(12, 1, 'CREAR_DESPACHOS', 1, 'system'),
(12, 2, 'LEER_DESPACHOS', 1, 'system'),
(12, 3, 'ACTUALIZAR_DESPACHOS', 1, 'system'),
(12, 4, 'ELIMINAR_DESPACHOS', 1, 'system'),
(12, 5, 'LISTAR_DESPACHOS', 1, 'system'),
(12, 6, 'BUSCAR_DESPACHOS', 1, 'system'),
(12, 7, 'IMPORTAR_DESPACHOS', 1, 'system'),
(12, 8, 'EXPORTAR_DESPACHOS', 1, 'system');

-- Permisos para DISTRIBUCION/SEGUIMIENTO (submodule_id = 13)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(13, 1, 'CREAR_SEGUIMIENTO', 1, 'system'),
(13, 2, 'LEER_SEGUIMIENTO', 1, 'system'),
(13, 3, 'ACTUALIZAR_SEGUIMIENTO', 1, 'system'),
(13, 4, 'ELIMINAR_SEGUIMIENTO', 1, 'system'),
(13, 5, 'LISTAR_SEGUIMIENTO', 1, 'system'),
(13, 6, 'BUSCAR_SEGUIMIENTO', 1, 'system'),
(13, 7, 'IMPORTAR_SEGUIMIENTO', 1, 'system'),
(13, 8, 'EXPORTAR_SEGUIMIENTO', 1, 'system');

-- Permisos para USUARIOS/ROLES (submodule_id = 14)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(14, 1, 'CREAR_ROLES', 1, 'system'),
(14, 2, 'LEER_ROLES', 1, 'system'),
(14, 3, 'ACTUALIZAR_ROLES', 1, 'system'),
(14, 4, 'ELIMINAR_ROLES', 1, 'system'),
(14, 5, 'LISTAR_ROLES', 1, 'system'),
(14, 6, 'BUSCAR_ROLES', 1, 'system'),
(14, 7, 'IMPORTAR_ROLES', 1, 'system'),
(14, 8, 'EXPORTAR_ROLES', 1, 'system');

-- Permisos para USUARIOS/PERMISOS (submodule_id = 15)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(15, 1, 'CREAR_PERMISOS', 1, 'system'),
(15, 2, 'LEER_PERMISOS', 1, 'system'),
(15, 3, 'ACTUALIZAR_PERMISOS', 1, 'system'),
(15, 4, 'ELIMINAR_PERMISOS', 1, 'system'),
(15, 5, 'LISTAR_PERMISOS', 1, 'system'),
(15, 6, 'BUSCAR_PERMISOS', 1, 'system'),
(15, 7, 'IMPORTAR_PERMISOS', 1, 'system'),
(15, 8, 'EXPORTAR_PERMISOS', 1, 'system');

-- Permisos para USUARIOS/USUARIOS (submodule_id = 16)
INSERT INTO app.permission (submodule_id, action_id, name, active, created_by)
VALUES
(16, 1, 'CREAR_USUARIOS', 1, 'system'),
(16, 2, 'LEER_USUARIOS', 1, 'system'),
(16, 3, 'ACTUALIZAR_USUARIOS', 1, 'system'),
(16, 4, 'ELIMINAR_USUARIOS', 1, 'system'),
(16, 5, 'LISTAR_USUARIOS', 1, 'system'),
(16, 6, 'BUSCAR_USUARIOS', 1, 'system'),
(16, 7, 'IMPORTAR_USUARIOS', 1, 'system'),
(16, 8, 'EXPORTAR_USUARIOS', 1, 'system');