CREATE DATABASE IF NOT EXISTS judang CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE judang;

CREATE TABLE IF NOT EXISTS type (
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS product (
  name VARCHAR(255) NOT NULL,
  type VARCHAR(255) NOT NULL,
  price INT NOT NULL,
  svolume INT NOT NULL DEFAULT 0,
  PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS logininfo (
  id VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bill (
  number BIGINT NOT NULL,
  date VARCHAR(20) NOT NULL,
  time VARCHAR(20) NOT NULL,
  name VARCHAR(255) NOT NULL,
  volume INT NOT NULL,
  total INT NOT NULL,
  pay VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS bills (
  number BIGINT NOT NULL,
  date VARCHAR(20) NOT NULL,
  time VARCHAR(20) NOT NULL,
  total INT NOT NULL,
  pay VARCHAR(20) NOT NULL
);

CREATE INDEX idx_product_type ON product(type);
CREATE INDEX idx_bills_date ON bills(date);
CREATE INDEX idx_bill_number_date_time ON bill(number, date, time);

INSERT IGNORE INTO type (name) VALUES ('DRINK'), ('FOOD');
INSERT IGNORE INTO product (name, type, price, svolume) VALUES
  ('Americano', 'DRINK', 3000, 0),
  ('Latte', 'DRINK', 3500, 0),
  ('Sandwich', 'FOOD', 5500, 0);

INSERT IGNORE INTO logininfo (id, password) VALUES ('admin', 'admin');

