-- ======================================================================================
-- Restaurante Management System Schema (MySQL Compatible)
-- DSS 2025/2026 - Grupo TP-28
-- ======================================================================================

CREATE DATABASE IF NOT EXISTS restaurante;
USE restaurante;

-- Desativar verificações de chaves estrangeiras para limpeza
SET FOREIGN_KEY_CHECKS = 0;

-- Limpeza de tabelas existentes
DROP TABLE IF EXISTS Tarefa;
DROP TABLE IF EXISTS LinhaStock;
DROP TABLE IF EXISTS LinhaProduto;
DROP TABLE IF EXISTS LinhaMenu;
DROP TABLE IF EXISTS LinhaPedido;
DROP TABLE IF EXISTS Produto_Passo;
DROP TABLE IF EXISTS Passo_Ingrediente;
DROP TABLE IF EXISTS Catalogo_Produto;
DROP TABLE IF EXISTS Catalogo_Menu;
DROP TABLE IF EXISTS Estacao_Trabalho;
DROP TABLE IF EXISTS Passo;
DROP TABLE IF EXISTS Mensagem;
DROP TABLE IF EXISTS Pagamento;
DROP TABLE IF EXISTS Pedido;
DROP TABLE IF EXISTS Menu;
DROP TABLE IF EXISTS Produto;
DROP TABLE IF EXISTS Ingrediente;
DROP TABLE IF EXISTS Estacao;
DROP TABLE IF EXISTS Funcionario;
DROP TABLE IF EXISTS Catalogo;
DROP TABLE IF EXISTS Restaurante;

-- Reativar verificações de chaves estrangeiras
SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================================================
-- TABELAS PRINCIPAIS
-- ======================================================================================

-- 1. Catalogo
CREATE TABLE Catalogo (
                          id INTEGER PRIMARY KEY AUTO_INCREMENT,
                          nome VARCHAR(255) NOT NULL
);

-- 2. Restaurante
CREATE TABLE Restaurante (
                             id INTEGER PRIMARY KEY AUTO_INCREMENT,
                             nome VARCHAR(255) NOT NULL,
                             localizacao VARCHAR(255),
                             catalogo_id INTEGER,
                             FOREIGN KEY (catalogo_id) REFERENCES Catalogo(id) ON DELETE SET NULL
);

-- 3. Funcionario
CREATE TABLE Funcionario (
                             id INTEGER PRIMARY KEY AUTO_INCREMENT,
                             restaurante_id INTEGER,
                             utilizador VARCHAR(255) NOT NULL UNIQUE,
                             password VARCHAR(255) NOT NULL,
                             funcao VARCHAR(50) NOT NULL, -- Enum: FUNCIONARIO, GERENTE, COO, SYSADMIN
                             FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE SET NULL
);

-- 4. Estacao (Polimórfica: COZINHA ou CAIXA)
CREATE TABLE Estacao (
                         id INTEGER PRIMARY KEY AUTO_INCREMENT,
                         restaurante_id INTEGER NOT NULL,
                         nome VARCHAR(255) NOT NULL,
                         tipo VARCHAR(50) NOT NULL, -- Discriminador: 'COZINHA' ou 'CAIXA'
                         FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
);

-- 5. Estacao_Trabalho (Especialidades para estações de Cozinha)
CREATE TABLE Estacao_Trabalho (
                                  estacao_id INTEGER NOT NULL,
                                  trabalho VARCHAR(50) NOT NULL, -- Enum: GRELHA, FRITURA, BEBIDAS, etc.
                                  PRIMARY KEY (estacao_id, trabalho),
                                  FOREIGN KEY (estacao_id) REFERENCES Estacao(id) ON DELETE CASCADE
);

-- 6. Ingrediente
CREATE TABLE Ingrediente (
                             id INTEGER PRIMARY KEY AUTO_INCREMENT,
                             nome VARCHAR(255) NOT NULL UNIQUE,
                             unidade VARCHAR(50),
                             alergenico VARCHAR(255)
);

-- 7. Produto
CREATE TABLE Produto (
                         id INTEGER PRIMARY KEY AUTO_INCREMENT,
                         nome VARCHAR(255) NOT NULL,
                         preco DECIMAL(10, 2) DEFAULT 0.00
);

-- 8. Menu
CREATE TABLE Menu (
                      id INTEGER PRIMARY KEY AUTO_INCREMENT,
                      nome VARCHAR(255) NOT NULL,
                      preco DECIMAL(10, 2) DEFAULT 0.00
);

-- 9. Pedido
CREATE TABLE Pedido (
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        restaurante_id INTEGER NOT NULL,
                        para_levar BOOLEAN DEFAULT FALSE,
                        estado VARCHAR(50) NOT NULL, -- Enum: INICIADO, AGUARDA_PAGAMENTO, CONFIRMADO, EM_PREPARACAO, PRONTO, ENTREGUE, CANCELADO
                        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        data_conclusao TIMESTAMP NULL,
                        FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
);

-- 10. Mensagem
CREATE TABLE Mensagem (
                          id INTEGER PRIMARY KEY AUTO_INCREMENT,
                          restaurante_id INTEGER NOT NULL,
                          texto TEXT NOT NULL,
                          data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
);

-- 11. Passo (Etapa de confeção)
CREATE TABLE Passo (
                       id INTEGER PRIMARY KEY AUTO_INCREMENT,
                       nome VARCHAR(255) NOT NULL,
                       duracao_minutos BIGINT NOT NULL, -- Mapeia java.time.Duration
                       trabalho VARCHAR(50) NOT NULL    -- Enum: GRELHA, FRITURA, BEBIDAS, etc.
);

-- 12. Pagamento
CREATE TABLE Pagamento (
                           id INTEGER PRIMARY KEY AUTO_INCREMENT,
                           pedido_id INTEGER NOT NULL UNIQUE,
                           tipo VARCHAR(50) NOT NULL,       -- Enum: CAIXA, TERMINAL
                           valor DECIMAL(10, 2) NOT NULL,
                           data_pagamento TIMESTAMP NULL,
                           confirmado BOOLEAN DEFAULT FALSE,
                           FOREIGN KEY (pedido_id) REFERENCES Pedido(id) ON DELETE CASCADE
);

-- ======================================================================================
-- TABELAS DE ASSOCIAÇÃO (MANY-TO-MANY)
-- ======================================================================================

-- Catalogo -> Menu
CREATE TABLE Catalogo_Menu (
                               catalogo_id INTEGER NOT NULL,
                               menu_id INTEGER NOT NULL,
                               PRIMARY KEY (catalogo_id, menu_id),
                               FOREIGN KEY (catalogo_id) REFERENCES Catalogo(id) ON DELETE CASCADE,
                               FOREIGN KEY (menu_id) REFERENCES Menu(id) ON DELETE CASCADE
);

-- Catalogo -> Produto
CREATE TABLE Catalogo_Produto (
                                  catalogo_id INTEGER NOT NULL,
                                  produto_id INTEGER NOT NULL,
                                  PRIMARY KEY (catalogo_id, produto_id),
                                  FOREIGN KEY (catalogo_id) REFERENCES Catalogo(id) ON DELETE CASCADE,
                                  FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE CASCADE
);

-- Produto -> Passo
CREATE TABLE Produto_Passo (
                               produto_id INTEGER NOT NULL,
                               passo_id INTEGER NOT NULL,
                               PRIMARY KEY (produto_id, passo_id),
                               FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE CASCADE,
                               FOREIGN KEY (passo_id) REFERENCES Passo(id) ON DELETE CASCADE
);

-- Passo -> Ingrediente
CREATE TABLE Passo_Ingrediente (
                                   passo_id INTEGER NOT NULL,
                                   ingrediente_id INTEGER NOT NULL,
                                   PRIMARY KEY (passo_id, ingrediente_id),
                                   FOREIGN KEY (passo_id) REFERENCES Passo(id) ON DELETE CASCADE,
                                   FOREIGN KEY (ingrediente_id) REFERENCES Ingrediente(id) ON DELETE CASCADE
);

-- ======================================================================================
-- TABELAS DE COMPOSIÇÃO (LINHAS)
-- ======================================================================================

-- LinhaPedido (Itens de um pedido)
CREATE TABLE LinhaPedido (
                             id INTEGER PRIMARY KEY AUTO_INCREMENT,
                             pedido_id INTEGER NOT NULL,
                             item_id INTEGER NOT NULL,
                             tipo VARCHAR(50) NOT NULL,       -- 'PRODUTO' ou 'MENU'
                             quantidade INTEGER DEFAULT 1,
                             preco_unitario DECIMAL(10, 2),
                             observacao TEXT,
                             FOREIGN KEY (pedido_id) REFERENCES Pedido(id) ON DELETE CASCADE
);

-- LinhaMenu (Produtos de um menu)
CREATE TABLE LinhaMenu (
                           id INTEGER PRIMARY KEY AUTO_INCREMENT,
                           menu_id INTEGER NOT NULL,
                           produto_id INTEGER NOT NULL,
                           quantidade INTEGER DEFAULT 1,
                           FOREIGN KEY (menu_id) REFERENCES Menu(id) ON DELETE CASCADE,
                           FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE CASCADE
);

-- LinhaProduto (Receita: ingredientes de um produto)
CREATE TABLE LinhaProduto (
                              id INTEGER PRIMARY KEY AUTO_INCREMENT,
                              produto_id INTEGER NOT NULL,
                              ingrediente_id INTEGER NOT NULL,
                              quantidade INTEGER DEFAULT 1,
                              FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE CASCADE,
                              FOREIGN KEY (ingrediente_id) REFERENCES Ingrediente(id) ON DELETE CASCADE
);

-- LinhaStock (Inventário por restaurante)
CREATE TABLE LinhaStock (
                            id INTEGER PRIMARY KEY AUTO_INCREMENT,
                            restaurante_id INTEGER NOT NULL,
                            ingrediente_id INTEGER NOT NULL,
                            quantidade INTEGER DEFAULT 0,
                            FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE,
                            FOREIGN KEY (ingrediente_id) REFERENCES Ingrediente(id) ON DELETE CASCADE,
                            UNIQUE KEY uk_stock (restaurante_id, ingrediente_id)
);

-- ======================================================================================
-- TABELA DE TAREFAS (Produção)
-- ======================================================================================

-- Tarefa (Unidade de trabalho na cozinha)
CREATE TABLE Tarefa (
                        id INTEGER PRIMARY KEY AUTO_INCREMENT,
                        passo_id INTEGER NOT NULL,
                        produto_id INTEGER NOT NULL,
                        pedido_id INTEGER NOT NULL,
                        estacao_id INTEGER NULL,         -- CORRIGIDO: Coluna adicionada para persistir a estação
                        estado VARCHAR(50) NOT NULL DEFAULT 'PENDENTE', -- Enum: PENDENTE, EM_EXECUCAO, ATRASADA, CONCLUIDA
                        data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        data_inicio TIMESTAMP NULL,
                        data_conclusao TIMESTAMP NULL,
                        FOREIGN KEY (passo_id) REFERENCES Passo(id) ON DELETE CASCADE,
                        FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE CASCADE,
                        FOREIGN KEY (pedido_id) REFERENCES Pedido(id) ON DELETE CASCADE,
                        FOREIGN KEY (estacao_id) REFERENCES Estacao(id) ON DELETE SET NULL
);

-- ======================================================================================
-- ÍNDICES PARA OTIMIZAÇÃO
-- ======================================================================================

CREATE INDEX idx_pedido_restaurante ON Pedido(restaurante_id);
CREATE INDEX idx_pedido_estado ON Pedido(estado);
CREATE INDEX idx_tarefa_pedido ON Tarefa(pedido_id);
CREATE INDEX idx_tarefa_estado ON Tarefa(estado);
CREATE INDEX idx_tarefa_estacao ON Tarefa(estacao_id);
CREATE INDEX idx_funcionario_restaurante ON Funcionario(restaurante_id);