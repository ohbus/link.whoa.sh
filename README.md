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

## Running the Application Locally

The project consists of a Spring Boot backend and an Angular 21 frontend. They can be run together or separately during development.

### 1. Start the Database
First, set up the necessary PostgreSQL database using Docker Compose.

```sh
docker-compose up -d
```

### 2. Running via Command Line (Terminal)

**To run the full-stack application (Backend + compiled UI):**
```sh
./gradlew bootRun
```
This builds the Angular UI, packages it into the Spring Boot static resources, and starts the server on `http://localhost:8844`.

**To run the UI separately for Frontend Development (Hot Reloading):**
In a new terminal window, navigate to the `ui` folder:
```sh
cd ui
npm install
npm start
```
This will start the Angular development server on `http://localhost:4200`. It will proxy API requests to your backend running on `8844`.

### 3. Using IDE Run Configurations (IntelliJ IDEA / WebStorm)

To make development easier, we provide shared IDE run configurations. They are stored in the `.run/` directory and should be automatically picked up by IntelliJ-based IDEs.

*   **`Docker Compose`**: Starts the PostgreSQL database container.
*   **`WhoaApplication`**: Starts the Spring Boot backend server (with the `dev` profile).
*   **`UI Serve`**: Runs `npm start` in the `ui` folder to start the Angular development server.

**How to use them:**
1. Open the project in IntelliJ IDEA.
2. In the top toolbar, you will see a dropdown next to the Run/Debug (Play) button.
3. Select `Docker Compose` and run it.
4. Select `WhoaApplication` and run it.
5. (Optional) For frontend work, select `UI Serve` and run it.

*If the configurations do not appear automatically:*
1. Go to **Run > Edit Configurations...**
2. Check if they are listed under the respective categories (Spring Boot, npm, Docker).
3. If missing, ensure your IDE is configured to load `.run` folder configurations (it usually does by default if the folder is tracked in VCS). You can also manually create them mirroring the XML files in `.run/`.

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

## Code Quality

This project uses [ktlint](https://ktlint.github.io/) for enforcing Kotlin coding standards.

To format your code according to the project's ktlint rules, run:

```sh
./gradlew ktlintFormat
```

To check for any formatting violations without applying fixes, run:

```sh
./gradlew ktlintCheck
```

## Contributing

Contributions are welcome! Please feel free to open a pull request or submit an issue.
