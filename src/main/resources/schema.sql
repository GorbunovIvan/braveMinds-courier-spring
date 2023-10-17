CREATE TABLE IF NOT EXISTS orders (
    id integer PRIMARY KEY AUTO_INCREMENT,
    latitude double NOT NULL,
    longitude double NOT NULL,
    delivery_from time NOT NULL,
    delivery_to time NOT NULL
);

TRUNCATE orders;

INSERT INTO orders (id, latitude, longitude, delivery_from, delivery_to) VALUES (1, 50.4839843, 30.5350063, '10:00:00', '20:00:00');
INSERT INTO orders (id, latitude, longitude, delivery_from, delivery_to) VALUES (2, 50.4716034, 30.4831969, '14:00:00', '16:00:00');
INSERT INTO orders (id, latitude, longitude, delivery_from, delivery_to) VALUES (3, 50.4085094, 30.5713501, '12:00:00', '15:00:00');
INSERT INTO orders (id, latitude, longitude, delivery_from, delivery_to) VALUES (4, 50.4258262, 30.5716457, '19:00:00', '20:00:00');
INSERT INTO orders (id, latitude, longitude, delivery_from, delivery_to) VALUES (5, 49.9947277, 36.1457429, '10:00:00', '18:00:00');