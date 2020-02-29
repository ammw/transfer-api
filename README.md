# transfer-api

## Description

An exercise to create a REST service without Spring and Hibernate

## Usage

Use maven to build and run.

Service may be started using `mvn exec:java`. By default, it listens on port 1234, to override it supply a new port number as an argument, e.g. `mvn exec:java -Dexec.args=4321`.

## API

| Method        | Endpoint           | Body  | Description  |
| ------------- |:-------------:|:-----:| --- |
| GET  | `/accounts` |  | List all accounts |
| POST | `/accounts` | `{"name":"<AccountName>"}` | Create an account for `<AccountName>` |
| GET  | `/accounts/:id` |  | Returns info (incl. balance) for account with ID `:id` |
| POST | `/accounts/:id/deposit` | `{"amount":<Amount>}` | Add `<Amount>` to balance of the account with ID `:id` |
| POST | `/accounts/:id/withdraw` | `{"amount":<Amount>}` | Subtract `<Amount>` from balance of the account with ID `:id` |
| POST | `/accounts/:id/transfer` | `{"amount":<Amount>, "to":"<ID>"}` | Transfers `<Amount>` from account with ID `:id` to the account with ID `<ID>` |
| GET  | `/accounts/:id/history` |  | Returns transfer history for account with ID `:id` |
