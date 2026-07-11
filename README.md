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
Abre a **tela de login** do sistema. Credenciais de teste criadas automaticamente:

| Usuário | Senha | Perfil | Situação |
|---|---|---|---|
| `adminmaster` | `Admin123` | Administrador | Autorizado |
| `atendente01` | `Atende01` | Atendente | Autorizado |

(Usuários novos cadastrados nascem como *Pendente* e só acessam após um administrador autorizá-los em *Gerenciar usuários*.)

## Persistência (SQLite)
- Ao iniciar, é criado o arquivo **`delivery.db`** na pasta de execução, com dados de exemplo (usuários, clientes, produtos e cupons).
- Os dados **persistem entre execuções** (clientes, produtos, estoque, pedidos, usuários).
- Para **reiniciar do zero**, feche a aplicação e apague o `delivery.db` — os dados de exemplo são recriados no próximo start.

## Auditoria (logs)
Os arquivos de log (`log.jsonl`, `log.csv`, `log.xml`) são gerados na pasta de execução, comprovando o funcionamento da biblioteca externa `log-auditoria`.
