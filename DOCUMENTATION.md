## Documentation

> [!NOTE]
> This is a detailed document relaying all technical details and information regarding 
> setting up the project and running it as a **self-hosted** service.

### Table of Contents

- [Project Architecture](#project-architecture)
- [Setup](#setup-database)
  
    - [Database](#setup-database)
    - [Third Party API](#setup-third-party-api)
    - [Environment Variables](#setup-environment) 

- [Run](#run)

### Project Architecture

This project uses a **modular** approach where each _application_ is isolated from the others and only depends on 
specified dependencies and _components_.

Project architecture is set up with organisation in mind by separating common dependencies not 
specific to an _application_ into their own module as _components_. 

Each _application_ only depends on libraries and _components_ it uses to reduce the possibility
of circular dependencies.

The project ensures compliance to circular dependency reduction during compilation and during runtime:

- **Gradle** enforces compliance during compilation by not compiling the project if an _application_ 
or _component_ uses a dependency not mentioned in its own `build.gradle.kts`.

- **@Configuration** annotation within Spring context is used to identify `@Bean`s used within each _component_,
therefore only the beans within a component are loaded into the application context on _application_ dependency injection.

Here is an overview of the full project folder structure:
```text
news-system
    /applications
        /analyse
        /collector
        /web
            /src
            build.gradle.kts
        
    /components
        /api-client
        /common
            /src
            build.gradle.kts
    
    settings.gradle.kts
    build.gradle.kts
    ...
```

Here is an overview of each application and component role within the system:

- **`applications/collector`** – A standalone Spring Boot service that fetches news articles from third‑party APIs, 
persists sources and articles into the shared database. 
It supports two operational modes controlled by Spring profiles:
    - `dev` – Runs once via a `CommandLineRunner` and exits (ideal for testing).
    - `prod` – Uses `@Scheduled` to execute daily at 08:00 (configurable).


- **`applications/analyse`** – A separate Spring Boot service responsible for computing daily analytics. 
It reads articles collected within a time window, extracts trending keywords, detects breaking news, and 
stores the results as an `Analytic` entity in the database.


- **`applications/web`** – The user‑facing dashboard built with Spring Boot, Thymeleaf, and Bootstrap. 
It displays articles with filtering (source, date, keyword), pagination and breaking‑news alerts. 
The web app only reads from the database.


- **`components/common`** – A shared library that contains all JPA entities (`Article`, `Source`, `Analytic`), 
Spring Data repositories, common services, Flyway migration scripts,
and the shared database configuration. Every application depends on this component to ensure 
a single source of truth for the data schema and data access.


- **`components/api-client`** – A library that encapsulates the HTTP client and domain objects 
required to communicate with third party APIs.

### Setup Database

This project uses **one central** SQL database.

The database could be changed, but it is **not recommended** since this project was
tested using **PostgreSQL** only.

Setting up and running the PostgreSQL database is simple, requiring only few steps:

You need to have **Docker** and **Docker Compose** installed in your local machine.
 
You can follow the installation guide provided [here](https://docs.docker.com/get-started/get-docker/) 
for Docker and [here](https://docs.docker.com/compose/install) for Docker Compose.
If you installed **Docker Desktop** then there is no need to install Docker Compose separately as it comes along with.
 
- After installing **Docker** and **Docker Compose**, you must set up **Docker Secrets** to safely store the database 
credentials. To do so, create a folder within the project root named `secrets`.
  - Within the `secrets` folder, you must create two files:
    
    - `postgres_pwd.txt`: Stores the password for database credentials
    - `postgres_user.txt`: Stores the username for database credentials

  - The credentials within each of the files must be **one** word with **no spaces or next lines**.

  - If you need to edit the folder or file names:
    
    - Edit the `docker-compose.yaml` file **only** in this section:
    ```yaml
    secrets:
      db_user:
        file: secrets/postgres_user.txt   # Location from root for database username
      db_pwd:
        file: secrets/postgres_pwd.txt    # Location from root for database password
    ```

- Next, ensure that Docker is running before you launch the database container.

- Run the `docker-compose.yaml` file, ensure your working directory is the root project folder:
  
  ```bash
    docker compose up -d
  ```
  If on Linux, you may need to use `sudo docker compose up -d`

Congratulations! You successfully launched your database container. You can always query it using:

```bash
    docker exec -it <container-id> psql -U <username> -d news
```

Use this command to obtain the `<container-id>` and find `news_database` container:
```bash
    docker ps
```

### Setup Third Party API

The **collector** application is responsible for public API requesting to retrieve news articles
and their sources to be collected within the database.

Currently, the **collector** application uses [News API](https://newsapi.org) as its sole source. Its architecture allows for more sources
to be implemented and extended onto the application.

Therefore as you set up this project on your local machine, you will need an API key
to run the collector application. This would then be added as an environment variable,
discussed in [Setup Environment](#setup-environment).

> [!NOTE]
> You can use the **free** tier of this API

To obtain an API key:

- Visit https://newsapi.org
- Create an account and verify your email
- You will find the API key in https://newsapi.org/account under **API key**

### Setup Environment

The table below discusses all the environment variables needed to set up the project environment
for running the system.

| Variable                 | Required by                 | Description                                                     | Example                                 |
|--------------------------|-----------------------------|-----------------------------------------------------------------|-----------------------------------------|
| `DB_URL`                 | All applications            | JDBC URL of the PostgreSQL database                             | `jdbc:postgresql://localhost:5432/news` |
| `DB_USER`                | All applications            | Database username (must match the secret `postgres_user.txt`)   | `admin`                                 |
| `DB_PASS`                | All applications            | Database password (must match the secret `postgres_pwd.txt`)    | `s3cret`                                |
| `NEWS_API_KEY`           | Collector                   | API key for News API                                            | `a1b2c3d4e5f6...`                       |
| `NEWS_API_BASE_URL`      | Collector                   | Base URL for News API endpoint                                  | `https://newsapi.org/v2`                       |
| `SPRING_PROFILES_ACTIVE` | Collector (`dev` \| `prod`) | Set to `prod` to enable scheduling; otherwise defaults to `dev` | `dev`                                   |


Use `export` in the terminal to export all required environment variables before running.


### Run

- **Build the whole project** from the root directory:
   ```bash
       ./gradlew build  
   ```
    
- Follow the [Setup Database](#setup-database) section to launch the PostgreSQL container and ensure the credentials are correctly placed in the secrets folder.


- Follow the [Setup Environment](#setup-environment) section to pass required environment variables correctly.


- Run each application independently. The order does not matter, but you might want start the collector first to populate data.

  - Collector
    ```bash
      ./gradlew :applications:collector:bootRun
    ```
          
  - Analyser
    ```bash
      ./gradlew :applications:analyse:bootRun
    ```

  - Web
    ```bash
      ./gradlew :applications:web:bootRun
    ```
  
- The dashboard will be available at http://localhost:8000.