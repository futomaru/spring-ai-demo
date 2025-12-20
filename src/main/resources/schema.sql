CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    description VARCHAR(512)
);

INSERT INTO items (name, price, description) VALUES
    ('リンゴ', 150, '新鮮な青森産リンゴ'),
    ('バナナ', 120, '熟した黄色いバナナ'),
    ('オレンジ', 200, 'ビタミンC豊富なオレンジ');
