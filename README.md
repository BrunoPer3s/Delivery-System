# Delivery System - CR2

## Equipe
- **Vinicius de Mello**
- **Bruno Peres**
- **Sahuan Pimentel**

## Projeto
Este projeto é um sistema de entrega de pedidos (Delivery) com um módulo independente de auditoria de logs, desenvolvido como parte da disciplina de Projetos de Sistemas de Software da UFES (Curso: Ciência da Computação).

## Estrutura do Projeto
- `log-auditoria`: Biblioteca independente para registro de logs em formatos JSONL, CSV e XML (Padrão Strategy).
- `DeliveryPedido`: Sistema de delivery que consome a biblioteca de logs.

## Como Executar e Testar

### 1. Clonar o Repositório
```bash
git clone https://github.com/BrunoPer3s/Delivery-System.git
cd Delivery-System
```

### 2. Build e Verificação de Dependência (JitPack)
Para garantir que o sistema está baixando a biblioteca `log-auditoria` da nuvem e não usando uma pasta local:
```bash
mvn clean install -pl DeliveryPedido
```
*Observe no log que o Maven fará o download do JAR a partir de `jitpack.io`.*

### 3. Executar o Sistema
Para rodar o caso de uso principal e ver os logs sendo gerados:
```bash
mvn exec:java -pl DeliveryPedido
```
Os arquivos de log serão gerados na pasta raiz do projeto (`log.jsonl`, `log.csv`, etc), comprovando o funcionamento da biblioteca externa.

## Estrutura do Projeto (CR2)
- `log-auditoria`: Biblioteca independente (Padrão Strategy). **Publicada via JitPack**.
- `DeliveryPedido`: Sistema de delivery que consome `log-auditoria` como dependência externa.

