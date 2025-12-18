# Whoa URL Shortener

Whoa is a simple and lightweight URL shortener service built with Spring Boot and Kotlin.

## Features

*   Shorten long URLs to a more manageable length.
*   Redirect short URLs to their original long-form counterparts.
*   (Future) Custom short URLs.
*   (Future) Analytics on URL usage.

## Prerequisites

Before you begin, ensure you have the following installed:

*   **Docker** and **Docker Compose** for containerized development and deployment.
*   **Java Development Kit (JDK) 21** or later. You can download it from [Oracle](https://www.oracle.com/java/technologies/downloads/#jdk21-linux) or use a package manager like SDKMAN!.
*   **Git** for cloning the repository.

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### 1. Clone the Repository

```sh
git clone https://github.com/your-username/whoa.git
cd whoa
```

## Running the Application

First, you need to set up the necessary environment. You can use Docker Compose to start a PostgreSQL database for local development.

```sh
docker-compose up -d
```

You can run the application directly using the Gradle wrapper. This will start the Spring Boot application on port 8844.

```sh
./gradlew bootRun
```

Once the application starts, you can access it at `http://localhost:8844`.

## Building the Application

To build the project and create an executable JAR file, run the following command. The resulting JAR will be located in the `build/libs/` directory.

```sh
./gradlew build
```

You can run the generated JAR file with:

```sh
java -jar build/libs/whoa-*.jar
```

## Running Tests

This project uses JUnit 5 for testing. To execute the test suite, run:

```sh
./gradlew test
```

A test report will be generated in the `build/reports/tests/test/` directory.

## Contributing

Contributions are welcome! Please feel free to open a pull request or submit an issue.
