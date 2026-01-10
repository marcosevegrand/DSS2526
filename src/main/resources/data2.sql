-- ======================================================================================
-- POVOAMENTO DE DADOS V4 - HISTÓRICO DE OPERAÇÕES
-- Foco: Teste de Estatísticas e Cronologia
-- DSS 2025/2026 - Grupo TP-28
-- ======================================================================================

USE restaurante;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE Tarefa;
TRUNCATE TABLE LinhaPedido;
TRUNCATE TABLE Pagamento;
TRUNCATE TABLE Pedido;
TRUNCATE TABLE Mensagem;
SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================================================
-- 1. PEDIDOS HISTÓRICOS (SEMANA PASSADA - 22 a 27 Dezembro)
-- Simulação de volume estável para testar filtros "All Time"
-- ======================================================================================

-- Pedido #100: Restaurante 1, 22 Dezembro, Menu Burguer
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (100, 1, 0, 'ENTREGUE', '2025-12-22 12:30:00', '2025-12-22 12:48:00');
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (100, 1, 'MENU', 1, 11.50);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (100, 11.50, 'TERMINAL', 1, '2025-12-22 12:31:00');
-- Tarefas (Hamburguer + Bebida) - estacao_id=2 (Setor Quente) e estacao_id=3 (Bar)
INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
                                                                                                                        (1, 1, 100, 2, 'CONCLUIDA', '2025-12-22 12:30:00', '2025-12-22 12:32:00', '2025-12-22 12:40:00'),
                                                                                                                        (6, 6, 100, 3, 'CONCLUIDA', '2025-12-22 12:30:00', '2025-12-22 12:41:00', '2025-12-22 12:42:00');

-- Pedido #101: Restaurante 2, 23 Dezembro, Pizzas
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (101, 2, 1, 'ENTREGUE', '2025-12-23 19:15:00', '2025-12-23 19:40:00');
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (101, 2, 'PRODUTO', 2, 10.00);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (101, 20.00, 'CAIXA', 1, '2025-12-23 19:16:00');
-- Tarefas - estacao_id=5 (Grelha e Forno do Porto)
INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
                                                                                                                        (2, 2, 101, 5, 'CONCLUIDA', '2025-12-23 19:15:00', '2025-12-23 19:17:00', '2025-12-23 19:30:00'),
                                                                                                                        (2, 2, 101, 5, 'CONCLUIDA', '2025-12-23 19:15:00', '2025-12-23 19:20:00', '2025-12-23 19:35:00');

-- Pedido #102: Restaurante 1, 25 Dezembro (Natal), Vários itens
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (102, 1, 0, 'ENTREGUE', '2025-12-25 13:00:00', '2025-12-25 13:25:00');
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
                                                                                   (102, 5, 'PRODUTO', 2, 12.00),
                                                                                   (102, 11, 'PRODUTO', 2, 4.50);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (102, 33.00, 'TERMINAL', 1, '2025-12-25 13:01:00');
INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
                                                                                                                        (1, 5, 102, 2, 'CONCLUIDA', '2025-12-25 13:00:00', '2025-12-25 13:05:00', '2025-12-25 13:12:00'),
                                                                                                                        (1, 5, 102, 2, 'CONCLUIDA', '2025-12-25 13:00:00', '2025-12-25 13:08:00', '2025-12-25 13:15:00'),
                                                                                                                        (7, 11, 102, 3, 'CONCLUIDA', '2025-12-25 13:00:00', '2025-12-25 13:16:00', '2025-12-25 13:18:00'),
                                                                                                                        (7, 11, 102, 3, 'CONCLUIDA', '2025-12-25 13:00:00', '2025-12-25 13:18:00', '2025-12-25 13:20:00');

-- ======================================================================================
-- 2. OPERAÇÕES DE ONTEM (28 Dezembro)
-- Simulação de um dia de pico para testar médias de espera
-- ======================================================================================

-- Pedidos em série para o Restaurante 1
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao) VALUES
                                                                                              (200, 1, 0, 'ENTREGUE', '2025-12-28 12:00:00', '2025-12-28 12:15:00'),
                                                                                              (201, 1, 0, 'ENTREGUE', '2025-12-28 12:10:00', '2025-12-28 12:35:00'),
                                                                                              (202, 1, 0, 'ENTREGUE', '2025-12-28 12:20:00', '2025-12-28 12:30:00');

INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario) VALUES
                                                                                   (200, 1, 'PRODUTO', 1, 8.50),
                                                                                   (201, 2, 'PRODUTO', 2, 10.00),
                                                                                   (202, 12, 'PRODUTO', 3, 1.00);

INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento) VALUES
                                                                               (200, 8.50, 'TERMINAL', 1, '2025-12-28 12:00:10'),
                                                                               (201, 20.00, 'TERMINAL', 1, '2025-12-28 12:10:20'),
                                                                               (202, 3.00, 'TERMINAL', 1, '2025-12-28 12:20:15');

INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
                                                                                                                        (1, 1, 200, 2, 'CONCLUIDA', '2025-12-28 12:00:00', '2025-12-28 12:02:00', '2025-12-28 12:10:00'),
                                                                                                                        (2, 2, 201, 2, 'CONCLUIDA', '2025-12-28 12:10:00', '2025-12-28 12:15:00', '2025-12-28 12:25:00'),
                                                                                                                        (2, 2, 201, 2, 'CONCLUIDA', '2025-12-28 12:10:00', '2025-12-28 12:20:00', '2025-12-28 12:30:00'),
                                                                                                                        (5, 12, 202, 3, 'CONCLUIDA', '2025-12-28 12:20:00', '2025-12-28 12:22:00', '2025-12-28 12:23:00'),
                                                                                                                        (5, 12, 202, 3, 'CONCLUIDA', '2025-12-28 12:20:00', '2025-12-28 12:23:00', '2025-12-28 12:24:00'),
                                                                                                                        (5, 12, 202, 3, 'CONCLUIDA', '2025-12-28 12:20:00', '2025-12-28 12:24:00', '2025-12-28 12:25:00');

-- Pedido Cancelado em Faro (Restaurante 3)
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (203, 3, 0, 'CANCELADO', '2025-12-28 20:00:00', NULL);
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (203, 1, 'PRODUTO', 5, 8.50);

-- ======================================================================================
-- 3. OPERAÇÕES DE HOJE (29 Dezembro)
-- Pedidos em diversos estados para testar o Monitor Global e o Dashboard em tempo real
-- ======================================================================================

-- Pedido ENTREGUE hoje cedo
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (300, 1, 0, 'ENTREGUE', '2025-12-29 09:00:00', '2025-12-29 09:12:00');
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (300, 12, 'PRODUTO', 2, 1.00);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (300, 2.00, 'CAIXA', 1, '2025-12-29 09:01:00');
INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
                                                                                                                        (5, 12, 300, 3, 'CONCLUIDA', '2025-12-29 09:00:00', '2025-12-29 09:05:00', '2025-12-29 09:06:00'),
                                                                                                                        (5, 12, 300, 3, 'CONCLUIDA', '2025-12-29 09:00:00', '2025-12-29 09:06:00', '2025-12-29 09:07:00');

-- Pedido PRONTO (A aguardar levantamento)
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (301, 1, 1, 'PRONTO', '2025-12-29 11:45:00', NULL);
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (301, 3, 'PRODUTO', 2, 7.50);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (301, 15.00, 'TERMINAL', 1, '2025-12-29 11:45:30');
INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
                                                                                                                        (3, 3, 301, 2, 'CONCLUIDA', '2025-12-29 11:45:00', '2025-12-29 11:46:00', '2025-12-29 11:53:00'),
                                                                                                                        (3, 3, 301, 2, 'CONCLUIDA', '2025-12-29 11:45:00', '2025-12-29 11:50:00', '2025-12-29 11:57:00');

-- Pedido EM_PREPARAÇÃO (Com tarefas pendentes e em execução)
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (302, 1, 0, 'EM_PREPARACAO', '2025-12-29 12:10:00', NULL);
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (302, 1, 'PRODUTO', 2, 8.50);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (302, 17.00, 'TERMINAL', 1, '2025-12-29 12:10:30');
INSERT INTO Tarefa (id, passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
                                                                                                                            (500, 1, 1, 302, 2, 'EM_EXECUCAO', '2025-12-29 12:10:00', '2025-12-29 12:11:00', NULL),
                                                                                                                            (501, 1, 1, 302, NULL, 'PENDENTE', '2025-12-29 12:10:00', NULL, NULL);

-- Pedido AGUARDA_PAGAMENTO (Finalizado pelo cliente mas não pago na caixa)
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (303, 1, 0, 'AGUARDA_PAGAMENTO', '2025-12-29 12:15:00', NULL);
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (303, 14, 'PRODUTO', 1, 2.50);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (303, 2.50, 'CAIXA', 0, NULL);

-- Pedido ATRASADO (Tarefa marcada como atrasada por falta de stock)
INSERT INTO Pedido (id, restaurante_id, para_levar, estado, data_criacao, data_conclusao)
VALUES (304, 2, 0, 'EM_PREPARACAO', '2025-12-29 12:00:00', NULL);
INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario)
VALUES (304, 5, 'PRODUTO', 1, 12.00);
INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento)
VALUES (304, 12.00, 'TERMINAL', 1, '2025-12-29 12:00:30');
INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES
    (1, 5, 304, 5, 'ATRASADA', '2025-12-29 12:00:00', '2025-12-29 12:05:00', NULL);

-- ======================================================================================
-- 4. MENSAGENS DE SISTEMA RECENTES
-- ======================================================================================
INSERT INTO Mensagem (restaurante_id, texto, data_hora) VALUES
                                                            (1, '[SISTEMA] Volume de pedidos elevado detectado.', '2025-12-29 12:05:00'),
                                                            (1, '[SISTEMA] Bem-vindos à rede Sabor e Tradição!', '2025-12-29 08:00:00'),
                                                            (2, 'ALERTA COZINHA: Falta de Carne Vaca 150g para o Pedido #304 (Tarefa atrasada)', '2025-12-29 12:06:00'),
                                                            (2, '[SISTEMA] Bem-vindos à rede Sabor e Tradição!', '2025-12-29 08:00:00'),
                                                            (3, '[SISTEMA] Bem-vindos à rede Sabor e Tradição!', '2025-12-29 08:00:00');

-- FIM DO SCRIPT