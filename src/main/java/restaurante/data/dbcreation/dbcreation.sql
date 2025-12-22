CREATE DATABASE IF NOT EXISTS restaurante_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'rest_user'@'localhost' IDENTIFIED BY 'RestUser123!';
GRANT ALL PRIVILEGES ON restaurante_db.* TO 'rest_user'@'localhost';
FLUSH PRIVILEGES;

USE restaurante_db;

