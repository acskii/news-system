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
This project uses a **modular, multi-stack architecture** where each _application_ is isolated from the others 
and only depends on specified dependencies and shared _components_.

Project architecture is set up with organisation in mind by separating common dependencies, 
database access layers, and domain models not specific to a single _application_ into their 
own shared modules as _components_.

Each _application_ relies on libraries and _components_ aligned with its runtime environment:

- **Spring Boot** (`applications/collector`) relies on Gradle for module isolation and compile-time dependency 
enforcement, alongside Spring `@Configuration` boundaries for context loading.


- **Flask** (`applications/analyser` and `applications/dashboard`) relies on shared component for ORM models, database session management, and queries.

Here is an overview of the full project folder structure:
```text
news-system
    /applications
        /analyser
        /collector
        /dashboard

    /components
        /api-client
        /database
        /common
            /src
            build.gradle.kts
    
    settings.gradle.kts
    build.gradle.kts
    ...
```

Here is an overview of each application and component role within the system:

#### Applications

- `applications/collector` – A standalone Spring Boot service that fetches news articles from third‑party APIs and persists sources and articles into the shared database. 
Upon completing collection, it issues an HTTP request to the `analyser` service to trigger data processing. 
It supports two operational modes controlled by Spring profiles:

  - `dev` – Runs once via a `CommandLineRunner` and exits.

  - `prod` – Uses `@Scheduled` to execute daily at **08:00**.


- `applications/analyser` – A Python and Flask microservice responsible for computing daily analytics. 
Triggered via HTTP, it evaluates articles within a time window, computes headline sentiment scores, 
detects keyword spikes, and clusters breaking news topics across sources.
Results are stored in the database.


- `applications/dashboard` – The user‑facing web application that displays articles.
The app reads from the database.

#### Components

- `components/database` – A shared Python library containing database connection configurations, 
ORM models, and database query abstractions. It is used across all Flask applications to provide a single source 
for database models and read/write operations.


- `components/common` – A shared Java library containing JPA entities, Spring Data repositories, Flyway database migration scripts,
and database configurations. Used by collector to manage schema migrations and data persistence.


- `components/api-client` – A library that encapsulates HTTP client implementations required to 
communicate with external and internal APIs.

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

  You can also use this command from the **Makefile** instead:
  
  ```bash
    make run/dashboard
  ```

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

| Variable                 | Required by                 | Description                                                     | Example |
|--------------------------|-----------------------------|-----------------------------------------------------------------|--------|
| `DB_HOST`                | All applications            | Host name of the PostgreSQL database [Default: as example]      | `localhost` |
| `DB_PORT`                | All applications            | Port for the PostgreSQL database     [Default: as example]      | `5432` |
| `DB_NAME`                | All applications            | Name of the PostgreSQL database      [Default: as example]      | `news` |
| `DB_USER`                | All applications            | Database username (must match the secret `postgres_user.txt`)   | `admin` |
| `DB_PASS`                | All applications            | Database password (must match the secret `postgres_pwd.txt`)    | `s3cret` |
| `NEWS_API_KEY`           | Collector                   | API key for News API                                            | `a1b2c3d4e5f6...` |
| `NEWS_API_BASE_URL`      | Collector                   | Base URL for News API endpoint                                  | `https://newsapi.org/v2` |
| `ANALYSER_BASE_URL`      | Collector                   | Base URL for analyse application service                        | `http://localhost:8010` |
| `SPRING_PROFILES_ACTIVE` | Collector (`dev` \| `prod`) | Set to `prod` to enable scheduling; otherwise defaults to `dev` | `dev`  |

Use the provided `.env.example` to view all environment variables mentioned in the above table.

To ensure that the applications and components have access to these, create a `.env` file, copy and paste the 
environment variables from `.env.example` then fill them with the necessary values.

After that, you are able to [run](#run) the applications.

### Run

The process has been made easier using a **Makefile** provided with the project.

All environment variables provided in `.env` will be automatically exported if the **Makefile** is
used for application running.

To know more about **Makefile**s, please check [this](https://makefiletutorial.com/#why-do-makefiles-exist).

> [!NOTE]
> Instructions on how to install Make in Windows systems will be added soon.

- Make sure you are in the root directory of the project.

- Setup all applications

  - Build the collector application:
       ```bash
           make build/collector
       ```

  - Install requirements for analyser application:
       ```bash
           make install/analyser
       ```

  - Install requirements for dashboard application:
       ```bash
           make install/dashboard
       ``` 

- Follow the [Setup Database](#setup-database) section to launch the PostgreSQL container and ensure the credentials are correctly placed in the secrets folder.


- Follow the [Setup Environment](#setup-environment) section to pass required environment variables correctly.


- Run each application independently. 

> [!WARNING]
> You must run the **analyser** _before_ the **collector**!

  - Analyser
    ```bash
      make run/analyser
    ```

  - Collector
    ```bash
      make run/collector
    ```

  - Dashboard
    ```bash
      make run/dashboard
    ```
  
- The dashboard will be available at http://localhost:5000.