CREATE DATABASE IF NOT EXISTS butoane_bd;
USE butoane_bd;

CREATE TABLE IF NOT EXISTS nr_apasari_butoane(
   id INT AUTO_INCREMENT PRIMARY KEY,
   nume_buton VARCHAR(25) UNIQUE NOT NULL,
   numar_apasari INT DEFAULT 0
)