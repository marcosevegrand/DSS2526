-- =================================================================================
-- SCRIPT DE POVOAMENTO DA BASE DE DADOS (DUMMY DATA)
-- =================================================================================
USE restaurante;

-- 1. Restaurantes
INSERT INTO Restaurante (Nome, Localizacao) VALUES ('Sabor Latino', 'Lisboa');
INSERT INTO Restaurante (Nome, Localizacao) VALUES ('Douro Vinhateiro', 'Porto');

-- 2. Catalogos
INSERT INTO Catalogos DEFAULT VALUES;

-- 3. Funcionarios
-- Restaurante 1 (Lisboa)
INSERT INTO Funcionarios (Restaurante_ID, Utilizador, Password, Funcao) VALUES (1, 'joao.silva', 'pass123', 'GESTOR');
INSERT INTO Funcionarios (Restaurante_ID, Utilizador, Password, Funcao) VALUES (1, 'maria.santos', 'pass123', 'ATENDIMENTO');
INSERT INTO Funcionarios (Restaurante_ID, Utilizador, Password, Funcao) VALUES (1, 'carlos.pereira', 'pass123', 'COZINHEIRO');

-- Restaurante 2 (Porto)
INSERT INTO Funcionarios (Restaurante_ID, Utilizador, Password, Funcao) VALUES (2, 'ana.costa', 'pass123', 'GESTOR');
INSERT INTO Funcionarios (Restaurante_ID, Utilizador, Password, Funcao) VALUES (2, 'pedro.almeida', 'pass123', 'ATENDIMENTO');
INSERT INTO Funcionarios (Restaurante_ID, Utilizador, Password, Funcao) VALUES (2, 'sofia.martins', 'pass123', 'COZINHEIRO');

-- 4. Estacoes (Tipos de Trabalho: PREPARACAO, COZEDURA, EMPRATAMENTO)
-- Restaurante 1
INSERT INTO Estacoes (Restaurante_ID, Trabalho) VALUES (1, 'PREPARACAO');
INSERT INTO Estacoes (Restaurante_ID, Trabalho) VALUES (1, 'COZEDURA');
INSERT INTO Estacoes (Restaurante_ID, Trabalho) VALUES (1, 'EMPRATAMENTO');

-- Restaurante 2
INSERT INTO Estacoes (Restaurante_ID, Trabalho) VALUES (2, 'PREPARACAO');
INSERT INTO Estacoes (Restaurante_ID, Trabalho) VALUES (2, 'COZEDURA');
INSERT INTO Estacoes (Restaurante_ID, Trabalho) VALUES (2, 'EMPRATAMENTO');

-- 5. Ingredientes
INSERT INTO Ingredientes (Nome, Unidade, Alergenico) VALUES ('Carne de Vaca', 'kg', 'Nenhum');
INSERT INTO Ingredientes (Nome, Unidade, Alergenico) VALUES ('Batata', 'kg', 'Nenhum');
INSERT INTO Ingredientes (Nome, Unidade, Alergenico) VALUES ('Arroz', 'kg', 'Nenhum');
INSERT INTO Ingredientes (Nome, Unidade, Alergenico) VALUES ('Ovo', 'un', 'Ovo');
INSERT INTO Ingredientes (Nome, Unidade, Alergenico) VALUES ('Alface', 'un', 'Nenhum');
INSERT INTO Ingredientes (Nome, Unidade, Alergenico) VALUES ('Tomate', 'kg', 'Nenhum');

-- 6. Produtos
INSERT INTO Produtos (Nome, Preco) VALUES ('Bitoque', 12.50);
INSERT INTO Produtos (Nome, Preco) VALUES ('Salada Mista', 5.00);
INSERT INTO Produtos (Nome, Preco) VALUES ('Refrigerante', 2.00);

-- 7. Passos (Tarefas associadas a trabalhos específicos)
-- Passos para o Bitoque
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Grelhar Bife', 600, 'COZEDURA');       -- 10 min
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Fritar Batata', 900, 'COZEDURA');      -- 15 min
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Cozer Arroz', 900, 'COZEDURA');        -- 15 min
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Estrelar Ovo', 180, 'COZEDURA');       -- 3 min
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Empratar Bitoque', 120, 'EMPRATAMENTO'); -- 2 min
-- Passos para a Salada
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Lavar Legumes', 300, 'PREPARACAO');    -- 5 min
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Cortar Legumes', 300, 'PREPARACAO');   -- 5 min
INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES ('Temperar Salada', 60, 'PREPARACAO');   -- 1 min

-- 8. Associação Produto -> Passos (Produto_Passo)
-- Bitoque (Produto ID 1) -> Passos IDs 1 a 5
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (1, 1, 1);
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (1, 2, 2);
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (1, 3, 3);
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (1, 4, 4);
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (1, 5, 5);

-- Salada (Produto ID 2) -> Passos IDs 6 a 8
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (2, 6, 1);
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (2, 7, 2);
INSERT INTO Produto_Passo (Produto_ID, Passo_ID, Ordem) VALUES (2, 8, 3);

-- 9. Receita (LinhaProduto - Ingredientes do Produto)
-- Bitoque (ID 1)
INSERT INTO LinhaProduto (Produto_ID, Ingrediente_ID, Quantidade) VALUES (1, 1, 0.2); -- 200g Carne
INSERT INTO LinhaProduto (Produto_ID, Ingrediente_ID, Quantidade) VALUES (1, 2, 0.2); -- 200g Batata
INSERT INTO LinhaProduto (Produto_ID, Ingrediente_ID, Quantidade) VALUES (1, 3, 0.1); -- 100g Arroz
INSERT INTO LinhaProduto (Produto_ID, Ingrediente_ID, Quantidade) VALUES (1, 4, 1);   -- 1 Ovo

-- Salada (ID 2)
INSERT INTO LinhaProduto (Produto_ID, Ingrediente_ID, Quantidade) VALUES (2, 5, 0.5); -- 0.5 Alface
INSERT INTO LinhaProduto (Produto_ID, Ingrediente_ID, Quantidade) VALUES (2, 6, 0.2); -- 0.2 Tomate

-- 10. Menus
INSERT INTO Menus (Nome, Preco) VALUES ('Menu Executivo', 14.00);

-- 11. LinhaMenu (Composição do Menu)
-- Menu Executivo (ID 1) contém Bitoque (ID 1) e Refrigerante (ID 3)
INSERT INTO LinhaMenu (Menu_ID, Produto_ID, Quantidade) VALUES (1, 1, 1);
INSERT INTO LinhaMenu (Menu_ID, Produto_ID, Quantidade) VALUES (1, 3, 1);

-- 12. Stock (LinhaStock)
-- Stock para Restaurante 1 (Lisboa)
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (1, 1, 50);  -- 50kg Carne
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (1, 2, 100); -- 100kg Batata
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (1, 3, 50);
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (1, 4, 200); -- 200 Ovos
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (1, 5, 20);
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (1, 6, 20);

-- Stock para Restaurante 2 (Porto)
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (2, 1, 40);
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (2, 2, 80);
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (2, 3, 40);
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (2, 4, 150);
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (2, 5, 15);
INSERT INTO LinhaStock (Restaurante_ID, Ingrediente_ID, Quantidade) VALUES (2, 6, 15);