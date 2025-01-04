# XRPL Wallet API

This project is a Kotlin-based Spring Boot application that provides a RESTful API for managing XRPL wallets.

Key libraries used in this project include:

- [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL Database](https://www.postgresql.org/)
- [XRPL4J](https://github.com/XRPLF/xrpl4j)

## Features

### Wallet Management

The API allows the creation and retrieval of test wallets on the XRPL. 
It provides an abstraction layer allowing users to interact with the XRPL without handling credentials themselves, as
they are handled within the service and persisted in a database.

### Transactions

Simple payments can be sent from wallets created through this API to any other XRPL address.


## Local Setup

### Prerequisites

For the application to work, the database needs to be running.
This can be done by executing the following command in the root directory of the project:

```shell
docker-compose up -d
```

### Running the Application

The application can be started by running the following command in the root directory of the project:

```shell
./gradlew bootRun --args='--spring.profiles.active=local'
```

Alternatively one can run the application within Intellij IDEA by running the `XrplWalletApiApplication` class.
Make sure to set the active profile to `local` in the run configuration.

### Accessing the API

Use the Postman collection in the `postman` directory to interact with the API.