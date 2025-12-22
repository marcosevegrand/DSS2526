USE restaurante_db;

CREATE TABLE Utilizador (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  tipo          ENUM('GERENTE','FUNCIONARIO','CLIENTE') NOT NULL
);

CREATE TABLE CadeiaRestaurante (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL
);

CREATE TABLE Restaurante (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  nome      VARCHAR(100) NOT NULL,
  morada    VARCHAR(255),
  cadeia_id INT,
  FOREIGN KEY (cadeia_id) REFERENCES CadeiaRestaurante(id)
);

CREATE TABLE Funcionario (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(100) NOT NULL,
  cargo         VARCHAR(50),
  restaurante_id INT NOT NULL,
  utilizador_id INT NOT NULL,
  FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id),
  FOREIGN KEY (utilizador_id) REFERENCES Utilizador(id)
);

-- Estações de trabalho: grelha, entrega, máquina de gelados
CREATE TABLE EstacaoT (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  tipo          ENUM('GRELHA','ENTREGA','GELADOS') NOT NULL,
  restaurante_id INT NOT NULL,
  FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id)
);

-- Terminais (caixas, ecrãs de produção)
CREATE TABLE TerminalVenda (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(50) NOT NULL,
  restaurante_id INT NOT NULL,
  estacao_id    INT,
  FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id),
  FOREIGN KEY (estacao_id)    REFERENCES EstacaoT(id)
);

CREATE TABLE TerminalProducao (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(50) NOT NULL,
  restaurante_id INT NOT NULL,
  estacao_id    INT NOT NULL,
  FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id),
  FOREIGN KEY (estacao_id)    REFERENCES EstacaoT(id)
);

-- Produtos individuais
CREATE TABLE Produto (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  nome      VARCHAR(100) NOT NULL,
  preco     DECIMAL(10,2) NOT NULL,
  tipo      ENUM('BURGER','ACOMPANHAMENTO','BEBIDA','SOBREMESA') NOT NULL,
  descricao VARCHAR(255)
);

-- Menus McDonald's (hambúrguer + batata + bebida)
CREATE TABLE Menu (
  id                 INT AUTO_INCREMENT PRIMARY KEY,
  nome               VARCHAR(100) NOT NULL,
  preco              DECIMAL(10,2) NOT NULL,
  burger_id          INT NOT NULL,
  acompanhamento_id  INT NOT NULL,
  bebida_id          INT NOT NULL,
  FOREIGN KEY (burger_id)         REFERENCES Produto(id),
  FOREIGN KEY (acompanhamento_id) REFERENCES Produto(id),
  FOREIGN KEY (bebida_id)         REFERENCES Produto(id)
);

-- Pedidos e itens
CREATE TABLE Pedido (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  data             DATETIME NOT NULL,
  estado           ENUM('ABERTO','EM_PREPARACAO','PRONTO','ENTREGUE','CANCELADO') NOT NULL,
  precoTotal       DECIMAL(10,2) NOT NULL DEFAULT 0,
  terminal_venda_id INT NOT NULL,
  cliente_id       INT NULL,        -- FK para Utilizador (CLIENTE), se quiseres
  FOREIGN KEY (terminal_venda_id) REFERENCES TerminalVenda(id),
  FOREIGN KEY (cliente_id)        REFERENCES Utilizador(id)
);

CREATE TABLE ItemPedido (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  pedido_id   INT NOT NULL,
  produto_id  INT,
  menu_id     INT,
  quantidade  INT NOT NULL,
  preco       DECIMAL(10,2) NOT NULL,
  estacao_id  INT NULL,    -- para saber em que estação é preparado
  FOREIGN KEY (pedido_id)  REFERENCES Pedido(id),
  FOREIGN KEY (produto_id) REFERENCES Produto(id),
  FOREIGN KEY (menu_id)    REFERENCES Menu(id),
  FOREIGN KEY (estacao_id) REFERENCES EstacaoT(id)
);

CREATE TABLE Talao (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  numero           INT NOT NULL,
  data             DATETIME NOT NULL,
  conteudo         VARCHAR(255),
  terminal_venda_id INT NOT NULL,
  FOREIGN KEY (terminal_venda_id) REFERENCES TerminalVenda(id)
);
