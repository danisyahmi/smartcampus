# Smartcampus
A distributed backend platform that delivers core campus services to multiple front-end channels (web portal, mobile app, and an administrative console) across a single university campus.

---

## System Architecture Overview

SmartCampus Connect operates as a distributed system where each business domain is isolated into its own deployable Maven module running an independent Spring Boot application.

---

## 🧩 Core Spring Boot Services

### 1. Student Profile Service (`student-profile-service`)
* **Port:** `8081`
* **Internal Pattern:** Controller (`@RestController`) ➡️ Service (`@Service`) ➡️ Model (In-memory Java Collections)
* **Responsibilities:** Exposes clean RESTful CRUD operations to manage student academic tracks and demographic data. 
* **Inter-Service Role:** Serves as a primary synchronous lookup provider for the enrollment engine.

### 2. Course Enrolment Service (`course-enrolment-service`)
* **Port:** `8082`
* **Internal Pattern:** Controller ➡️ Service ➡️ Model
* **Responsibilities:** Manages dynamic course capacity thresholds, additions, and multi-phase drop sequences.
* **Dependencies:** Makes synchronous HTTP calls out to the *Student Profile Service* at runtime to verify student records before committing enrollment seats.

### 3. Notification Service (`notification-service`)
* **Port:** `8083`
* **Internal Pattern:** TCP Socket Consumer Thread ➡️ Service ➡️ Notification DTO
* **Responsibilities:** Acts strictly as a passive asynchronous consumer background worker.
* **Operational Model:** Listens continuously on its dedicated TCP Socket server connection. When enrollment commits, payments resolve, or library events occur, it receives the raw socket stream data payload and processes the real-time notification alert context.

### 4. Library / Booking Service (`library-booking-service`)
* **Port:** `8084`
* **Internal Pattern:** Controller (Dual REST/SOAP) ➡️ Service ➡️ Model
* **Responsibilities:** Manages physical campus asset distributions, including study room reservations and library book loans.
* **Legacy Simulation Integration:** Exposes at least one transaction endpoint through an explicit **SOAP/WSDL** interface structure (Week 7 Milestone) alongside normal REST controllers to mimic deep enterprise system hooks.

### 5. Reporting / Analytics Service (`reporting-analytics-service`)
* **Port:** `8085`
* **Internal Pattern:** Controller ➡️ Service ➡️ Aggregation Data Models
* **Responsibilities:** Aggregates cross-domain datasets (e.g., total active enrollment volumes distributed per academic degree scheme) by systematically scanning or pulling relevant reference metrics from neighboring service blocks.

---

## Protocol & Communication Taxonomy

| Service Target | Port | Protocol Pattern | Interface Mechanism | Target Action / Responsibility |
| :--- | :--- | :--- | :--- | :--- |
| **Student Profile** | `8081` | Synchronous | REST / HTTP (JSON) | Demographic / Academic Profile CRUD |
| **Course Enrolment** | `8082` | Synchronous | REST / HTTP (JSON) | Semester registration & capacity checks |
| **Notification** | `8083` | Asynchronous | TCP Socket Stream | Custom Producer-Consumer Event listener |
| **Library / Booking** | `8084` | Hybrid | REST (JSON) + SOAP (XML) | Book loans and room reservations |
| **Reporting / Analytics**| `8085` | Read-Oriented | REST / HTTP (JSON) | Data compile-point summaries & counts |

---

## Repository & Maven Multi-Module Workspace Structure

The project uses a parent Maven setup containing separate sub-modules for every application block. Each module maintains its own dedicated `pom.xml` file to house its distinct framework requirements.

```bash
smartcampus-connect/
├── pom.xml                                 # Parent Maven POM (Manages collective module scopes)
│
├── student-profile-service/
│   ├── pom.xml                             # Module POM
│   └── src/main/java/com/campus/profile/
│       ├── controller/                     # REST Controllers (@RestController)
│       ├── service/                        # Business logic handles local variables
│       └── model/                          # Stateful in-memory collection models
│
├── course-enrolment-service/
│   ├── pom.xml                             # Module POM
│   └── src/main/java/com/campus/enrolment/
│       ├── controller/                     # Enrolment Controllers
│       ├── service/                        # Synchronous WebClient lookup & capacity logic
│       └── model/                          # Enrolment state data
│
├── notification-service/
│   ├── pom.xml                             # Module POM
│   └── src/main/java/com/campus/notification/
│       ├── controller/                     # Optional administrative view controllers
│       ├── service/                        # Background TCP Socket listener consumer loop
│       └── model/                          # In-memory notification audit log storage
│
├── library-booking-service/
│   ├── pom.xml                             # Module POM (Includes Spring-WS web services)
│   └── src/main/java/com/campus/library/
│       ├── controller/                     # Dual: REST Controllers & Legacy SOAP @Endpoints
│       ├── service/                        # Asset reservation & legacy translation logic
│       └── model/                          # Room and loan structures
│
└── reporting-analytics-service/
    ├── pom.xml                             # Module POM
    └── src/main/java/com/campus/analytics/
        ├── controller/                     # Summary reporting endpoints
        ├── service/                        # Data gathering & evaluation engine
        └── model/                          # Analytical metric matrices