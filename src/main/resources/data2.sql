-- ======================================================================================
-- SCRIPT DE POVOAMENTO - RESTAURANTE FASTBURGER
-- Cenário: 3 Restaurantes, 2 Catálogos, Staff hierárquico, Histórico e Operação em Tempo Real
-- ======================================================================================

USE restaurante;

-- Desativar verificação de chaves estrangeiras para permitir truncates e inserções por ordem arbitrária se necessário
SET FOREIGN_KEY_CHECKS = 0;

-- Limpeza inicial (opcional, para garantir um estado limpo)
TRUNCATE TABLE Tarefa;
TRUNCATE TABLE LinhaPedido;
TRUNCATE TABLE Pedido;
TRUNCATE TABLE Mensagem;
TRUNCATE TABLE LinhaStock;
TRUNCATE TABLE Estacao;
TRUNCATE TABLE Funcionario;
TRUNCATE TABLE LinhaMenu;
TRUNCATE TABLE LinhaProduto;
TRUNCATE TABLE Produto_Passo; -- Tabela de junção implícita
TRUNCATE TABLE Passo_Ingrediente; -- Tabela de junção implícita
TRUNCATE TABLE Catalogo_Produto; -- Tabela de junção implícita
TRUNCATE TABLE Catalogo_Menu; -- Tabela de junção implícita
TRUNCATE TABLE Menu;
TRUNCATE TABLE Produto;
TRUNCATE TABLE Passo;
TRUNCATE TABLE Ingrediente;
TRUNCATE TABLE Restaurante;
TRUNCATE TABLE Catalogo;

-- ======================================================================================
-- 1. DADOS MESTRES (Catálogos, Ingredientes, Passos, Produtos, Menus)
-- ======================================================================================

-- 1.1. Catálogos
INSERT INTO Catalogo (id, nome) VALUES 
(1, 'Catálogo Standard 2024'),
(2, 'Catálogo Premium & Verão');

-- 1.2. Ingredientes
INSERT INTO Ingrediente (id, nome, unidade, alergenico) VALUES
(1, 'Pão Brioche', 'un', 'GLUTEN'),
(2, 'Carne de Vaca 150g', 'un', NULL),
(3, 'Queijo Cheddar', 'fatia', 'LACTOSE'),
(4, 'Alface', 'folha', NULL),
(5, 'Tomate', 'fatia', NULL),
(6, 'Batata Palito', 'kg', NULL),
(7, 'Sal', 'g', NULL),
(8, 'Refrigerante Cola', 'L', NULL),
(9, 'Frango Panado', 'un', 'GLUTEN'),
(10, 'Molho Especial', 'ml', 'OVO'),
(11, 'Gelado Baunilha', 'kg', 'LACTOSE'),
(12, 'Bacon', 'fatia', NULL),
(13, 'Cogumelos', 'g', NULL);

-- 1.3. Passos (Workflows de Produção)
-- Definimos a duração e o posto de trabalho
INSERT INTO Passo (id, nome, duracao_minutos, trabalho) VALUES
-- Hambúrgueres
(1, 'Grelhar Carne', 5, 'GRELHA'),
(2, 'Tostar Pão', 1, 'GRELHA'),
(3, 'Montar Hambúrguer', 2, 'MONTAGEM'),
-- Fritos
(4, 'Fritar Batatas', 4, 'FRITURA'),
(5, 'Fritar Frango', 6, 'FRITURA'),
(6, 'Temperar e Embalar Batatas', 1, 'MONTAGEM'),
-- Bebidas e Sobremesas
(7, 'Servir Bebida', 1, 'BEBIDAS'),
(8, 'Preparar Sundae', 3, 'GELADOS'),
-- Entrega
(9, 'Entregar ao Cliente', 1, 'CAIXA');

-- Associação Passo -> Ingrediente (Lógica de consumo)
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(1, 2), -- Grelhar gasta Carne
(2, 1), -- Tostar gasta Pão
(3, 3), (3, 4), (3, 5), (3, 10), -- Montar gasta Queijo, Alface, Tomate, Molho
(4, 6), -- Fritar gasta Batata
(6, 7), -- Temperar gasta Sal
(7, 8); -- Bebida gasta Cola

-- 1.4. Produtos
INSERT INTO Produto (id, nome, preco) VALUES
(1, 'Hambúrguer Clássico', 5.50),
(2, 'Cheeseburger Bacon', 6.90),
(3, 'Batata Frita Média', 2.00),
(4, 'Refrigerante Cola', 1.50),
(5, 'Sundae Baunilha', 2.50),
(6, 'Chicken Burger', 5.00);

-- Receita dos Produtos (LinhaProduto) - Opcional para cálculo de custos, mas importante para stock
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(1, 1, 1, 1), (2, 1, 2, 1), -- Classico: Pão, Carne
(3, 2, 1, 1), (4, 2, 2, 1), (5, 2, 12, 2), -- Bacon: Pão, Carne, 2x Bacon
(6, 3, 6, 0.2), -- Batata: 200g batata
(7, 4, 8, 0.4); -- Bebida: 400ml cola

-- Workflow dos Produtos (Quais passos são necessários)
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 9), -- Classico: Grelhar, Tostar, Montar, Entregar
(2, 1), (2, 2), (2, 3), (2, 9), -- Bacon: Igual
(3, 4), (3, 6), (3, 9),         -- Batata: Fritar, Embalar, Entregar
(4, 7), (4, 9),                 -- Bebida: Servir, Entregar
(5, 8), (5, 9);                 -- Sundae: Preparar, Entregar

-- 1.5. Menus
INSERT INTO Menu (id, nome, preco) VALUES
(1, 'Menu Clássico', 8.50),
(2, 'Menu Bacon Lovers', 9.90);

-- Composição dos Menus (LinhaMenu)
INSERT INTO LinhaMenu (id, menu_id, produto_id, quantidade) VALUES
(1, 1, 1, 1), (2, 1, 3, 1), (3, 1, 4, 1), -- Menu 1: Classico + Batata + Bebida
(4, 2, 2, 1), (5, 2, 3, 1), (6, 2, 4, 1); -- Menu 2: Bacon + Batata + Bebida

-- Associar Produtos e Menus aos Catálogos
INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES
(1, 1), (1, 3), (1, 4), (1, 6), -- Catálogo 1 tem cenas básicas
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5); -- Catálogo 2 tem cenas premium

INSERT INTO Catalogo_Menu (catalogo_id, menu_id) VALUES
(1, 1),
(2, 1), (2, 2);

-- ======================================================================================
-- 2. INFRAESTRUTURA (Restaurantes, Staff, Estações, Stock)
-- ======================================================================================

-- 2.1. Restaurantes
INSERT INTO Restaurante (id, nome, localizacao, catalogo_id) VALUES
(1, 'FastBurger Lisboa', 'Baixa-Chiado', 1),
(2, 'FastBurger Porto', 'Avenida dos Aliados', 1),
(3, 'FastBurger Algarve', 'Vilamoura Marina', 2); -- Usa catálogo Premium

-- 2.2. Funcionários
-- Global
INSERT INTO Funcionario (id, restaurante_id, utilizador, password, funcao) VALUES
(1, NULL, 'admin.coo', 'admin123', 'COO');

-- Lisboa (Rest 1)
INSERT INTO Funcionario (id, restaurante_id, utilizador, password, funcao) VALUES
(2, 1, 'ana.gerente', 'pass', 'GERENTE'),
(3, 1, 'joao.cozinha', 'pass', 'FUNCIONARIO'),
(4, 1, 'maria.caixa', 'pass', 'FUNCIONARIO'),
(5, 1, 'pedro.grelha', 'pass', 'FUNCIONARIO');

-- Porto (Rest 2)
INSERT INTO Funcionario (id, restaurante_id, utilizador, password, funcao) VALUES
(6, 2, 'rui.gerente', 'pass', 'GERENTE'),
(7, 2, 'sofia.cozinha', 'pass', 'FUNCIONARIO'),
(8, 2, 'tiago.caixa', 'pass', 'FUNCIONARIO'),
(9, 2, 'beatriz.grelha', 'pass', 'FUNCIONARIO');

-- Algarve (Rest 3)
INSERT INTO Funcionario (id, restaurante_id, utilizador, password, funcao) VALUES
(10, 3, 'carlos.gerente', 'pass', 'GERENTE'),
(11, 3, 'ines.cozinha', 'pass', 'FUNCIONARIO'),
(12, 3, 'lucas.bar', 'pass', 'FUNCIONARIO'),
(13, 3, 'andre.caixa', 'pass', 'FUNCIONARIO');

-- 2.3. Estações de Trabalho (Configuração básica para todos)
INSERT INTO Estacao (id, restaurante_id, trabalho) VALUES
-- Lisboa
(1, 1, 'CAIXA'), (2, 1, 'GRELHA'), (3, 1, 'FRITURA'), (4, 1, 'MONTAGEM'), (5, 1, 'BEBIDAS'),
-- Porto
(6, 2, 'CAIXA'), (7, 2, 'GRELHA'), (8, 2, 'FRITURA'), (9, 2, 'MONTAGEM'), (10, 2, 'BEBIDAS'),
-- Algarve (tem Gelados extra)
(11, 3, 'CAIXA'), (12, 3, 'GRELHA'), (13, 3, 'FRITURA'), (14, 3, 'MONTAGEM'), (15, 3, 'GELADOS');

-- 2.4. Stock Inicial (Resumido)
-- Lisboa tem stock normal, Porto tem falta de Bacon (para simular problemas)
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES
(1, 1, 100), (1, 2, 100), (1, 6, 500), -- Lisboa OK
(2, 1, 100), (2, 2, 100), (2, 12, 2),  -- Porto: Pouco Bacon!
(3, 1, 200), (3, 11, 50); -- Algarve

-- ======================================================================================
-- 3. OPERAÇÃO (Pedidos e Tarefas)
-- Lógica Temporal: NOW() é o momento atual.
-- ======================================================================================

-- --------------------------------------------------------------------------------------
-- CENÁRIO 1: Pedido Histórico (CONCLUÍDO/ENTREGUE) - Restaurante Lisboa
-- Pedido feito há 2 horas, entregue há 1h45.
-- Conteúdo: 1 Hambúrguer Clássico
-- --------------------------------------------------------------------------------------

INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
(1, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 120 MINUTE), DATE_SUB(NOW(), INTERVAL 105 MINUTE));

INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
(1, 1, 1, 'PRODUTO', 1, 5.50); -- 1 Hamburguer Classico

-- Tarefas do Pedido 1 (Todas CONCLUIDAS, sequência temporal lógica)
INSERT INTO Tarefa (id, pedido_id, produto_id, passo_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
-- 1. Grelhar (5 min)
(1, 1, 1, 1, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 120 MINUTE), DATE_SUB(NOW(), INTERVAL 119 MINUTE), DATE_SUB(NOW(), INTERVAL 114 MINUTE)),
-- 2. Tostar Pão (1 min) - Feito em paralelo
(2, 1, 1, 2, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 120 MINUTE), DATE_SUB(NOW(), INTERVAL 119 MINUTE), DATE_SUB(NOW(), INTERVAL 118 MINUTE)),
-- 3. Montar (2 min) - Começa depois de grelhar
(3, 1, 1, 3, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 120 MINUTE), DATE_SUB(NOW(), INTERVAL 114 MINUTE), DATE_SUB(NOW(), INTERVAL 112 MINUTE)),
-- 4. Entregar (1 min) - Último passo
(4, 1, 1, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 120 MINUTE), DATE_SUB(NOW(), INTERVAL 110 MINUTE), DATE_SUB(NOW(), INTERVAL 109 MINUTE));

-- --------------------------------------------------------------------------------------
-- CENÁRIO 2: Pedido Pronto a Sair (PRONTO) - Restaurante Porto
-- Feito há 15 minutos, comida pronta, falta entregar na caixa.
-- Conteúdo: 1 Batata Frita
-- --------------------------------------------------------------------------------------

INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
(2, 2, TRUE, 'PRONTO', DATE_SUB(NOW(), INTERVAL 15 MINUTE), NULL);

INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
(2, 2, 3, 'PRODUTO', 1, 2.00); 

INSERT INTO Tarefa (id, pedido_id, produto_id, passo_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
-- Fritar (Concluido)
(5, 2, 3, 4, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 15 MINUTE), DATE_SUB(NOW(), INTERVAL 14 MINUTE), DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
-- Embalar (Concluido)
(6, 2, 3, 6, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 15 MINUTE), DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_SUB(NOW(), INTERVAL 9 MINUTE)),
-- Entrega (PENDENTE - O pedido está pronto mas ainda não foi entregue ao cliente)
(7, 2, 3, 9, 'PENDENTE', DATE_SUB(NOW(), INTERVAL 15 MINUTE), NULL, NULL);

-- --------------------------------------------------------------------------------------
-- CENÁRIO 3: Pedido Em Produção (EM_PREPARACAO) - Restaurante Algarve
-- Pedido Complexo: Menu. Alguns passos feitos, outros em curso.
-- --------------------------------------------------------------------------------------

INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
(3, 3, FALSE, 'EM_PREPARACAO', DATE_SUB(NOW(), INTERVAL 5 MINUTE), NULL);

INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
(3, 3, 2, 'MENU', 1, 9.90); -- Menu Bacon Lovers (Hamburguer + Batata + Bebida)

-- Tarefas (Geradas para os produtos dentro do menu)
-- Vamos simular: Bebida já servida, Hambúrguer na grelha, Batata pendente.
INSERT INTO Tarefa (id, pedido_id, produto_id, passo_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
-- Produto 2 (Burger Bacon) - Grelhar Carne: EM EXECUÇÃO
(8, 3, 2, 1, 'EM_EXECUCAO', DATE_SUB(NOW(), INTERVAL 5 MINUTE), DATE_SUB(NOW(), INTERVAL 2 MINUTE), NULL),
-- Produto 2 (Burger Bacon) - Tostar Pão: CONCLUIDA
(9, 3, 2, 2, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 5 MINUTE), DATE_SUB(NOW(), INTERVAL 4 MINUTE), DATE_SUB(NOW(), INTERVAL 3 MINUTE)),
-- Produto 2 (Burger Bacon) - Montar: PENDENTE
(10, 3, 2, 3, 'PENDENTE', DATE_SUB(NOW(), INTERVAL 5 MINUTE), NULL, NULL),

-- Produto 3 (Batata) - Fritar: PENDENTE (Fila de espera)
(11, 3, 3, 4, 'PENDENTE', DATE_SUB(NOW(), INTERVAL 5 MINUTE), NULL, NULL),

-- Produto 4 (Bebida) - Servir: CONCLUIDA (Rápido)
(12, 3, 4, 7, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 5 MINUTE), DATE_SUB(NOW(), INTERVAL 4 MINUTE), DATE_SUB(NOW(), INTERVAL 3 MINUTE));

-- --------------------------------------------------------------------------------------
-- CENÁRIO 4: Pedido Com Problemas (EM_PREPARACAO / ATRASADO) - Restaurante Porto
-- Falta Bacon no stock, tarefa bloqueada.
-- --------------------------------------------------------------------------------------

INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
(4, 2, FALSE, 'EM_PREPARACAO', DATE_SUB(NOW(), INTERVAL 20 MINUTE), NULL);

INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
(4, 4, 2, 'PRODUTO', 1, 6.90); -- Cheeseburger Bacon

INSERT INTO Tarefa (id, pedido_id, produto_id, passo_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
-- Grelhar Carne: CONCLUIDA
(13, 4, 2, 1, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 20 MINUTE), DATE_SUB(NOW(), INTERVAL 18 MINUTE), DATE_SUB(NOW(), INTERVAL 13 MINUTE)),
-- Montar (Requer Bacon): ATRASADA
(14, 4, 2, 3, 'ATRASADA', DATE_SUB(NOW(), INTERVAL 20 MINUTE), DATE_SUB(NOW(), INTERVAL 10 MINUTE), NULL);

-- --------------------------------------------------------------------------------------
-- DADOS HISTÓRICOS ADICIONAIS: +14 Pedidos Entregues em Lisboa (Total 15 em Lisboa)
-- ID Pedidos: 10 a 23 | ID Tarefas: 20 a 65
-- --------------------------------------------------------------------------------------

-- 1. PEDIDOS DE BEBIDAS (Simples, 2 tarefas) - Pedidos 10 a 14
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
(10, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 20 HOUR), DATE_SUB(NOW(), INTERVAL 19 HOUR)),
(11, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 18 HOUR), DATE_SUB(NOW(), INTERVAL 17 HOUR)),
(12, 1, TRUE,  'ENTREGUE', DATE_SUB(NOW(), INTERVAL 16 HOUR), DATE_SUB(NOW(), INTERVAL 15 HOUR)),
(13, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 14 HOUR), DATE_SUB(NOW(), INTERVAL 13 HOUR)),
(14, 1, TRUE,  'ENTREGUE', DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_SUB(NOW(), INTERVAL 11 HOUR));

INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
(10, 10, 4, 'PRODUTO', 1, 1.50), -- Cola
(11, 11, 4, 'PRODUTO', 1, 1.50),
(12, 12, 4, 'PRODUTO', 1, 1.50),
(13, 13, 4, 'PRODUTO', 1, 1.50),
(14, 14, 4, 'PRODUTO', 1, 1.50);

INSERT INTO Tarefa (id, pedido_id, produto_id, passo_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
-- Pedido 10
(20, 10, 4, 7, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 20 HOUR), DATE_SUB(NOW(), INTERVAL 19 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 19 HOUR) - INTERVAL 10 MINUTE),
(21, 10, 4, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 20 HOUR), DATE_SUB(NOW(), INTERVAL 19 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 19 HOUR)),
-- Pedido 11
(22, 11, 4, 7, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 18 HOUR), DATE_SUB(NOW(), INTERVAL 17 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 17 HOUR) - INTERVAL 10 MINUTE),
(23, 11, 4, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 18 HOUR), DATE_SUB(NOW(), INTERVAL 17 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 17 HOUR)),
-- Pedido 12
(24, 12, 4, 7, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 16 HOUR), DATE_SUB(NOW(), INTERVAL 15 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 15 HOUR) - INTERVAL 10 MINUTE),
(25, 12, 4, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 16 HOUR), DATE_SUB(NOW(), INTERVAL 15 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
-- Pedido 13
(26, 13, 4, 7, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 14 HOUR), DATE_SUB(NOW(), INTERVAL 13 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 13 HOUR) - INTERVAL 10 MINUTE),
(27, 13, 4, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 14 HOUR), DATE_SUB(NOW(), INTERVAL 13 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 13 HOUR)),
-- Pedido 14
(28, 14, 4, 7, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_SUB(NOW(), INTERVAL 11 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 11 HOUR) - INTERVAL 10 MINUTE),
(29, 14, 4, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_SUB(NOW(), INTERVAL 11 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 11 HOUR));

-- 2. PEDIDOS DE BATATAS (3 tarefas) - Pedidos 15 a 19
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
(15, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR)),
(16, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 9 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR)),
(17, 1, TRUE,  'ENTREGUE', DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(18, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 7 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(19, 1, TRUE,  'ENTREGUE', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR));

INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
(15, 15, 3, 'PRODUTO', 1, 2.00), -- Batatas M
(16, 16, 3, 'PRODUTO', 1, 2.00),
(17, 17, 3, 'PRODUTO', 1, 2.00),
(18, 18, 3, 'PRODUTO', 1, 2.00),
(19, 19, 3, 'PRODUTO', 1, 2.00);

INSERT INTO Tarefa (id, pedido_id, produto_id, passo_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
-- Pedido 15
(30, 15, 3, 4, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR) - INTERVAL 40 MINUTE, DATE_SUB(NOW(), INTERVAL 9 HOUR) - INTERVAL 30 MINUTE), -- Fritar
(31, 15, 3, 6, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR) - INTERVAL 20 MINUTE, DATE_SUB(NOW(), INTERVAL 9 HOUR) - INTERVAL 15 MINUTE), -- Embalar
(32, 15, 3, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 9 HOUR)), -- Entregar
-- Pedido 16
(33, 16, 3, 4, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 9 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR) - INTERVAL 40 MINUTE, DATE_SUB(NOW(), INTERVAL 8 HOUR) - INTERVAL 30 MINUTE),
(34, 16, 3, 6, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 9 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR) - INTERVAL 20 MINUTE, DATE_SUB(NOW(), INTERVAL 8 HOUR) - INTERVAL 15 MINUTE),
(35, 16, 3, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 9 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 8 HOUR)),
-- Pedido 17
(36, 17, 3, 4, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR) - INTERVAL 40 MINUTE, DATE_SUB(NOW(), INTERVAL 7 HOUR) - INTERVAL 30 MINUTE),
(37, 17, 3, 6, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR) - INTERVAL 20 MINUTE, DATE_SUB(NOW(), INTERVAL 7 HOUR) - INTERVAL 15 MINUTE),
(38, 17, 3, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
-- Pedido 18
(39, 18, 3, 4, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 7 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR) - INTERVAL 40 MINUTE, DATE_SUB(NOW(), INTERVAL 6 HOUR) - INTERVAL 30 MINUTE),
(40, 18, 3, 6, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 7 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR) - INTERVAL 20 MINUTE, DATE_SUB(NOW(), INTERVAL 6 HOUR) - INTERVAL 15 MINUTE),
(41, 18, 3, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 7 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
-- Pedido 19
(42, 19, 3, 4, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR) - INTERVAL 40 MINUTE, DATE_SUB(NOW(), INTERVAL 5 HOUR) - INTERVAL 30 MINUTE),
(43, 19, 3, 6, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR) - INTERVAL 20 MINUTE, DATE_SUB(NOW(), INTERVAL 5 HOUR) - INTERVAL 15 MINUTE),
(44, 19, 3, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 5 HOUR));

-- 3. PEDIDOS DE HAMBÚRGUERES (4 tarefas) - Pedidos 20 a 23
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
(20, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(21, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(22, 1, TRUE,  'ENTREGUE', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(23, 1, FALSE, 'ENTREGUE', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE));

INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
(20, 20, 1, 'PRODUTO', 1, 5.50), -- Classico
(21, 21, 1, 'PRODUTO', 1, 5.50),
(22, 22, 1, 'PRODUTO', 1, 5.50),
(23, 23, 1, 'PRODUTO', 1, 5.50);

INSERT INTO Tarefa (id, pedido_id, produto_id, passo_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
-- Pedido 20
(50, 20, 1, 1, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 3 HOUR) - INTERVAL 40 MINUTE), -- Grelhar
(51, 20, 1, 2, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 3 HOUR) - INTERVAL 45 MINUTE), -- Tostar
(52, 20, 1, 3, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR) - INTERVAL 30 MINUTE, DATE_SUB(NOW(), INTERVAL 3 HOUR) - INTERVAL 15 MINUTE), -- Montar
(53, 20, 1, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 3 HOUR)), -- Entregar
-- Pedido 21
(54, 21, 1, 1, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 2 HOUR) - INTERVAL 40 MINUTE),
(55, 21, 1, 2, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 2 HOUR) - INTERVAL 45 MINUTE),
(56, 21, 1, 3, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR) - INTERVAL 30 MINUTE, DATE_SUB(NOW(), INTERVAL 2 HOUR) - INTERVAL 15 MINUTE),
(57, 21, 1, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
-- Pedido 22
(58, 22, 1, 1, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 1 HOUR) - INTERVAL 40 MINUTE),
(59, 22, 1, 2, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR) - INTERVAL 50 MINUTE, DATE_SUB(NOW(), INTERVAL 1 HOUR) - INTERVAL 45 MINUTE),
(60, 22, 1, 3, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR) - INTERVAL 30 MINUTE, DATE_SUB(NOW(), INTERVAL 1 HOUR) - INTERVAL 15 MINUTE),
(61, 22, 1, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR) - INTERVAL 5 MINUTE, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
-- Pedido 23
(62, 23, 1, 1, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE) - INTERVAL 25 MINUTE, DATE_SUB(NOW(), INTERVAL 30 MINUTE) - INTERVAL 20 MINUTE),
(63, 23, 1, 2, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE) - INTERVAL 25 MINUTE, DATE_SUB(NOW(), INTERVAL 30 MINUTE) - INTERVAL 24 MINUTE),
(64, 23, 1, 3, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE) - INTERVAL 15 MINUTE, DATE_SUB(NOW(), INTERVAL 30 MINUTE) - INTERVAL 5 MINUTE),
(65, 23, 1, 9, 'CONCLUIDA', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE) - INTERVAL 2 MINUTE, DATE_SUB(NOW(), INTERVAL 30 MINUTE));

-- Mensagem de alerta sobre o atraso
INSERT INTO Mensagem (restaurante_id, texto, data_hora) VALUES
(2, '[URGENTE] Tarefa #14 PARADA - Falta Bacon', NOW());

-- ======================================================================================
-- FIM DO SCRIPT
-- ======================================================================================

SET FOREIGN_KEY_CHECKS = 1;