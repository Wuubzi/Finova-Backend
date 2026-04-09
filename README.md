

<h1 align="center">💰 Finova — Banking Microservices Platform</h1>

<p align="center">
  <strong>A production-ready, event-driven banking backend built with Kotlin, Spring Boot 4, and deployed on AWS using a full CI/CD pipeline.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2-purple?logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0-green?logo=springboot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java"/>
  <img src="https://img.shields.io/badge/Apache_Kafka-Event_Driven-black?logo=apachekafka" alt="Kafka"/>
  <img src="https://img.shields.io/badge/Redis-Cache-red?logo=redis" alt="Redis"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Containerized-blue?logo=docker" alt="Docker"/>
  <img src="https://img.shields.io/badge/AWS-EC2_|_ECR_|_S3-orange?logo=amazonaws" alt="AWS"/>
  <img src="https://img.shields.io/badge/Jenkins-CI/CD-red?logo=jenkins" alt="Jenkins"/>
  <img src="https://img.shields.io/badge/SonarQube-Code_Quality-blue?logo=sonarqube" alt="SonarQube"/>
</p>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Microservices](#-microservices)
- [Event-Driven Architecture](#-event-driven-architecture)
- [Security](#-security)
- [CI/CD Pipeline](#-cicd-pipeline)
- [API Endpoints](#-api-endpoints)
- [Infrastructure](#-infrastructure)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)

---

## 🌟 Overview

**Finova** is a full-featured banking backend platform built with a **microservices architecture**. It supports user registration with profile image uploads (scanned by **VirusTotal** for malware), account management, and financial transactions (deposits, withdrawals, transfers) — all orchestrated through **Apache Kafka** events and deployed to **AWS EC2** via a **Jenkins CI/CD pipeline**.

### Key Highlights

- 🏗️ **Hexagonal Architecture** (Ports & Adapters) in every microservice
- 📨 **Event-Driven Architecture** with a single Kafka topic for all transaction lifecycle events
- 🔑 **JWT Authentication** with refresh tokens, OTP-based password recovery, and gateway-level authorization
- 🛡️ **VirusTotal Integration** — every uploaded profile image is scanned for malware before storage
- ☁️ **AWS S3** for secure file storage
- ⚡ **Redis Caching** for high-performance reads on accounts & transactions
- 📬 **Automated Email Notifications** with rich HTML templates for every transaction event
- 🚀 **Full CI/CD** with Jenkins, SonarQube quality gates, Docker, AWS ECR & EC2
- 🧪 **Comprehensive Testing** with JUnit 5, Mockito-Kotlin, Testcontainers, and WireMock

---

## 🏗 Architecture

### System Architecture

```
                          ┌──────────────┐
                          │   Client /   │
                          │   Frontend   │
                          └──────┬───────┘
                                 │
                                 ▼
                        ┌────────────────┐
                        │    Gateway     │ ← JWT validation + routing
                        │   (port 8000)  │
                        └───────┬────────┘
                                │
                 ┌──────────────┼──────────────────┐
                 │              │                   │
                 ▼              ▼                   ▼
          ┌────────────┐ ┌────────────┐    ┌──────────────┐
          │    Auth    │ │    User    │    │   Account    │
          │  Service   │ │  Service   │    │   Service    │
          └─────┬──────┘ └────────────┘    └──────┬───────┘
                │                                  │
                │         ┌────────────┐           │
                │         │Transaction │           │
                │         │  Service   │◄──────────┘
                │         └─────┬──────┘    (WebClient)
                │               │
                ▼               ▼
          ┌──────────────────────────┐     ┌──────────────┐
          │      Apache Kafka       │────►│ Notification │
          │  (transactions.events)  │     │   Service    │
          └──────────────────────────┘     └──────┬───────┘
                                                   │
                                                   ▼
                                             📧 Email (SMTP)
```

### Hexagonal Architecture (per microservice)

```
┌─────────────────────────────────────────────────────┐
│                   Infrastructure                     │
│  ┌────────────┐  ┌────────────┐  ┌───────────────┐  │
│  │ Controllers│  │  Adapters  │  │  Kafka/Redis  │  │
│  │  (REST)    │  │(Repository)│  │  (Consumers)  │  │
│  └─────┬──────┘  └─────┬──────┘  └───────┬───────┘  │
│        │               │                 │           │
│  ┌─────┴───────────────┴─────────────────┴────────┐  │
│  │              Application Layer                  │  │
│  │   ┌──────────┐  ┌──────────┐  ┌─────────────┐  │  │
│  │   │ Use Cases│  │   DTOs   │  │Ports (in/out)│  │  │
│  │   │(Services)│  │(Req/Res) │  │ (Interfaces) │  │  │
│  │   └──────────┘  └──────────┘  └─────────────┘  │  │
│  └────────────────────────┬───────────────────────┘  │
│                           │                          │
│  ┌────────────────────────┴───────────────────────┐  │
│  │               Domain Layer                      │  │
│  │          ┌──────────────────┐                   │  │
│  │          │   Domain Models  │                   │  │
│  │          └──────────────────┘                   │  │
│  └─────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

---

## 🛠 Tech Stack

| Category | Technologies |
|----------|-------------|
| **Language** | Kotlin 2.2, Java 21 |
| **Framework** | Spring Boot 4.0, Spring Cloud 2025.1 |
| **Build Tool** | Gradle (Kotlin DSL) |
| **Databases** | PostgreSQL 16 (one per service) |
| **Caching** | Redis |
| **Messaging** | Apache Kafka |
| **API Gateway** | Spring Cloud Gateway (reactive) |
| **Service Discovery** | Netflix Eureka |
| **Centralized Config** | Spring Cloud Config Server (Git-backed) |
| **Authentication** | JWT (access + refresh tokens), Spring Security, OAuth2 Resource Server |
| **File Storage** | AWS S3 (with Spring Cloud AWS 4.0) |
| **Malware Scanning** | VirusTotal API |
| **Email** | Spring Mail (SMTP) with HTML templates |
| **Database Migration** | Flyway |
| **Containerization** | Docker, Docker Compose |
| **CI/CD** | Jenkins (declarative pipeline) |
| **Code Quality** | SonarQube, JaCoCo (code coverage) |
| **Cloud** | AWS EC2, AWS ECR, AWS S3 |
| **Testing** | JUnit 5, Mockito-Kotlin, Testcontainers, WireMock, WebTestClient |

---

## 📦 Microservices

| Service | Port (Docker) | Database | Description |
|---------|:---:|:---:|-------------|
| **Eureka** | 8761 | — | Service discovery & registry |
| **Config** | 8888 | — | Centralized configuration from Git repo |
| **Gateway** | 8000 | — | API Gateway with JWT filter & load balancing |
| **Auth** | 8081 | `auth_db` | Registration, login, logout, OTP, password recovery, refresh tokens |
| **User** | 8082 | `user_db` | User profile CRUD |
| **Account** | 8083 | `account_db` | Bank account management (create, block, unblock, balance) |
| **Transaction** | 8084 | `transaction_db` | Deposits, withdrawals, transfers |
| **Notification** | 8085 | — | Kafka consumer → email notifications |

---

## 📨 Event-Driven Architecture

All transaction-related communication uses a **single Kafka topic** (`transactions.events`) with event types to manage the full transaction lifecycle:

```
┌─────────────────┐     TRANSACTION_CREATED     ┌─────────────────┐
│   Transaction   │ ──────────────────────────► │    Account      │
│    Service      │                              │    Service      │
│                 │ ◄────────────────────────── │                 │
│                 │  TRANSACTION_COMPLETED /     │  (balance       │
│                 │  TRANSACTION_FAILED          │   validation)   │
└────────┬────────┘                              └─────────────────┘
         │
         │  All events also consumed by:
         ▼
┌─────────────────┐
│  Notification   │ → Sends HTML emails for:
│    Service      │   • Transaction Created (⏳ In Progress)
│                 │   • Transaction Completed (✅ Success)
└─────────────────┘   • Transaction Failed (❌ Failed)
```

### Event Types

| Event | Producer | Consumers | Description |
|-------|----------|-----------|-------------|
| `TRANSACTION_CREATED` | Transaction Service | Account, Notification | New transaction initiated |
| `TRANSACTION_COMPLETED` | Account Service | Transaction, Notification | Balance updated successfully |
| `TRANSACTION_FAILED` | Account Service | Transaction, Notification | Insufficient funds / account blocked |

### Additional Kafka Topics

| Topic | Producer | Consumer | Description |
|-------|----------|----------|-------------|
| `user-created` | Auth Service | User Service | Sync user profile data on registration |
| `user-deleted` | User Service | Auth, Account | Cascade delete across services |
| `recover-password` | Auth Service | Notification | Send OTP email for password recovery |

---

## 🔑 Security

### Authentication Flow

```
Client → POST /api/v1/auth/login
       ← { token: "eyJ...", refreshToken: "abc-123" }

Client → GET /api/v1/account (Authorization: Bearer eyJ...)
       → Gateway validates JWT → extracts userId → forwards X-User-Id header
       ← { accountId, accountNumber, balance, ... }
```

### Security Features

- **JWT Access Tokens** — Short-lived tokens with `userId` and `email` claims
- **Refresh Tokens** — Stored in database, used to obtain new access tokens
- **BCrypt Password Hashing** — All passwords securely hashed
- **OTP Password Recovery** — 6-digit code sent via email, expires in 10 minutes
- **Gateway-Level Auth Filter** — Validates JWT before requests reach microservices
- **OAuth2 Resource Server** — Each microservice validates tokens independently
- **VirusTotal Malware Scanning** — Profile images scanned before upload to S3
- **File Validation** — Only JPG/PNG allowed, verified by reading image bytes (not just extension)

---

## 🔄 CI/CD Pipeline

The project uses a **Jenkins declarative pipeline** with complete CI/CD:

```
┌─────────────────────────────────────────────────────────────────┐
│                        CI (all branches)                        │
│                                                                 │
│  Checkout → Build → Unit Tests → SonarQube → Quality Gate      │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                        CD (main only)                           │
│                                                                 │
│  Docker Build → Push to ECR → Deploy to EC2 → Health Checks    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### CI Stage
| Step | Description |
|------|-------------|
| **Build** | `./gradlew clean build -x test` — compiles all 8 microservices |
| **Unit Tests** | `./gradlew test jacocoTestReport` — runs tests with coverage |
| **SonarQube Analysis** | Static code analysis, duplication detection, coverage report |
| **Quality Gate** | Blocks merge if quality standards are not met |

### CD Stage (main branch only)
| Step | Description |
|------|-------------|
| **Docker Build** | Builds Docker images for all 8 microservices |
| **Push to ECR** | Pushes tagged images to AWS Elastic Container Registry |
| **Deploy to EC2** | SSH into EC2, pulls images, orchestrates startup order |
| **Health Checks** | Verifies all services respond on `/actuator/health` |

### Deployment Order (on EC2)
```
1. Databases (PostgreSQL) + Kafka + Redis
2. Eureka (service discovery)
3. Config Server (centralized configuration)
4. Auth, User, Account, Transaction, Notification
5. Gateway (API entry point)
```

### Jenkins Screenshots

<details>
<summary>📸 Jenkins CI Pipeline (Feature Branch)</summary>

![img.png](images/jenkins_feature.png) 
</details>

<details>
<summary>📸 Jenkins CD Pipeline (Main Branch)</summary>

![img.png](images/jenkins_main.png)
</details>


### SonarQube Screenshots


<details>
<summary>📸 SonarQube Quality Gate — Passed</summary>

![img.png](images/sonarqube_passed.png)
</details>

---

## 📡 API Endpoints

Base URL: `http://<your-ec2-ip>:8000`

### 🔐 Auth Service — `/api/v1/auth`

| Method | Endpoint | Description | Auth |
|:------:|----------|-------------|:----:|
| `POST` | `/register` | Register user (multipart: JSON + profile image) | ❌ |
| `POST` | `/login` | Login and get JWT tokens | ❌ |
| `POST` | `/logout` | Invalidate refresh token | ❌ |
| `POST` | `/recover-password` | Send OTP to email | ❌ |
| `POST` | `/validate-otp` | Validate OTP and get reset token | ❌ |
| `POST` | `/change-password` | Change password with reset token | ❌ |
| `POST` | `/refresh-token` | Get new access token | ❌ |

<details>
<summary>📸 Register</summary>

![img.png](images/img.png)
</details>

<details>
<summary>📸 Login</summary>

![img_1.png](images/img_1.png)
</details>

<details>
<summary>📸 Logout</summary>

![img.png](images/logout.png)
</details>

<details>
<summary>📸 Recover Password</summary>

![img.png](images/recover_password.png)
</details>

<details>
<summary>📸 Validate OTP</summary>

![img.png](images/validate_otp.png)
</details>

<details>
<summary>📸 Change Password</summary>

![img.png](images/change_password.png)
</details>

<details>
<summary>📸 Refresh Token</summary>

![img.png](images/refresh_token.png)
</details>

---

### 👤 User Service — `/api/v1/users`

| Method | Endpoint | Description | Auth |
|:------:|----------|-------------|:----:|
| `GET` | `/me` | Get authenticated user profile | ✅ |
| `PUT` | `/me` | Update user profile | ✅ |
| `DELETE` | `/me` | Delete user account (cascades to all services) | ✅ |

<details>
<summary>📸 Get User Profile</summary>

![img.png](images/get_user.png)
</details>

<details>
<summary>📸 Update User</summary>

![img.png](images/update_user.png)
</details>

<details>
<summary>📸 Delete User</summary>

![img.png](images/delete_user.png)
</details>

---

### 🏦 Account Service — `/api/v1/account`

| Method | Endpoint | Description | Auth |
|:------:|----------|-------------|:----:|
| `GET` | `/` | Get user's account | ✅ |
| `GET` | `/{accountNumber}` | Get account by number | ✅ |
| `POST` | `/` | Create bank account | ✅ |
| `PUT` | `/` | Update account | ✅ |
| `DELETE` | `/` | Delete account | ✅ |
| `GET` | `/balance` | Get account balance | ✅ |
| `PUT` | `/block` | Block account | ✅ |
| `PUT` | `/unblock` | Unblock account | ✅ |

<details>
<summary>📸 Get Account</summary>

![img.png](images/get_account.png)
</details>

<details>
<summary>📸 Create Account</summary>

![img.png](images/create_account.png)
</details>

<details>
<summary>📸 Update Account</summary>

![img.png](images/update_account.png)
</details>

<details>
<summary>📸 Delete Account</summary>

![img.png](images/delete_account.png)
</details>

<details>
<summary>📸 Get Balance</summary>

![img.png](images/get_balance.png)
</details>

<details>
<summary>📸 Block Account</summary>

![img.png](images/block_account.png)
</details>

<details>
<summary>📸 Unblock Account</summary>

![img.png](images/unblock_account.png)
</details>

---

### 💸 Transaction Service — `/api/v1/transaction`

| Method | Endpoint | Description | Auth |
|:------:|----------|-------------|:----:|
| `POST` | `/deposit` | Deposit money into account | ✅ |
| `POST` | `/withdraw` | Withdraw money from account | ✅ |
| `POST` | `/transfer` | Transfer between accounts | ✅ |
| `GET` | `/all?accountNumber=XXX` | Get all transactions for account | ✅ |
| `GET` | `/{transactionId}` | Get specific transaction | ✅ |

<details>
<summary>📸 Deposit</summary>

![img.png](images/deposit.png)
</details>

<details>
<summary>📸 Withdraw</summary>

![img.png](images/widhtdraw.png)
</details>

<details>
<summary>📸 Transfer</summary>

![img.png](images/transfer.png)
</details>

<details>
<summary>📸 Get All Transactions</summary>

![img.png](images/get_all_transaction.png)
</details>

<details>
<summary>📸 Get Transaction by ID</summary>

![img.png](images/get_transaciton.png)
</details>

---

### 📬 Notification Service (Internal — No REST API)

The Notification Service is an **event-driven consumer** that listens to Kafka topics and sends automated HTML emails:

| Trigger | Email Sent |
|---------|------------|
| Transaction Created | ⏳ "Transaction In Progress" email |
| Transaction Completed | ✅ "Transaction Successful" email |
| Transaction Failed | ❌ "Transaction Failed" email |
| Password Recovery | 🔐 "OTP Verification Code" email |

<details>
<summary>📸 Transaction Email Notification</summary>

![img.png](images/transaction_email.png)
</details>

<details>
<summary>📸 Password Recovery Email</summary>

![img.png](images/email_recover.png)
</details>

---

## ☁️ Infrastructure

### AWS Architecture

```
┌────────────────────────────────────────────────────┐
│                     AWS Cloud                       │
│                                                     │
│  ┌──────────────┐         ┌──────────────────────┐  │
│  │   AWS ECR    │         │    AWS EC2            │  │
│  │  (8 Docker   │ pull ──►│  (m7i-flex.large)    │  │
│  │   images)    │         │                       │  │
│  └──────────────┘         │  ┌─────────────────┐  │  │
│                           │  │ Docker Compose   │  │  │
│  ┌──────────────┐         │  │ • 8 services     │  │  │
│  │   AWS S3     │         │  │ • 4 databases    │  │  │
│  │  (profile    │◄────────│  │ • Kafka + Redis  │  │  │
│  │   images)    │         │  └─────────────────┘  │  │
│  └──────────────┘         └──────────────────────┘  │
│                                                     │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│                   Jenkins Server                    │
│                                                     │
│  Build → Test → SonarQube → Docker → ECR → EC2     │
└────────────────────────────────────────────────────┘
```

### Docker Compose Services (15 containers)

| Category | Services | Memory |
|----------|----------|:------:|
| **Infrastructure** | Eureka, Config, Gateway | 400M each |
| **Databases** | auth-db, user-db, account-db, transaction-db | 256M each |
| **Messaging** | Apache Kafka | 768M |
| **Caching** | Redis | 128M |
| **Microservices** | Auth, User, Account, Transaction, Notification | 512M each |

### Eureka Dashboard

<details>
<summary>📸 Eureka Service Registry</summary>

![Eureka Dashboard](images/eureka_dashboard.png)
</details>

### AWS Screenshots

<details>
<summary>📸 AWS EC2 — Instance Running</summary>

![EC2 Instance](docs/images/aws-ec2-instance.png)
</details>

<details>
<summary>📸 AWS ECR — Docker Repositories</summary>

![ECR Repositories](docs/images/aws-ecr-repositories.png)
</details>

<details>
<summary>📸 AWS S3 — Profile Images Bucket</summary>

![S3 Bucket](docs/images/aws-s3-bucket.png)
</details>

<details>
<summary>📸 AWS Security Groups — Inbound Rules</summary>

![Security Groups](docs/images/aws-security-groups.png)
</details>

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** (JDK)
- **Docker** & **Docker Compose**
- **PostgreSQL 16** (or use Docker)
- **Kafka** (or use Docker)
- **Redis** (or use Docker)

### Local Development

```bash
# 1. Clone the repository
git clone https://github.com/Wuubzi/backend-finova.git
cd backend-finova

# 2. Start infrastructure with Docker
docker compose up -d auth-db user-db account-db transaction-db kafka redis

# 3. Start Eureka
./gradlew :eureka:bootRun

# 4. Start Config Server
./gradlew :config:bootRun

# 5. Start microservices (in separate terminals)
./gradlew :auth:bootRun
./gradlew :user:bootRun
./gradlew :account:bootRun
./gradlew :transaction:bootRun
./gradlew :notification:bootRun

# 6. Start Gateway
./gradlew :gateway:bootRun
```

### Run Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Coverage reports at: {service}/build/reports/jacoco/test/html/index.html
```

### Build Docker Images

```bash
./gradlew clean build -x test

# Build all images
docker compose build
```

---

## 🔐 Environment Variables

Create a `.env` file in the project root:

```env
# Database
AUTH_DB_USER=auth_user
AUTH_DB_PASS=auth_pass
USER_DB_USER=user_user
USER_DB_PASS=user_pass
ACCOUNT_DB_USER=account_user
ACCOUNT_DB_PASS=account_pass
TRANSACTION_DB_USER=transaction_user
TRANSACTION_DB_PASS=transaction_pass

# JWT
JWT_SECRET=your-base64-encoded-secret-key

# AWS
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_S3_BUCKET=your-s3-bucket-name

# VirusTotal
VIRUSTOTAL_API_KEY=your-virustotal-api-key

# Email (SMTP)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Docker (set by CI/CD)
ECR_REGISTRY=your-ecr-registry-url
IMAGE_TAG=latest
```

---

## 📂 Project Structure

```
backend-finova/
├── auth/                    # Authentication microservice
│   └── src/main/kotlin/
│       ├── application/     # Use cases, DTOs, Ports
│       ├── domain/          # Domain models
│       └── infrastructure/  # Controllers, Adapters, Config
├── user/                    # User profile microservice
├── account/                 # Bank account microservice
├── transaction/             # Transaction microservice
├── notification/            # Email notification (Kafka consumer)
├── gateway/                 # API Gateway (Spring Cloud Gateway)
├── eureka/                  # Service discovery (Eureka Server)
├── config/                  # Centralized config (Config Server)
├── docker-compose.yml       # Full orchestration (15 containers)
├── Jenkinsfile              # CI/CD pipeline definition
├── build.gradle.kts         # Root build configuration
└── settings.gradle.kts      # Multi-module project settings
```

---

## 📄 License

This project was built for educational and portfolio purposes.

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/Wuubzi">Wuubzi</a>
</p>

