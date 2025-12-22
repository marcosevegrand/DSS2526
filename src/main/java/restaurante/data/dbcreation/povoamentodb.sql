USE restaurante_db;

-- Utilizadores
INSERT INTO Utilizador (username, password_hash, tipo) VALUES
  ('gerente1',    '1234', 'GERENTE'),
  ('func_grelha', '1234', 'FUNCIONARIO'),
  ('func_entrega','1234', 'FUNCIONARIO'),
  ('func_gelados','1234', 'FUNCIONARIO'),
  ('cliente1',    '1234', 'CLIENTE'),
  ('cliente2',    '1234', 'CLIENTE');

-- Cadeia e restaurante
INSERT INTO CadeiaRestaurante (nome) VALUES ('McChain Portugal');

INSERT INTO Restaurante (nome, morada, cadeia_id) VALUES
  ('McDonalds Centro',  'Rua Principal 100', 1),
  ('McDonalds Shopping','Av. Comercial 50', 1);

-- Estações de trabalho
INSERT INTO EstacaoT (tipo, restaurante_id) VALUES
  ('GRELHA',  1),
  ('ENTREGA', 1),
  ('GELADOS', 1),
  ('GRELHA',  2),
  ('ENTREGA', 2),
  ('GELADOS', 2);

-- Terminais de venda (caixas)
INSERT INTO TerminalVenda (nome, restaurante_id, estacao_id) VALUES
  ('Caixa 1 Centro', 1, 2),  -- associada à estação ENTREGA
  ('Caixa 2 Centro', 1, 2),
  ('Caixa 1 Shop',   2, 5);

-- Terminais de produção (ecrãs/cozinha)
INSERT INTO TerminalProducao (nome, restaurante_id, estacao_id) VALUES
  ('TV Grelha Centro',   1, 1),
  ('TV Entrega Centro',  1, 2),
  ('TV Gelados Centro',  1, 3),
  ('TV Grelha Shop',     2, 4),
  ('TV Entrega Shop',    2, 5),
  ('TV Gelados Shop',    2, 6);

-- Funcionários ligados às estações
INSERT INTO Funcionario (nome, cargo, restaurante_id, utilizador_id) VALUES
  ('Ana Gerente',      'GERENTE',    1, 1),
  ('Bruno Grelha',     'GRELHA',     1, 2),
  ('Carla Entrega',    'ENTREGA',    1, 3),
  ('Diana Gelados',    'GELADOS',    1, 4);

-- Produtos (hambúrgueres)
INSERT INTO Produto (nome, preco, tipo, descricao) VALUES
  ('Big Mac',          4.00, 'BURGER', 'Dois hambúrgueres e molho especial'),
  ('McChicken',        3.80, 'BURGER', 'Frango panado'),
  ('Cheeseburger',     1.80, 'BURGER', 'Hambúrguer com queijo'),
  ('McVeggie',         3.90, 'BURGER', 'Hambúrguer vegetariano');

-- Acompanhamentos (batatas)
INSERT INTO Produto (nome, preco, tipo, descricao) VALUES
  ('Batata Pequena',   1.50, 'ACOMPANHAMENTO', 'Batata frita pequena'),
  ('Batata Média',     1.80, 'ACOMPANHAMENTO', 'Batata frita média'),
  ('Batata Grande',    2.10, 'ACOMPANHAMENTO', 'Batata frita grande');

-- Bebidas
INSERT INTO Produto (nome, preco, tipo, descricao) VALUES
  ('Coca-Cola 33cl',   1.50, 'BEBIDA', 'Refrigerante'),
  ('Fanta 33cl',       1.50, 'BEBIDA', 'Refrigerante'),
  ('Água 33cl',        1.20, 'BEBIDA', 'Água mineral');

-- Sobremesas / gelados
INSERT INTO Produto (nome, preco, tipo, descricao) VALUES
  ('McFlurry Oreo',    2.50, 'SOBREMESA', 'Gelado com Oreo'),
  ('Sundae Caramelo',  1.80, 'SOBREMESA', 'Gelado com caramelo'),
  ('Cone Simples',     1.00, 'SOBREMESA', 'Cone de gelado');

-- Menus (assumindo IDs de produtos na ordem inserida)
-- Hamburgueres: 1..4, Acompanhamentos: 5..7, Bebidas: 8..10
INSERT INTO Menu (nome, preco, burger_id, acompanhamento_id, bebida_id) VALUES
  ('McMenu Big Mac',      6.50, 1, 6, 8),  -- Big Mac + Batata Média + Coca
  ('McMenu McChicken',    6.30, 2, 6, 8),
  ('McMenu Cheeseburger', 5.50, 3, 5, 8),
  ('McMenu McVeggie',     6.20, 4, 6, 9);

-- Pedidos de exemplo
INSERT INTO Pedido (data, estado, precoTotal, terminal_venda_id, cliente_id) VALUES
  (NOW(), 'EM_PREPARACAO',  0,    1, 5),  -- cliente1
  (NOW(), 'PRONTO',        25.20, 1, 6),  -- cliente2
  (NOW(), 'ENTREGUE',      15.30, 2, 5);

-- Itens ligados às estações de produção
-- Pedido 1: McMenu Big Mac + McFlurry
INSERT INTO ItemPedido (pedido_id, produto_id, menu_id, quantidade, preco, estacao_id) VALUES
  (1, NULL, 1, 1, 6.50, 1),  -- menu vai para GRELHA (preparar burger + fritar batata)
  (1, 11,  NULL, 1, 2.50, 3);-- McFlurry na estação GELADOS

-- Pedido 2: McMenu McChicken + cone simples
INSERT INTO ItemPedido (pedido_id, produto_id, menu_id, quantidade, preco, estacao_id) VALUES
  (2, NULL, 2, 1, 6.30, 1),
  (2, 13,  NULL, 2, 2.00, 3);

-- Pedido 3: apenas menus
INSERT INTO ItemPedido (pedido_id, produto_id, menu_id, quantidade, preco, estacao_id) VALUES
  (3, NULL, 3, 1, 5.50, 1),
  (3, NULL, 4, 1, 6.20, 1);

-- Talões
INSERT INTO Talao (numero, data, conteudo, terminal_venda_id) VALUES
  (1001, NOW(), 'Pedido 2 pronto para levantamento', 1),
  (1002, NOW(), 'Pedido 3 entregue',                  2);
