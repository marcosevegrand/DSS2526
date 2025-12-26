-- ======================================================================================
-- POVOAMENTO DE DADOS (SEM PEDIDOS/TAREFAS)
-- Nota: Executar o script schema.sql primeiro para criar as tabelas.
-- ======================================================================================

USE restaurante;

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

-- 1. Catálogos
INSERT INTO Catalogo (id, nome) VALUES
(1, 'Menu Geral FastBurger');

-- 2. Restaurantes
INSERT INTO Restaurante (id, nome, localizacao, catalogo_id) VALUES
(1, 'FastBurger - Lisboa Centro', 'Lisboa - Baixa', 1),
(2, 'FastBurger - Lisboa Oriente', 'Lisboa - Parque das Nações', 1),
(3, 'FastBurger - Porto Aliados', 'Porto - Aliados', 1),
(4, 'FastBurger - Almada Seixal', 'Almada - Pragal', 1),
(5, 'FastBurger - Braga Minho', 'Braga - Minho', 1);

-- 3. Funcionários
INSERT INTO Funcionario (id, restaurante_id, utilizador, password, funcao) VALUES
(1, 1, 'joao.caixa',      'pass', 'FUNCIONARIO'),
(2, 1, 'maria.grelha',    'pass', 'FUNCIONARIO'),
(3, 1, 'ana.gerente',     'pass', 'GERENTE'),
(4, 2, 'tiago.fritura',   'pass', 'FUNCIONARIO'),
(5, 2, 'carlos.coo',      'pass', 'COO'),
(6, 3, 'rita.sysadmin',   'pass', 'SYSADMIN'),
(7, 4, 'pedro.montagem',  'pass', 'FUNCIONARIO'),
(8, 5, 'sara.bebidas',    'pass', 'FUNCIONARIO');

-- 4. Estações (6 tipos por restaurante = 30 estações)
-- Lisboa Centro (1)
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (1, 'CAIXA'), (1, 'GRELHA'), (1, 'FRITURA'), (1, 'BEBIDAS'), (1, 'GELADOS'), (1, 'MONTAGEM');
-- Lisboa Oriente (2)
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (2, 'CAIXA'), (2, 'GRELHA'), (2, 'FRITURA'), (2, 'BEBIDAS'), (2, 'GELADOS'), (2, 'MONTAGEM');
-- Porto Aliados (3)
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (3, 'CAIXA'), (3, 'GRELHA'), (3, 'FRITURA'), (3, 'BEBIDAS'), (3, 'GELADOS'), (3, 'MONTAGEM');
-- Almada (4)
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (4, 'CAIXA'), (4, 'GRELHA'), (4, 'FRITURA'), (4, 'BEBIDAS'), (4, 'GELADOS'), (4, 'MONTAGEM');
-- Braga (5)
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (5, 'CAIXA'), (5, 'GRELHA'), (5, 'FRITURA'), (5, 'BEBIDAS'), (5, 'GELADOS'), (5, 'MONTAGEM');

-- 5. Ingredientes (Expandido)
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
(15,'Pão de brioche',    'un', 'GLUTEN'),
(16,'Hamburguer Vegan',  'un', 'SOJA'),
(17,'Maionese',          'g', 'OVO'),
(18,'Ketchup',           'g', NULL),
(19,'Mostarda',          'g', 'MOSTARDA'),
(20,'Cebola roxa',       'fatia', NULL),
(21,'Picles',            'un', NULL),
(22,'Gelado Chocolate',  'g', 'LACTOSE'),
(23,'Leite',             'L', 'LACTOSE'),
(24,'Xarope Morango',    'ml', NULL);

-- 6. Produtos (Expandido)
INSERT INTO Produto (id, nome, preco) VALUES
(1, 'Hambúrguer Clássico',           4.50),
(2, 'Cheeseburger',                  4.90),
(3, 'Hambúrguer Bacon & Cheese',     5.50),
(4, 'Batatas Fritas Pequenas',       1.80),
(5, 'Batatas Fritas Grandes',        2.30),
(6, 'Chicken Nuggets (6 unidades)',  3.90),
(7, 'Refrigerante Cola 0.5L',        1.70),
(8, 'Gelado de Baunilha',            1.50),
(9, 'Veggie Burger',                 5.90),
(10,'Chicken Sandwich',              4.80),
(11,'Milkshake Morango',             3.50),
(12,'Sundae Chocolate',              2.00);

-- 7. Menus
INSERT INTO Menu (id, nome, preco) VALUES
(1, 'Menu Clássico',                   7.90),
(2, 'Menu Bacon & Cheese',             8.90),
(3, 'Menu Nuggets',                    7.50),
(4, 'Menu Infantil',                   5.50),
(5, 'Menu Veggie',                     8.50),
(6, 'Menu Frango',                     7.80);

-- 8. Catalogo -> Menu / Produto
INSERT INTO Catalogo_Menu (catalogo_id, menu_id) VALUES (1,1), (1,2), (1,3), (1,4), (1,5), (1,6);
INSERT INTO Catalogo_Produto (catalogo_id, produto_id) SELECT 1, id FROM Produto;

-- 9. Linhas de Menu
INSERT INTO LinhaMenu (menu_id, produto_id, quantidade) VALUES
(1, 1, 1), (1, 5, 1), (1, 7, 1), -- Classico: Burguer + Batata G + Cola
(2, 3, 1), (2, 5, 1), (2, 7, 1), -- Bacon: BaconBurguer + Batata G + Cola
(3, 6, 1), (3, 4, 1), (3, 7, 1), -- Nuggets: Nuggets + Batata P + Cola
(4, 2, 1), (4, 4, 1), (4, 8, 1), -- Infantil: Cheese + Batata P + Gelado
(5, 9, 1), (5, 5, 1), (5, 7, 1), -- Veggie: Veggie + Batata G + Cola
(6, 10,1), (6, 5, 1), (6, 7, 1); -- Frango: Sanduiche + Batata G + Cola

-- 10. Passos (Workflow: Preparação -> Montagem -> Caixa)
-- 1-10: Preparação, 11-15: Montagem, 16-20: Caixa
INSERT INTO Passo (id, nome, duracao_minutos, trabalho) VALUES
-- Cozinha Quente
(1, 'Grelhar Carne',        5, 'GRELHA'),
(2, 'Grelhar Veggie',       6, 'GRELHA'),
(3, 'Fritar Batatas',       4, 'FRITURA'),
(4, 'Fritar Nuggets',       5, 'FRITURA'),
(5, 'Fritar Frango',        5, 'FRITURA'),
-- Bar / Sobremesas
(6, 'Servir Bebida',        1, 'BEBIDAS'),
(7, 'Preparar Milkshake',   3, 'BEBIDAS'),
(8, 'Servir Gelado',        1, 'GELADOS'),
-- Montagem
(9, 'Montar Hambúrguer',    2, 'MONTAGEM'),
(10,'Montar Sanduíche',     2, 'MONTAGEM'),
(11,'Embalar Batatas',      1, 'MONTAGEM'),
(12,'Embalar Nuggets',      1, 'MONTAGEM'),
-- Entrega
(13,'Entrega ao Cliente',   1, 'CAIXA'); 

-- 11. Produto -> Passos
-- Hambúrgueres: Grelha -> Montagem -> Caixa
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES 
(1, 1), (1, 9), (1, 13), -- Classico
(2, 1), (2, 9), (2, 13), -- Cheese
(3, 1), (3, 9), (3, 13), -- Bacon
(9, 2), (9, 9), (9, 13), -- Veggie
(10,5), (10,10),(10,13); -- Frango Sanduiche

-- Acompanhamentos: Fritura -> Montagem -> Caixa
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES
(4, 3), (4, 11), (4, 13), -- Batata P
(5, 3), (5, 11), (5, 13), -- Batata G
(6, 4), (6, 12), (6, 13); -- Nuggets

-- Bebidas/Sobremesas: Preparação -> Caixa (Sem montagem)
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES
(7, 6), (7, 13), -- Cola
(11,7), (11,13), -- Milkshake
(8, 8), (8, 13), -- Gelado Baunilha
(12,8), (12,13); -- Sundae

-- 12. Passo -> Ingrediente (Exemplos principais)
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
(1, 2), -- Grelhar Carne: Carne
(2, 16), -- Grelhar Veggie: Veggie Burger
(3, 5), (3, 6), (3, 13), -- Fritar Batatas
(4, 14), (4, 6), -- Fritar Nuggets
(5, 14), (5, 6), -- Fritar Frango
(6, 10), (6, 12), -- Bebida: Cola + Copo
(7, 23), (7, 24), (7, 12), -- Milkshake: Leite + Xarope + Copo
(8, 11), (8, 12), -- Gelado: Baunilha + Copo
(9, 1), (9, 7), (9, 8), (9, 9), -- Montar Burguer: Pão, Alface, Tomate, Molho
(10,15), (10,7), (10,17); -- Montar Sanduiche: Brioche, Alface, Maionese

-- 13. Stock Inicial (Para os 5 restaurantes)
-- Inserir stock básico para todos os ingredientes (1-24) em todos os restaurantes (1-5)
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade)
SELECT r.id, i.id, 500 -- Quantidade generica inicial
FROM Restaurante r CROSS JOIN Ingrediente i;

-- 14. Mensagens Iniciais
INSERT INTO Mensagem (restaurante_id, texto) VALUES
(1, 'Bem-vindos ao novo sistema de Produção!'),
(1, 'Lembrar de verificar a temperatura das arcas.'),
(2, 'Promoção Menu Veggie ativa hoje.'),
(3, 'Stock de copos baixo, aguardar reposição.');