-- =================================================================================
-- SCRIPT DE POVOAMENTO (MySQL Compatible)
-- =================================================================================

-- Disable FK checks temporarily for bulk insertion safety
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Catalogo
INSERT INTO Catalogo (nome) VALUES ('Geral');

-- 2. Restaurante
INSERT INTO Restaurante (nome, localizacao, catalogo_id) VALUES ('Sabor Latino', 'Lisboa', 1);
INSERT INTO Restaurante (nome, localizacao, catalogo_id) VALUES ('Douro Vinhateiro', 'Porto', 1);

-- 3. Funcionario
-- Restaurante 1 (Lisboa)
INSERT INTO Funcionario (restaurante_id, utilizador, password, funcao) VALUES (1, 'joao.silva', 'pass123', 'GESTOR');
INSERT INTO Funcionario (restaurante_id, utilizador, password, funcao) VALUES (1, 'maria.santos', 'pass123', 'ATENDIMENTO');
INSERT INTO Funcionario (restaurante_id, utilizador, password, funcao) VALUES (1, 'carlos.pereira', 'pass123', 'COZINHEIRO');

-- Restaurante 2 (Porto)
INSERT INTO Funcionario (restaurante_id, utilizador, password, funcao) VALUES (2, 'ana.costa', 'pass123', 'GESTOR');
INSERT INTO Funcionario (restaurante_id, utilizador, password, funcao) VALUES (2, 'pedro.almeida', 'pass123', 'ATENDIMENTO');
INSERT INTO Funcionario (restaurante_id, utilizador, password, funcao) VALUES (2, 'sofia.martins', 'pass123', 'COZINHEIRO');

-- 4. Estacao
-- Restaurante 1
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (1, 'PREPARACAO');
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (1, 'COZEDURA');
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (1, 'EMPRATAMENTO');

-- Restaurante 2
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (2, 'PREPARACAO');
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (2, 'COZEDURA');
INSERT INTO Estacao (restaurante_id, trabalho) VALUES (2, 'EMPRATAMENTO');

-- 5. Ingrediente
INSERT INTO Ingrediente (nome, unidade, alergenico) VALUES ('Carne de Vaca', 'kg', 'Nenhum');
INSERT INTO Ingrediente (nome, unidade, alergenico) VALUES ('Batata', 'kg', 'Nenhum');
INSERT INTO Ingrediente (nome, unidade, alergenico) VALUES ('Arroz', 'kg', 'Nenhum');
INSERT INTO Ingrediente (nome, unidade, alergenico) VALUES ('Ovo', 'un', 'Ovo');
INSERT INTO Ingrediente (nome, unidade, alergenico) VALUES ('Alface', 'un', 'Nenhum');
INSERT INTO Ingrediente (nome, unidade, alergenico) VALUES ('Tomate', 'kg', 'Nenhum');

-- 6. Produto
INSERT INTO Produto (nome, preco) VALUES ('Bitoque', 12.50);
INSERT INTO Produto (nome, preco) VALUES ('Salada Mista', 5.00);
INSERT INTO Produto (nome, preco) VALUES ('Refrigerante', 2.00);

-- 7. Associar Produtos ao Catalogo
INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES (1, 1);
INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES (1, 2);
INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES (1, 3);

-- 8. Passo
-- Bitoque
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Grelhar Bife', 10, 'COZEDURA');
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Fritar Batata', 15, 'COZEDURA');
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Cozer Arroz', 15, 'COZEDURA');
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Estrelar Ovo', 3, 'COZEDURA');
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Empratar Bitoque', 2, 'EMPRATAMENTO');
-- Salada
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Lavar Legumes', 5, 'PREPARACAO');
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Cortar Legumes', 5, 'PREPARACAO');
INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES ('Temperar Salada', 1, 'PREPARACAO');

-- 9. Produto_Passo
-- Bitoque (ID 1)
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (1, 1);
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (1, 2);
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (1, 3);
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (1, 4);
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (1, 5);
-- Salada (ID 2)
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (2, 6);
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (2, 7);
INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (2, 8);

-- 10. LinhaProduto (Receita)
-- Bitoque (ID 1)
INSERT INTO LinhaProduto (produto_id, ingrediente_id, quantidade) VALUES (1, 1, 0.2);
INSERT INTO LinhaProduto (produto_id, ingrediente_id, quantidade) VALUES (1, 2, 0.2);
INSERT INTO LinhaProduto (produto_id, ingrediente_id, quantidade) VALUES (1, 3, 0.1);
INSERT INTO LinhaProduto (produto_id, ingrediente_id, quantidade) VALUES (1, 4, 1);
-- Salada (ID 2)
INSERT INTO LinhaProduto (produto_id, ingrediente_id, quantidade) VALUES (2, 5, 0.5);
INSERT INTO LinhaProduto (produto_id, ingrediente_id, quantidade) VALUES (2, 6, 0.2);

-- 11. Menu
INSERT INTO Menu (nome, preco) VALUES ('Menu Executivo', 14.00);

-- 12. Associar Menu ao Catalogo
INSERT INTO Catalogo_Menu (catalogo_id, menu_id) VALUES (1, 1);

-- 13. LinhaMenu (Composicao)
INSERT INTO LinhaMenu (menu_id, produto_id, quantidade) VALUES (1, 1, 1);
INSERT INTO LinhaMenu (menu_id, produto_id, quantidade) VALUES (1, 3, 1);

-- 14. LinhaStock
-- Lisboa
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (1, 1, 50);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (1, 2, 100);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (1, 3, 50);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (1, 4, 200);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (1, 5, 20);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (1, 6, 20);
-- Porto
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (2, 1, 40);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (2, 2, 80);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (2, 3, 40);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (2, 4, 150);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (2, 5, 15);
INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (2, 6, 15);

-- Re-enable FK checks
SET FOREIGN_KEY_CHECKS = 1;