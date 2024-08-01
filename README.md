# BCB - Projeto de Envio de Mensagens SMS

Este é um projeto BACKEND para fornecer as API's para cadastro, consulta e manutenção de um software de envio de SMS.
Foi desenvolvido em Java 17, utilizando o gerenciador de dependências Maven, persistência em Postgresql e a estrutura de testes JUnit, além do Docker.

## Funcionalidades

- Consta a opção de cadastrar o cliente, sua conta pré-paga ou pós-paga, com controle de créditos e limite disponível.
- Ainda sobre o cliente, é possível listar todos cadastrados, atualizar ou excluir o mesmo.
- Tem a opção também de alterar o limite ou trocar o tipo da conta, inserir créditosa, consultar saldo.
- E a principal função que é o envio de mensagens SMS como o registro do envio.
- Para acessar todas as funcionalidades disponíveis e seus parâmetros após subir o projeto basta acessar o endereço http://localhost:8080/api/swagger-ui/index.html#/

## Diagrama de Classes
![Gestão de Projetos](src/docs/Diagrama.png)
## Requisitos

- Java 17
- Maven
- Postgresql
- Docker

# Como baixar e subir local o projeto

Inicialmente você deve clonar o reposítório, abrindo um terminal e utilizando o comando abaixo:
```
git clonte https://github.com/marcoaureliosilva/BCB.git
```
Depois para subir o projeto vamos utilizar os comandos docker

```
docker-compose build
docker-compose up
```

# Autor
Marco Aurélio da Silva
https://www.linkedin.com/in/maraureliosilva


