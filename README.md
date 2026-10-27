# 🌱 AgroTrace ESG - Automação de Testes de API (BDD)

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue)
![Cucumber](https://img.shields.io/badge/Cucumber_BDD-7.15.0-brightgreen)
![RestAssured](https://img.shields.io/badge/Rest_Assured-5.4.0-green)
![Docker](https://img.shields.io/badge/Docker-WireMock-2496ED)

Projeto acadêmico (FIAP) de **Testes Automatizados de API REST** utilizando **Java 17, Cucumber (BDD em Português), Rest Assured, JSON Schema Validator, Lombok, Gson, Docker e GitHub Actions**, aplicado ao contexto de **ESG (Environmental, Social and Governance)** no Agronegócio Brasileiro.

---

## 🎯 Sobre o Projeto e Contexto ESG

O **AgroTrace ESG** é uma API de Auditoria e Rastreabilidade Socioambiental de Lotes Agrícolas (como soja e café) voltada para certificação de exportação (**Selo Verde**). A API valida automaticamente os três pilares ESG antes de aprovar qualquer lote:

| Pilar ESG | Regra de Negócio Validada na API |
| :--- | :--- |
| **🌍 E (Ambiental)** | Verifica se a propriedade rural possui **zero focos de queimada recentes** (`focosQueimadaRecentes == 0`). Lotes oriundos de áreas com queimadas recebem bloqueio imediato (`HTTP 422`). |
| **🤝 S (Social)** | Valida a certidão de **regularidade trabalhista** (`regularidadeTrabalhista == true`), impedindo a comercialização de lotes ligados a trabalho irregular. |
| **⚖️ G (Governança)** | Exige o código válido do **Cadastro Ambiental Rural (CAR)** (`codigoCar`) como chave obrigatória de rastreabilidade (`HTTP 400` caso ausente) e emite um hash único (`idAuditoria`) para auditoria pública. |

---

## 🛠️ Tecnologias e Ferramentas Utilizadas

* **Java 17** – Linguagem base de desenvolvimento.
* **Maven** – Gerenciamento de dependências e execução da suíte.
* **Cucumber & Gherkin (JUnit 4)** – Escrita de cenários BDD em português (`# language: pt`).
* **Rest Assured** – Disparo de requisições HTTP (`POST` / `GET`) e validação de Status Code e corpo JSON.
* **JSON Schema Validator** – Teste de contrato automatizado da API.
* **Lombok & Google Gson** – Modelagem limpa de DTOs e serialização JSON.
* **Docker & WireMock** – Simulação/virtualização da API REST na porta `8080`.
* **GitHub Actions** – Pipeline de CI/CD executando `mvn clean test` a cada push na branch `main`.

---

## 📂 Estrutura de Pastas do Projeto

```text
agrotrace-esg-qa/
├── .github/
│   └── workflows/
│       └── ci.yml                             # Pipeline CI/CD no GitHub Actions
├── src/
│   └── test/
│       ├── java/br/com/agrotrace/
│       │   ├── model/
│       │   │   └── LoteAuditoriaModel.java    # DTO mapeado com Lombok (@Data, @Builder)
│       │   ├── runner/
│       │   │   └── TestRunner.java            # Executor JUnit + Cucumber e geração de relatórios
│       │   ├── services/
│       │   │   └── AuditoriaEsgService.java   # Chamadas Rest Assured + Validação JSON Schema
│       │   └── steps/
│       │       └── AuditoriaEsgSteps.java     # Step Definitions conectando Gherkin ao Java
│       └── resources/
│           ├── features/
│           │   └── AuditoriaLoteEsg.feature   # Cenários BDD em Português
│           └── schemas/
│               └── auditoria-sucesso-schema.json # Contrato JSON Schema da API
├── wiremock/
│   └── mappings/
│       └── auditoria-esg.json                 # Endpoints e regras mockadas da API ESG
├── docker-compose.yml                         # Container Docker da API (WireMock)
└── pom.xml                                    # Dependências do projeto Maven
```

---

## 🧪 Cenários BDD Automatizados

Os cenários estão especificados em Gherkin no arquivo `src/test/resources/features/AuditoriaLoteEsg.feature`:

| Tag Principal | Cenário | Método / Endpoint | Status Esperado |
| :--- | :--- | :--- | :---: |
| `@caminhoFeliz` `@contrato` | Registrar lote agrícola em total conformidade ESG com sucesso e validar contrato JSON Schema | `POST /api/v1/lotes/auditoria` | `201 Created` |
| `@negativo` `@ambiental` | Reprovar auditoria de lote oriundo de propriedade com focos de queimada recentes | `POST /api/v1/lotes/auditoria` | `422 Unprocessable Entity` |
| `@negativo` `@governanca` | Impedir registro de auditoria sem código do Cadastro Ambiental Rural (CAR) | `POST /api/v1/lotes/auditoria` | `400 Bad Request` |
| `@smoke` `@auditoriaConsulta` | Consultar certificado de auditoria ESG previamente aprovado por ID | `GET /api/v1/lotes/auditoria/{id}` | `200 OK` |

---

## 🚀 Como Executar os Testes Localmente

### Opção 1: Execução Direta via Maven (Com inicialização automática da API)
O `TestRunner` possui inicialização inteligente embarcada: caso o container Docker não esteja rodando na máquina local, o próprio Java sobe o servidor da API na porta `8080` automaticamente antes de executar os testes:

```bash
mvn clean test
```

Para executar filtrando por uma tag específica (ex: apenas testes `@smoke`):

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

### Opção 2: Subindo a API via Docker Compose (Opcional)
Caso deseje subir o container Docker manualmente antes de rodar os testes:

```bash
docker compose up -d
mvn clean test
```

---

## 📊 Relatórios de Execução (Cucumber HTML Report)

Após executar `mvn clean test`, o relatório visual completo em HTML e o arquivo JSON de evidências são gerados automaticamente na pasta `target`:

* **Relatório Visual HTML:** `target/cucumber-reports.html`
* **Relatório JSON:** `target/cucumber.json`

Basta clicar com o botão direito no arquivo `target/cucumber-reports.html` dentro da IDE (`Open In > Browser`) ou abri-lo diretamente no navegador para visualizar as métricas de execução (`100% passed`) e o detalhamento de cada passo em Gherkin.

---

## ⚙️ CI/CD - GitHub Actions

O projeto conta com um pipeline de Integração Contínua configurado no arquivo `.github/workflows/ci.yml`. A cada `push` ou `pull_request` na branch `main`, o GitHub Actions executa automaticamente:

1. **Checkout do Código Fonte** (`actions/checkout@v4`);
2. **Configuração do Ambiente Java 17** (`actions/setup-java@v4` - distribuição Temurin com cache Maven);
3. **Subida da API em Container Docker** (`docker compose up -d` disponibilizando os endpoints na porta `8080`);
4. **Execução da Suíte de Testes** (`mvn clean test`);
5. **Publicação de Artefatos** (`actions/upload-artifact@v4` disponibilizando o relatório `cucumber-reports.html` para download direto na aba *Actions* do GitHub).

---

👩‍💻 **Desenvolvido por:** Amanda Souza/ Deryck De Lucca/ Iago Dantas/ Daniel Vilela – FIAP 2026