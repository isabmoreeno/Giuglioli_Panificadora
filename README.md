# Giuglioli Panificadora

Painel interno para programação de pedidos e entregas, catálogo de produtos e controle básico de ingredientes, baseado nas telas compartilhadas do protótipo Figma Make.

## Escopo desta primeira versão

- Programações por data e período com linhas de cliente e colunas de produto; quantidades podem ser registradas e ajustadas pelos funcionários.
- Cadastros básicos de clientes e produtos.
- Ingredientes com saldo, limite mínimo e movimentações manuais de entrada/consumo.
- Dashboard com contagem real de programações do dia, produtos e clientes cadastrados.
- API REST em Spring Boot, persistência PostgreSQL via Flyway e interface Angular.

Não há checkout, pagamentos, vendas de balcão, baixa automática de ingredientes por receita, login ou autorização por perfil nesta versão. As telas do Figma mostram indicadores ilustrativos; métricas de venda e lucro não serão apresentadas como dados reais sem fonte operacional. A API deve permanecer em rede local/confiável até que autenticação e autorização sejam implementadas.

## Arquitetura

Monólito modular Spring Boot (REST, JDBC e Flyway), PostgreSQL e aplicação Angular standalone. O módulo de programação guarda cabeçalho, linhas cliente-produto e trilha de alteração. O frontend é servido separadamente durante desenvolvimento.

## Pré-requisitos

- Node.js 20.19+ ou 22.12+ e npm.
- JDK 21 e Maven 3.9+.
- Docker Desktop com Docker Compose (ou PostgreSQL 16 local).

## Executar localmente

1. Inicie o PostgreSQL: `docker compose up -d db`.
2. Inicie a API: `cd backend; mvn spring-boot:run`.
3. Em outro terminal instale e inicie a interface: `cd frontend; npm install; npm start`.
4. Acesse `http://localhost:4200`. API: `http://localhost:8080/api`; documentação OpenAPI: `http://localhost:8080/swagger-ui/index.html`.

Copie `.env.example` para `.env` para configurar o banco local; o Compose lê esse arquivo automaticamente. As configurações da API também aceitam as variáveis listadas. Não use os valores locais em produção.

## Modelo de dados inicial

- `products`: nome, categoria, unidade, preço/custo decimal e ativo.
- `customers`: nome, cidade e ativo.
- `schedules`: data de entrega, período e estado.
- `schedule_lines`: programação, cliente, produto e quantidade (inteiro não negativo).
- `schedule_audit`: usuário/identificador de funcionário, instante e resumo da alteração.
- `ingredients` e `stock_movements`: saldo, unidade, estoque mínimo e entradas/consumos/ajustes com motivo.

O preço/custo usa decimal no backend. Não foi adotada receita por produto nem geração automática de consumo. Uma programação existente é atualizada em transação; a implementação inicial substitui suas linhas em conjunto e registra auditoria. Integrações com vendas exigem definição posterior.

Assunção inicial: existe uma programação por data e período (Manhã, Tarde ou Noite), definida por restrição no banco. As quantidades registradas são os pedidos informados pelos clientes, conforme ajuste de escopo da usuária.

## Validação

Execute `mvn test` em `backend` e `npm test` em `frontend` após instalar as dependências. Confira também `mvn verify` e `npm run build`. Os testes existentes cobrem a validação de quantidade negativa na API e os cálculos dos totais da programação. A execução e os resultados precisam ser registrados antes do uso operacional.

## Limitações de segurança e escopo

Esta entrega é uma base funcional de desenvolvimento, não está pronta para uso com dados reais: autenticação e autorização de funcionários ainda não estão implementadas. A programação aceita um rótulo de funcionário informado pela interface, sem identidade verificada; isso serve apenas para registrar contexto de edição no protótipo. Mantenha os serviços em ambiente local confiável. Não exponha a API à internet ou a uma rede compartilhada até adicionar login, papéis e proteção dos endpoints. Indicadores financeiros e relatórios exportáveis dependem de uma fonte de vendas ainda não especificada.

## Próximas decisões

- Definir contas e níveis de acesso dos funcionários antes de expor fora da rede interna.
- Definir se as programações são únicas por data/período e quais nomes de períodos serão usados.
- Definir política de correção/cancelamento após a impressão.
- Definir receitas, perdas e fluxo de produção antes de automatizar o estoque.
- Definir a origem de vendas antes de ligar relatórios financeiros.
