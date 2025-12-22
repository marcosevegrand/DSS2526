# Guia de Compilação e Execução

Este documento explica como compilar e executar o Sistema de Gestão de Restaurantes.

## Métodos de Compilação

### 1. Makefile (Recomendado - Mais Simples)

O projeto inclui um Makefile que simplifica o processo de compilação.

**Comandos disponíveis:**

```bash
make compile    # Compila todos os ficheiros Java
make run        # Compila e executa o sistema
make clean      # Remove ficheiros compilados
make rebuild    # Limpa e recompila tudo
make help       # Mostra ajuda
```

**Exemplo de uso:**
```bash
# Primeira vez - compilar e executar
make run

# Após fazer alterações no código
make run

# Se tiver problemas, recompilar tudo
make rebuild
```

### 2. Scripts de Compilação (Linux/Mac)

Incluímos scripts bash para facilitar a compilação:

```bash
# Tornar scripts executáveis (apenas primeira vez)
chmod +x build.sh run.sh

# Compilar
./build.sh

# Executar
./run.sh
```

### 3. Compilação Manual

Se preferir ter controlo total sobre o processo:

```bash
# Criar diretório de saída
mkdir -p build

# Compilar
javac -d build -sourcepath src/main/java \
    $(find src/main/java -name "*.java")

# Executar
java -cp build restaurante.Main
```

### 4. Maven (Profissional)

Para projetos mais complexos ou quando precisar de dependências externas:

```bash
# Compilar
mvn compile

# Executar
mvn exec:java

# Criar JAR
mvn package

# Executar JAR
java -jar target/restaurant-system-1.0-SNAPSHOT.jar
```

## Estrutura de Diretórios

```
restaurant-system/
├── src/main/java/          # Código fonte
│   └── restaurante/
│       ├── Main.java       # Ponto de entrada
│       ├── business/       # Lógica de negócio
│       ├── data/           # Acesso a dados
│       └── ui/             # Interface utilizador
├── build/                  # Ficheiros compilados (.class)
├── target/                 # Saída do Maven
├── Makefile               # Automação de build
├── pom.xml                # Configuração Maven
└── README.md              # Documentação principal
```

## Resolução de Problemas

### "javac: command not found"
**Problema:** Java não está instalado ou não está no PATH.

**Solução:**
```bash
# Verificar se Java está instalado
java -version
javac -version

# Se não estiver instalado:
# Ubuntu/Debian:
sudo apt install openjdk-11-jdk

# macOS:
brew install openjdk@11

# Adicionar ao PATH (se necessário)
export PATH="/usr/lib/jvm/java-11-openjdk/bin:$PATH"
```

### "make: command not found"
**Problema:** Make não está instalado.

**Solução:**
```bash
# Ubuntu/Debian:
sudo apt install make

# macOS:
xcode-select --install
```

Use os scripts bash (`./build.sh` e `./run.sh`) como alternativa.

### Erros de compilação
**Problema:** Código fonte tem erros.

**Solução:**
1. Verifique a mensagem de erro
2. Corrija o ficheiro indicado
3. Recompile com `make rebuild`

### "NoClassDefFoundError" ao executar
**Problema:** Classes não foram compiladas corretamente.

**Solução:**
```bash
make clean
make compile
```

## Desenvolvimento

### Workflow Recomendado

1. **Fazer alterações no código**
   - Editar ficheiros em `src/main/java/`

2. **Compilar e testar**
   ```bash
   make run
   ```

3. **Se houver erros**
   - Corrigir o código
   - Recompilar: `make run`

4. **Antes de commit**
   ```bash
   make rebuild  # Garantir que tudo compila
   ```

### Adicionar Novas Classes

1. Criar ficheiro `.java` no pacote apropriado
2. Compilar com `make compile`
3. O Makefile encontra automaticamente novos ficheiros

### Debug

Para debug com prints:
```java
System.out.println("DEBUG: valor = " + valor);
```

Para debug com IDE (IntelliJ/Eclipse):
- Importar projeto como "Maven Project"
- Configurar run configuration para `restaurante.Main`

## Próximos Passos

Depois de conseguir compilar e executar:

1. **Implementar lógica de negócio** nos subsistemas
2. **Adicionar base de dados** (MySQL/PostgreSQL)
3. **Desenvolver GUI** (Swing/JavaFX)
4. **Criar testes** (JUnit)
5. **Gerar documentação** (Javadoc)

## Recursos Adicionais

- [Documentação Java](https://docs.oracle.com/en/java/)
- [Maven Tutorial](https://maven.apache.org/guides/getting-started/)
- [Make Tutorial](https://makefiletutorial.com/)
