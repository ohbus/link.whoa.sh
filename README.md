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

### 1. Start the Database (Dependencies Only)
First, set up the necessary PostgreSQL database using Docker Compose. We provide multiple Compose files depending on your needs.

**For Local Development (Recommended):**
This starts *only* the database dependencies. You will run the backend and frontend manually via Gradle/NPM to allow for hot-reloading.
```sh
docker-compose up -d
```

**For a Full-Stack Containerized Test (Pro Developers):**
If you want to run the *entire* application (Database + Spring Boot + Compiled Angular UI) inside Docker without manually running Gradle, you can use the full compose file:
```sh
docker-compose -f docker-compose.full.yml up -d --build
```
*(Note: This is mostly for verifying the Docker build before deployment. It does not support hot-reloading).*

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

**How to use them in order:**
1. Open the project in IntelliJ IDEA.
2. Select **`Docker Compose`** from the run configurations dropdown and run it to start the database.
3. Select **`WhoaApplication`** and run it to start the backend API.
4. Select **`UI Serve`** and run it to start the frontend hot-reloading server.

*If the configurations do not appear automatically:*
1. Go to **Run > Edit Configurations...**
2. Check if they are listed under the respective categories (Spring Boot, npm, Docker).
3. If missing, ensure your IDE is configured to load `.run` folder configurations (it usually does by default if the folder is tracked in VCS).

### 4. Local Testing Pitfalls & Configurations

When developing a separated frontend and backend locally, there are common pitfalls:

*   **CORS (Cross-Origin Resource Sharing)**: We have completely locked down CORS on the backend. You do not need to configure allowed origins for local development. 
    *   *Why it works:* When you use `npm start` (or the `UI Serve` run configuration), Angular uses `ui/proxy.conf.json` to proxy `/api` and `/actuator` requests directly to `localhost:8844`. This tricks the browser into thinking it's a same-origin request, bypassing CORS issues entirely and mirroring the monolithic production deployment.
*   **Database Not Ready**: If you start `WhoaApplication` before the PostgreSQL container is fully initialized, the backend will fail to start. Always ensure the `Docker Compose` run configuration finishes initializing the database first.
*   **Caching Collisions**: If you manually delete rows from the database during local testing, the Caffeine cache (`UrlCacheService`) might still return stale records for up to 10 minutes. If you encounter weird state, restart the backend server to clear the in-memory cache.

### FAQ

**Q: Do I need to run `UI Serve` if I just want to test the API?**
A: No. If you only want to test the Spring Boot API, you just need `Docker Compose` and `WhoaApplication`.

**Q: Why do I get a `404 Not Found` when I refresh a UI page like `/settings`?**
A: To prevent collisions between your generated short links (e.g., `link.whoa.sh/abc123`) and the UI router paths, the Angular application is configured to use **Hash Routing**. Your UI URLs will look like `http://localhost:4200/#/settings`. 

**Q: How do I change the rate limiting thresholds for local testing?**
A: Rate limiting is configured via Bucket4j in `application.properties`. You can adjust `bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity` to a higher number if you are running stress tests locally.

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
