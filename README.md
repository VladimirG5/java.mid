# Abysalto Hiring Assignment

A simple full-stack e-commerce application that allows users to browse products, manage a shopping cart, and maintain a list of favourite products. Product data is sourced from the public [DummyJSON](https://dummyjson.com) API and cached in memory to minimise network traffic.

---

## Features

- **Authentication** — register and log in with JWT-based stateless auth
- **Product browsing** — paginated product listing with configurable page size and sorting
- **Shopping cart** — add products, adjust quantities, remove items; decreasing to zero auto-removes the item
- **Favourites** — mark/unmark products as favourites, view the full favourites list
- **User profile** — view account details for the authenticated user

---

## Tech Stack

### Backend
| Technology                 | Purpose                                    |
|----------------------------|--------------------------------------------|
| Java 17                    | Language                                   |
| Spring Boot 3.4.3          | Application framework                      |
| Spring Security + JWT      | Stateless authentication                   |
| Spring Data JDBC           | Database access (no JPA/ORM overhead)      |
| MySQL 8.0                  | Production database                        |
| Liquibase                  | Database schema migrations                 |
| WebClient (Spring WebFlux) | Non-blocking HTTP client for DummyJSON API |
| Caffeine                   | In-memory product cache                    |
| Spring Actuator            | Health and metrics endpoints               |
| Springdoc OpenAPI 3.0      | Interactive API documentation (Swagger UI) |
| Lombok                     | Boilerplate reduction                      |
| Logback                    | Loggging framework                         |


### Frontend
| Technology | Purpose |
|---|---|
| React 18 + TypeScript | UI framework |
| Vite 5 | Dev server and build tool |
| React Router 6 | Client-side routing |
| Axios | HTTP client with JWT interceptor |
| Nginx | Static file serving + API reverse proxy (Docker/K8s) |

### Testing
| Technology | Purpose |
|---|---|
| JUnit 5 + Mockito | Unit tests |
| Spring MockMvc | Controller-layer slice tests |
| WireMock | HTTP client contract tests for DummyJSON |
| H2 (MySQL mode) + Liquibase | Integration tests against a real in-memory database |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker | Containerisation (multi-stage builds) |
| Kubernetes / Minikube | Local cluster deployment |

---

## Prerequisites

| Tool | Mode 1 (Local) | Mode 2 (Docker) | Mode 3 (Kubernetes) |
|---|:---:|:---:|:---:|
| Java 17+ | ✅ | | |
| Maven 3.9+ | ✅ | | |
| Node.js 20+ | ✅ | | |
| MySQL 8.0 | ✅ | | |
| Docker | | ✅ | ✅ |
| Minikube | | | ✅ |
| kubectl | | | ✅ |

---

## Mode 1 — Local Development

Run the backend and frontend as separate processes. Vite proxies all `/api` requests to the Spring Boot server, so no CORS configuration is needed.

### 1. Start MySQL

Ensure a MySQL 8.0 instance is running on `localhost:3306`. The application will create the `assignment` database automatically on first start.

```bash
# default credentials expected by application.properties
# host:     localhost:3306
# user:     root
# password: password
```

### 2. Start the backend

```bash
mvn spring-boot:run
```

The API is available at `http://localhost:8080/api`.
Swagger UI is available at `http://localhost:8080/api/swagger-ui/index.html`.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

### 4. Open the application

```
http://localhost:5173
```

---

## Mode 2 — Docker

Build and run all three components (MySQL, backend, frontend) as Docker containers on a shared network.

### 1. Create a shared network

```bash
docker network create java-mid-network
```

### 2. Start MySQL

```bash
docker run -d \
  --name mysql \
  --network java-mid-network \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=assignment \
  -p 3306:3306 \
  mysql:8.0
```

### 3. Build and start the backend

```bash
# Build
docker build -t java-mid-backend:latest .

# Run
docker run -d \
  --name backend \
  --network java-mid-network \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/assignment?allowPublicKeyRetrieval=true&useSSL=false" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -p 8080:8080 \
  java-mid-backend:latest
```

### 4. Build and start the frontend

```bash
# Build
docker build -t java-mid-frontend:latest ./frontend

# Run
docker run -d \
  --name frontend \
  --network java-mid-network \
  -p 3000:80 \
  java-mid-frontend:latest
```

The Nginx reverse proxy inside the container forwards `/api` requests to the `backend` container by hostname.

### 5. Open the application

```
http://localhost:3000
```

Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`

### Stopping

```bash
docker stop frontend backend mysql
docker rm frontend backend mysql
docker network rm java-mid-network
```

---

## Mode 3 — Kubernetes (Minikube)

Deploy the full stack to a local Minikube cluster. The frontend is exposed on a fixed NodePort (`30080`); the backend and MySQL are internal cluster services.

### 1. Start Minikube

```bash
minikube start
```

### 2. Point Docker to Minikube's daemon

This makes images built locally available to Minikube without pushing to a registry.

```bash
# Linux / macOS
eval $(minikube docker-env)

# Windows (PowerShell)
& minikube -p minikube docker-env | Invoke-Expression
```

### 3. Build images inside Minikube

```bash
docker build -t java-mid-backend:latest .
docker build -t java-mid-frontend:latest ./frontend
```

### 4. Apply the manifests

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/mysql-secret.yaml
kubectl apply -f k8s/mysql.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
```

### 5. Wait for all pods to become ready

```bash
kubectl get pods -n java-mid --watch
```

All three pods (`mysql`, `backend`, `frontend`) should reach `Running` / `Ready` status. The backend readiness probe waits for `/api/actuator/health` to return 200 before accepting traffic.

### 6. Get the Minikube IP

```bash
minikube ip
```

### 7. Open the application

```
http://<minikube-ip>:30080
```

For example, if `minikube ip` returns `192.168.49.2`:

```
http://192.168.49.2:30080
```

### Teardown

```bash
kubectl delete namespace java-mid
```

---

## API Documentation

Swagger UI is served at `/api/swagger-ui/index.html` in all modes where the backend port is accessible. All secured endpoints require a `Bearer <token>` header, which can be set directly in the Swagger UI after logging in via `POST /api/auth/login`.

---

## Running Tests

```bash
mvn test
```

The test suite uses an H2 in-memory database (MySQL compatibility mode) with Liquibase migrations, so no external database is required.
