-- Restaurante Management System Schema

CREATE DATABASE IF NOT EXISTS restaurante;
USE restaurante;

-- Apagar tabelas se existirem para recriar limpo
DROP TABLE IF EXISTS LinhaEstacao;
DROP TABLE IF EXISTS LinhaStock;
DROP TABLE IF EXISTS LinhaProduto;
DROP TABLE IF EXISTS LinhaMenu;
DROP TABLE IF EXISTS LinhaPedido;
DROP TABLE IF EXISTS Produto_Passo; -- Tabela de associação Produto <-> Passo (Tarefas)
DROP TABLE IF EXISTS Passos;
DROP TABLE IF EXISTS Mensagens;
DROP TABLE IF EXISTS Pedidos;
DROP TABLE IF EXISTS Menus;
DROP TABLE IF EXISTS Produtos;
DROP TABLE IF EXISTS Ingredientes;
DROP TABLE IF EXISTS Estacoes;
DROP TABLE IF EXISTS Funcionarios;
DROP TABLE IF EXISTS Catalogos;
DROP TABLE IF EXISTS Restaurante;

-- 1. Restaurante
CREATE TABLE Restaurante (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    Localizacao TEXT
);

-- 2. Catalogos
CREATE TABLE Catalogos (
    ID INTEGER PRIMARY KEY AUTOINCREMENT
    -- Assumindo que o catálogo agrega items via tabelas de associação ou lógica de negócio
);

-- 3. Funcionarios
CREATE TABLE Funcionarios (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Restaurante_ID INTEGER,
    Utilizador TEXT NOT NULL UNIQUE,
    Password TEXT NOT NULL,
    Funcao TEXT NOT NULL, -- Enum armazenado como String
    FOREIGN KEY (Restaurante_ID) REFERENCES Restaurante(ID)
);

-- 4. Estacoes
CREATE TABLE Estacoes (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Restaurante_ID INTEGER,
    Trabalho TEXT, -- Enum armazenado como String
    FOREIGN KEY (Restaurante_ID) REFERENCES Restaurante(ID)
);

-- 5. Ingredientes
CREATE TABLE Ingredientes (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL UNIQUE,
    Unidade TEXT,
    Alergenico TEXT
);

-- 6. Produtos (Item)
CREATE TABLE Produtos (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    Preco REAL DEFAULT 0.0
);

-- 7. Menus (Item)
CREATE TABLE Menus (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    Preco REAL DEFAULT 0.0
);

-- 8. Pedidos
CREATE TABLE Pedidos (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Restaurante_ID INTEGER,
    Estado TEXT NOT NULL, -- Enum: REGISTADO, PREPARACAO, PRONTO, ENTREGUE, PAGO
    DataHora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Para_Levar BOOLEAN DEFAULT 0,
    FOREIGN KEY (Restaurante_ID) REFERENCES Restaurante(ID)
);

-- 9. Mensagens
CREATE TABLE Mensagens (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Texto TEXT NOT NULL,
    DataHora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Urgente BOOLEAN DEFAULT 0
);

-- 10. Passos (Tarefas definidas)
CREATE TABLE Passos (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    Duracao BIGINT, -- Armazenado em segundos (Duration)
    Trabalho TEXT -- Tipo de trabalho necessário
);

-- --- Tabelas de Associação e Linhas ---

-- Associação Produto -> Passos (Lista de tarefas do produto)
CREATE TABLE Produto_Passo (
    Produto_ID INTEGER,
    Passo_ID INTEGER,
    Ordem INTEGER, -- Para manter a sequência
    PRIMARY KEY (Produto_ID, Passo_ID),
    FOREIGN KEY (Produto_ID) REFERENCES Produtos(ID),
    FOREIGN KEY (Passo_ID) REFERENCES Passos(ID)
);

-- LinhaPedido (Item num Pedido)
-- Nota: Item pode ser Produto ou Menu. Uma forma simples é ter FKs opcionais ou tratar ID genericamente se a lógica aplicacional gerir.
-- Aqui uso colunas separadas para clareza, ou apenas Item_ID se houver uma tabela pai "Itens". 
-- Dado o esquema Java, Item é interface. Vamos assumir que guardamos o ID e um "Tipo" ou tabelas separadas. 
-- Simplificação: LinhaPedido aponta para Produto ou Menu.
CREATE TABLE LinhaPedido (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Pedido_ID INTEGER NOT NULL,
    Produto_ID INTEGER, -- Pode ser NULL se for Menu
    Menu_ID INTEGER,    -- Pode ser NULL se for Produto
    Quantidade INTEGER NOT NULL,
    PrecoUnitario REAL,
    Observacao TEXT,
    FOREIGN KEY (Pedido_ID) REFERENCES Pedidos(ID),
    FOREIGN KEY (Produto_ID) REFERENCES Produtos(ID),
    FOREIGN KEY (Menu_ID) REFERENCES Menus(ID)
);

-- LinhaMenu (Produtos dentro de um Menu)
CREATE TABLE LinhaMenu (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Menu_ID INTEGER NOT NULL,
    Produto_ID INTEGER NOT NULL,
    Quantidade INTEGER DEFAULT 1,
    FOREIGN KEY (Menu_ID) REFERENCES Menus(ID),
    FOREIGN KEY (Produto_ID) REFERENCES Produtos(ID)
);

-- LinhaProduto (Ingredientes dentro de um Produto - Receita)
CREATE TABLE LinhaProduto (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Produto_ID INTEGER NOT NULL,
    Ingrediente_ID INTEGER NOT NULL,
    Quantidade REAL NOT NULL,
    FOREIGN KEY (Produto_ID) REFERENCES Produtos(ID),
    FOREIGN KEY (Ingrediente_ID) REFERENCES Ingredientes(ID)
);

-- LinhaStock (Stock de ingredientes num Restaurante)
CREATE TABLE LinhaStock (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Restaurante_ID INTEGER NOT NULL,
    Ingrediente_ID INTEGER NOT NULL,
    Quantidade REAL DEFAULT 0,
    FOREIGN KEY (Restaurante_ID) REFERENCES Restaurante(ID),
    FOREIGN KEY (Ingrediente_ID) REFERENCES Ingredientes(ID)
);

-- LinhaEstacao (Tarefas atribuídas/em execução numa Estação para um Pedido)
CREATE TABLE LinhaEstacao (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Estacao_ID INTEGER,
    Pedido_ID INTEGER,
    Passo_ID INTEGER, -- A "Tarefa"
    Concluido BOOLEAN DEFAULT 0,
    FOREIGN KEY (Estacao_ID) REFERENCES Estacoes(ID),
    FOREIGN KEY (Pedido_ID) REFERENCES Pedidos(ID),
    FOREIGN KEY (Passo_ID) REFERENCES Passos(ID)
);