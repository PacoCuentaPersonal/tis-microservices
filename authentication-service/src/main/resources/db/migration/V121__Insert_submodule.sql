INSERT INTO app.submodule (module_id, name, icon, route, active, created_by)
VALUES
    (1, 'STOCK', 'layers', '/inventory/stock', 1, 'system'),
    (1, 'MOVIMIENTOS', 'move', '/inventory/movements', 1, 'system'),
    (1, 'AJUSTES', 'edit', '/inventory/adjustments', 1, 'system'),
    (1, 'CONTEOS', 'clipboard', '/inventory/counts', 1, 'system');

INSERT INTO app.submodule (module_id, name, icon, route, active, created_by)
VALUES
    (2, 'ORDENES_COMPRA', 'file-text', '/procurement/orders', 1, 'system'),
    (2, 'REQUISICIONES', 'shopping-bag', '/procurement/requisitions', 1, 'system'),
    (2, 'RECEPCIONES', 'check-circle', '/procurement/receipts', 1, 'system');

INSERT INTO app.submodule (module_id, name, icon, route, active, created_by)
VALUES
    (3, 'UBICACIONES', 'map-pin', '/warehouse/locations', 1, 'system'),
    (3, 'PICKING', 'package', '/warehouse/picking', 1, 'system'),
    (3, 'CROSSDOCK', 'refresh-cw', '/warehouse/crossdock', 1, 'system');

INSERT INTO app.submodule (module_id, name, icon, route, active, created_by)
VALUES
    (4, 'RUTAS', 'map', '/distribution/routes', 1, 'system'),
    (4, 'DESPACHOS', 'truck', '/distribution/shipping', 1, 'system'),
    (4, 'SEGUIMIENTO', 'navigation', '/distribution/tracking', 1, 'system');

INSERT INTO app.submodule (module_id, name, icon, route, active, created_by)
VALUES
    (5, 'ROLES', 'shield', '/users/roles', 1, 'system'),
    (5, 'PERMISOS', 'key', '/users/permissions', 1, 'system'),
    (5, 'USUARIOS', 'people', '/users/users', 1, 'system');

