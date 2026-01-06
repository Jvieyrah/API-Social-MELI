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
                                                            (1, 'joaosilva', 3),
                                                            (2, 'mariasantos', 2),
                                                            (3, 'pedrocosta', 1),
                                                            (4, 'anapereira', 0),
                                                            (5, 'carlosmendes', 4),
                                                            (6, 'juliaferreira', 2),
                                                            (7, 'lucasoliveira', 1),
                                                            (8, 'beatrizlima', 0),
                                                            (9, 'fernandogomes', 3),
                                                            (10, 'camilarodrigs', 1);

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

-- Inserir produtos
INSERT INTO products (product_id, product_name, type, brand, color, notes) VALUES
                                                                                 (101, 'Mouse Gamer RGB', 'Periférico', 'Logitech', 'Preto', 'Alta precisão'),
                                                                                 (102, 'Teclado Mecânico', 'Periférico', 'Razer', 'Preto', 'Switch Blue'),
                                                                                 (201, 'Cadeira Gamer', 'Móvel', 'DXRacer', 'Vermelho', 'Ergonômica com apoio lombar'),
                                                                                 (202, 'Mesa para Computador', 'Móvel', 'Madesa', 'Branco', 'Com suporte para monitor'),
                                                                                 (301, 'Notebook Dell', 'Eletrônico', 'Dell', 'Prata', 'i7 16GB RAM 512GB SSD'),
                                                                                 (302, 'Monitor 27 polegadas', 'Eletrônico', 'LG', 'Preto', 'Full HD IPS'),
                                                                                 (501, 'Fone Bluetooth', 'Áudio', 'Sony', 'Preto', 'Cancelamento de ruído'),
                                                                                 (502, 'Caixa de Som Portátil', 'Áudio', 'JBL', 'Azul', 'À prova d água'),
                                                                                 (503, 'Microfone USB', 'Áudio', 'Blue Yeti', 'Preto', 'Para streaming'),
                                                                                 (601, 'Webcam Full HD', 'Eletrônico', 'Logitech', 'Preto', 'Para videochamadas'),
                                                                                 (901, 'SSD 1TB', 'Hardware', 'Kingston', 'Preto', 'NVMe Gen4'),
                                                                                 (902, 'Placa de Vídeo RTX', 'Hardware', 'NVIDIA', 'Preto', '8GB GDDR6');

-- Inserir posts
-- Posts de joaosilva (1)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (1, 1, '2025-12-10', 101, 58, 299.90, true, 50.00, 0),
                                                                                                               (2, 1, '2025-12-12', 102, 58, 599.90, false, 0, 0);

-- Posts de mariasantos (2)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (3, 2, '2025-12-05', 201, 100, 1299.00, true, 200.00, 0),
                                                                                                               (4, 2, '2025-12-14', 202, 100, 450.00, false, 0, 0);

-- Posts de pedrocoста (3)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (5, 3, '2025-12-08', 301, 25, 4500.00, true, 500.00, 0),
                                                                                                               (6, 3, '2025-12-15', 302, 25, 899.00, false, 0, 0);

-- Posts de carlosmendes (5)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (7, 5, '2025-12-03', 501, 45, 699.00, true, 100.00, 0),
                                                                                                               (8, 5, '2025-12-11', 502, 45, 349.00, true, 50.00, 0),
                                                                                                               (9, 5, '2025-12-16', 503, 45, 899.00, false, 0, 0);

-- Posts de juliaferreira (6)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
    (10, 6, '2025-12-07', 601, 25, 299.00, false, 0, 0);

-- Posts de fernando (9)
INSERT INTO posts (post_id, user_id, date, product_id, category, price, has_promo, discount, likes_count) VALUES
                                                                                                               (11, 9, '2025-12-09', 901, 30, 599.00, true, 100.00, 0),
                                                                                                               (12, 9, '2025-12-13', 902, 30, 3500.00, false, 0, 0);

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

-- Atualizar contador de likes nos posts
UPDATE posts SET likes_count = 5 WHERE post_id = 1;
UPDATE posts SET likes_count = 3 WHERE post_id = 3;
UPDATE posts SET likes_count = 4 WHERE post_id = 5;
UPDATE posts SET likes_count = 6 WHERE post_id = 7;
UPDATE posts SET likes_count = 2 WHERE post_id = 8;
UPDATE posts SET likes_count = 3 WHERE post_id = 11;