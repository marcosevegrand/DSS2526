# Sistema de Gestão de Restaurante - DSS 2025/2026

## Descrição

Sistema completo de gestão para uma cadeia de restaurantes fast-food, desenvolvido no âmbito da UC de Desenvolvimento de Sistemas de Software.

O sistema é composto por três módulos principais:
- **Venda (POS)** - Terminal de pedidos para clientes
- **Produção** - Gestão de cozinha e caixa
- **Gestão** - Administração e estatísticas

## Requisitos

- Java 17 ou superior
- MySQL 8.0 ou superior
- Gradle 8.0 ou superior (opcional, pode usar gradlew)

## Configuração da Base de Dados

1. Criar a base de dados:
```sql
CREATE DATABASE restaurante;
```

2. Executar o schema:
```bash
mysql -u root -p restaurante < src/main/resources/schema.sql
```

3. Povoar dados de teste:
```bash
mysql -u root -p restaurante < src/main/resources/data.sql
```

4. Configurar credenciais em `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/restaurante?serverTimezone=Europe/Lisbon
db.user=root
db.password=root
```

## Compilação e Execução

### Com Gradle

```bash
# Compilar
./gradlew build

# Executar
./gradlew run

# Criar JAR executável
./gradlew jar
java -jar build/libs/dss-restaurante-1.0-SNAPSHOT.jar
```

### Manualmente

```bash
# Compilar
mkdir -p build/classes
javac -d build/classes -sourcepath src/main/java \
  -cp "mysql-connector-java-8.0.33.jar" \
  $(find src/main/java -name "*.java")

# Executar
java -cp "build/classes:mysql-connector-java-8.0.33.jar" dss2526.app.App
```

## Estrutura do Projeto

```
dss-project/
├── src/main/
│   ├── java/dss2526/
│   │   ├── app/           # Ponto de entrada
│   │   ├── data/          # DAOs e persistência
│   │   ├── domain/        # Entidades e enumerações
│   │   ├── service/       # Facades (lógica de negócio)
│   │   └── ui/            # Interface CLI
│   └── resources/
│       ├── schema.sql     # Estrutura da BD
│       ├── data.sql       # Dados de teste
│       └── db.properties  # Configuração
├── assets/latex/          # Relatório LaTeX
├── build.gradle
└── README.md
```

## Credenciais de Teste

### Módulo de Gestão
| Utilizador | Password | Função |
|------------|----------|--------|
| marco.coo | pass | COO (acesso global) |
| gerente.lisboa | pass | Gerente Lisboa |
| gerente.porto | pass | Gerente Porto |
| gerente.faro | pass | Gerente Faro |

### Módulo de Produção
Qualquer restaurante/estação pode ser selecionado sem autenticação.

## Use Cases Implementados

1. **Fazer Pedido** - Terminal de vendas completo
2. **Levantar Pedido** - Entrega e devoluções
3. **Autenticar-se** - Login para gestão
4. **Consultar Fila de Pedidos** - Monitor de progresso
5. **Preparar Pedido** - Gestão de tarefas da cozinha
6. **Consultar Estatísticas** - Dashboard de gestão

## Padrões de Design

- **Singleton** - Facades e DAOs
- **DAO** - Acesso a dados
- **Facade** - Simplificação da API
- **Identity Map** - Cache de entidades
- **MVC** - Interface de utilizador

## Arquitetura

```
┌──────────────────────────────────────────────────────────┐
│                      UI Layer (CLI)                      │
│  ┌──────────┐    ┌──────────────┐    ┌─────────────┐     │
│  │ VendaUI  │    │ ProducaoUI   │    │  GestaoUI   │     │
│  └────┬─────┘    └──────┬───────┘    └──────┬──────┘     │
│       │                 │                   │            │
│  ┌────┴─────┐    ┌──────┴───────┐    ┌──────┴──────┐     │
│  │Controller│    │ Controller   │    │ Controller  │     │
│  └────┬─────┘    └──────┬───────┘    └──────┬──────┘     │
├───────┼─────────────────┼───────────────────┼────────────┤
│       │                 │                   │            │
│  ┌────┴─────────────────┴───────────────────┴────────┐   │
│  │               Service Layer (Facades)             │   │
│  │  ┌───────────┐  ┌──────────────┐  ┌────────────┐  │   │
│  │  │VendaFacade│  │ProducaoFacade│  │GestaoFacade│  │   │
│  │  └─────┬─────┘  └──────┬───────┘  └─────┬──────┘  │   │
│  └────────┼───────────────┼────────────────┼─────────┘   │
├───────────┼───────────────┼────────────────┼─────────────┤
│           │               │                │             │
│  ┌────────┴───────────────┴────────────────┴────────┐    │
│  │                    Data Layer (DAOs)             │    │
│  │  RestauranteDAO, PedidoDAO, TarefaDAO, ...       │    │
│  └──────────────────────┬───────────────────────────┘    │
├─────────────────────────┼────────────────────────────────┤
│                         │                                │
│                    ┌────┴────┐                           │
│                    │  MySQL  │                           │
│                    └─────────┘                           │
└──────────────────────────────────────────────────────────┘
```

## Autores

Grupo TP-28 - DSS 2025/2026
Universidade do Minho