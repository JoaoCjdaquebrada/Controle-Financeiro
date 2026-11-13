# Organiza — Controle Financeiro Pessoal

Aplicação full stack para registrar contas, categorias, receitas, despesas e orçamentos mensais. O sistema possui frontend em ReactJS, API REST com Java e Spring Boot e persistência em MySQL utilizando JPA / Spring Data JPA.

## Integrantes

- **João Augusto de Andrade Pereira** — RA 25340354-2
- **Leandro Augusto Krajevski** — RA 25001376-2

## Tecnologias

- Java 26 (versão exigida pela atividade)
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- Jakarta Validation
- MySQL 8.4 ou superior
- ReactJS
- Vite
- Node.js e npm
- Maven Wrapper
- JUnit 5 e Mockito

> **Atenção à versão do Java:** a atividade exige Java 26. Antes da entrega, confira se o `pom.xml` contém `<java.version>26</java.version>` e se o projeto foi executado/testado com JDK 26. O ambiente de desenvolvimento utilizado durante parte da implementação também teve JDK 27 instalado; compatibilidade de compilação não é a mesma coisa que executar com JDK 26.

## Funcionalidades

- Cadastro e consulta de contas financeiras.
- Cadastro e consulta de categorias de receita e despesa.
- Registro, consulta e exclusão de lançamentos financeiros.
- Cadastro, consulta, atualização e exclusão de orçamentos mensais.
- Consulta de lançamentos por conta e por período de orçamento.
- Dashboard com resumo de saldo, receitas, despesas e orçamentos.
- Validação de dados e tratamento centralizado de erros da API.

## Regras de negócio implementadas

1. O valor de uma movimentação deve ser maior que zero.
2. O tipo do lançamento deve corresponder ao tipo da categoria (receita com categoria de receita; despesa com categoria de despesa).
3. Uma despesa não pode ultrapassar o orçamento mensal disponível para sua categoria, considerando os lançamentos já registrados no mesmo mês e ano.
4. Orçamentos devem utilizar categorias de despesa e ter limite positivo.
5. Não é permitido cadastrar mais de um orçamento para a mesma categoria no mesmo mês e ano.
6. O nome e o tipo de uma categoria não podem se repetir.

## Estrutura do projeto

```text
controlefinanceiro/
├── database/
│   └── schema.sql
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── src/
│   ├── main/
│   │   ├── java/br/com/controlefinanceiro/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── domain/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/application.properties
│   └── test/java/
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Banco de dados

O projeto utiliza MySQL. O script de criação está em `database/schema.sql`.

1. Instale e inicie o MySQL Server.
2. Abra o MySQL Workbench e conecte-se ao servidor local.
3. Abra `database/schema.sql` no editor SQL.
4. Execute o script completo. Ele cria o banco `controle_financeiro` e suas quatro tabelas: `contas_financeiras`, `categorias`, `lancamentos` e `orcamentos_mensais`.
5. Confira se as tabelas aparecem no schema `controle_financeiro`.

O script cria a estrutura, mas não insere dados de demonstração. Os registros podem ser cadastrados pela interface React ou pela API.

### Configuração da conexão

Edite `src/main/resources/application.properties`:

```properties
spring.application.name=controlefinanceiro
spring.datasource.url=jdbc:mysql://localhost:3306/controle_financeiro
spring.datasource.username=root
spring.datasource.password=COLOQUE_SUA_SENHA_LOCAL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Substitua `COLOQUE_SUA_SENHA_LOCAL` pela senha configurada no seu MySQL. **Não publique sua senha no Git ou em repositório compartilhado.** O banco e as tabelas precisam existir antes de iniciar a aplicação, pois `ddl-auto=none` não cria a estrutura automaticamente.

## Como executar o backend

Pré-requisitos: JDK 26, MySQL em execução e o banco criado pelo script SQL.

No terminal, na pasta raiz do projeto:

```powershell
.\mvnw.cmd clean test
```

Para iniciar a API:

```powershell
.\mvnw.cmd spring-boot:run
```

Também é possível executar a classe `ControlefinanceiroApplication` pelo IntelliJ IDEA.

A API ficará disponível em:

```text
http://localhost:8080
```

## Como executar o frontend

Pré-requisitos: Node.js e npm.

Abra outro terminal na pasta raiz do projeto e execute:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

O Vite disponibilizará a interface em:

```text
http://localhost:5173
```

Mantenha o backend e o frontend executando em terminais separados. No Windows PowerShell, `npm.cmd` pode ser utilizado no lugar de `npm` quando a política de execução bloquear o script `npm.ps1`.

## API REST — principais endpoints

Base URL: `http://localhost:8080`

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/contas` | Cadastrar conta |
| GET | `/api/contas` | Listar contas |
| GET | `/api/contas/{id}` | Consultar conta por ID |
| DELETE | `/api/contas/{id}` | Excluir conta |
| POST | `/api/categorias` | Cadastrar categoria |
| GET | `/api/categorias` | Listar categorias |
| GET | `/api/categorias/{id}` | Consultar categoria por ID |
| GET | `/api/categorias/tipo/{tipo}` | Filtrar categorias por `RECEITA` ou `DESPESA` |
| POST | `/api/lancamentos` | Cadastrar lançamento |
| GET | `/api/lancamentos` | Listar lançamentos |
| GET | `/api/lancamentos/{id}` | Consultar lançamento por ID |
| GET | `/api/lancamentos/conta/{contaId}` | Listar lançamentos de uma conta |
| DELETE | `/api/lancamentos/{id}` | Excluir lançamento |
| POST | `/api/orcamentos` | Cadastrar orçamento |
| GET | `/api/orcamentos` | Listar orçamentos |
| GET | `/api/orcamentos/{id}` | Consultar orçamento por ID |
| GET | `/api/orcamentos/periodo/{mes}/{ano}` | Consultar orçamentos por período |
| PUT | `/api/orcamentos/{id}` | Atualizar orçamento |
| DELETE | `/api/orcamentos/{id}` | Excluir orçamento |

### Exemplo de cadastro de lançamento

`POST /api/lancamentos`

```json
{
  "descricao": "Compra no mercado",
  "valor": 180.00,
  "dataLancamento": "2026-11-10",
  "tipo": "DESPESA",
  "contaId": 1,
  "categoriaId": 2
}
```

Os IDs de conta e categoria devem corresponder a registros existentes no banco.

## Testes automatizados

Os testes ficam em `src/test/java`. Para executá-los:

```powershell
.\mvnw.cmd test
```

Foram implementados testes de regras de domínio para orçamento e testes unitários do `LancamentoService` com JUnit 5 e Mockito, incluindo cenários de valor inválido, categoria incompatível, orçamento excedido e despesa dentro do limite.

## Tratamento de erros

O `GlobalExceptionHandler` centraliza respostas para erros de validação e regras de negócio. Entre os status utilizados estão:

- `201 Created`: cadastro realizado.
- `200 OK`: consulta ou atualização realizada.
- `204 No Content`: exclusão realizada.
- `400 Bad Request`: dados inválidos ou regra de negócio violada.
- `409 Conflict`: conflito com restrição de integridade do banco.

## Observações para execução e apresentação

- Inicie o MySQL antes do backend.
- Execute o backend antes de utilizar o frontend.
- O CORS está configurado para permitir o frontend em `http://localhost:5173`.
- Para testar rotas `localhost` pelo Postman Web, utilize o Desktop Agent conectado; o Cloud Agent não acessa o `localhost` do computador.
- Demonstre o fluxo React → Controller → Service → Repository/JPA → MySQL e a resposta JSON.
- Apresente também os testes automatizados e as regras de negócio.
