<div align="center">

# 🎓 Campus Alliance
### Enterprise Academic Collaboration & Resource Management Platform

A modern, full-stack university ecosystem designed for seamless academic collaboration, real-time notice broadcasting, multi-version document control, and administrative governance.

[![Live Application](https://img.shields.io/badge/Live_Portal-Vercel-blue?style=for-the-badge&logo=vercel)](https://campus-alliance-nu.vercel.app)
[![Backend API](https://img.shields.io/badge/REST_API-Render-green?style=for-the-badge&logo=render)](https://campusalliance.onrender.com/actuator/health)
[![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular_18-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL_15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

</div>

---

## 📌 Overview

**Campus Alliance** is an academic management portal built to bridge communication and resource-sharing gaps across university departments. It replaces fragmented email chains and unversioned drives with a unified, role-governed platform supporting live event feeds, revision-tracked academic repositories, interactive discussion threads, and real-time infrastructure diagnostics.

---

## 🌟 Key Capabilities

### 📢 Real-Time Notice Broadcasting
* **Server-Sent Events (SSE)**: Instant, push-based delivery of urgent notices and circulars without requiring client polling or manual page refreshes.
* **Audience Targeting**: Faculty and administrators can tag notices for specific groups (e.g., *All Students*, *3rd Year CSE*, *Faculty Only*).
* **Read-Receipt Analytics**: Comprehensive visibility into student engagement with percentage-based view tracking.
* **Contextual Discussion**: Integrated inquiry threads under notices for rapid clarification between students and faculty.

### 📁 Academic Resource Repository
* **Multi-Version Control**: Preserves complete revision histories for syllabus copies, lecture slides, and question banks.
* **High-Capacity Storage**: Native support for documents (PDF, DOCX, PPTX) up to **50MB** with zero file corruption on download.
* **Peer Star Ratings**: 5-star community rating system with automated average score aggregation.
* **Bookmarking**: Persistent quick-access lists for personalized student libraries.

### 🛡️ Administrative Governance & Security
* **User Lifecycle Management**: Real-time multi-criteria search (name, email), role-based filtering (Student, Faculty, Admin), and account suspension capabilities.
* **Security Safeguards**: Suspended accounts are immediately locked out of all endpoints with dedicated status notifications; built-in safeguards prevent administrators from accidental self-suspension.
* **Immutable Audit Trail**: Chronological, searchable logging of all authentication events, document uploads, deletions, and policy changes.
* **Infrastructure Heartbeat**: Background diagnostic probes querying database connection pools (HikariCP), server latency, and disk thresholds via Spring Boot Actuator.

---

## 👥 Role-Based Access Matrix

| Platform Capability | Student | Faculty | Administrator |
|---|:---:|:---:|:---:|
| Browse & Search Academic Materials | ✅ | ✅ | ✅ |
| Download Material Versions | ✅ | ✅ | ✅ |
| Bookmark & Rate Resources | ✅ | ✅ | ✅ |
| Participate in Notice Discussions | ✅ | ✅ | ✅ |
| Publish Targeted Campus Notices | ❌ | ✅ | ✅ |
| Upload & Version Academic Documents | ❌ | ✅ | ✅ |
| Access Notice Engagement Analytics | ❌ | ✅ | ✅ |
| User Access Management & Status Control | ❌ | ❌ | ✅ |
| Permanent Deletion (Notices & Resources) | ❌ | ❌ | ✅ |
| System Infrastructure Health Probes | ❌ | ❌ | ✅ |
| Platform Audit Logs & Security Trails | ❌ | ❌ | ✅ |

---

## 🏛 System Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER (Angular 18)                      │
│   ┌──────────────┐   ┌──────────────┐   ┌──────────────────────────┐   │
│   │ Authentication│   │ Live Notices │   │   Resource Repository    │   │
│   │ & RBAC Guards │   │ (SSE Stream) │   │ (Versioning & Ratings)   │   │
│   └───────┬──────┘   └──────┬───────┘   └────────────┬─────────────┘   │
│           │                 │                        │                 │
│           └─────────────────┴───────────┬────────────┘                 │
│                                HTTP / HTTPS & SSE                      │
└─────────────────────────────────────────┼──────────────────────────────┘
                                          │
┌─────────────────────────────────────────┼──────────────────────────────┐
│                      APPLICATION LAYER (Spring Boot 3.3)               │
│   ┌─────────────────────────────────────┴──────────────────────────┐   │
│   │                     Spring Security & JWT Filter               │   │
│   └─────────────────────────────────────┬──────────────────────────┘   │
│                                         │                              │
│   ┌─────────────────────────────────────┴──────────────────────────┐   │
│   │                          REST Controllers                      │   │
│   │   AuthController · NoticeController · ResourceController       │   │
│   │   UserManagementController · AuditLogController · SSEController│   │
│   └─────────────────────────────────────┬──────────────────────────┘   │
│                                         │                              │
│   ┌─────────────────────────────────────┴──────────────────────────┐   │
│   │                      Service & Business Logic                  │   │
│   │   HikariCP · JPA Auditing · Data Seeder · Actuator Probes      │   │
│   └─────────────────────────────────────┬──────────────────────────┘   │
└─────────────────────────────────────────┼──────────────────────────────┘
                                          │ JDBC
┌─────────────────────────────────────────┼──────────────────────────────┐
│                       DATA LAYER (PostgreSQL 15)                       │
│   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────────┐   │
│   │    Users    │   │   Notices   │   │  Resources  │   │  Audit   │   │
│   │   & Roles   │   │  & Comments │   │ & Versions  │   │   Logs   │   │
│   └─────────────┘   └─────────────┘   └─────────────┘   └──────────┘   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠 Technology Stack

### Backend Framework & Libraries
* **Language & Runtime**: Java 17 (OpenJDK / Eclipse Temurin)
* **Framework**: Spring Boot 3.3.2
* **Security**: Spring Security 6, Stateless JWT (`jjwt 0.12.6`), BCrypt Password Encoding
* **Data Access**: Spring Data JPA, Hibernate ORM
* **Observability**: Spring Boot Actuator, Micrometer
* **Database Driver**: PostgreSQL JDBC Driver, Hikari Connection Pool

### Frontend Architecture
* **Framework**: Angular 18 (Standalone Components, TypeScript)
* **Reactivity**: RxJS (Observables, EventSource SSE listeners, Timer Probes)
* **Styling**: Bespoke, responsive CSS design system with CSS custom properties
* **Asset Pipeline**: Angular CLI Application Builder

### Cloud & DevOps
* **Database**: Neon Cloud Serverless PostgreSQL 15
* **Backend Hosting**: Render Web Service (Dockerized container runtime)
* **Frontend Hosting**: Vercel Global Edge Network
* **Local Virtualization**: Docker & Docker Compose

---

## 💻 Local Development Setup

### Prerequisites
* **Java**: JDK 17 or higher
* **Node.js**: v18.x or v20.x
* **Docker & Docker Compose** (Optional, recommended)

### Quick Start with Docker Compose
To spin up the complete platform (Frontend, Backend, and PostgreSQL database) in one step:

```bash
# Clone the repository
git clone https://github.com/Pritam16345/CampusAlliance.git
cd CampusAlliance

# Build and start all services
docker-compose up --build -d
```

| Service | Local URL |
|---|---|
| **Frontend Portal** | `http://localhost` |
| **Backend REST API** | `http://localhost:8080/api` |
| **Actuator Health Probe** | `http://localhost:8080/actuator/health` |

---

### Manual Setup (Without Docker)

#### 1. Database Configuration
Ensure a PostgreSQL instance is running with a database named `campusalliance`. Configure connection details in `backend/src/main/resources/application.yml` or set environment variables:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/campusalliance
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

#### 2. Start the Backend API
```bash
cd backend
./mvnw spring-boot:run
```

#### 3. Start the Frontend Application
```bash
cd frontend
npm install
npm run start
```
Access the application at `http://localhost:4200`.

---

## 🔒 Security & Data Integrity Highlights

* **Cryptographic Token Verification**: All authenticated endpoints validate HMAC-SHA256 signed JSON Web Tokens passed in authorization headers.
* **Strict Email Domain Validation**: Registration enforces official collegiate domains (`.edu`, `.ac.in`) to prevent unauthorized public onboarding.
* **Optimistic Locking**: JPA `@Version` controls safeguard notices and collaborative entities against concurrent overwrite conflicts.
* **Centralized Exception Handling**: Custom `@RestControllerAdvice` sanitizes error responses to prevent internal stack trace leakage.

---

## 👨‍💻 Project Maintainer

**Pritam Kundu**  
*KIIT University*  
GitHub: [@Pritam16345](https://github.com/Pritam16345)

---

<div align="center">
  <sub>Campus Alliance © 2026. Designed for modern higher education institutions.</sub>
</div>
