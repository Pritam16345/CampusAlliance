# 🎓 Campus Alliance — Academic Management Portal

<div align="center">

**A full-stack university portal for seamless collaboration between students, faculty, and administration.**

Built with **Spring Boot 3** · **Angular 18** · **PostgreSQL** · **JWT Authentication**

[![Live Demo](https://img.shields.io/badge/🌐_Live_Demo-campus--alliance-blue?style=for-the-badge)](https://campus-alliance-sooty.vercel.app)
[![Backend API](https://img.shields.io/badge/🔗_API-Render-green?style=for-the-badge)](https://campus-alliance-api.onrender.com/actuator/health)

</div>

---

## ✨ Key Features

### 🔐 Role-Based Access Control (RBAC)
| Feature | Student | Faculty | Admin |
|---|:---:|:---:|:---:|
| View Notices & Resources | ✅ | ✅ | ✅ |
| Bookmark & Rate Resources | ✅ | ✅ | ✅ |
| Comment on Notices (Q&A) | ✅ | ✅ | ✅ |
| Create Notices (with Target Audience) | ❌ | ✅ | ✅ |
| Upload Resources | ❌ | ✅ | ✅ |
| View Notice Analytics (Seen %) | ❌ | ✅ | ✅ |
| User Management Dashboard | ❌ | ❌ | ✅ |
| System Audit Logs | ❌ | ❌ | ✅ |
| System Health Monitoring | ❌ | ❌ | ✅ |

### 📢 Live Notice Board (Real-Time)
- **Server-Sent Events (SSE)** for instant, push-based notice delivery — no page refresh needed
- **Targeted Notices** — Faculty can tag notices for specific audiences (e.g., "3rd Year CSE")
- **Seen Tracking** — Faculty and Admins see what percentage of students have viewed each notice
- **Comments / Q&A** — Students can ask clarifying questions directly under any notice

### 📁 Resource Repository
- Centralized hub for uploading and downloading academic materials (notes, PYQs, assignments)
- **Version Control** — Re-uploading creates a new version, preserving full revision history
- **⭐ Star Ratings** — Any user can rate a resource (1-5 stars), with average displayed
- **🔖 Bookmarks** — Save frequently accessed resources for quick retrieval

### 👥 Admin Dashboard
- **User Management** — View all registered users, see role distribution stats, and suspend/reactivate accounts
- **Audit Logs** — Chronological timeline of all critical actions (user registrations, notice creation, resource uploads)
- **System Health** — Live monitoring of database connectivity, disk storage, and server status via Spring Boot Actuator

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 3.3, Spring Security, Spring Data JPA, Hibernate |
| **Frontend** | Angular 18, RxJS, TypeScript, Vanilla CSS (Custom Design System) |
| **Database** | PostgreSQL 15 (Neon Cloud) |
| **Auth** | JWT (JSON Web Tokens) with BCrypt password hashing |
| **Real-Time** | Server-Sent Events (SSE) |
| **Deployment** | Render (Backend), Vercel (Frontend), Docker & Docker Compose |

---

## 🏗 Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Angular 18)                      │
│  ┌─────────┐ ┌──────────┐ ┌───────────┐ ┌─────────┐ ┌─────────┐ │
│  │  Login   │ │ Notices  │ │ Resources │ │Bookmarks│ │  Admin  │ │
│  │Component │ │Board+Q&A │ │ +Ratings  │ │  Page   │ │Dashboard│ │
│  └────┬─────┘ └────┬─────┘ └─────┬─────┘ └────┬────┘ └────┬────┘ │
│       │             │             │             │           │      │
│       └──────┬──────┴─────────────┴──────┬──────┘           │      │
│              │    Auth Interceptor (JWT)  │                  │      │
│              └───────────────┬────────────┘                  │      │
└──────────────────────────────┼────────────────────────────────┘      
                               │ HTTPS                                 
┌──────────────────────────────┼────────────────────────────────┐      
│                     BACKEND (Spring Boot 3)                    │      
│  ┌──────────┐ ┌───────────┐ ┌───────────┐ ┌───────────────┐  │      
│  │   Auth   │ │  Notice   │ │ Resource  │ │    Admin      │  │      
│  │Controller│ │Controller │ │Controller │ │  Controller   │  │      
│  └────┬─────┘ └─────┬─────┘ └─────┬─────┘ └───────┬───────┘  │      
│       │              │             │               │           │      
│  ┌────┴──────────────┴─────────────┴───────────────┴───────┐  │      
│  │              Service Layer (Business Logic)              │  │      
│  │  AuthService · NoticeService · ResourceService           │  │      
│  │  BookmarkService · AuditLogService · UserMgmtService     │  │      
│  └──────────────────────────┬───────────────────────────────┘  │      
│                             │                                   │      
│  ┌──────────────────────────┴───────────────────────────────┐  │      
│  │             Spring Data JPA + Hibernate                   │  │      
│  └──────────────────────────┬───────────────────────────────┘  │      
└──────────────────────────────┼──────────────────────────────────┘      
                               │ JDBC                                    
                    ┌──────────┴──────────┐                              
                    │  PostgreSQL Database │                              
                    │  (Neon Cloud / Local)│                              
                    └─────────────────────┘                              
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ (JDK)
- Node.js 18+ and npm
- PostgreSQL 15+ (or Docker)

### Option 1: Docker Compose (Recommended)
```bash
git clone https://github.com/Pritam16345/CampusAlliance.git
cd CampusAlliance
docker-compose up --build -d
```
- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080/api
- **Health Check:** http://localhost:8080/actuator/health

### Option 2: Manual Setup

#### Database
```bash
# Using Docker for just the database:
docker-compose up db -d

# Or create manually in PostgreSQL:
# Database: campus_connect | User: postgres | Password: password
```

#### Backend
```bash
cd backend
./mvnw spring-boot:run
# API available at http://localhost:8080
```

#### Frontend
```bash
cd frontend
npm install
ng serve
# UI available at http://localhost:4200
```

### Default Admin Credentials
The system auto-provisions a master admin on first startup:
| Email | Password |
|---|---|
| `admin@university.edu` | `admin123` |

---

## 🧪 Testing

```bash
cd backend
./mvnw test
```
- Unit tests use **Mockito** to isolate the service layer from the database
- Tests cover authentication, registration, and duplicate email validation

---

## 📐 Architecture Decisions

| Decision | Rationale |
|---|---|
| **Vanilla CSS** over Tailwind/Bootstrap | Full control over the premium, bespoke UI design. Demonstrates fundamental CSS mastery. |
| **SSE** over WebSockets | Notice delivery is unidirectional (server → client), making SSE significantly lighter and simpler over standard HTTP. |
| **JWT** over Sessions | Stateless authentication scales horizontally without shared session stores. |
| **Admin Auto-Seeding** | Master admin created on startup (like WordPress/Jira), preventing public admin registration vulnerabilities. |
| **@Version Optimistic Locking** | Prevents silent data overwrites when multiple users edit the same notice simultaneously. |

---

## 🔒 Security

- **JWT Authentication** — Cryptographically signed tokens prevent tampering
- **BCrypt** — Passwords are never stored in plain text
- **@PreAuthorize** — Backend endpoints enforce role-based access at the API level
- **Route Guards** — Frontend prevents unauthorized URL navigation
- **Admin Lockdown** — No public registration for admin accounts; auto-seeded on server startup
- **CORS** — Configured to accept only trusted frontend origins

---

## 📁 Project Structure

```
CampusAlliance/
├── backend/                          # Spring Boot API
│   └── src/main/java/com/campusalliance/
│       ├── controller/               # REST endpoints
│       ├── service/                   # Business logic
│       ├── repository/               # Data access (JPA)
│       ├── entity/                    # Database models
│       ├── dto/                       # Data Transfer Objects
│       ├── security/                  # JWT, filters, config
│       ├── exception/                 # Global error handling
│       └── seeder/                    # Auto-provisioning
├── frontend/                         # Angular 18 SPA
│   └── src/app/
│       ├── auth/                     # Login, guards, interceptor
│       ├── layout/                   # Sidebar navigation
│       ├── notices/                  # Notice board + analytics
│       ├── resources/                # Resource repository
│       ├── bookmarks/                # Saved items
│       ├── admin/                    # User management + audit logs
│       └── health/                   # System monitoring
├── docker-compose.yml
└── README.md
```

---

## 👨‍💻 Author

**Pritam Kundu** — KIIT University

---

<div align="center">
  <sub>Built with ☕ Java, 🅰️ Angular, and 🐘 PostgreSQL</sub>
</div>
