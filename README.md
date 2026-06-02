# Delivery System - CR2

## Equipe
- **Ándre Tavares**
- **Bruno Peres**
- **Sahuan Pimentel**

## Projeto
Este projeto é um sistema de entrega de pedidos (Delivery) com um módulo independente de auditoria de logs, desenvolvido como parte da disciplina de Projetos de Sistemas de Software da UFES (Curso: Ciência da Computação).

## Estrutura do Projeto
- `log-auditoria`: Biblioteca independente para registro de logs em formatos JSONL, CSV e XML (Padrão Strategy).
- `DeliveryPedido`: Sistema de delivery que consome a biblioteca de logs.

## Como Executar
1. Certifique-se de ter o Java 21 e Maven instalados.
2. Na raiz do projeto, execute:
   mvn clean install
3. Para rodar o caso de uso principal:
   mvn exec:java -pl DeliveryPedido

## Link do Reposit�rio
- **Git Clone:** git clone <link-do-repositorio-aqui>
