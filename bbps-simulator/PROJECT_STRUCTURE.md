# BBPS Simulator

## Project Overview
The BBPS (Bharat Bill Payment System) Simulator is a lightweight Spring Boot application designed to emulate the central NPCI BBPS environment for local integration testing. It acts as a mock gateway by receiving BillFetch and BillPayment requests, forwarding them to the local `biller-integrator` instance, and generating simplified XML Acknowledgments (`<Ack>`) for the integrator's responses.

## Directory Tree

```
.
├── pom.xml
├── postman
│   ├── BBPS-Simulator-Biller-Integration.json
│   └── BBPS-Simulator.postman_collection.json
└── src
    └── main
        ├── java
        │   └── bharat
        │       └── connect
        │           └── simulator
        │               ├── BbpsSimulatorApplication.java
        │               └── controller
        │                   └── BbpsController.java
        └── resources
            └── application.properties

9 directories, 6 files
```

## Detailed Breakdown

### Folders
- **`postman/`**: Contains exported Postman collections to help developers manually test the API interactions and verify integration scenarios.
- **`src/`**: The standard Maven structure housing all application source code and resources.
- **`src/main/java/bharat/connect/simulator/`**: The root Java package containing the application logic.
- **`src/main/java/bharat/connect/simulator/controller/`**: Houses the Spring MVC REST controllers that define the API endpoints exposed by the simulator.
- **`src/main/resources/`**: Holds configuration files and property files that dictate application behavior at runtime.

### Files
- **`pom.xml`**: The Maven Project Object Model file that configures the build lifecycle, Java versions, and specifies dependencies like `spring-boot-starter-web`.
- **`postman/BBPS-Simulator-Biller-Integration.json`**: A Postman collection specifically crafted to orchestrate and test end-to-end integration flows between the simulator and the biller integrator.
- **`postman/BBPS-Simulator.postman_collection.json`**: A Postman collection containing standalone API requests to hit the simulator's mocking endpoints directly.
- **`src/main/java/bharat/connect/simulator/BbpsSimulatorApplication.java`**: The main entry class containing the `SpringApplication.run()` method to bootstrap and launch the Spring Boot framework.
- **`src/main/java/bharat/connect/simulator/controller/BbpsController.java`**: The core API router and logic handler. It maps endpoints for fetching and paying bills, forwards exact XML bodies using a `RestTemplate` directly to the integrator, and issues XML Acknowledgments.
- **`src/main/resources/application.properties`**: Declares Spring configuration properties, including setting the simulator's server port (`8112`) and defining the routing URL for the integrator (`integrator.baseUrl=http://localhost:8111`).

## Key Entry Points

- **`src/main/java/bharat/connect/simulator/BbpsSimulatorApplication.java`**: Start reading here to understand where the application boots up.
- **`src/main/java/bharat/connect/simulator/controller/BbpsController.java`**: Start here to see all the API endpoints the mock server provides to handle the primary XML requests.
- **`src/main/resources/application.properties`**: Review this file to see how networking and backend forwarding routes are configured.

## Tech Stack

- **Core Language**: Java 17
- **Framework**: Spring Boot (v2.7.2)
- **Web Layer**: Spring Web (Spring MVC) for REST APIs
- **Build Tool**: Maven
- **Protocols/Formats**: REST / HTTP, XML (`application/xml`) for request and response bodies
