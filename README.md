# 🌐 WebDocs

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?logo=spring)
![Java](https://img.shields.io/badge/Java-23-blue?logo=java)
![Maven](https://img.shields.io/badge/Maven-Build-orange?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

> **WebDocs** is a Spring Boot application that combines static website generation with a unified API gateway for the Mynger and DeepDiary platforms.

---

## 📋 Overview

WebDocs extends the original WEBDOS static site generator (a NetBeans Java project) into a full-featured Spring Boot REST application. It:

- Generates static HTML websites from local directory trees
- Acts as a unified API proxy for **Mynger** (user management, messaging, files)
- Acts as a unified API proxy for **DeepDiary** (projects, tasks, time tracking)
- Provides a **Swagger UI** for interactive API documentation
- Hosts a **Thymeleaf dashboard** at `/`

---

## 🚀 Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| **Java JDK** | 23+ | Installed at `/mnt/c/Program Files/Java/jdk-23` |
| **Maven** | 3.8+ | At `~/.m2/wrapper/dists/apache-maven-3.8.6-bin/.../apache-maven-3.8.6/bin/mvn` |
| **Git** | any | Available via bash |

---

## ⚡ How to Run

### Option 1 — Bash (WSL or Git Bash)

```bash
# Set Java 23
export JAVA_HOME="/mnt/c/Program Files/Java/jdk-23"
export PATH="$JAVA_HOME/bin:$PATH"

# Set Maven
export MVN="$HOME/.m2/wrapper/dists/apache-maven-3.8.6-bin/1ks0nkde5v1pk9vtc31i9d0lcd/apache-maven-3.8.6/bin/mvn"

# Navigate to project
cd "/mnt/c/Users/Administrator/Documents/CodingProjects/WEBDOS"

# Build (skip tests for fast start)
"$MVN" clean package -DskipTests

# Run the jar
java -jar target/webdocs-1.0.0.jar
```

### Option 2 — Run directly with Maven (no build step needed)

```bash
export JAVA_HOME="/mnt/c/Program Files/Java/jdk-23"
export PATH="$JAVA_HOME/bin:$PATH"
export MVN="$HOME/.m2/wrapper/dists/apache-maven-3.8.6-bin/1ks0nkde5v1pk9vtc31i9d0lcd/apache-maven-3.8.6/bin/mvn"

cd "/mnt/c/Users/Administrator/Documents/CodingProjects/WEBDOS"
"$MVN" spring-boot:run
```

### Option 3 — IntelliJ IDEA

1. **File → Open** → select the `WEBDOS` folder
2. IntelliJ auto-detects the `pom.xml` and imports dependencies
3. Set Project SDK to **Java 23** (File → Project Structure → SDK)
4. Open `src/main/java/com/webdocs/WebDocsApplication.java`
5. Click the **▶ green Run button** next to `main()`

### Option 4 — NetBeans

1. **File → Open Project** → select the `WEBDOS` folder
2. NetBeans detects `pom.xml` and loads it as a Maven project
3. Right-click the project → **Run** (or press F6)

---

## 🌐 Access the Application

Once running, open your browser:

| URL | Description |
|-----|-------------|
| http://localhost:8080/ | Main dashboard |
| http://localhost:8080/swagger-ui.html | Interactive API docs |
| http://localhost:8080/api/webdocs/health | Health check |
| http://localhost:8080/v3/api-docs | Raw OpenAPI JSON |

---

## 🧪 Running Tests

```bash
export JAVA_HOME="/mnt/c/Program Files/Java/jdk-23"
export PATH="$JAVA_HOME/bin:$PATH"
export MVN="$HOME/.m2/wrapper/dists/apache-maven-3.8.6-bin/1ks0nkde5v1pk9vtc31i9d0lcd/apache-maven-3.8.6/bin/mvn"

cd "/mnt/c/Users/Administrator/Documents/CodingProjects/WEBDOS"
"$MVN" test
```

Expected output:
```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Test classes:
- `SiteGeneratorServiceTest` — integration tests with real temp directories
- `MyngerServiceTest` — uses MockWebServer to test Mynger API proxying
- `DeepDiaryServiceTest` — uses MockWebServer to test DeepDiary API proxying

---

## 🗺️ API Endpoints

### WebDocs (Site Generator)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/webdocs/generate` | Generate static site from directory path; returns ZIP |
| GET | `/api/webdocs/templates` | List available HTML templates |
| POST | `/api/webdocs/preview` | Preview a page from template + content map |
| GET | `/api/webdocs/health` | Health check with upstream API status |

### Auth (→ Mynger)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signin` | Sign in |
| POST | `/api/auth/signup` | Sign up |
| POST | `/api/auth/confirm-signup` | Confirm registration |

### Users (→ Mynger)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List users |
| POST | `/api/users` | Create user |
| GET | `/api/users/{username}` | Get user |
| PATCH | `/api/users/{username}` | Update user |
| DELETE | `/api/users/{username}` | Delete user |

### Notifications (→ Mynger)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/notifications/send-otp` | Send OTP |
| POST | `/api/notifications/verify-otp` | Verify OTP |
| POST | `/api/notifications/forgot-password` | Forgot password |
| POST | `/api/notifications/send-email` | Send email |

### Messages (→ Mynger)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/messages` | List messages |
| POST | `/api/messages` | Create message |
| GET | `/api/messages/{id}` | Get message |
| PATCH | `/api/messages/{id}` | Update message |
| DELETE | `/api/messages/{id}` | Delete message |

### Rooms (→ Mynger)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/rooms` | List rooms |
| POST | `/api/rooms` | Create room |
| GET | `/api/rooms/{id}` | Get room |
| PATCH | `/api/rooms/{id}` | Update room |
| DELETE | `/api/rooms/{id}` | Delete room |

### Files (→ Mynger)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/files/presigned-url` | Get presigned upload URL |

### Projects (→ DeepDiary)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | List projects |
| POST | `/api/projects` | Create project |
| GET | `/api/projects/{id}` | Get project |
| PATCH | `/api/projects/{id}` | Update project |
| DELETE | `/api/projects/{id}` | Delete project |

### Tasks (→ DeepDiary)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | List tasks |
| POST | `/api/tasks` | Create task |
| GET | `/api/tasks/{id}` | Get task |
| PATCH | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

### Time (→ DeepDiary)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/time` | Get time entries |
| POST | `/api/time` | Create time entry |

---

## ⚙️ Configuration

`src/main/resources/application.yml`:

```yaml
server:
  port: 8080

webdocs:
  mynger-api-url: http://api.mynger.com
  deepdiary-api-url: http://api.deepdiary.mynger.com
  output-dir: ${java.io.tmpdir}/webdocs-output

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     WebDocs (port 8080)                     │
│                                                             │
│  ┌─────────────┐   ┌──────────────┐   ┌─────────────────┐  │
│  │  Dashboard  │   │  Swagger UI  │   │  REST API       │  │
│  │  (/)        │   │  /swagger-ui │   │  /api/**        │  │
│  └─────────────┘   └──────────────┘   └─────────────────┘  │
│                                               │             │
│           ┌───────────────┬──────────────────┤             │
│           ▼               ▼                  ▼             │
│  ┌──────────────┐ ┌─────────────┐ ┌──────────────────┐    │
│  │ SiteGenerator│ │ MyngerSvc   │ │ DeepDiarySvc     │    │
│  │ Service      │ │ (WebClient) │ │ (WebClient)      │    │
│  └──────────────┘ └─────────────┘ └──────────────────┘    │
│          │                │                  │              │
│          ▼                ▼                  ▼              │
│  ┌──────────────┐ ┌───────────────┐ ┌────────────────────┐ │
│  │ sitegenerator│ │ api.mynger.com│ │api.deepdiary.mynger│ │
│  │ (Java engine)│ │               │ │.com                │ │
│  └──────────────┘ └───────────────┘ └────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 📸 Screenshots

Screenshots and build artifacts can be found in the `bin/` folder.

---

## 📦 Package Structure

```
com.webdocs
├── WebDocsApplication.java
├── config/
│   ├── SwaggerConfig.java
│   └── WebClientConfig.java
├── controller/
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── FileController.java
│   ├── MessageController.java
│   ├── NotificationController.java
│   ├── ProjectController.java
│   ├── RoomController.java
│   ├── TaskController.java
│   ├── TimeController.java
│   ├── UserController.java
│   └── WebDocsController.java
├── dto/
│   ├── ConfirmSignUpDto.java
│   ├── ForgotPasswordRequest.java
│   ├── GenerateSiteRequest.java
│   ├── GenericRequest.java
│   ├── HealthResponse.java
│   ├── OtpRequest.java
│   ├── PreviewRequest.java
│   ├── SignInRequestDto.java
│   └── SignUpRequestDto.java
├── service/
│   ├── DeepDiaryService.java
│   ├── MyngerService.java
│   └── SiteGeneratorService.java
└── sitegenerator/
    ├── Content.java
    ├── Directory.java
    ├── DirectoryMapper.java
    ├── FileTreeWalker.java
    ├── HTMLItem.java
    ├── IOManager.java
    ├── PageCreator.java
    └── PathItem.java
```

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

*Built with ❤️ using Spring Boot 3, WebClient, SpringDoc OpenAPI, and Thymeleaf.*
