-- Restaurante Management System Schema

CREATE DATABASE IF NOT EXISTS restaurante;
USE restaurante;

DROP TABLE IF EXISTS tarefas;
DROP TABLE IF EXISTS linha_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS catalogo_ingredientes;
DROP TABLE IF EXISTS catalogo_menus;
DROP TABLE IF EXISTS catalogo_produtos;
DROP TABLE IF EXISTS catalogos;
DROP TABLE IF EXISTS linha_menu;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS passo_producao;
DROP TABLE IF EXISTS linha_ingrediente;
DROP TABLE IF EXISTS produtos;
DROP TABLE IF EXISTS ingredientes;

-- Entidades Base

CREATE TABLE ingredientes (
    id INT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    unidade_medida VARCHAR(50),
    alergenico VARCHAR(50)
);

CREATE TABLE produtos (
    id INT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE menus (
    id INT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE catalogos (
    id INT PRIMARY KEY
);

-- Relacionamentos de Produtos e Menus

CREATE TABLE linha_ingrediente (
    produto_id INT,
    ingrediente_id INT,
    quantidade DOUBLE NOT NULL,
    unidade VARCHAR(50),
    PRIMARY KEY (produto_id, ingrediente_id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id),
    FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
);

CREATE TABLE passo_producao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT,
    nome VARCHAR(255) NOT NULL,
    estacao VARCHAR(50),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE linha_menu (
    menu_id INT,
    produto_id INT,
    quantidade INT NOT NULL,
    PRIMARY KEY (menu_id, produto_id),
    FOREIGN KEY (menu_id) REFERENCES menus(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

-- Relacionamentos do Catalogo

CREATE TABLE catalogo_produtos (
    catalogo_id INT,
    produto_id INT,
    PRIMARY KEY (catalogo_id, produto_id),
    FOREIGN KEY (catalogo_id) REFERENCES catalogos(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE catalogo_menus (
    catalogo_id INT,
    menu_id INT,
    PRIMARY KEY (catalogo_id, menu_id),
    FOREIGN KEY (catalogo_id) REFERENCES catalogos(id),
    FOREIGN KEY (menu_id) REFERENCES menus(id)
);

CREATE TABLE catalogo_ingredientes (
    catalogo_id INT,
    ingrediente_id INT,
    PRIMARY KEY (catalogo_id, ingrediente_id),
    FOREIGN KEY (catalogo_id) REFERENCES catalogos(id),
    FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
);

-- Pedidos e Tarefas

CREATE TABLE pedidos (
    id INT PRIMARY KEY,
    para_levar BOOLEAN NOT NULL DEFAULT FALSE,
    estado VARCHAR(50) NOT NULL,
    data_hora DATETIME
);

CREATE TABLE linha_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT,
    item_id INT, -- Pode ser Produto ou Menu id. Não temos FK estrita para suportar ambos facilmente sem heranca na BD
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);

CREATE TABLE tarefas (
    id INT PRIMARY KEY,
    pedido_id INT,
    produto_id INT,
    estacao VARCHAR(50),
    concluida BOOLEAN DEFAULT FALSE,
    data_criacao DATETIME,
    data_conclusao DATETIME,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);
