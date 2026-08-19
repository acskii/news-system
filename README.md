# News System

A self‑hosted, modular news aggregation platform that collects articles daily, 
performs trend analysis, and presents an interactive dashboard.

## Documentation

Check the technical documentation [here](./DOCUMENTATION.md) to learn more about setting up this project on your own machine.

## Run

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

- Follow the [Setup Database](./DOCUMENTATION.md#setup-database) documentation to launch the PostgreSQL container and ensure the credentials are correctly placed in the secrets folder.


- Follow the [Setup Environment](./DOCUMENTATION.md#setup-environment) documentation to pass required environment variables correctly.


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

## Features

- Daily headline news collection

- Automated trend analysis (spike detection, breaking news clustering)

- Responsive web dashboard with filtering and pagination

- Applications are independently deployable using **modular** architecture