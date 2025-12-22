# Sistema de Gestão de Restaurantes - Fast Food Chain

## Descrição do Projeto

Sistema integrado para gestão de uma cadeia de restaurantes de fast-food, desenvolvido como trabalho prático da UC de Desenvolvimento de Sistemas de Software (DSS).

## Funcionalidades Principais

### Para Clientes
- Fazer pedidos através de terminais tácteis
- Personalizar itens do menu
- Filtrar alergénicos
- Efetuar pagamento
- Levantar pedidos

### Para Funcionários
- Autenticação no sistema
- Consultar fila de pedidos
- Preparar pedidos
- Reportar atrasos e problemas

### Para Gerentes
- Consultar estatísticas de faturação
- Consultar estatísticas de atendimento
- Consultar estatísticas de desempenho
- Gestão de stock

## Arquitetura

O sistema segue uma arquitetura multi-camada:

```
src/main/java/restaurante/
├── Main.java                          # Entry point
├── business/                          # Business Logic Layer
│   ├── IRestauranteFacade.java       # Facade interface
│   ├── RestauranteFacade.java        # Facade implementation
│   ├── pedidos/                      # Order subsystem
│   ├── funcionarios/                 # Employee classes
│   ├── terminais/                    # Terminal classes
│   ├── restaurantes/                 # Restaurant classes
│   ├── estatisticas/                 # Statistics subsystem
│   └── tarefas/                      # Task classes
├── data/                             # Data Access Layer
│   ├── IRestauranteDAO.java          # DAO interface
│   └── RestauranteDAO.java           # DAO implementation
└── ui/                               # Presentation Layer
    └── text/                         # Text UI (temporary)
        └── TextUI.java
```

## Estrutura de Pacotes

- **business**: Contém toda a lógica de negócio
  - **pedidos**: Gestão de pedidos, items, pagamentos
  - **funcionarios**: Cozinheiros, operadores de caixa, gerentes
  - **terminais**: Terminais de produção e venda
  - **restaurantes**: Cadeia de restaurantes
  - **estatisticas**: Geração de relatórios e estatísticas
  - **tarefas**: Gestão de tarefas dos funcionários

- **data**: Camada de acesso a dados
  - Interface DAO para persistência
  - Implementação in-memory (temporária)

- **ui**: Interface com o utilizador
  - TextUI básica (temporária)
  - TODO: Implementar GUI com touchscreens

## Como Compilar e Executar

### Pré-requisitos
- Java JDK 11 ou superior
- Maven (opcional, para gestão de dependências)
- Make (opcional, para usar Makefile)

### Opção 1: Usando Makefile (Mais Simples)
```bash
# Compilar o projeto
make compile

# Compilar e executar
make run

# Limpar ficheiros compilados
make clean

# Recompilar tudo (clean + compile)
make rebuild

# Ver todas as opções disponíveis
make help
```

Os ficheiros compilados ficam no diretório `build/`.

### Opção 2: Compilação Manual
```bash
# Criar diretório de compilação
mkdir -p build

# Compilar todos os ficheiros Java
javac -d build -sourcepath src/main/java $(find src/main/java -name "*.java")

# Executar
java -cp build restaurante.Main
```

### Opção 3: Com Maven (Recomendado para projetos maiores)
```bash
# Compilar
mvn compile

# Executar
mvn exec:java -Dexec.mainClass="restaurante.Main"

# Criar JAR executável
mvn package

# Executar o JAR
java -jar target/restaurant-system-1.0-SNAPSHOT.jar
```

## Próximos Passos

### 1. Implementação da Lógica de Negócio
- [ ] Completar SubsistemaPedidos
- [ ] Implementar SubsistemaEstatisticas
- [ ] Adicionar lógica de autenticação
- [ ] Implementar gestão de stock
- [ ] Adicionar validação de alergénicos

### 2. Camada de Dados
- [ ] Implementar persistência em base de dados (MySQL/PostgreSQL)
- [ ] Adicionar configuração JDBC
- [ ] Criar scripts SQL para tabelas
- [ ] Implementar transações

### 3. Interface de Utilizador
- [ ] Desenvolver GUI com Swing ou JavaFX
- [ ] Criar interface touchscreen para clientes
- [ ] Criar interface de funcionários
- [ ] Criar painel de gerente com gráficos

### 4. Diagramas UML
- [ ] Diagrama de Sequência para cada Use Case
- [ ] Diagrama de Componentes
- [ ] Diagrama de Deployment
- [ ] Atualizar Diagrama de Classes

### 5. Testes
- [ ] Testes unitários (JUnit)
- [ ] Testes de integração
- [ ] Testes de aceitação

### 6. Documentação
- [ ] Manual de utilizador
- [ ] Documento técnico
- [ ] Javadoc completo

## Modelo de Domínio

O sistema implementa o modelo de domínio fornecido, incluindo:
- Cadeia de Restaurantes
- Restaurantes individuais
- Funcionários (Cozinheiros, Operadores de Caixa, Gerentes)
- Terminais (Produção e Venda)
- Pedidos e Items
- Menu e Alimentos
- Ingredientes e Alergénicos
- Stock
- Estatísticas

## Casos de Uso Implementados

1. **Fazer Pedido** (Cliente)
2. **Levantar Pedido** (Cliente)
3. **Autenticar-se no Sistema** (Funcionário/Gerente)
4. **Consultar Fila de Pedidos** (Funcionário)
5. **Preparar Pedido** (Funcionário)
6. **Consultar Estatísticas** (Gerente)

## Autores

[Adicionar nomes dos elementos do grupo]

## Licença

Trabalho académico - Universidade do Minho - DSS 2025/2026
