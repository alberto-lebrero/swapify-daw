CREATE DATABASE IF NOT EXISTS swapify 
	CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY 'admin';

GRANT ALL PRIVILEGES ON swapify.* TO 'admin'@'localhost';

FLUSH PRIVILEGES;

use swapify;

SELECT BIN_TO_UUID(role_id), email FROM users;
SELECT BIN_TO_UUID(id), name FROM roles;

UPDATE users
SET role_id = UUID_TO_BIN('436a54c6-56e6-11f1-82a1-0a002700000b')
WHERE email = 'admin@admin.com';

select * from users;
select * from profiles;
select * from roles;
select * from posts;
select * from categories;
select * from favorites;
select * from images;

INSERT INTO roles (id, name) VALUES
    (UUID_TO_BIN(UUID()), 'USER'),
    (UUID_TO_BIN(UUID()), 'ADMIN');
    
INSERT INTO categories (id, name, description, icon_url) VALUES
(UUID_TO_BIN(UUID()), 'Electrónica', 'Dispositivos electrónicos, móviles, ordenadores y accesorios', NULL),
(UUID_TO_BIN(UUID()), 'Ropa y accesorios', 'Ropa, calzado, bolsos y complementos de moda', NULL),
(UUID_TO_BIN(UUID()), 'Hogar y muebles', 'Muebles, decoración y artículos para el hogar', NULL),
(UUID_TO_BIN(UUID()), 'Libros y material educativo', 'Libros, apuntes, material escolar y universitario', NULL),
(UUID_TO_BIN(UUID()), 'Deportes y ocio', 'Equipamiento deportivo, bicicletas y artículos de ocio', NULL),
(UUID_TO_BIN(UUID()), 'Juguetes y juegos', 'Juguetes, juegos de mesa y videojuegos', NULL),
(UUID_TO_BIN(UUID()), 'Vehículos y accesorios', 'Coches, motos, bicicletas y sus accesorios', NULL),
(UUID_TO_BIN(UUID()), 'Coleccionismo', 'Objetos de colección, antigüedades y artículos vintage', NULL),
(UUID_TO_BIN(UUID()), 'Alimentación', 'Productos alimenticios, bebidas y artículos gourmet', NULL),
(UUID_TO_BIN(UUID()), 'Clases y formación', 'Clases particulares, idiomas y formación académica', NULL),
(UUID_TO_BIN(UUID()), 'Reparaciones y mantenimiento', 'Servicios de reparación de electrodomésticos, muebles y otros', NULL),
(UUID_TO_BIN(UUID()), 'Transporte y mudanzas', 'Servicios de transporte, mensajería y mudanzas', NULL),
(UUID_TO_BIN(UUID()), 'Informática y tecnología', 'Servicios de soporte técnico, programación y diseño web', NULL),
(UUID_TO_BIN(UUID()), 'Diseño y creatividad', 'Diseño gráfico, fotografía, ilustración y artes creativas', NULL),
(UUID_TO_BIN(UUID()), 'Jardinería y limpieza', 'Servicios de jardinería, limpieza del hogar y mantenimiento', NULL),
(UUID_TO_BIN(UUID()), 'Cuidado de personas y animales', 'Cuidado de niños, mayores, mascotas y servicios afines', NULL),
(UUID_TO_BIN(UUID()), 'Eventos y fotografía', 'Organización de eventos, fotografía y servicios audiovisuales', NULL),
(UUID_TO_BIN(UUID()), 'Salud y bienestar', 'Servicios de salud, fisioterapia, nutrición y bienestar personal', NULL),
(UUID_TO_BIN(UUID()), 'Otros', 'Artículos y servicios que no encajan en otras categorías', NULL);