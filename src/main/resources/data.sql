-- Limpar dados existentes
DELETE FROM post_likes;
DELETE FROM posts;
DELETE FROM products;
DELETE FROM user_follows;
DELETE FROM users;

-- Reset auto increment
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE posts AUTO_INCREMENT = 1;
ALTER TABLE user_follows AUTO_INCREMENT = 1;
ALTER TABLE post_likes AUTO_INCREMENT = 1;

-- Inserir usuários
INSERT INTO users (user_id, user_name, followers_count) VALUES
                                                            (1, 'joaosilva', 0),
                                                            (2, 'mariasantos', 0),
                                                            (3, 'pedrocosta', 0),
                                                            (4, 'anapereira', 0),
                                                            (5, 'carlosmendes', 0),
                                                            (6, 'juliaferreira', 0),
                                                            (7, 'lucasoliveira', 0),
                                                            (8, 'beatrizlima', 0),
                                                            (9, 'fernandogomes', 0),
                                                            (10, 'camilarodrigs', 0);

-- Inserir mais usuários
INSERT INTO users (user_id, user_name, followers_count) VALUES
                                                            (11, 'rafaelalves', 0),
                                                            (12, 'leticbarbosa', 0),
                                                            (13, 'guilhermesou', 0),
                                                            (14, 'brunamartins', 0),
                                                            (15, 'thiagoribeiro', 0),
                                                            (16, 'amandarocha', 0),
                                                            (17, 'felipearaujo', 0),
                                                            (18, 'renatacardoso', 0),
                                                            (19, 'viniciusmoura', 0),
                                                            (20, 'isabellydias', 0),
                                                            (21, 'marcosnascimen', 0),
                                                            (22, 'carolinacavalca', 0),
                                                            (23, 'danieloliveira', 0),
                                                            (24, 'larissasilveira', 0),
                                                            (25, 'gustavohenrique', 0),
                                                            (26, 'patriciandrade', 0),
                                                            (27, 'eduardopereira', 0),
                                                            (28, 'marianacorreia', 0),
                                                            (29, 'rodrigomonteiro', 0),
                                                            (30, 'luanasilva', 0),
                                                            (31, 'gabrielalima', 0),
                                                            (32, 'vitoriagomes', 0),
                                                            (33, 'hugocarvalho', 0),
                                                            (34, 'julianapinto', 0),
                                                            (35, 'matheusfreitas', 0),
                                                            (36, 'yasminteixeira', 0),
                                                            (37, 'otaviofernandes', 0),
                                                            (38, 'diegomachado', 0),
                                                            (39, 'paulasales', 0),
                                                            (40, 'brunobarros', 0),
                                                            (41, 'gabrielasantos', 0),
                                                            (42, 'arthurlopes', 0),
                                                            (43, 'emanuellymelo', 0),
                                                            (44, 'andersonmiranda', 0),
                                                            (45, 'lucianacampos', 0),
                                                            (46, 'renanramos', 0),
                                                            (47, 'milenaalmeida', 0),
                                                            (48, 'tiagofonseca', 0),
                                                            (49, 'stefaniacosta', 0),
                                                            (50, 'caioqueiroz', 0);

-- Inserir relacionamentos de follow
-- joaosilva (1) segue: maria (2), pedro (3), carlos (5)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (1, 2, '2025-12-01 10:00:00'),
                                                                     (1, 3, '2025-12-02 11:00:00'),
                                                                     (1, 5, '2025-12-03 12:00:00');

-- mariasantos (2) segue: joao (1), carlos (5), julia (6)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (2, 1, '2025-12-01 13:00:00'),
                                                                     (2, 5, '2025-12-04 14:00:00'),
                                                                     (2, 6, '2025-12-05 15:00:00');

-- pedrocoста (3) segue: joao (1), fernando (9)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (3, 1, '2025-12-02 16:00:00'),
                                                                     (3, 9, '2025-12-06 17:00:00');

-- anapereira (4) segue: carlos (5), julia (6), camila (10)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (4, 5, '2025-12-03 18:00:00'),
                                                                     (4, 6, '2025-12-07 19:00:00'),
                                                                     (4, 10, '2025-12-08 20:00:00');

-- carlosmendes (5) segue: joao (1), maria (2)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (5, 1, '2025-12-04 21:00:00'),
                                                                     (5, 2, '2025-12-09 22:00:00');

-- juliaferreira (6) segue: carlos (5), fernando (9)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (6, 5, '2025-12-05 08:00:00'),
                                                                     (6, 9, '2025-12-10 09:00:00');

-- lucasoliveira (7) segue: fernando (9)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
    (7, 9, '2025-12-06 10:00:00');

-- camilarodrigs (10) segue: carlos (5)
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
    (10, 5, '2025-12-11 11:00:00');

-- Inserir mais relacionamentos de follow
INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (11, 1, '2025-12-12 09:00:00'),
                                                                     (11, 2, '2025-12-12 09:30:00'),
                                                                     (11, 5, '2025-12-12 10:00:00'),
                                                                     (12, 1, '2025-12-12 10:30:00'),
                                                                     (12, 6, '2025-12-12 11:00:00'),
                                                                     (12, 9, '2025-12-12 11:30:00'),
                                                                     (13, 2, '2025-12-13 09:00:00'),
                                                                     (13, 3, '2025-12-13 09:30:00'),
                                                                     (13, 7, '2025-12-13 10:00:00'),
                                                                     (14, 5, '2025-12-13 10:30:00'),
                                                                     (14, 6, '2025-12-13 11:00:00'),
                                                                     (14, 10, '2025-12-13 11:30:00'),
                                                                     (15, 1, '2025-12-14 08:00:00'),
                                                                     (15, 9, '2025-12-14 08:30:00'),
                                                                     (16, 2, '2025-12-14 09:00:00'),
                                                                     (16, 5, '2025-12-14 09:30:00'),
                                                                     (17, 3, '2025-12-14 10:00:00'),
                                                                     (17, 6, '2025-12-14 10:30:00'),
                                                                     (18, 1, '2025-12-14 11:00:00'),
                                                                     (18, 7, '2025-12-14 11:30:00'),
                                                                     (19, 5, '2025-12-15 08:00:00'),
                                                                     (19, 9, '2025-12-15 08:30:00'),
                                                                     (20, 2, '2025-12-15 09:00:00'),
                                                                     (20, 10, '2025-12-15 09:30:00');

INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (21, 1, '2025-12-16 08:00:00'),
                                                                     (21, 2, '2025-12-16 08:30:00'),
                                                                     (22, 5, '2025-12-16 09:00:00'),
                                                                     (22, 6, '2025-12-16 09:30:00'),
                                                                     (23, 9, '2025-12-16 10:00:00'),
                                                                     (23, 1, '2025-12-16 10:30:00'),
                                                                     (24, 2, '2025-12-16 11:00:00'),
                                                                     (24, 3, '2025-12-16 11:30:00'),
                                                                     (25, 5, '2025-12-17 08:00:00'),
                                                                     (25, 7, '2025-12-17 08:30:00'),
                                                                     (26, 6, '2025-12-17 09:00:00'),
                                                                     (26, 9, '2025-12-17 09:30:00'),
                                                                     (27, 1, '2025-12-17 10:00:00'),
                                                                     (27, 10, '2025-12-17 10:30:00'),
                                                                     (28, 2, '2025-12-17 11:00:00'),
                                                                     (28, 5, '2025-12-17 11:30:00'),
                                                                     (29, 3, '2025-12-18 08:00:00'),
                                                                     (29, 9, '2025-12-18 08:30:00'),
                                                                     (30, 1, '2025-12-18 09:00:00'),
                                                                     (30, 6, '2025-12-18 09:30:00');

INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (31, 5, '2025-12-19 08:00:00'),
                                                                     (31, 9, '2025-12-19 08:30:00'),
                                                                     (32, 1, '2025-12-19 09:00:00'),
                                                                     (32, 2, '2025-12-19 09:30:00'),
                                                                     (33, 3, '2025-12-19 10:00:00'),
                                                                     (33, 7, '2025-12-19 10:30:00'),
                                                                     (34, 6, '2025-12-19 11:00:00'),
                                                                     (34, 10, '2025-12-19 11:30:00'),
                                                                     (35, 1, '2025-12-20 08:00:00'),
                                                                     (35, 9, '2025-12-20 08:30:00'),
                                                                     (36, 2, '2025-12-20 09:00:00'),
                                                                     (36, 5, '2025-12-20 09:30:00'),
                                                                     (37, 3, '2025-12-20 10:00:00'),
                                                                     (37, 6, '2025-12-20 10:30:00'),
                                                                     (38, 7, '2025-12-20 11:00:00'),
                                                                     (38, 9, '2025-12-20 11:30:00'),
                                                                     (39, 10, '2025-12-21 08:00:00'),
                                                                     (39, 5, '2025-12-21 08:30:00'),
                                                                     (40, 1, '2025-12-21 09:00:00'),
                                                                     (40, 2, '2025-12-21 09:30:00');

INSERT INTO user_follows (follower_id, followed_id, followed_at) VALUES
                                                                     (41, 5, '2025-12-22 08:00:00'),
                                                                     (41, 6, '2025-12-22 08:30:00'),
                                                                     (42, 9, '2025-12-22 09:00:00'),
                                                                     (42, 1, '2025-12-22 09:30:00'),
                                                                     (43, 2, '2025-12-22 10:00:00'),
                                                                     (43, 7, '2025-12-22 10:30:00'),
                                                                     (44, 3, '2025-12-22 11:00:00'),
                                                                     (44, 10, '2025-12-22 11:30:00'),
                                                                     (45, 1, '2025-12-23 08:00:00'),
                                                                     (45, 5, '2025-12-23 08:30:00'),
                                                                     (46, 2, '2025-12-23 09:00:00'),
                                                                     (46, 9, '2025-12-23 09:30:00'),
                                                                     (47, 6, '2025-12-23 10:00:00'),
                                                                     (47, 7, '2025-12-23 10:30:00'),
                                                                     (48, 3, '2025-12-23 11:00:00'),
                                                                     (48, 5, '2025-12-23 11:30:00'),
                                                                     (49, 10, '2025-12-24 08:00:00'),
                                                                     (49, 1, '2025-12-24 08:30:00'),
                                                                     (50, 2, '2025-12-24 09:00:00'),
                                                                     (50, 9, '2025-12-24 09:30:00');

-- Recalcular followers_count baseado nas relações de follow
UPDATE users u
SET u.followers_count = COALESCE((
    SELECT COUNT(DISTINCT uf.follower_id)
    FROM user_follows uf
    WHERE uf.followed_id = u.user_id
), 0);

-- Inserir produtos
INSERT INTO products (product_id, product_name, type, brand, color, notes) VALUES
                                                                                 (101, 'Mouse Gamer RGB', 'Periférico', 'Logitech', 'Preto', 'Alta precisão'),
                                                                                 (102, 'Teclado Mecânico', 'Periférico', 'Razer', 'Preto', 'Switch Blue'),
                                                                                 (201, 'Cadeira Gamer', 'Móvel', 'DXRacer', 'Vermelho', 'Ergonomica'),
                                                                                 (202, 'Mesa para Computador', 'Móvel', 'Madesa', 'Branco', 'Suporte monitor'),
                                                                                 (301, 'Notebook Dell', 'Eletrônico', 'Dell', 'Prata', 'i7 16GB 512SSD'),
                                                                                 (302, 'Monitor 27 polegadas', 'Eletrônico', 'LG', 'Preto', 'Full HD IPS'),
                                                                                 (501, 'Fone Bluetooth', 'Áudio', 'Sony', 'Preto', 'Cancelamento'),
                                                                                 (502, 'Caixa de Som Portátil', 'Áudio', 'JBL', 'Azul', 'À prova d água'),
                                                                                 (503, 'Microfone USB', 'Áudio', 'Blue Yeti', 'Preto', 'Para streaming'),
                                                                                 (601, 'Webcam Full HD', 'Eletrônico', 'Logitech', 'Preto', 'Videochamadas'),
                                                                                 (901, 'SSD 1TB', 'Hardware', 'Kingston', 'Preto', 'NVMe Gen4'),
                                                                                 (902, 'Placa de Vídeo RTX', 'Hardware', 'NVIDIA', 'Preto', '8GB GDDR6');

-- Inserir mais produtos
INSERT INTO products (product_id, product_name, type, brand, color, notes) VALUES
                                                                                 (903, 'Memória RAM 32GB', 'Hardware', 'Corsair', 'Preto', 'DDR4 3200MHz'),
                                                                                 (904, 'Fonte 750W', 'Hardware', 'EVGA', 'Preto', '80 Plus Gold'),
                                                                                 (905, 'Gabinete Mid Tower', 'Hardware', 'NZXT', 'Branco', 'Vidro temperado'),
                                                                                 (906, 'Roteador Wi-Fi 6', 'Eletrônico', 'TP-Link', 'Preto', 'Dual band'),
                                                                                 (907, 'Headset Gamer', 'Áudio', 'HyperX', 'Preto', 'Surround 7.1'),
                                                                                 (908, 'Mousepad XL', 'Periférico', 'SteelSeries', 'Preto', 'Antiderrapante'),
                                                                                 (909, 'Controle Bluetooth', 'Periférico', '8BitDo', 'Preto', 'Compatível PC'),
                                                                                 (910, 'Hub USB-C', 'Eletrônico', 'Anker', 'Cinza', 'HDMI e PD'),
                                                                                 (911, 'Smartwatch', 'Eletrônico', 'Samsung', 'Preto', 'Monitoramento saúde'),
                                                                                 (912, 'Carregador GaN 65W', 'Eletrônico', 'Ugreen', 'Branco', 'USB-C PD'),
                                                                                 (1001, 'Câmera de Segurança', 'Eletrônico', 'Intelbras', 'Branco', 'Wi-Fi Full HD'),
                                                                                 (1002, 'Estabilizador', 'Eletrônico', 'APC', 'Preto', 'Proteção surtos'),
                                                                                 (1003, 'Impressora Multifuncional', 'Eletrônico', 'HP', 'Branco', 'Wi-Fi'),
                                                                                 (1004, 'Cadeira Escritório', 'Móvel', 'Flexform', 'Preto', 'Ajustes completos'),
                                                                                 (1005, 'Luminária LED', 'Móvel', 'Philips', 'Branco', 'Dimerizável'),
                                                                                 (1101, 'Teclado Slim', 'Periférico', 'Microsoft', 'Preto', 'Sem fio'),
                                                                                 (1102, 'Monitor 24 polegadas', 'Eletrônico', 'AOC', 'Preto', '75Hz'),
                                                                                 (1103, 'HD Externo 2TB', 'Hardware', 'Seagate', 'Preto', 'USB 3.0'),
                                                                                 (1104, 'Micro SD 256GB', 'Hardware', 'SanDisk', 'Vermelho', 'UHS-I'),
                                                                                 (1105, 'Adaptador Bluetooth', 'Eletrônico', 'Baseus', 'Preto', '5.0');

-- Inserir posts
-- Posts de joaosilva (1)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (1, 1, '2026-01-07', 101, 58, 299.90, true, 50.00, 0),
                                                                                                               (2, 1, '2026-01-06', 102, 58, 599.90, false, 0, 0);

-- Posts de mariasantos (2)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (3, 2, '2026-01-05', 201, 100, 1299.00, true, 200.00, 0),
                                                                                                               (4, 2, '2026-01-04', 202, 100, 450.00, false, 0, 0);

-- Posts de pedrocoста (3)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (5, 3, '2026-01-03', 301, 25, 4500.00, true, 500.00, 0),
                                                                                                               (6, 3, '2026-01-02', 302, 25, 899.00, false, 0, 0);

-- Posts de carlosmendes (5)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (7, 5, '2026-01-01', 501, 45, 699.00, true, 100.00, 0),
                                                                                                               (8, 5, '2026-01-07', 502, 45, 349.00, true, 50.00, 0),
                                                                                                               (9, 5, '2025-12-16', 503, 45, 899.00, false, 0, 0);

-- Posts de juliaferreira (6)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
    (10, 6, '2025-12-07', 601, 25, 299.00, false, 0, 0);

-- Posts de fernando (9)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (11, 9, '2025-12-09', 901, 30, 599.00, true, 100.00, 0),
                                                                                                               (12, 9, '2025-12-13', 902, 30, 3500.00, false, 0, 0);

-- Inserir mais posts
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                                (13, 11, '2026-01-08', 903, 30, 799.00, true, 120.00, 0),
                                                                                                                (14, 11, '2026-01-09', 908, 58, 129.90, false, 0, 0),
                                                                                                                (15, 12, '2026-01-08', 904, 30, 649.90, true, 50.00, 0),
                                                                                                                (16, 12, '2026-01-10', 910, 25, 199.00, false, 0, 0),
                                                                                                                (17, 13, '2026-01-09', 905, 30, 899.90, true, 100.00, 0),
                                                                                                                (18, 13, '2026-01-11', 909, 58, 249.90, false, 0, 0),
                                                                                                                (19, 14, '2026-01-10', 907, 45, 399.00, true, 80.00, 0),
                                                                                                                (20, 14, '2026-01-11', 906, 25, 499.00, false, 0, 0),
                                                                                                                (21, 15, '2026-01-12', 911, 25, 1499.00, true, 150.00, 0),
                                                                                                                (22, 15, '2026-01-12', 912, 25, 219.90, false, 0, 0);

INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                                (23, 16, '2026-01-13', 1001, 25, 299.00, true, 30.00, 0),
                                                                                                                (24, 16, '2026-01-13', 1105, 25, 59.90, false, 0, 0),
                                                                                                                (25, 17, '2026-01-14', 1102, 25, 699.00, true, 100.00, 0),
                                                                                                                (26, 17, '2026-01-14', 1103, 30, 499.00, false, 0, 0),
                                                                                                                (27, 18, '2026-01-15', 1004, 100, 999.00, true, 120.00, 0),
                                                                                                                (28, 18, '2026-01-15', 1005, 100, 129.00, false, 0, 0),
                                                                                                                (29, 19, '2026-01-16', 1003, 25, 1099.00, true, 200.00, 0),
                                                                                                                (30, 19, '2026-01-16', 1101, 58, 169.90, false, 0, 0),
                                                                                                                (31, 20, '2026-01-17', 1104, 30, 149.90, true, 20.00, 0),
                                                                                                                (32, 20, '2026-01-17', 1002, 25, 229.00, false, 0, 0);

INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                                (33, 21, '2026-01-18', 101, 58, 279.90, true, 40.00, 0),
                                                                                                                (34, 22, '2026-01-18', 201, 100, 1199.00, true, 150.00, 0),
                                                                                                                (35, 23, '2026-01-18', 302, 25, 849.00, false, 0, 0),
                                                                                                                (36, 24, '2026-01-19', 901, 30, 549.00, true, 80.00, 0),
                                                                                                                (37, 25, '2026-01-19', 502, 45, 329.00, false, 0, 0),
                                                                                                                (38, 26, '2026-01-19', 503, 45, 849.00, true, 90.00, 0),
                                                                                                                (39, 27, '2026-01-20', 601, 25, 279.00, false, 0, 0),
                                                                                                                (40, 28, '2026-01-20', 902, 30, 3399.00, true, 300.00, 0),
                                                                                                                (41, 29, '2026-01-20', 301, 25, 4399.00, false, 0, 0),
                                                                                                                (42, 30, '2026-01-21', 102, 58, 579.90, true, 70.00, 0);

INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                                (43, 31, '2026-01-21', 501, 45, 649.00, true, 80.00, 0),
                                                                                                                (44, 32, '2026-01-21', 202, 100, 429.00, false, 0, 0),
                                                                                                                (45, 33, '2026-01-22', 906, 25, 479.00, true, 40.00, 0),
                                                                                                                (46, 34, '2026-01-22', 907, 45, 369.00, false, 0, 0),
                                                                                                                (47, 35, '2026-01-22', 910, 25, 179.00, true, 20.00, 0),
                                                                                                                (48, 36, '2026-01-23', 909, 58, 239.90, false, 0, 0),
                                                                                                                (49, 37, '2026-01-23', 905, 30, 879.90, true, 90.00, 0),
                                                                                                                (50, 38, '2026-01-23', 904, 30, 629.90, false, 0, 0),
                                                                                                                (51, 39, '2026-01-24', 911, 25, 1399.00, true, 100.00, 0),
                                                                                                                (52, 40, '2026-01-24', 912, 25, 209.90, false, 0, 0);

INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                                (53, 41, '2026-01-24', 1005, 100, 119.00, false, 0, 0),
                                                                                                                (54, 42, '2026-01-25', 1004, 100, 949.00, true, 100.00, 0),
                                                                                                                (55, 43, '2026-01-25', 1102, 25, 689.00, true, 90.00, 0),
                                                                                                                (56, 44, '2026-01-25', 1001, 25, 289.00, false, 0, 0),
                                                                                                                (57, 45, '2026-01-26', 1103, 30, 479.00, true, 50.00, 0),
                                                                                                                (58, 46, '2026-01-26', 1104, 30, 139.90, false, 0, 0),
                                                                                                                (59, 47, '2026-01-26', 1003, 25, 1049.00, true, 150.00, 0),
                                                                                                                (60, 50, '2026-01-27', 903, 30, 769.00, false, 0, 0);

-- Ajustar datas dos posts para ficarem dentro das últimas duas semanas
UPDATE posts
SET date = DATE_SUB(CURDATE(), INTERVAL (post_id % 14) DAY);

-- Inserir likes nos posts
-- Post 1 (Mouse Gamer) tem 5 likes
INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (2, 1, '2025-12-10 14:00:00'),
                                                        (3, 1, '2025-12-10 15:00:00'),
                                                        (5, 1, '2025-12-10 16:00:00'),
                                                        (6, 1, '2025-12-11 10:00:00'),
                                                        (9, 1, '2025-12-11 11:00:00');

-- Post 3 (Cadeira Gamer) tem 3 likes
INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (1, 3, '2025-12-05 20:00:00'),
                                                        (4, 3, '2025-12-06 09:00:00'),
                                                        (5, 3, '2025-12-06 10:00:00');

-- Post 5 (Notebook) tem 4 likes
INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (1, 5, '2025-12-08 18:00:00'),
                                                        (2, 5, '2025-12-08 19:00:00'),
                                                        (6, 5, '2025-12-09 08:00:00'),
                                                        (9, 5, '2025-12-09 09:00:00');

-- Post 7 (Fone Bluetooth) tem 6 likes
INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (1, 7, '2025-12-03 22:00:00'),
                                                        (2, 7, '2025-12-04 08:00:00'),
                                                        (3, 7, '2025-12-04 09:00:00'),
                                                        (4, 7, '2025-12-04 10:00:00'),
                                                        (6, 7, '2025-12-04 11:00:00'),
                                                        (10, 7, '2025-12-04 12:00:00');

-- Post 8 (Caixa de Som) tem 2 likes
INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (1, 8, '2025-12-11 16:00:00'),
                                                        (2, 8, '2025-12-11 17:00:00');

-- Post 11 (SSD) tem 3 likes
INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (3, 11, '2025-12-09 20:00:00'),
                                                        (6, 11, '2025-12-09 21:00:00'),
                                                        (7, 11, '2025-12-09 22:00:00');

-- Post 13 (Memória RAM) tem 4 likes
INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (1, 13, '2026-01-10 10:00:00'),
                                                        (2, 13, '2026-01-10 10:05:00'),
                                                        (3, 13, '2026-01-10 10:10:00'),
                                                        (5, 13, '2026-01-10 10:15:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (6, 15, '2026-01-10 11:00:00'),
                                                        (9, 15, '2026-01-10 11:05:00'),
                                                        (10, 15, '2026-01-10 11:10:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (11, 19, '2026-01-12 09:00:00'),
                                                        (12, 19, '2026-01-12 09:05:00'),
                                                        (13, 19, '2026-01-12 09:10:00'),
                                                        (14, 19, '2026-01-12 09:15:00'),
                                                        (15, 19, '2026-01-12 09:20:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (16, 21, '2026-01-13 08:00:00'),
                                                        (17, 21, '2026-01-13 08:05:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (18, 29, '2026-01-17 14:00:00'),
                                                        (19, 29, '2026-01-17 14:05:00'),
                                                        (20, 29, '2026-01-17 14:10:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (21, 33, '2026-01-18 12:00:00'),
                                                        (22, 33, '2026-01-18 12:05:00'),
                                                        (23, 33, '2026-01-18 12:10:00'),
                                                        (24, 33, '2026-01-18 12:15:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (25, 40, '2026-01-21 09:00:00'),
                                                        (26, 40, '2026-01-21 09:05:00'),
                                                        (27, 40, '2026-01-21 09:10:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (28, 49, '2026-01-24 10:00:00'),
                                                        (29, 49, '2026-01-24 10:05:00');

INSERT INTO post_likes (user_id, post_id, liked_at) VALUES
                                                        (30, 59, '2026-01-27 18:00:00'),
                                                        (31, 59, '2026-01-27 18:05:00'),
                                                        (32, 59, '2026-01-27 18:10:00'),
                                                        (33, 59, '2026-01-27 18:15:00'),
                                                        (34, 59, '2026-01-27 18:20:00');

-- Atualizar contador de likes nos posts
UPDATE posts SET likes_count = 5 WHERE post_id = 1;
UPDATE posts SET likes_count = 3 WHERE post_id = 3;
UPDATE posts SET likes_count = 4 WHERE post_id = 5;
UPDATE posts SET likes_count = 6 WHERE post_id = 7;
UPDATE posts SET likes_count = 2 WHERE post_id = 8;
UPDATE posts SET likes_count = 3 WHERE post_id = 11;

UPDATE posts SET likes_count = 4 WHERE post_id = 13;
UPDATE posts SET likes_count = 3 WHERE post_id = 15;
UPDATE posts SET likes_count = 5 WHERE post_id = 19;
UPDATE posts SET likes_count = 2 WHERE post_id = 21;
UPDATE posts SET likes_count = 3 WHERE post_id = 29;
UPDATE posts SET likes_count = 4 WHERE post_id = 33;
UPDATE posts SET likes_count = 3 WHERE post_id = 40;
UPDATE posts SET likes_count = 2 WHERE post_id = 49;
UPDATE posts SET likes_count = 5 WHERE post_id = 59;