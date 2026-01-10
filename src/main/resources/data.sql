-- ======================================================================================
-- POVOAMENTO DE DADOS V3
-- DSS 2025/2026 - Grupo TP-28
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
TRUNCATE TABLE Estacao_Trabalho;
TRUNCATE TABLE Passo;
TRUNCATE TABLE Mensagem;
TRUNCATE TABLE Pagamento;
TRUNCATE TABLE Pedido;
TRUNCATE TABLE Menu;
TRUNCATE TABLE Produto;
TRUNCATE TABLE Ingrediente;
TRUNCATE TABLE Estacao;
TRUNCATE TABLE Funcionario;
TRUNCATE TABLE Catalogo;
TRUNCATE TABLE Restaurante;
SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================================================
-- 1. CATÁLOGOS
-- ======================================================================================
INSERT INTO Catalogo (id, nome) VALUES
                                    (1, 'Menu Completo Restaurante'),
                                    (2, 'Menu Sem Sobremesas Geladas');

-- ======================================================================================
-- 2. RESTAURANTES
-- ======================================================================================
INSERT INTO Restaurante (id, nome, localizacao, catalogo_id) VALUES
                                                                 (1, 'Sabor e Tradição - Lisboa', 'Lisboa Centro', 1),
                                                                 (2, 'Sabor e Tradição - Porto', 'Porto Boavista', 1),
                                                                 (3, 'Sabor e Tradição - Faro', 'Faro Baixa', 2);

-- ======================================================================================
-- 3. FUNCIONÁRIOS (1 COO, 1 Gerente/Rest, 4 Func/Rest)
-- ======================================================================================
INSERT INTO Funcionario (id, restaurante_id, utilizador, password, funcao) VALUES
-- Administração Central
(1, NULL, 'marco.coo', 'pass', 'COO'),
-- Restaurante 1 (Lisboa)
(2, 1, 'gerente.lisboa', 'pass', 'GERENTE'),
(3, 1, 'ana.lisboa', 'pass', 'FUNCIONARIO'),
(4, 1, 'pedro.lisboa', 'pass', 'FUNCIONARIO'),
(5, 1, 'rita.lisboa', 'pass', 'FUNCIONARIO'),
(6, 1, 'joao.lisboa', 'pass', 'FUNCIONARIO'),
-- Restaurante 2 (Porto)
(7, 2, 'gerente.porto', 'pass', 'GERENTE'),
(8, 2, 'bruno.porto', 'pass', 'FUNCIONARIO'),
(9, 2, 'carla.porto', 'pass', 'FUNCIONARIO'),
(10, 2, 'diogo.porto', 'pass', 'FUNCIONARIO'),
(11, 2, 'elisa.porto', 'pass', 'FUNCIONARIO'),
-- Restaurante 3 (Faro)
(12, 3, 'gerente.faro', 'pass', 'GERENTE'),
(13, 3, 'filipe.faro', 'pass', 'FUNCIONARIO'),
(14, 3, 'guilherme.faro', 'pass', 'FUNCIONARIO'),
(15, 3, 'helena.faro', 'pass', 'FUNCIONARIO'),
(16, 3, 'ines.faro', 'pass', 'FUNCIONARIO');

-- ======================================================================================
-- 4. ESTAÇÕES DE TRABALHO
-- ======================================================================================
INSERT INTO Estacao (id, restaurante_id, nome, tipo) VALUES
-- Restaurante 1 (Lisboa)
(1, 1, 'Caixa Central', 'CAIXA'),
(2, 1, 'Setor Quente', 'COZINHA'),
(3, 1, 'Setor Frio e Bar', 'COZINHA'),
-- Restaurante 2 (Porto)
(4, 2, 'Balcão de Pagamento', 'CAIXA'),
(5, 2, 'Grelha e Forno', 'COZINHA'),
(6, 2, 'Fritura e Prep. Fria', 'COZINHA'),
-- Restaurante 3 (Faro - Simplificado)
(7, 3, 'Caixa e Entrega', 'CAIXA'),
(8, 3, 'Cozinha Geral', 'COZINHA');

-- ======================================================================================
-- 5. ESPECIALIDADES DE TRABALHO (ESTACAO_TRABALHO)
-- ======================================================================================
INSERT INTO Estacao_Trabalho (estacao_id, trabalho) VALUES
-- Restaurante 1 (Lisboa)
(2, 'GRELHA'), (2, 'FRITURA'), (2, 'FORNO'),
(3, 'SALADAS'), (3, 'BEBIDAS'), (3, 'GELADOS'), (3, 'PASTELARIA'), (3, 'CAFETARIA'),
-- Restaurante 2 (Porto)
(5, 'GRELHA'), (5, 'FORNO'), (5, 'PASTELARIA'),
(6, 'FRITURA'), (6, 'SALADAS'), (6, 'BEBIDAS'), (6, 'CAFETARIA'), (6, 'GELADOS'),
-- Restaurante 3 (Faro - Faz tudo exceto gelados conforme catálogo)
(8, 'GRELHA'), (8, 'FRITURA'), (8, 'FORNO'), (8, 'SALADAS'),
(8, 'BEBIDAS'), (8, 'PASTELARIA'), (8, 'CAFETARIA');

-- ======================================================================================
-- 6. INGREDIENTES
-- ======================================================================================
INSERT INTO Ingrediente (id, nome, unidade, alergenico) VALUES
                                                            (1, 'Pão de Hambúrguer', 'un', 'GLUTEN'),
                                                            (2, 'Carne Vaca 150g', 'un', NULL),
                                                            (3, 'Massa de Pizza', 'un', 'GLUTEN'),
                                                            (4, 'Queijo Mozzarella', 'kg', 'LACTOSE'),
                                                            (5, 'Batatas', 'kg', NULL),
                                                            (6, 'Mix de Alfaces', 'kg', NULL),
                                                            (7, 'Peito de Frango', 'un', NULL),
                                                            (8, 'Café Grão', 'kg', NULL),
                                                            (9, 'Cerveja 0.33L', 'un', 'GLUTEN'),
                                                            (10, 'Leite', 'L', 'LACTOSE'),
                                                            (11, 'Base Gelado Baunilha', 'kg', 'LACTOSE'),
                                                            (12, 'Ovos', 'un', 'OVOS'),
                                                            (13, 'Farinha', 'kg', 'GLUTEN'),
                                                            (14, 'Água Mineral', 'un', NULL);

-- ======================================================================================
-- 7. PRODUTOS
-- ======================================================================================
INSERT INTO Produto (id, nome, preco) VALUES
-- 5 Principais
(1, 'Hambúrguer de Assinatura', 8.50),  -- GRELHA
(2, 'Pizza Margherita', 10.00),         -- FORNO
(3, 'Frango Frito Crocante', 7.50),     -- FRITURA
(4, 'Salada Caesar', 9.00),             -- SALADAS
(5, 'Bife na Grelha', 12.00),           -- GRELHA
-- 3 Bebidas
(6, 'Cerveja da Casa', 2.50),           -- BEBIDAS
(7, 'Sumo de Laranja Natural', 3.00),   -- BEBIDAS
(8, 'Água das Pedras', 1.50),           -- BEBIDAS
-- 3 Gelados
(9, 'Copa de Baunilha', 4.00),          -- GELADOS
(10, 'Gelado de Chocolate', 4.00),      -- GELADOS
(11, 'Sorvete de Frutos Vermelhos', 4.50), -- GELADOS
-- 3 Outros
(12, 'Espresso Gourmet', 1.00),         -- CAFETARIA
(13, 'Fatia de Bolo de Chocolate', 3.50), -- PASTELARIA
(14, 'Pão de Alho no Forno', 2.50);     -- FORNO

-- ======================================================================================
-- 8. MENUS
-- ======================================================================================
INSERT INTO Menu (id, nome, preco) VALUES
                                       (1, 'Menu Burguer (Burguer + Batata + Bebida)', 11.50),
                                       (2, 'Menu Italiano (Pizza + Bebida + Café)', 12.50),
                                       (3, 'Menu Fit (Salada + Água + Fruta)', 10.00);

-- ======================================================================================
-- 9. CATÁLOGO -> PRODUTOS (Catálogo 2 exclui gelados: 9, 10, 11)
-- ======================================================================================
INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES
                                                           (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),
                                                           (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,12),(2,13),(2,14);

-- ======================================================================================
-- 10. CATÁLOGO -> MENUS
-- ======================================================================================
INSERT INTO Catalogo_Menu (catalogo_id, menu_id) VALUES
                                                     (1,1),(1,2),(1,3),
                                                     (2,1),(2,2),(2,3);

-- ======================================================================================
-- 11. LINHAS DE MENU (Produtos que compõem cada menu)
-- ======================================================================================
INSERT INTO LinhaMenu (menu_id, produto_id, quantidade) VALUES
                                                            (1, 1, 1),  -- Menu Burguer: 1x Hambúrguer
                                                            (1, 6, 1),  -- Menu Burguer: 1x Cerveja
                                                            (2, 2, 1),  -- Menu Italiano: 1x Pizza
                                                            (2, 6, 1),  -- Menu Italiano: 1x Cerveja
                                                            (2, 12, 1), -- Menu Italiano: 1x Café
                                                            (3, 4, 1),  -- Menu Fit: 1x Salada
                                                            (3, 8, 1);  -- Menu Fit: 1x Água

-- ======================================================================================
-- 12. PASSOS DE CONFEÇÃO
-- ======================================================================================
INSERT INTO Passo (id, nome, duracao_minutos, trabalho) VALUES
                                                            (1, 'Grelhar Carne', 6, 'GRELHA'),
                                                            (2, 'Preparar Massa e Forno', 8, 'FORNO'),
                                                            (3, 'Fritar Peças de Frango', 7, 'FRITURA'),
                                                            (4, 'Lavar e Montar Salada', 4, 'SALADAS'),
                                                            (5, 'Extrair Café', 1, 'CAFETARIA'),
                                                            (6, 'Servir Bebida Fria', 1, 'BEBIDAS'),
                                                            (7, 'Preparar Taça Gelada', 2, 'GELADOS'),
                                                            (8, 'Preparar Massa Pastelaria', 15, 'PASTELARIA');

-- ======================================================================================
-- 13. PRODUTO -> PASSOS (Associação entre produtos e passos de confeção)
-- ======================================================================================
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES
                                                     (1, 1),   -- Hambúrguer -> Grelhar
                                                     (2, 2),   -- Pizza -> Forno
                                                     (3, 3),   -- Frango Frito -> Fritura
                                                     (4, 4),   -- Salada -> Preparar Salada
                                                     (5, 1),   -- Bife -> Grelhar
                                                     (6, 6),   -- Cerveja -> Servir Bebida
                                                     (7, 6),   -- Sumo -> Servir Bebida
                                                     (8, 6),   -- Água -> Servir Bebida
                                                     (9, 7),   -- Copa Baunilha -> Preparar Gelado
                                                     (10, 7),  -- Gelado Chocolate -> Preparar Gelado
                                                     (11, 7),  -- Sorvete -> Preparar Gelado
                                                     (12, 5),  -- Café -> Extrair Café
                                                     (13, 8),  -- Bolo -> Preparar Pastelaria
                                                     (14, 2);  -- Pão Alho -> Forno

-- ======================================================================================
-- 14. PASSO -> INGREDIENTE (Ingredientes necessários por passo)
-- ======================================================================================
INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES
                                                             (1, 2),   -- Grelhar: Carne
                                                             (1, 1),   -- Grelhar: Pão
                                                             (2, 3),   -- Forno: Massa Pizza
                                                             (2, 4),   -- Forno: Queijo
                                                             (3, 7),   -- Fritura: Frango
                                                             (4, 6),   -- Salada: Mix Alfaces
                                                             (5, 8),   -- Café: Café Grão
                                                             (6, 9),   -- Bebida: Cerveja
                                                             (7, 11),  -- Gelado: Base Gelado
                                                             (8, 13),  -- Pastelaria: Farinha
                                                             (8, 12);  -- Pastelaria: Ovos

-- ======================================================================================
-- 15. STOCK INICIAL (Quantidade significativa para todos os restaurantes)
-- ======================================================================================
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade)
SELECT r.id, i.id, 5000 FROM Restaurante r CROSS JOIN Ingrediente i;

-- ======================================================================================
-- 16. MENSAGENS DE BOAS-VINDAS
-- ======================================================================================
INSERT INTO Mensagem (restaurante_id, texto, data_hora) VALUES
                                                            (1, '[SISTEMA] Bem-vindos à rede Sabor e Tradição! Bom trabalho a todos.', CURRENT_TIMESTAMP),
                                                            (2, '[SISTEMA] Bem-vindos à rede Sabor e Tradição! Bom trabalho a todos.', CURRENT_TIMESTAMP),
                                                            (3, '[SISTEMA] Bem-vindos à rede Sabor e Tradição! Bom trabalho a todos.', CURRENT_TIMESTAMP);