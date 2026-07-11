# Delivery System

## Equipe
- **Vinicius de Mello**
- **Bruno Peres**
- **Sahuan Pimentel**

## Projeto
Sistema de entrega de pedidos (Delivery) desenvolvido na disciplina de Projetos de Sistemas de Software da UFES (Ciência da Computação). Cobre as histórias US01 a US12 (autenticação, gestão de usuários, clientes, produtos, estoque, pedido, pagamento simulado e auditoria), seguindo o modelo MVP Passive View. A auditoria de logs é uma biblioteca independente, reaproveitada do CR2.

## Estrutura do Projeto
- `log-auditoria`: Biblioteca independente de registro de logs em JSONL, CSV e XML (Padrão Strategy). **Publicada via JitPack** e consumida como dependência externa.
- `DeliveryPedido`: Aplicação desktop (Swing) do delivery. Persistência em **SQLite**; repositórios em memória mantidos como implementação alternativa atrás das mesmas interfaces.

## Pré-requisitos
- **JDK 21**
- **Maven 3.8+**
- **Acesso à internet** no primeiro build (baixa `log-auditoria` do `jitpack.io` e `sqlite-jdbc` do Maven Central).

## Como Executar e Testar

Todos os comandos são executados a partir da pasta `Delivery-System/` (onde fica o `pom.xml` pai).

### 1. Clonar o Repositório
```bash
git clone https://github.com/BrunoPer3s/Delivery-System.git
cd Delivery-System
```

### 2. Build
```bash
mvn clean install -pl DeliveryPedido
```
No log, o Maven baixa a biblioteca `log-auditoria` de `jitpack.io` e o driver `sqlite-jdbc` do Maven Central.

### 3. Executar o Sistema
```bash
mvn exec:java -pl DeliveryPedido
```
Abre a **tela de login** do sistema.

### 4. Executar os Testes
```bash
mvn test -pl DeliveryPedido
```
Suíte JUnit 5 cobrindo os cenários de aceite das histórias verificáveis fora da interface gráfica (US01, US02, US03, US04, US06, US08, US10 e US11), incluindo a baixa de estoque em transação única e o pagamento simulado com fonte de aleatoriedade determinística.

## Primeiro acesso

**Não existem usuários pré-cadastrados.** O banco nasce sem nenhum usuário, conforme a regra da US02.

1. Na tela de login, escolha **Cadastrar usuário**.
2. Informe nome, nome de usuário e senha.
3. Como não há usuário persistido, esse **primeiro cadastro recebe automaticamente o perfil Administrador e a situação Autorizado** — ele já pode acessar o sistema.

A partir daí, todo cadastro posterior nasce como **Atendente** com situação **Pendente**, e só consegue iniciar sessão depois que um administrador o autorizar em *Gerenciar usuários* (menu restrito ao perfil Administrador).

Regras de preenchimento:

| Campo | Regra |
|---|---|
| Nome | 2 a 120 caracteres; letras, espaços, apóstrofos e hífens |
| Nome de usuário | 3 a 30 caracteres; apenas letras minúsculas e algarismos, sem espaços; único no sistema |
| Senha | 8 a 64 caracteres; armazenada apenas como hash |

## Persistência (SQLite)
- Ao iniciar, é criado o arquivo **`delivery.db`** na pasta de execução.
- O banco é semeado com dados de exemplo de **clientes, produtos e cupons**, para permitir demonstrar pedidos, estoque e pagamento sem cadastro manual. **Usuários não são semeados** — veja *Primeiro acesso*.
- Os dados **persistem entre execuções** (usuários, clientes, produtos, estoque, pedidos).
- Para **reiniciar do zero**, feche a aplicação e apague o `delivery.db`. No próximo start, os dados de exemplo são recriados e o sistema volta a exigir o cadastro do primeiro usuário (que será administrador).

## Auditoria (logs)
Cada execução utiliza uma **única modalidade** de persistência de auditoria, evitando registros duplicados em formatos distintos. A modalidade ativa por padrão é **JSONL**, gerando o arquivo `log.jsonl` na pasta de execução. As estratégias `CsvLogger` e `XmlLogger` também são fornecidas pela biblioteca `log-auditoria` e podem ser trocadas em tempo de execução via `GerenciadorDeLogAtivo`.

São registrados, entre outros: autenticação (sucesso e falha), cadastro de usuário, autorização, desautorização e exclusão, manutenção de clientes e produtos, movimentações de estoque, aplicação e recusa de cupom, tentativa e resultado do pagamento, baixa de estoque e transições de estado do pedido. Senhas e dados financeiros sensíveis não integram os registros.
