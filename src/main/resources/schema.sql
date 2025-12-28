-- Restaurante Management System Schema (MySQL Compatible)
CREATE DATABASE IF NOT EXISTS restaurante;
USE restaurante;

-- Disable foreign key checks to allow dropping tables in any order
SET FOREIGN_KEY_CHECKS = 0;

-- Clean up existing tables
DROP TABLE IF EXISTS Tarefa;
DROP TABLE IF EXISTS LinhaStock;
DROP TABLE IF EXISTS LinhaProduto;
DROP TABLE IF EXISTS LinhaMenu;
DROP TABLE IF EXISTS LinhaPedido;
DROP TABLE IF EXISTS Produto_Passo;
DROP TABLE IF EXISTS Passo_Ingrediente;
DROP TABLE IF EXISTS Catalogo_Produto;
DROP TABLE IF EXISTS Catalogo_Menu;
DROP TABLE IF EXISTS Passo;
DROP TABLE IF EXISTS Mensagem;
DROP TABLE IF EXISTS Pedido;
DROP TABLE IF EXISTS Menu;
DROP TABLE IF EXISTS Produto;
DROP TABLE IF EXISTS Ingrediente;
DROP TABLE IF EXISTS Estacao;
DROP TABLE IF EXISTS Funcionario;
DROP TABLE IF EXISTS Catalogo;
DROP TABLE IF EXISTS Restaurante;

-- Re-enable foreign key checks for table creation
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Catalogo
CREATE TABLE Catalogo (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome TEXT
);

-- 2. Restaurante
CREATE TABLE Restaurante (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome TEXT NOT NULL,
    localizacao TEXT,
    catalogo_id INTEGER,
    FOREIGN KEY (catalogo_id) REFERENCES Catalogo(id)
);

-- 3. Funcionario
CREATE TABLE Funcionario (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    restaurante_id INTEGER,
    utilizador VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    funcao VARCHAR(50) NOT NULL, -- Enum name
    FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id)
);

-- 4. Estacao
CREATE TABLE Estacao (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    restaurante_id INTEGER,
    trabalho VARCHAR(50), -- Enum name
    FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id)
);

-- 5. Ingrediente
CREATE TABLE Ingrediente (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL UNIQUE,
    unidade VARCHAR(50),
    alergenico VARCHAR(255)
);

-- 6. Produto
CREATE TABLE Produto (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(10, 2) DEFAULT 0.0
);

-- 7. Menu
CREATE TABLE Menu (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(10, 2) DEFAULT 0.0
);

-- 8. Pedido
CREATE TABLE Pedido (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    restaurante_id INTEGER NOT NULL,
    para_levar BOOLEAN DEFAULT FALSE,
    estado VARCHAR(50) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_conclusao TIMESTAMP NULL,
    FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id)
);


-- 9. Mensagem
CREATE TABLE Mensagem (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    restaurante_id INTEGER,
    texto TEXT NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id)
);

-- 10. Passo
CREATE TABLE Passo (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    duracao_minutos BIGINT,
    trabalho VARCHAR(50)
);

-- --- Association Tables ---

-- Catalogo -> Menu
CREATE TABLE Catalogo_Menu (
    catalogo_id INTEGER,
    menu_id INTEGER,
    PRIMARY KEY (catalogo_id, menu_id),
    FOREIGN KEY (catalogo_id) REFERENCES Catalogo(id),
    FOREIGN KEY (menu_id) REFERENCES Menu(id)
);

-- Catalogo -> Produto
CREATE TABLE Catalogo_Produto (
    catalogo_id INTEGER,
    produto_id INTEGER,
    PRIMARY KEY (catalogo_id, produto_id),
    FOREIGN KEY (catalogo_id) REFERENCES Catalogo(id),
    FOREIGN KEY (produto_id) REFERENCES Produto(id)
);

-- Produto -> Passo
CREATE TABLE Produto_Passo (
    produto_id INTEGER,
    passo_id INTEGER,
    PRIMARY KEY (produto_id, passo_id),
    FOREIGN KEY (produto_id) REFERENCES Produto(id),
    FOREIGN KEY (passo_id) REFERENCES Passo(id)
);

-- Passo -> Ingrediente
CREATE TABLE Passo_Ingrediente (
    passo_id INTEGER,
    ingrediente_id INTEGER,
    PRIMARY KEY (passo_id, ingrediente_id),
    FOREIGN KEY (passo_id) REFERENCES Passo(id),
    FOREIGN KEY (ingrediente_id) REFERENCES Ingrediente(id)
);

-- --- Line/Detail Tables ---

-- LinhaPedido
CREATE TABLE LinhaPedido (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    pedido_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- 'PRODUTO' or 'MENU'
    quantidade INTEGER DEFAULT 0,
    preco_unitario DECIMAL(10, 2),
    observacao TEXT,
    FOREIGN KEY (pedido_id) REFERENCES Pedido(id)
);

-- LinhaMenu
CREATE TABLE LinhaMenu (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    menu_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER DEFAULT 0,
    FOREIGN KEY (menu_id) REFERENCES Menu(id),
    FOREIGN KEY (produto_id) REFERENCES Produto(id)
);

-- LinhaProduto (Receita)
CREATE TABLE LinhaProduto (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    produto_id INTEGER NOT NULL,
    ingrediente_id INTEGER NOT NULL,
    quantidade INTEGER DEFAULT 0,
    FOREIGN KEY (produto_id) REFERENCES Produto(id),
    FOREIGN KEY (ingrediente_id) REFERENCES Ingrediente(id)
);

-- LinhaStock
CREATE TABLE LinhaStock (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    restaurante_id INTEGER NOT NULL,
    ingrediente_id INTEGER NOT NULL,
    quantidade INTEGER DEFAULT 0,
    FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id),
    FOREIGN KEY (ingrediente_id) REFERENCES Ingrediente(id)
);

-- Tarefa
CREATE TABLE Tarefa (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    passo_id INTEGER,
    produto_id INTEGER,
    pedido_id INTEGER,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP NULL DEFAULT NULL,
    data_inicio TIMESTAMP NULL DEFAULT NULL,
    data_conclusao TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (passo_id) REFERENCES Passo(id),
    FOREIGN KEY (produto_id) REFERENCES Produto(id),
    FOREIGN KEY (pedido_id) REFERENCES Pedido(id)
);