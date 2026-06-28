# SmartCampus Connect

A distributed microservices platform delivering core campus services — student profile management, course enrolment, event-driven notifications, resource booking, and analytics — over REST, AMQP, and SOAP.

Built with **Java 21**, **Spring Boot 3.5**, **RabbitMQ**, **MySQL**, and **Docker Compose**. All six services, the API gateway, message broker, and database spin up with a single command.

---

## Prerequisites

Install these on a clean machine before you begin. Verify each with the command shown.

| Tool | Minimum Version | Verify |
|---|---|---|
| Docker Desktop | 24.x | `docker --version` |
| Docker Compose | 2.x (bundled with Docker Desktop) | `docker compose version` |
| Java JDK | 21 | `java -version` |
| Apache Maven | 3.9 | `mvn -version` |
| Git | any | `git --version` |

> **Windows users:** use PowerShell or Git Bash for all commands below.  
> **macOS / Linux:** use any terminal.

---

## Quick Start — Full Stack in One Command

Clone the repository and start every service, database, broker, and gateway together:

```bash
git clone <your-repo-url> smartcampus
cd smartcampus
docker compose up -d
```

That single command:
1. Builds Docker images for all six services from source
2. Starts MySQL, RabbitMQ, all six Spring Boot / JAX-WS services, Nginx gateway, and the frontend
3. Runs `sqls/init.sql` to create all five database schemas
4. Runs `sqls/seed-data.sql` to insert sample students, courses, enrollments, bookings, and notifications
5. Waits for each service to pass its health check before starting dependents

**Wait approximately 60–90 seconds** on first run (Maven downloads dependencies during image build). On subsequent runs the stack is up in under 15 seconds.

---

## Verifying the Stack is Up

Run this after `docker compose up -d` to confirm every container is healthy:

```bash
docker compose ps
```

All containers should show **`healthy`** or **`running`** in the STATUS column. Then hit the gateway health endpoint:

```bash
curl http://localhost/health
```

Expected response:

```json
{"gateway": "UP"}
```

Check individual service health endpoints:

```bash
curl http://localhost/api/students/health
curl http://localhost/api/enrollments/health
curl http://localhost/api/notifications/health
curl http://localhost/api/bookings/health
curl http://localhost/api/reports/health
curl http://localhost:8888/ws/library?wsdl
```

---

## Service Map

All REST traffic goes through the **Nginx API Gateway on port 80**. Services are also directly reachable on their own ports for debugging.

| Service | Container Name | Direct Port | Gateway Path |
|---|---|---|---|
| API Gateway (Nginx) | `gateway` | `80` | — |
| Student Profile | `student-svc` | `8081` | `/api/students` |
| Course Enrollment | `enrollment-svc` | `8082` | `/api/enrollments`, `/api/courses` |
| Notification | `notification-svc` | `8083` | `/api/notifications` |
| Booking | `booking-api-svc` | `8084` | `/api/bookings` |
| Report / Analytics | `report-svc` | `8085` | `/api/reports` |
| Library SOAP | `library-soap-svc` | `8888` | `/ws/library` |
| Frontend (static) | `frontend-module` | `3000` | — |
| RabbitMQ AMQP | `rabbitmq-server` | `5672` | — |
| RabbitMQ Management UI | `rabbitmq-server` | `15672` | — |
| MySQL | `microservices-mysql` | `3306` | — |

**Frontend:** open `http://localhost:3000` in your browser.  
**RabbitMQ Management UI:** open `http://localhost:15672` — default credentials `guest` / `guest`.

---

## Default Credentials

| Service | Username | Password |
|---|---|---|
| MySQL root | `root` | `1234` |
| RabbitMQ | `guest` | `guest` |

---

## Build Without Docker (Maven)

To compile and run unit tests locally without Docker:

```bash
mvn clean install
```

To skip tests and just build all JARs:

```bash
mvn clean install -DskipTests
```

To build a single service module only (example — Student service):

```bash
cd student
mvn clean install
```

---

## Alternative Run Scripts

Convenience wrapper scripts are included at the repository root:

**macOS / Linux:**

```bash
# Start the stack
./run.sh up

# Stop the stack
./run.sh down
```

**Windows:**

```bat
:: Start the stack
run.bat up

:: Stop the stack
run.bat down
```

Both scripts call `docker compose up -d` and `docker compose down` respectively.

---

## Stopping the Stack

```bash
# Stop all containers (data is preserved in the db-data volume)
docker compose down

# Stop and delete all data (full clean reset)
docker compose down -v
```

---

## Running the API Test Script

A full end-to-end test script is included at `test_api.sh`. It exercises every endpoint across all services and reports pass/fail counts.

```bash
# Make it executable (first time only)
chmod +x test_api.sh

# Run all tests via the API Gateway (default)
./test_api.sh

# Run all tests hitting each service directly on its own port
./test_api.sh direct
```

The script prints coloured pass/fail output and exits with code `0` if all tests pass or `1` if any fail — compatible with CI pipelines.

---

## Project Structure

```
smartcampus/
├── pom.xml                   # Parent Maven POM — manages all modules
├── docker-compose.yml        # Full stack definition
├── nginx/
│   └── nginx.conf            # API Gateway routing rules
├── sqls/
│   ├── init.sql              # Creates all five MySQL schemas
│   └── seed-data.sql         # Inserts sample data
├── student/                  # Student Profile Service (port 8081)
├── enrollment/               # Course Enrollment Service (port 8082)
├── notification/             # Notification Service (port 8083)
├── booking/                  # Booking Service (port 8084)
├── report/                   # Report / Analytics Service (port 8085)
├── library_system/           # Library SOAP Service (port 8888, standalone JAX-WS)
├── frontend/                 # Static HTML frontend (port 3000)
├── run.sh                    # Linux/macOS start/stop helper
├── run.bat                   # Windows start/stop helper
└── test_api.sh               # Full end-to-end API test script
```

Each Spring Boot module follows the same internal layout:

```
<service>/
├── pom.xml
└── src/
    └── main/
        ├── java/com/smartcampus/<service>/
        │   ├── controllers/      # @RestController — HTTP layer
        │   ├── services/         # @Service — business logic
        │   ├── models/           # @Entity — JPA data models
        │   ├── repositories/     # @Repository — Spring Data JPA
        │   ├── dto/              # Event/transfer objects
        │   └── config/           # RabbitMQ, RestTemplate config
        └── resources/
            └── application.properties
```

---

## Communication Patterns

| Pattern | Used Between | Technology |
|---|---|---|
| Synchronous REST | All clients → Gateway → Services | HTTP/JSON via `@RestController` |
| Synchronous REST (inter-service) | Enrollment → Student (validation) | `RestTemplate` with 3 s timeout |
| Asynchronous messaging | Student/Enrollment → Notification | RabbitMQ AMQP, TopicExchange `notification.exchange` |
| SOAP/XML (legacy integration) | Booking → Library | SAAJ client → JAX-WS `@WebService` |
| Data aggregation | Report → all four services | `RestTemplate` HTTP GET |

---

## Database Schemas

Each service owns exactly one MySQL schema. No cross-schema queries exist.

| Service | Schema | Tables |
|---|---|---|
| Student | `student_db` | `students` |
| Enrollment | `enrollment_db` | `courses`, `enrollments` |
| Notification | `notification_db` | `notifications` |
| Booking | `booking_db` | `bookings` |
| Report | *(none — stateless aggregator)* | — |

---

## Seed Data

The following sample records are loaded automatically on first startup:

**Students:** B032510001 (Zayn Malik), B032510002 (Harry Styles), B032510003 (Niall Horan), B032510034 (Adib Nazli)

**Courses:** BITS1223 (Introduction to Computer Science), BITS1233 (Software Architecture & Microservices), BITS1112 (Programming Techniques)

**Enrollments:** B032510001 → BITS1223, B032510002 → BITS1233, B032510003 → BITS1233

---

## Troubleshooting

**Containers keep restarting:**

```bash
docker compose logs <container-name>
```

Example:

```bash
docker compose logs student-svc
docker compose logs rabbitmq-server
docker compose logs microservices-mysql
```

**MySQL not ready in time:** on slower machines the MySQL healthcheck may take longer than expected. Wait 30 seconds and run `docker compose ps` again.

**Port already in use:** ensure nothing else is running on ports 80, 3000, 3306, 5672, 8081–8085, 8888, or 15672 before starting the stack.

**Full reset (wipe all data and rebuild images):**

```bash
docker compose down -v --rmi all
docker compose up -d --build
```

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| REST | Spring MVC (`@RestController`) |
| ORM | Spring Data JPA / Hibernate |
| Messaging | Spring AMQP / RabbitMQ |
| SOAP (server) | JAX-WS (`jaxws-rt` 4.0.2) |
| SOAP (client) | SAAJ (`jakarta.xml.soap` 3.0.1) |
| Database | MySQL (latest) |
| Gateway | Nginx (alpine) |
| Containerisation | Docker Compose |
| Build | Apache Maven (multi-module) |