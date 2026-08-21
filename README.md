# 🎓 Campus Alliance — Academic Management Portal

<div align="center">

**A full-stack university portal for seamless collaboration between students, faculty, and administration.**

Built with **Spring Boot 3** · **Angular 18** · **PostgreSQL** · **JWT Authentication**

[![Live Demo](https://img.shields.io/badge/🌐_Live_Demo-campus--alliance-blue?style=for-the-badge)](https://campus-alliance-nu.vercel.app)
[![Backend API](https://img.shields.io/badge/🔗_API-Render-green?style=for-the-badge)](https://campusalliance.onrender.com/actuator/health)

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
| Upload Resources (PDF, DOCX, PPTX up to 50MB) | ❌ | ✅ | ✅ |
| View Notice Analytics (Seen %) | ❌ | ✅ | ✅ |
| Delete Any Notice or Resource | ❌ | ❌ | ✅ |
| User Management & Account Suspension | ❌ | ❌ | ✅ |
| System Audit Trail & Logs | ❌ | ❌ | ✅ |
| Real-Time Live Infrastructure Health Probes | ❌ | ❌ | ✅ |

### 📢 Live Notice Board (Real-Time)
- **Server-Sent Events (SSE)** for instant, push-based notice delivery — zero page refreshes needed
- **Targeted Notices** — Faculty can tag notices for specific audiences (e.g., "All Students", "3rd Year CSE")
- **Seen Tracking** — Faculty and Admins see live view counts and read percentages
- **Comments / Q&A** — Students and faculty can ask questions directly under any notice

### 📁 Resource Repository
- Centralized hub for uploading and downloading academic materials (PDFs, Word docs, Slides)
- **Version Control** — Re-uploading creates a new version, preserving complete revision history
- **Star Ratings** — Interactive 5-star rating system with real-time average aggregation
- **Bookmarks** — Save frequently accessed resources for instant access

### 👥 Admin Dashboard
- **User Management** — Search users by name/email, filter by role (Students, Faculty, Admins) or status (Active, Suspended), and toggle account suspensions with built-in self-protection.
- **Audit Logs** — Immutable chronological timeline recording logins, registrations, uploads, deletions, and security events with auto-refresh and category filters.
- **Live System Health** — Background 5-second heartbeat probes monitoring PostgreSQL database connectivity, connection pooling, and disk space.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 3.3, Spring Security, Spring Data JPA, Hibernate, Actuator |
| **Frontend** | Angular 18, RxJS, TypeScript, Bespoke Responsive CSS Design System |
| **Database** | PostgreSQL 15 (Neon Cloud) |
| **Auth** | Stateless JWT (JSON Web Tokens) with BCrypt password hashing |
| **Real-Time** | Server-Sent Events (SSE) & Reactive RxJS Timers |
| **Deployment** | Render (Backend API), Vercel (Frontend SPA), Docker |

---

## 🚀 Demo Accounts

| Role | Email / Roll | Password | Full Name |
|---|---|---|---|
| **Master Admin** | `admin@university.edu` | `admin123` | Master Admin |
| **Faculty** | `dr.sharma@kiit.ac.in` | `faculty1` | Dr. Rajesh Sharma |
| **Faculty** | `prof.das@kiit.ac.in` | `faculty2` | Prof. Subhash Das |
| **Faculty** | `dr.mukherjee@kiit.ac.in` | `faculty3` | Dr. Swati Mukherjee |
| **Student** | `23051800@kiit.ac.in` | `student1800` | Aarav Patel |
| **Student** | `23051801@kiit.ac.in` | `student1801` | Ananya Roy |
| **Student** | `23051802@kiit.ac.in` | `student1802` | Rohan Sharma |
| **Student** | `23051806@kiit.ac.in` | `student1806` | Devendra Mehta |

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

## 📁 Project Structure

```
CampusAlliance/
├── backend/                          # Spring Boot API
│   ├── src/main/java/com/campusalliance/
│   │   ├── controller/               # REST Endpoints
│   │   ├── service/                  # Business Logic Layer
│   │   ├── repository/               # Spring Data JPA Repositories
│   │   ├── entity/                   # JPA Entity Models
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── security/                 # JWT Authentication & RBAC Filters
│   │   ├── exception/                # Centralized Global Error Handler
│   │   └── seeder/                   # Database Auto-Provisioning
│   ├── src/main/resources/           # Configuration (application.yml)
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                         # Angular 18 Single Page Application
│   ├── src/app/
│   │   ├── auth/                     # Authentication & Route Guards
│   │   ├── layout/                   # Sidebar Navigation Layout
│   │   ├── notices/                  # Real-Time Notice Board & Analytics
│   │   ├── resources/                # Resource Repository & Version History
│   │   ├── bookmarks/                # User Saved Bookmarks
│   │   ├── admin/                    # User Management & System Audit Logs
│   │   └── health/                   # Live System Health Monitor
│   ├── src/environments/             # Environment Configurations
│   ├── public/                       # Favicon & Static Assets
│   ├── Dockerfile
│   ├── angular.json
│   └── package.json
├── docker-compose.yml
└── README.md
```

---

## 👨‍💻 Author

**Pritam Kundu** — KIIT University
