-- Assumir que o schema já foi criado exatamente como no enunciado
-- e que estamos numa BD MySQL vazia.

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE Tarefa;
TRUNCATE TABLE LinhaStock;
TRUNCATE TABLE LinhaProduto;
TRUNCATE TABLE LinhaMenu;
TRUNCATE TABLE LinhaPedido;
TRUNCATE TABLE Produto_Passo;
TRUNCATE TABLE Passo_Ingrediente;
TRUNCATE TABLE Catalogo_Produto;
TRUNCATE TABLE Catalogo_Menu;
TRUNCATE TABLE Passo;
TRUNCATE TABLE Mensagem;
TRUNCATE TABLE Pedido;
TRUNCATE TABLE Menu;
TRUNCATE TABLE Produto;
TRUNCATE TABLE Ingrediente;
TRUNCATE TABLE Estacao;
TRUNCATE TABLE Funcionario;
TRUNCATE TABLE Catalogo;
TRUNCATE TABLE Restaurante;
SET FOREIGN_KEY_CHECKS = 1;

--------------------------------------------------
-- 1. Catálogo
--------------------------------------------------
INSERT INTO Catalogo (id, nome) VALUES
(1, 'Menu Principal FastBurger'),
(2, 'Menu Pequeno-Almoço FastBurger');

--------------------------------------------------
-- 2. Restaurantes (cadeia de fast food)
--------------------------------------------------
INSERT INTO Restaurante (id, nome, localizacao, catalogo_id) VALUES
(1, 'FastBurger - Lisboa Centro', 'Lisboa - Baixa', 1),
(2, 'FastBurger - Lisboa Oriente', 'Lisboa - Parque das Nações', 1),
(3, 'FastBurger - Porto Aliados', 'Porto - Aliados', 1);

--------------------------------------------------
-- 3. Funcionários (usar enum Funcao como texto)
--------------------------------------------------
INSERT INTO Funcionario (id, restaurante_id, utilizador, password, funcao) VALUES
(1, 1, 'joao.caixa',      'pass123', 'FUNCIONARIO'),
(2, 1, 'maria.grelha',    'pass123', 'FUNCIONARIO'),
(3, 1, 'ana.gerente',     'pass123', 'GERENTE'),
(4, 2, 'tiago.fritura',   'pass123', 'FUNCIONARIO'),
(5, 2, 'carlos.coo',      'pass123', 'COO'),
(6, 3, 'rita.sysadmin',   'pass123', 'SYSADMIN');

--------------------------------------------------
-- 4. Estações de trabalho (usar enum Trabalho como texto)
--------------------------------------------------
INSERT INTO Estacao (id, restaurante_id, trabalho) VALUES
(1, 1, 'CAIXA'),
(2, 1, 'GRELHA'),
(3, 1, 'FRITURA'),
(4, 1, 'BEBIDAS'),
(5, 2, 'CAIXA'),
(6, 2, 'MONTAGEM'),
(7, 3, 'CAIXA'),
(8, 3, 'GELADOS');

--------------------------------------------------
-- 5. Ingredientes (alguns típicos de fast food)
--------------------------------------------------
INSERT INTO Ingrediente (id, nome, unidade, alergenico) VALUES
(1, 'Pão de hambúrguer', 'un', 'GLUTEN'),
(2, 'Carne de vaca',     'un', NULL),
(3, 'Queijo cheddar',    'fatia', 'LACTOSE'),
(4, 'Bacon',             'fatia', NULL),
(5, 'Batata para fritar','kg', NULL),
(6, 'Óleo vegetal',      'L', NULL),
(7, 'Alface',            'folha', NULL),
(8, 'Tomate',            'fatia', NULL),
(9, 'Molho especial',    'g', 'OVO'),
(10,'Refrigerante cola', 'L', NULL),
(11,'Gelado baunilha',   'g', 'LACTOSE'),
(12,'Copo descartável',  'un', NULL),
(13,'Sal',               'g', NULL),
(14,'Frango panado',     'un', 'GLUTEN'),
(15,'Pão de brioche',    'un', 'GLUTEN');

--------------------------------------------------
-- 6. Produtos (itens individuais do menu)
--------------------------------------------------
INSERT INTO Produto (id, nome, preco) VALUES
(1, 'Hambúrguer Clássico',           4.50),
(2, 'Cheeseburger',                  4.90),
(3, 'Hambúrguer Bacon & Cheese',     5.50),
(4, 'Batatas Fritas Pequenas',       1.80),
(5, 'Batatas Fritas Grandes',        2.30),
(6, 'Chicken Nuggets (6 unidades)',  3.90),
(7, 'Refrigerante Cola 0.5L',        1.70),
(8, 'Gelado de Baunilha',            1.50);

--------------------------------------------------
-- 7. Menus (combos)
--------------------------------------------------
INSERT INTO Menu (id, nome, preco) VALUES
(1, 'Menu Clássico',                   7.90),
(2, 'Menu Bacon & Cheese',             8.90),
(3, 'Menu Nuggets',                    7.50),
(4, 'Menu Infantil (hambúrguer mini)', 5.50);

--------------------------------------------------
-- 8. Associação Catálogo -> Menu
--------------------------------------------------
INSERT INTO Catalogo_Menu (catalogo_id, menu_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4);

--------------------------------------------------
-- 9. Associação Catálogo -> Produto
--------------------------------------------------
INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8);

--------------------------------------------------
-- 10. Linhas de Menu (quais produtos compõem cada menu)
--------------------------------------------------
INSERT INTO LinhaMenu (id, menu_id, produto_id, quantidade) VALUES
-- Menu Clássico: Hambúrguer Clássico + Batata média (grande) + bebida
(1, 1, 1, 1),
(2, 1, 5, 1),
(3, 1, 7, 1),

-- Menu Bacon & Cheese: Bacon & Cheese + Batata grande + bebida
(4, 2, 3, 1),
(5, 2, 5, 1),
(6, 2, 7, 1),

-- Menu Nuggets: Nuggets + Batata pequena + bebida
(7, 3, 6, 1),
(8, 3, 4, 1),
(9, 3, 7, 1),

-- Menu Infantil: Hambúrguer Clássico + Batata pequena + gelado
(10, 4, 1, 1),
(11, 4, 4, 1),
(12, 4, 8, 1);

--------------------------------------------------
-- 11. LinhaProduto (receitas básicas dos produtos)
--------------------------------------------------
-- Hambúrguer Clássico
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(1, 1, 1, 1.000),  -- pão
(2, 1, 2, 1.000),  -- carne
(3, 1, 7, 1.000),  -- alface
(4, 1, 8, 1.000),  -- tomate
(5, 1, 9, 20.000); -- molho

-- Cheeseburger
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(6, 2, 1, 1.000),
(7, 2, 2, 1.000),
(8, 2, 3, 1.000),
(9, 2, 9, 20.000);

-- Bacon & Cheese
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(10, 3, 15, 1.000),
(11, 3, 2, 1.000),
(12, 3, 3, 1.000),
(13, 3, 4, 2.000),
(14, 3, 9, 25.000);

-- Batatas pequenas
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(15, 4, 5, 0.150),
(16, 4, 6, 0.020),
(17, 4, 13, 2.000);

-- Batatas grandes
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(18, 5, 5, 0.220),
(19, 5, 6, 0.030),
(20, 5, 13, 3.000);

-- Nuggets
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(21, 6, 14, 6.000),
(22, 6, 6, 0.020),
(23, 6, 13, 1.000);

-- Refrigerante
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(24, 7, 10, 0.500),
(25, 7, 12, 1.000);

-- Gelado
INSERT INTO LinhaProduto (id, produto_id, ingrediente_id, quantidade) VALUES
(26, 8, 11, 0.120),
(27, 8, 12, 1.000);

--------------------------------------------------
-- 12. Passos (workflow de preparação) + associação Produto_Passo
-- Trabalho deve usar enum Trabalho como texto
--------------------------------------------------
INSERT INTO Passo (id, nome, duracao_minutos, trabalho) VALUES
(1, 'Grelhar hambúrguer',        5, 'GRELHA'),
(2, 'Montar hambúrguer',         2, 'MONTAGEM'),
(3, 'Fritar batatas',            4, 'FRITURA'),
(4, 'Fritar nuggets',            5, 'FRITURA'),
(5, 'Servir refrigerante',       1, 'BEBIDAS'),
(6, 'Servir gelado de baunilha', 1, 'GELADOS');

-- Associações produtos -> passos
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES
-- Hambúrguer Clássico
(1, 1),
(1, 2),

-- Cheeseburger
(2, 1),
(2, 2),

-- Bacon & Cheese
(3, 1),
(3, 2),

-- Batatas
(4, 3),
(5, 3),

-- Nuggets
(6, 4),

-- Refrigerante
(7, 5),

-- Gelado
(8, 6);

--------------------------------------------------
-- 13. Passo_Ingrediente (quais ingredientes usados em cada passo)
--------------------------------------------------
-- Grelhar hambúrguer
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(1, 2);

-- Montar hambúrguer (pão + vegetais + queijo/bacon/molho etc.)
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(2, 1),
(2, 3),
(2, 4),
(2, 7),
(2, 8),
(2, 9),
(2, 15);

-- Fritar batatas
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(3, 5),
(3, 6),
(3, 13);

-- Fritar nuggets
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(4, 14),
(4, 6),
(4, 13);

-- Servir refrigerante
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(5, 10),
(5, 12);

-- Servir gelado
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(6, 11),
(6, 12);

--------------------------------------------------
-- 14. Stock por restaurante
--------------------------------------------------
INSERT INTO LinhaStock (id, restaurante_id, ingrediente_id, quantidade) VALUES
-- Restaurante 1
(1, 1, 1, 500),
(2, 1, 2, 400),
(3, 1, 3, 300),
(4, 1, 4, 200),
(5, 1, 5, 150.000),
(6, 1, 6, 50.000),
(7, 1, 7, 300),
(8, 1, 8, 300),
(9, 1, 9, 10.000),
(10,1, 10, 80.000),
(11,1, 11, 30.000),
(12,1, 12, 1000),
(13,1, 13, 5.000),
(14,1, 14, 400),
(15,1, 15, 200),

-- Restaurante 2
(16, 2, 1, 300),
(17, 2, 2, 250),
(18, 2, 3, 200),
(19, 2, 4, 150),
(20, 2, 5, 120.000),
(21, 2, 6, 40.000),
(22, 2, 7, 200),
(23, 2, 8, 200),
(24, 2, 9, 8.000),
(25, 2, 10, 60.000),
(26, 2, 11, 25.000),
(27, 2, 12, 800),
(28, 2, 13, 4.000),
(29, 2, 14, 300),
(30, 2, 15, 150),

-- Restaurante 3
(31, 3, 1, 200),
(32, 3, 2, 200),
(33, 3, 3, 150),
(34, 3, 4, 100),
(35, 3, 5, 100.000),
(36, 3, 6, 30.000),
(37, 3, 7, 150),
(38, 3, 8, 150),
(39, 3, 9, 6.000),
(40, 3, 10, 50.000),
(41, 3, 11, 20.000),
(42, 3, 12, 600),
(43, 3, 13, 3.000),
(44, 3, 14, 250),
(45, 3, 15, 120);

--------------------------------------------------
-- 15. Pedidos (usar enum EstadoPedido como texto)
--------------------------------------------------
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_hora) VALUES
(1, 1, 0, 'INICIADO',      NOW() - INTERVAL 30 MINUTE),
(2, 1, 1, 'EM_PREPARACAO', NOW() - INTERVAL 20 MINUTE),
(3, 1, 0, 'PRONTO',        NOW() - INTERVAL 10 MINUTE),
(4, 2, 1, 'ENTREGUE',      NOW() - INTERVAL 1 HOUR),
(5, 3, 0, 'CANCELADO',     NOW() - INTERVAL 2 HOUR);

--------------------------------------------------
-- 16. Linhas de Pedido (TipoItem como texto em "tipo")
--------------------------------------------------
INSERT INTO LinhaPedido (id, pedido_id, item_id, tipo, quantidade, preco_unitario, observacao) VALUES
-- Pedido 1: Menu Clássico no restaurante 1
(1, 1, 1, 'MENU',    1, 7.90, 'Sem tomate'),
-- Pedido 2: Menu Bacon & Cheese para levar
(2, 2, 2, 'MENU',    1, 8.90, 'Bacon extra'),
(3, 2, 8, 'PRODUTO', 2, 1.50, 'Gelado extra'),
-- Pedido 3: Apenas produtos individuais
(4, 3, 3, 'PRODUTO', 1, 5.50, 'Sem molho especial'),
(5, 3, 5, 'PRODUTO', 1, 2.30, NULL),
(6, 3, 7, 'PRODUTO', 1, 1.70, 'Sem gelo'),
-- Pedido 4: Menu Nuggets entregue
(7, 4, 3, 'MENU',    2, 7.50, NULL),
-- Pedido 5: cancelado, sem linhas ou apenas 1 item
(8, 5, 2, 'PRODUTO', 1, 4.90, 'Pedido cancelado pelo cliente');

--------------------------------------------------
-- 17. Mensagens internas
--------------------------------------------------
INSERT INTO Mensagem (id, restaurante_id, texto, data_hora) VALUES
(1, 1, 'Reforçar stock de carne de vaca e pão de hambúrguer.', NOW() - INTERVAL 3 HOUR),
(2, 1, 'Promoção de Menu Clássico ao almoço.', NOW() - INTERVAL 1 HOUR),
(3, 2, 'Máquina de gelados em manutenção até amanhã.', NOW() - INTERVAL 2 HOUR),
(4, 3, 'Formação de novos colaboradores na estação de CAIXA.', NOW() - INTERVAL 4 HOUR);

--------------------------------------------------
-- 18. Tarefas de cozinha (workflow de preparação)
-- EstadoTarefa não está em coluna própria, mas os campos concluido/data_conclusao
-- permitem inferir o estado.
--------------------------------------------------
INSERT INTO Tarefa (id, passo_id, produto_id, pedido_id, data_criacao, data_conclusao, concluido) VALUES
-- Pedido 1 (INICIADO): tarefas ainda pendentes
(1, 1, 1, 1, NOW() - INTERVAL 25 MINUTE, NULL, 0), -- grelhar hambúrguer
(2, 2, 1, 1, NOW() - INTERVAL 23 MINUTE, NULL, 0), -- montar hambúrguer
(3, 3, 5, 1, NOW() - INTERVAL 24 MINUTE, NULL, 0), -- fritar batatas
(4, 5, 7, 1, NOW() - INTERVAL 22 MINUTE, NULL, 0), -- servir bebida

-- Pedido 2 (EM_PREPARACAO): algumas concluídas, outras não
(5, 1, 3, 2, NOW() - INTERVAL 18 MINUTE, NOW() - INTERVAL 14 MINUTE, 1),
(6, 2, 3, 2, NOW() - INTERVAL 13 MINUTE, NULL, 0),
(7, 3, 5, 2, NOW() - INTERVAL 15 MINUTE, NOW() - INTERVAL 10 MINUTE, 1),
(8, 4, 6, 2, NOW() - INTERVAL 16 MINUTE, NOW() - INTERVAL 11 MINUTE, 1),
(9, 5, 7, 2, NOW() - INTERVAL 12 MINUTE, NULL, 0),
(10,6, 8, 2, NOW() - INTERVAL 12 MINUTE, NULL, 0),

-- Pedido 3 (PRONTO): tudo concluído
(11,1, 3, 3, NOW() - INTERVAL 20 MINUTE, NOW() - INTERVAL 15 MINUTE, 1),
(12,2, 3, 3, NOW() - INTERVAL 19 MINUTE, NOW() - INTERVAL 14 MINUTE, 1),
(13,3, 5, 3, NOW() - INTERVAL 18 MINUTE, NOW() - INTERVAL 13 MINUTE, 1),
(14,5, 7, 3, NOW() - INTERVAL 17 MINUTE, NOW() - INTERVAL 12 MINUTE, 1),

-- Pedido 4 (ENTREGUE): tudo concluído há mais tempo
(15,4, 6, 4, NOW() - INTERVAL 70 MINUTE, NOW() - INTERVAL 65 MINUTE, 1),
(16,3, 4, 4, NOW() - INTERVAL 69 MINUTE, NOW() - INTERVAL 64 MINUTE, 1),
(17,5, 7, 4, NOW() - INTERVAL 68 MINUTE, NOW() - INTERVAL 63 MINUTE, 1),

-- Pedido 5 (CANCELADO): tarefas criadas mas abortadas (não concluídas)
(18,1, 2, 5, NOW() - INTERVAL 115 MINUTE, NULL, 0),
(19,2, 2, 5, NOW() - INTERVAL 114 MINUTE, NULL, 0);
