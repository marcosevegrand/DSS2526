-- Utilizadores
SELECT * FROM Utilizador;

-- Estações por restaurante
SELECT e.id, e.tipo, r.nome
FROM EstacaoT e JOIN Restaurante r ON e.restaurante_id = r.id
ORDER BY r.id, e.tipo;

-- Menus detalhados
SELECT m.nome AS menu,
       pb.nome AS burger,
       pa.nome AS batata,
       b.nome  AS bebida,
       m.preco
FROM Menu m
JOIN Produto pb ON m.burger_id = pb.id
JOIN Produto pa ON m.acompanhamento_id = pa.id
JOIN Produto b  ON m.bebida_id = b.id;

-- Itens por estação (para veres a “grelha / entrega / gelados”)
SELECT p.id AS pedido, ip.id AS item,
       pr.nome AS produto, m.nome AS menu,
       e.tipo AS estacao, ip.quantidade, ip.preco
FROM ItemPedido ip
JOIN Pedido p       ON ip.pedido_id = p.id
LEFT JOIN Produto pr ON ip.produto_id = pr.id
LEFT JOIN Menu m     ON ip.menu_id = m.id
LEFT JOIN EstacaoT e ON ip.estacao_id = e.id
ORDER BY p.id, ip.id;
