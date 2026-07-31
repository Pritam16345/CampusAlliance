# 🎓 Campus Alliance — Complete File-by-File Project Guide

> **Purpose of this document:** This is your one-stop learning guide. It explains every
> single file in the project in very easy words so you can understand the full picture
> and confidently present it in your deep skilling course or any interview.

---

## ⚡ Super Quick Summary (Read This First!)

If someone asks you **"What is your project?"** — here's what you say:

> "I built a **university portal** called Campus Alliance. Think of it like a private
> mini-website for a college. **Teachers** can post announcements and upload study
> materials (PDFs). **Students** can read those announcements in real-time (they pop
> up instantly without refreshing the page), download the PDFs, leave comments, and
> save their favorite items. There's also an **Admin panel** where the admin can see
> who's doing what, manage user accounts, and check if the system is healthy."

**How does it work behind the scenes?**
> "The website you see (buttons, pages, forms) is built using **Angular** (a Google
> framework). When you click a button, Angular sends a message to my **Spring Boot**
> server (written in Java). The server checks if you're allowed to do that action,
> does the work, and saves/reads data from a **PostgreSQL database**. The server sends
> back a response, and Angular updates the screen."

**In short:** Angular = what you see. Spring Boot = what thinks. PostgreSQL = what remembers.

---

## Table of Contents

1. [What Is This Project?](#1-what-is-this-project)
2. [Technology Stack — What & Why](#2-technology-stack--what--why)
3. [How the System Works (End-to-End Flow)](#3-how-the-system-works-end-to-end-flow)
4. [Full Folder Structure](#4-full-folder-structure)
5. [Root-Level Files](#5-root-level-files)
6. [Backend — Every File Explained](#6-backend--every-file-explained)
7. [Frontend — Every File Explained](#7-frontend--every-file-explained)
8. [Database Tables & Relationships](#8-database-tables--relationships)
9. [Deployment Architecture](#9-deployment-architecture)
10. [Interview Presentation Workflow](#10-interview-presentation-workflow)
11. [Common Interview Questions & Answers](#11-common-interview-questions--answers)

---

## 1. What Is This Project?

Campus Alliance is a **full-stack web application** that acts as a university's digital hub.
It solves three real problems:

| Problem | How Campus Alliance Solves It |
|---|---|
| Students miss important announcements | A **real-time Notice Board** using Server-Sent Events pushes notices instantly |
| Study materials are scattered across WhatsApp groups | A **Resource Repository** with version control stores all PDFs centrally |
| No accountability for who posted what | **Audit Logs** and **role-based access** track every action |

**Three user roles** exist:
- **Student** — Can view notices, download resources, comment, rate, and bookmark.
- **Faculty** — Can do everything a student can, plus create notices and upload resources.
- **Admin** — Can do everything, plus manage users, view system health, and read audit logs.

---

## 2. Technology Stack — What & Why

### Angular 18 (Frontend)
- **What:** A TypeScript-based framework made by Google for building Single Page Applications (SPAs).
- **Why we chose it:** Angular is "batteries-included" — it ships with a router, form validation, HTTP client, and dependency injection. You don't need to install 10 extra libraries like you would with React. This makes it perfect for enterprise applications.
- **Simple analogy:** Angular is the **steering wheel and dashboard** of a car — it's everything the driver (user) sees and touches.

### Spring Boot 3 + Java 23 (Backend)
- **What:** A Java framework for building production-ready REST APIs extremely fast.
- **Why we chose it:** Spring Boot handles security (JWT authentication), database communication (JPA/Hibernate), and error handling out of the box. Java is statically typed, meaning the compiler catches bugs before the code even runs.
- **Simple analogy:** Spring Boot is the **engine** of the car — it does all the heavy lifting behind the scenes.

### PostgreSQL (Database)
- **What:** The world's most advanced open-source relational database.
- **Why we chose it:** Our data is deeply interconnected (a User creates a Notice, another User bookmarks it, yet another User comments on it). Relational databases handle these connections using foreign keys, joins, and constraints.
- **Simple analogy:** PostgreSQL is the **trunk** of the car — it safely stores all your data.

### Supporting Tools
| Tool | Purpose |
|---|---|
| **JWT (JSON Web Token)** | Stateless authentication — no server-side sessions needed |
| **BCrypt** | One-way password hashing so raw passwords are never stored |
| **Server-Sent Events (SSE)** | Lightweight one-way real-time streaming from server to browser |
| **Maven** | Java build tool that manages dependencies (like npm for Java) |
| **Docker** | Packages the backend into a portable container for deployment |
| **Vercel** | Hosts the Angular frontend (auto-deploys on git push) |
| **Render** | Hosts the Spring Boot backend (auto-deploys Docker container) |

---

## 3. How the System Works (End-to-End Flow)

### Flow A: User Login
```
User types email & password
       ↓
Angular validates format (must be .edu or .ac.in)
       ↓
Angular sends POST /api/auth/login to Spring Boot
       ↓
Spring Boot looks up user in PostgreSQL
       ↓
BCrypt compares hashed passwords
       ↓
If match → JwtUtils generates a signed JWT token containing {email, role}
       ↓
Token sent back to Angular → stored in localStorage
       ↓
Every future HTTP request includes "Authorization: Bearer <token>"
```

### Flow B: Real-Time Notice
```
Faculty clicks "Post Notice" in Angular
       ↓
Angular sends POST /api/notices to Spring Boot
       ↓
Spring Boot saves notice to PostgreSQL
       ↓
SseService loops through all connected EventSource clients
       ↓
Pushes the new notice JSON to every student's browser
       ↓
Angular receives it and adds the card to the screen — no page reload!
```

### Flow C: File Upload with Versioning
```
Faculty uploads a new PDF for "Data Structures Notes"
       ↓
Angular wraps file in FormData, sends POST /api/resources
       ↓
ResourceService checks: does this resource already exist?
       ↓
YES → creates a new ResourceVersion (v2) linked to same Resource
NO  → creates a new Resource + ResourceVersion (v1)
       ↓
File bytes stored as @Lob in PostgreSQL
       ↓
Students see "2 Versions" badge on the resource card
```

---

## 4. Full Folder Structure

```
CampusConnect/
├── .gitignore                          ← Tells Git which files to ignore
├── docker-compose.yml                  ← Docker orchestration for local dev
├── PROJECT_DOCUMENTATION.md            ← Previous documentation (being replaced by this file)
├── FULL_PROJECT_GUIDE.md               ← THIS FILE — the ultimate guide
├── README.md                           ← GitHub repo description
│
├── backend/                            ← Spring Boot (Java) application
│   ├── Dockerfile                      ← Container build instructions
│   ├── pom.xml                         ← Maven dependencies & build config
│   ├── mvnw.cmd                        ← Maven wrapper (runs Maven without installing it)
│   └── src/
│       ├── main/
│       │   ├── java/com/campusalliance/
│       │   │   ├── CampusAllianceApplication.java
│       │   │   ├── config/
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   └── JpaAuditingConfig.java
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── NoticeController.java
│       │   │   │   ├── NoticeCommentController.java
│       │   │   │   ├── ResourceController.java
│       │   │   │   ├── BookmarkController.java
│       │   │   │   ├── EventController.java
│       │   │   │   ├── SseController.java
│       │   │   │   ├── UserManagementController.java
│       │   │   │   └── AuditLogController.java
│       │   │   ├── service/
│       │   │   │   ├── AuthService.java
│       │   │   │   ├── NoticeService.java
│       │   │   │   ├── NoticeCommentService.java
│       │   │   │   ├── ResourceService.java
│       │   │   │   ├── BookmarkService.java
│       │   │   │   ├── EventService.java
│       │   │   │   ├── SseService.java
│       │   │   │   ├── UserManagementService.java
│       │   │   │   └── AuditLogService.java
│       │   │   ├── repository/
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── NoticeRepository.java
│       │   │   │   ├── NoticeCommentRepository.java
│       │   │   │   ├── NoticeSeenByRepository.java
│       │   │   │   ├── ResourceRepository.java
│       │   │   │   ├── ResourceVersionRepository.java
│       │   │   │   ├── ResourceRatingRepository.java
│       │   │   │   ├── BookmarkRepository.java
│       │   │   │   ├── EventRepository.java
│       │   │   │   └── AuditLogRepository.java
│       │   │   ├── entity/
│       │   │   │   ├── Auditable.java
│       │   │   │   ├── User.java
│       │   │   │   ├── Role.java
│       │   │   │   ├── Notice.java
│       │   │   │   ├── NoticeComment.java
│       │   │   │   ├── NoticeSeenBy.java
│       │   │   │   ├── Resource.java
│       │   │   │   ├── ResourceVersion.java
│       │   │   │   ├── ResourceRating.java
│       │   │   │   ├── Bookmark.java
│       │   │   │   ├── Event.java
│       │   │   │   └── AuditLog.java
│       │   │   ├── dto/
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── RegisterRequest.java
│       │   │   │   ├── AuthResponse.java
│       │   │   │   ├── ErrorResponse.java
│       │   │   │   ├── NoticeDto.java
│       │   │   │   ├── NoticeRequest.java
│       │   │   │   ├── NoticeCommentDto.java
│       │   │   │   ├── ResourceDto.java
│       │   │   │   ├── ResourceVersionDto.java
│       │   │   │   ├── ResourceRatingDto.java
│       │   │   │   ├── BookmarkDto.java
│       │   │   │   ├── EventDto.java
│       │   │   │   ├── EventRequest.java
│       │   │   │   ├── AuditLogDto.java
│       │   │   │   └── UserDto.java
│       │   │   ├── security/
│       │   │   │   ├── JwtUtils.java
│       │   │   │   ├── JwtAuthenticationFilter.java
│       │   │   │   ├── CustomUserDetails.java
│       │   │   │   └── CustomUserDetailsService.java
│       │   │   ├── exception/
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   └── ResourceNotFoundException.java
│       │   │   └── seeder/
│       │   │       └── DataSeeder.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│           └── java/com/campusalliance/service/
│               ├── AuthServiceTest.java
│               └── ResourceServiceTest.java
│
└── frontend/                           ← Angular 18 application
    ├── angular.json                    ← Angular workspace configuration
    ├── package.json                    ← npm dependencies
    ├── tsconfig.json                   ← TypeScript compiler options
    ├── vercel.json                     ← Vercel deployment config
    └── src/
        ├── index.html                  ← Single HTML page shell
        ├── main.ts                     ← Application bootstrap entry point
        ├── styles.css                  ← Global design system (CSS variables)
        └── app/
            ├── app.component.ts/html/css
            ├── app.config.ts
            ├── app.routes.ts
            ├── auth/
            │   ├── auth.service.ts
            │   ├── auth.guard.ts
            │   ├── auth.interceptor.ts
            │   ├── role.guard.ts
            │   └── login/
            │       ├── login.component.ts
            │       ├── login.component.html
            │       └── login.component.css
            ├── layout/
            │   ├── layout.component.ts
            │   ├── layout.component.html
            │   └── layout.component.css
            ├── notices/
            │   ├── notice.service.ts
            │   ├── comment.service.ts
            │   ├── live-notices/
            │   │   ├── live-notices.component.ts
            │   │   ├── live-notices.component.html
            │   │   └── live-notices.component.css
            │   └── notice-analytics/
            │       ├── notice-analytics.component.ts
            │       ├── notice-analytics.component.html
            │       └── notice-analytics.component.css
            ├── resources/
            │   ├── resource.service.ts
            │   └── resource-repository/
            │       ├── resource-repository.component.ts
            │       ├── resource-repository.component.html
            │       └── resource-repository.component.css
            ├── bookmarks/
            │   ├── bookmark.service.ts
            │   ├── bookmarks.component.ts
            │   ├── bookmarks.component.html
            │   └── bookmarks.component.css
            ├── health/
            │   ├── system-health.service.ts
            │   ├── system-health.component.ts
            │   ├── system-health.component.html
            │   └── system-health.component.css
            └── admin/
                ├── user-management/
                │   ├── user-management.component.ts
                │   ├── user-management.component.html
                │   └── user-management.component.css
                └── audit-logs/
                    ├── audit-logs.component.ts
                    ├── audit-logs.component.html
                    └── audit-logs.component.css
```

---

## 5. Root-Level Files

### `.gitignore`
Tells Git which files and folders to NOT track. This prevents accidentally pushing massive folders like `node_modules/`, `target/`, and `.angular/` to GitHub.

### `docker-compose.yml`
A Docker orchestration file. It defines how to spin up both the backend and a PostgreSQL database together in containers for local development. You run `docker-compose up` and everything starts automatically.

### `README.md`
The file displayed on your GitHub repository's main page. It contains the project description, features, and setup instructions.

### `PROJECT_DOCUMENTATION.md`
The earlier version of this documentation (shorter and less detailed).

---

## 6. Backend — Every File Explained

The backend follows a strict **Layered Architecture**. Think of it as a factory assembly line:

```
HTTP Request arrives
      ↓
[Controller] — Receives the request and validates the URL/parameters
      ↓
[Service] — Contains the actual business logic (the brain)
      ↓
[Repository] — Translates Java method calls into SQL queries
      ↓
[Entity] — Maps directly to a database table (the data shape)
      ↓
PostgreSQL Database
```

---

### 6.1 Entry Point

#### `CampusAllianceApplication.java`
- **What it does:** This is the very first file Java runs. Think of it as the **ON button** of the entire backend. When you run this file, the whole server starts up.
- **Key annotation:** `@SpringBootApplication` — This one line does three things automatically:
  1. It tells Spring "hey, look at all my folders and find every Controller, Service, and Repository file".
  2. It tells Spring "automatically set up the database connection, the web server, and everything else".
  3. It tells Spring "use the settings from `application.yml`".
- **In simple words:** Without this file, nothing works. It's the power switch.

---

### 6.2 Configuration Files

#### `SecurityConfig.java`
- **What it does:** The bouncer of the application. It configures:
  - **Which URLs are public:** `/api/auth/**` (login/register) and `/api/sse/**` (real-time streams) are open to everyone.
  - **Which URLs need a login:** Everything else requires a valid JWT token.
  - **Which URLs need a specific role:** `/api/admin/**` is restricted to `ADMIN` only.
  - **CORS rules:** Allows requests from the Vercel frontend domain.
  - **Password encoder:** Uses `BCryptPasswordEncoder` to hash passwords.
  - **JWT filter:** Registers `JwtAuthenticationFilter` to run before every request.
- **In simple words:** Traditional websites remember you by storing a "session" on the server (like a hotel giving you a room key that's stored at the front desk). We do it differently — we give you a JWT token (like a **passport** that you carry with you). Every time you make a request, you show your passport. The server doesn't need to remember anything about you — it just reads your passport. This is called "stateless".

#### `JpaAuditingConfig.java`
- **What it does:** Automatically fills in "who created this" and "when was it created" on every database record.
- **How it works:** It reads the currently authenticated user's email from the JWT security context and injects it into `@CreatedBy` and `@LastModifiedBy` fields.
- **Why it matters:** Without writing a single line of code in your services, every `Notice`, `Resource`, `User`, etc. automatically gets `createdAt`, `createdBy`, `updatedAt`, and `updatedBy` filled in.

#### `application.yml`
- **What it does:** The master configuration file. It stores:
  - **Database connection:** URL, username, password for PostgreSQL (reads from environment variables in production).
  - **JPA settings:** `ddl-auto: update` tells Hibernate to automatically create/update database tables based on your Entity classes. You never write raw SQL to create tables.
  - **JWT settings:** The secret key used to sign tokens and the token expiration time.
  - **Server port:** Runs on port 8080.
  - **Actuator settings:** Exposes health check endpoints for monitoring.

#### `pom.xml`
- **What it does:** The "package.json" of Java. Lists every library (dependency) the project needs:
  - `spring-boot-starter-web` — REST API support
  - `spring-boot-starter-security` — Authentication & authorization
  - `spring-boot-starter-data-jpa` — Database ORM (Hibernate)
  - `spring-boot-starter-validation` — Input validation
  - `spring-boot-starter-actuator` — Health monitoring
  - `postgresql` — PostgreSQL JDBC driver
  - `jjwt-api/impl/jackson` — JWT token creation and parsing
  - `lombok` — Reduces boilerplate code (auto-generates getters, setters, constructors)

#### `Dockerfile`
- **What it does:** Instructions to package the backend into a Docker container.
- **Two stages:**
  1. **Build stage:** Uses a full JDK image to compile the Java code with Maven.
  2. **Run stage:** Uses a lightweight JRE image (no compiler needed) to run the compiled `.jar` file. This makes the final container much smaller.

---

### 6.3 Security Layer (`/security`)

#### `JwtUtils.java`
- **What it does:** This is the **passport printing machine**. It has four jobs:
  - `generateToken(email, role)` — Makes a new passport with your email and role stamped inside.
  - `getEmailFromToken(token)` — Reads the email from a passport.
  - `getRoleFromToken(token)` — Reads the role (Student/Faculty/Admin) from a passport.
  - `validateToken(token)` — Checks if the passport is real and hasn't expired.
- **How it stays secure:** The token is "signed" with a secret password (stored in `application.yml`). If a hacker tries to change their role from STUDENT to ADMIN inside the token, the signature will break, and the server will reject it. Think of it like a tamper-proof seal on a medicine bottle.

#### `JwtAuthenticationFilter.java`
- **What it does:** Think of this as the **security checkpoint at the airport**. Every single request that comes into the server must pass through this filter first.
- **What happens step by step:**
  1. It looks at the incoming request and checks: "Does this request have a passport (JWT token) attached?"
  2. If yes, it hands the passport to `JwtUtils` and asks: "Is this passport real and not expired?"
  3. If the passport is valid, it reads the person's email and role from it.
  4. It then tells the rest of the system: "This request is from amit@kiit.ac.in and they are a STUDENT."
  5. Now every other file in the backend knows who is making the request.
- **In simple words:** No valid token = request blocked. Valid token = request allowed and we know who you are.

#### `CustomUserDetails.java`
- **What it does:** Spring Security has its own idea of what a "user" looks like. But our `User.java` entity looks different. This file is like a **translator** — it takes our User and wraps it in a format that Spring Security can understand.
- **In simple words:** Spring Security speaks one language, our User speaks another. This file translates between them.

#### `CustomUserDetailsService.java`
- **What it does:** When someone tries to log in, Spring Security needs to find that user in the database. This file does exactly that — it searches the database by email and returns the user.
- **In simple words:** It's like a receptionist looking up your name in the guest list when you arrive at a party.

---

### 6.4 Entity Layer (`/entity`) — The Database Tables

Think of Entity files as **blueprints for your database tables**. You write a Java class, and Spring automatically creates the matching table in PostgreSQL. You never need to write `CREATE TABLE` SQL commands yourself!

#### `Auditable.java` (The Parent Template)
- **What it does:** This is like a **template** that all other entities copy from. It adds four useful fields to every table automatically:
  - `createdAt` — When was this row created?
  - `createdBy` — Who created it?
  - `updatedAt` — When was it last changed?
  - `updatedBy` — Who changed it last?
- **Why this is smart:** Instead of writing these four fields in every single entity file (User, Notice, Resource, etc.), we write them ONCE here and every entity inherits them. Less code, fewer mistakes.

#### `Role.java` (Enum)
- **What it does:** Defines the three fixed user roles: `STUDENT`, `FACULTY`, `ADMIN`.
- **Stored as:** A string in the database column (e.g., "STUDENT"), not a number, thanks to `@Enumerated(EnumType.STRING)`.

#### `User.java` → Table: `users`
- **Fields:** `id`, `email` (unique), `password` (BCrypt hash), `fullName`, `role`, `active` (boolean)
- **Key:** The most referenced entity in the system. Almost every other entity has a foreign key pointing back to `User`.

#### `Notice.java` → Table: `notices`
- **Fields:** `id`, `title`, `content`, `targetAudience`, `priority`, `version` (for optimistic locking)
- **Relationships:**
  - `@ManyToOne User postedBy` — Which faculty/admin created the notice.
  - `@OneToMany List<NoticeSeenBy> seenByRecords` — Tracks which users have read it.
- **Optimistic Locking (`@Version`):** If two admins try to edit the same notice simultaneously, the second save will fail with an `OptimisticLockException` instead of silently overwriting.

#### `NoticeComment.java` → Table: `notice_comments`
- **Fields:** `id`, `content` (max 2000 chars)
- **Relationships:** `@ManyToOne Notice`, `@ManyToOne User` — Links a comment to a specific notice and the user who wrote it.

#### `NoticeSeenBy.java` → Table: `notice_seen_by`
- **Fields:** `id`, `seenAt`
- **What it does:** When a student views a notice, a record is created here. This powers the Notice Analytics feature (how many students have read the notice).
- **Unique constraint:** A user can only mark a notice as "seen" once.

#### `Resource.java` → Table: `resources`
- **Fields:** `id`, `title`, `description`, `courseName`
- **Relationships:**
  - `@ManyToOne User uploadedBy` — The professor who created the resource.
  - `@OneToMany List<ResourceVersion> versions` — The actual file uploads, ordered by version number.

#### `ResourceVersion.java` → Table: `resource_versions`
- **Fields:** `id`, `versionNumber`, `fileName`, `contentType`, `fileSize`, `fileData`
- **How the PDF is stored:** The actual PDF file is converted into raw bytes and saved directly in the database using `@Lob` (Large Object).
- **Smart loading:** When you open the resources page, the system only loads the titles and metadata — it does NOT load the huge PDF files. The actual file bytes are only fetched when someone clicks "Download". This keeps the page fast. (This is called "Lazy Loading" — load heavy stuff only when you actually need it.)

#### `ResourceRating.java` → Table: `resource_ratings`
- **Fields:** `id`, `rating` (integer 1-5)
- **Unique constraint:** Each user can rate each resource only once (one rating per user per resource).

#### `Bookmark.java` → Table: `bookmarks`
- **Fields:** `id`, `targetType` ("NOTICE" or "RESOURCE"), `targetId`
- **What it does:** This is a clever design. Instead of making two separate tables for "bookmarked notices" and "bookmarked resources", we made ONE table that stores what TYPE of thing you bookmarked (notice or resource) and its ID. This keeps things simple.
- **Unique constraint:** You can't bookmark the same thing twice — the database prevents duplicates.

#### `Event.java` → Table: `events`
- **Fields:** `id`, `title`, `description`, `location`, `eventDate`
- **What it does:** Represents campus events (seminars, workshops, etc.).

#### `AuditLog.java` → Table: `audit_logs`
- **Fields:** `id`, `action`, `performedBy`, `details`, `performedAt`
- **What it does:** A standalone logging table. Every significant action (user registration, notice creation, resource upload) writes a record here. This gives admins a complete security trail.
- **Not auditable itself:** Unlike other entities, this does NOT extend `Auditable` — it manages its own `performedAt` timestamp via `@PrePersist`.

---

### 6.5 DTO Layer (`/dto`) — Data Transfer Objects

Imagine you have a student's full file in the office — it contains their marks, address, phone number, parent details, medical records, everything. If a teacher asks "what's this student's name?", you don't hand them the entire file. You write the name on a sticky note and hand that over. **DTOs are those sticky notes.**

We NEVER send the raw database objects (Entities) directly to the frontend because:
1. **Security risk** — The User entity contains the password hash. We don't want that leaving the server.
2. **Crash risk** — Entities have connected data that might not be loaded yet. Accessing it outside the database connection will crash.
3. **Control** — DTOs let us choose exactly what information the frontend gets.

| DTO | Purpose |
|---|---|
| `LoginRequest` | Carries email + password from the login form |
| `RegisterRequest` | Carries email + password + fullName + role from registration |
| `AuthResponse` | Returns the JWT token + user details after successful login |
| `ErrorResponse` | Standardized error format: `{status, message, timestamp}` |
| `NoticeDto` | Notice data for display (title, content, author name, seen count) |
| `NoticeRequest` | Data needed to create/update a notice |
| `NoticeCommentDto` | Comment content + author name + timestamp |
| `ResourceDto` | Resource metadata (title, course, rating, version count) |
| `ResourceVersionDto` | Version details (version number, file name, file size, date) |
| `ResourceRatingDto` | Rating value for a specific resource |
| `BookmarkDto` | Bookmark with target type, title, content preview |
| `EventDto` / `EventRequest` | Event display data and creation payload |
| `AuditLogDto` | Audit log entry for admin dashboard |
| `UserDto` | User info for admin user management (no password!) |

---

### 6.6 Controller Layer (`/controller`)

Controllers are the "receptionists" — they receive HTTP requests and delegate work to services.

| Controller | URL Prefix | Key Endpoints |
|---|---|---|
| `AuthController` | `/api/auth` | `POST /login`, `POST /register` |
| `NoticeController` | `/api/notices` | `GET /` (list all), `POST /` (create), `PUT /{id}`, `DELETE /{id}`, `POST /{id}/seen` |
| `NoticeCommentController` | `/api/notices/{id}/comments` | `GET /`, `POST /`, `DELETE /{commentId}` |
| `ResourceController` | `/api/resources` | `GET /` (list), `POST /` (upload), `GET /{id}/versions`, `POST /{id}/versions`, `GET /versions/{vId}/download`, `POST /{id}/rate` |
| `BookmarkController` | `/api/bookmarks` | `POST /` (toggle), `GET /` (list mine), `GET /check` |
| `SseController` | `/api/sse` | `GET /notices` (SSE stream — keeps connection open) |
| `EventController` | `/api/events` | Standard CRUD for campus events |
| `UserManagementController` | `/api/admin/users` | `GET /` (list users), `PUT /{id}/toggle-status` |
| `AuditLogController` | `/api/admin/audit-logs` | `GET /` (list all audit logs) |

---

### 6.7 Service Layer (`/service`)

Services contain the real business logic. Here's what each one does:

#### `AuthService.java`
- Handles user registration (checks for duplicate emails, hashes password with BCrypt, saves user).
- Handles login (validates credentials, generates JWT via `JwtUtils`).
- Writes audit log entries for both actions.

#### `NoticeService.java`
- Creates notices and links them to the posting user.
- Marks notices as "seen" by creating `NoticeSeenBy` records.
- Calculates engagement statistics (how many users saw a notice).

#### `NoticeCommentService.java`
- CRUD operations for comments under a notice.
- Validates that the notice exists before allowing comments.

#### `ResourceService.java`
- Handles file uploads using `MultipartFile`.
- Implements version control: if a resource exists, creates a new `ResourceVersion` instead of overwriting.
- Uses `@Transactional(readOnly = true)` to prevent `LazyInitializationException` when counting versions.
- Calculates average ratings.

#### `BookmarkService.java`
- Toggle bookmark (if bookmarked → remove, if not bookmarked → add).
- Fetches bookmarks and resolves the actual notice/resource content for display.

#### `SseService.java`
- Manages a list of active `SseEmitter` connections.
- When a new notice is created, loops through all emitters and pushes the notice data.
- Handles client disconnection gracefully.

#### `EventService.java`
- Standard CRUD for campus events.

#### `UserManagementService.java`
- Lists all users for admin dashboard.
- Toggles user active/disabled status.

#### `AuditLogService.java`
- Saves audit log entries to the database.
- Retrieves all logs for the admin audit dashboard.

---

### 6.8 Repository Layer (`/repository`)

This is where the magic happens. Repositories are just Java interfaces (not even classes!) with method names that describe what you want. Spring reads the method name and **automatically writes the SQL query for you**. You literally never write a single line of SQL in the entire project!

| Repository | Example Method | Auto-Generated SQL |
|---|---|---|
| `UserRepository` | `findByEmail(String email)` | `SELECT * FROM users WHERE email = ?` |
| `NoticeRepository` | `findAllByOrderByCreatedAtDesc()` | `SELECT * FROM notices ORDER BY created_at DESC` |
| `BookmarkRepository` | `findByUserIdAndTargetTypeAndTargetId(...)` | `SELECT * FROM bookmarks WHERE user_id=? AND target_type=? AND target_id=?` |
| `ResourceRatingRepository` | `findByResourceIdAndUserId(...)` | `SELECT * FROM resource_ratings WHERE resource_id=? AND user_id=?` |

---

### 6.9 Exception Handling (`/exception`)

#### `GlobalExceptionHandler.java`
- **What it does:** Catches ALL exceptions thrown anywhere in the backend and converts them into clean, consistent JSON error responses. Without this, Spring would return ugly HTML error pages.
- **Handles:** Validation errors (400), bad credentials (401), missing records (404), optimistic lock conflicts (409), and generic server errors (500).

#### `ResourceNotFoundException.java`
- A custom exception thrown when an entity isn't found (e.g., "Notice not found with id: 42").

---

### 6.10 Data Seeder (`/seeder`)

#### `DataSeeder.java`
- **What it does:** Runs automatically when the application starts.
- **Purpose:** Checks if an admin account (`admin@university.edu`) exists. If not, it creates one with password `admin123` and role `ADMIN`.
- **Why it matters:** Without this, you'd have no way to access the admin dashboard when deploying the app for the first time.

---

## 7. Frontend — Every File Explained

The frontend uses Angular's **Component-Based Architecture**. Every visible piece of the UI is a self-contained component with three files:
- `.ts` — Logic (TypeScript class)
- `.html` — Template (the HTML structure)
- `.css` — Styles (scoped CSS — styles don't leak to other components)

---

### 7.1 Application Bootstrap

#### `index.html`
- The one and only HTML page in this Single Page Application. Contains `<app-root></app-root>` where Angular mounts the entire app.

#### `main.ts`
- The JavaScript entry point. Calls `bootstrapApplication(AppComponent, appConfig)` which starts Angular.

#### `styles.css`
- The global design system. Defines:
  - CSS custom properties (`:root` variables like `--primary-color`, `--bg-main`, `--bg-sidebar`)
  - Base resets (box-sizing, font family)
  - Reusable utility classes (`.card`, `.btn`, `.btn-primary`, `.badge`, `.form-control`)
  - Imports Google's "Inter" font for modern typography.

#### `app.component.ts`
- The root component. Its only job is to contain `<router-outlet>`, which is where Angular swaps in different page components based on the current URL.

#### `app.config.ts`
- Configures Angular's dependency injection system:
  - `provideRouter(routes)` — Enables routing.
  - `provideHttpClient(withInterceptors([authInterceptor]))` — Enables HTTP calls and automatically attaches JWT tokens.
  - `provideZoneChangeDetection({ eventCoalescing: true })` — Performance optimization for change detection.

#### `app.routes.ts`
- The routing table. Maps URLs to components:
  - `/login` → `LoginComponent` (no guard — public)
  - All other routes are wrapped in `LayoutComponent` (protected by `authGuard`):
    - `/resources` → `ResourceRepositoryComponent`
    - `/notices` → `LiveNoticesComponent`
    - `/notices/:id/analytics` → `NoticeAnalyticsComponent` (Faculty/Admin only)
    - `/bookmarks` → `BookmarksComponent`
    - `/health` → `SystemHealthComponent` (Admin only)
    - `/admin/users` → `UserManagementComponent` (Admin only)
    - `/admin/audit-logs` → `AuditLogsComponent` (Admin only)

---

### 7.2 Authentication (`/auth`)

#### `auth.service.ts`
- The central authentication service. It:
  - Sends login/register requests to the backend.
  - Stores the JWT token and user info in `localStorage`.
  - Exposes a `BehaviorSubject` (`currentUser$`) so any component can reactively know who is logged in.
  - Provides helper methods: `getToken()`, `getRole()`, `getUserName()`, `isLoggedIn()`, `logout()`.

#### `auth.guard.ts`
- A route guard function. Before navigating to any protected route, it checks if the user is logged in. If not, it redirects to `/login`.

#### `role.guard.ts`
- A more specific guard. It checks if the logged-in user's role matches the required roles defined on the route (e.g., only `ADMIN` can access `/health`). If not, it redirects to `/resources`.

#### `auth.interceptor.ts`
- An HTTP interceptor function. It automatically attaches the `Authorization: Bearer <token>` header to every outgoing API call. Without this, you'd have to manually add the token to every `HttpClient.get()` and `.post()` call.

#### `login.component.ts` / `.html` / `.css`
- The login/registration page UI. Key features:
  - **Toggle mode:** One form for both login and registration.
  - **Role selection tabs:** When registering, user picks "Student" or "Faculty".
  - **Custom validator:** `collegeEmailValidator()` checks that the email ends in `.edu` or `.ac.in`.
  - **Inline error messages:** Shows "Password must be at least 6 characters", "Please use an institutional email", etc.
  - **Loading state:** Button shows "Signing in..." while waiting for the backend response.

---

### 7.3 Layout (`/layout`)

#### `layout.component.ts` / `.html` / `.css`
- The persistent shell of the application. Contains:
  - **Sidebar:** Logo, navigation links (Resources, Notices, Bookmarks), and admin-only links (System Health, User Management, Audit Logs).
  - **`<router-outlet>`:** Where page content loads based on the current route.
  - **Role-based visibility:** Uses `*ngIf="userRole === 'ADMIN'"` to show/hide admin menu items.
  - **Logout button:** Calls `AuthService.logout()` and redirects to `/login`.

---

### 7.4 Notice Board (`/notices`)

#### `notice.service.ts`
- API service for notices. Key methods:
  - `getNotices()` — Fetches all notices.
  - `createNotice(data)` — Posts a new notice.
  - `markAsSeen(id)` — Records that the current user has read the notice.
  - `listenToLiveNotices()` — Opens an `EventSource` SSE connection to receive real-time updates inside Angular's `NgZone`.

#### `comment.service.ts`
- API service for notice comments: `getComments()`, `addComment()`, `deleteComment()`.

#### `live-notices.component.ts` / `.html` / `.css`
- The main notice board page. Features:
  - **Real-time updates:** Subscribes to SSE stream on init, unsubscribes on destroy.
  - **Create notice modal:** Faculty/Admin can write and post new notices.
  - **Read more/less:** Expands long notice content with a toggle button.
  - **Commenting:** Inline comment section per notice with add/delete.
  - **Bookmarking:** Toggle bookmark on each notice card.
  - **Relative timestamps:** Shows "2 hours ago" instead of raw dates.

#### `notice-analytics.component.ts` / `.html` / `.css`
- Analytics dashboard for a specific notice (Faculty/Admin only). Shows:
  - How many users have seen the notice.
  - Reach percentage (seen ÷ total audience).
  - A CSS conic-gradient pie chart visualization.

---

### 7.5 Resource Repository (`/resources`)

#### `resource.service.ts`
- API service for resources. Key methods:
  - `getResources()` / `getResource(id)` — Fetch resource list or details.
  - `getVersions(id)` — Fetch version history for a resource.
  - `uploadNewResource(formData)` — Upload a new resource (file + metadata).
  - `uploadNewVersion(id, formData)` — Upload a new version of an existing resource.
  - `downloadResource(versionId)` — Download a specific file version as a blob.
  - `rateResource(id, rating)` — Submit a 1-5 star rating.

#### `resource-repository.component.ts` / `.html` / `.css`
- The resource browsing page. Features:
  - **Search bar:** Filters resources by title.
  - **Upload modal:** Faculty can upload new files with title, course name, and description.
  - **Version history panel:** Slide-over showing all versions with download buttons.
  - **Star ratings:** Users can rate resources.
  - **Bookmarking:** Toggle bookmark on each resource card.

---

### 7.6 Bookmarks (`/bookmarks`)

#### `bookmark.service.ts`
- API service: `toggleBookmark()`, `getBookmarks()`, `checkBookmark()`.

#### `bookmarks.component.ts` / `.html` / `.css`
- Displays all saved bookmarks. Features:
  - **Content preview:** Shows the full notice text or resource description directly in the bookmark card.
  - **Expand/collapse:** "Read more" / "Show less" toggle for long content.
  - **Unbookmark:** Remove bookmark directly from this page.

---

### 7.7 System Health (`/health`) — Admin Only

#### `system-health.service.ts`
- Calls the Spring Boot Actuator `/actuator/health` endpoint to get system status.

#### `system-health.component.ts` / `.html` / `.css`
- Admin dashboard showing:
  - Overall system status (UP/DOWN).
  - Database connection status.
  - Disk storage status.

---

### 7.8 Admin Panel (`/admin`)

#### `user-management.component.ts` / `.html` / `.css`
- Lists all registered users with their roles. Admin can:
  - See user counts by role (Students, Faculty, Admins).
  - Toggle a user's active/disabled status (effectively banning/unbanning them).

#### `audit-logs.component.ts` / `.html` / `.css`
- Displays a chronological list of every significant action in the system:
  - "User registered: amit@kiit.ac.in"
  - "Notice created: Mid-Semester Exam Schedule"
  - "Resource uploaded: DSA Notes v2"
- Each log entry has a color-coded badge based on action type.

---

## 8. Database Tables & Relationships

```
┌──────────┐     ┌───────────┐     ┌─────────────────┐
│  users   │────<│  notices   │────<│ notice_comments  │
│          │     │            │     └─────────────────┘
│          │     │            │────<│ notice_seen_by   │
│          │     └───────────┘     └─────────────────┘
│          │
│          │────<│ resources  │────<│ resource_versions │
│          │     │            │────<│ resource_ratings   │
│          │     └───────────┘     └──────────────────┘
│          │
│          │────<│ bookmarks  │     (polymorphic: targets notices OR resources)
│          │     └───────────┘
│          │
│          │────<│   events   │
│          │     └───────────┘
└──────────┘
                 ┌─────────────┐
                 │ audit_logs  │    (standalone — no foreign keys)
                 └─────────────┘
```

---

## 9. Deployment Architecture

```
┌─────────────────────┐        HTTPS         ┌──────────────────────┐
│     VERCEL           │ ──────────────────> │      RENDER            │
│  (Angular Frontend)  │   API calls with    │  (Spring Boot Backend) │
│  campus-alliance-    │   JWT Bearer token   │  campus-alliance-api.  │
│  sooty.vercel.app    │ <────────────────── │  onrender.com          │
└─────────────────────┘    JSON responses     └──────────────────────┘
                                                      │
                                                      │ JDBC
                                                      ↓
                                              ┌──────────────────┐
                                              │   PostgreSQL DB   │
                                              │ (Supabase / Neon) │
                                              └──────────────────┘
```

- **Vercel** automatically deploys on every `git push` to the `main` branch. It serves the static Angular files.
- **Render** detects the push, builds the Docker container using the `Dockerfile`, and deploys the Java backend.
- The database runs on a managed PostgreSQL service (always running, separate from both).

---

## 10. Interview Presentation Workflow

Follow this exact sequence when demonstrating the project:

**Step 1: Open the Login Page → Show Validation**
> "The frontend uses Angular ReactiveForms with custom validators. Only institutional emails ending in `.edu` or `.ac.in` are accepted. Passwords must be at least 6 characters. All validation is instant and inline."

**Step 2: Log in as a Student → Show Read-Only Experience**
> "I'm logging in as a student. Notice the sidebar only shows Resources, Notices, and Bookmarks. The student can read, comment, rate, and bookmark — but cannot create notices or upload resources."

**Step 3: Show Real-Time Notices**
> "The Notice Board uses Server-Sent Events. When I open a second browser as Faculty and post a notice, it appears here instantly without refreshing."

**Step 4: Log in as Faculty → Show Write Access**
> "As faculty, I can create notices targeted to specific audiences and upload versioned study resources. The backend maintains version history, so old versions are never lost."

**Step 5: Log in as Admin → Show Admin Dashboard**
> "As admin, three extra menu items unlock: System Health (Spring Actuator), User Management (enable/disable accounts), and Audit Logs (complete security trail of every action)."

---

## 11. Common Interview Questions & Answers

**Q: Why Angular instead of React?**
> "Angular provides built-in routing, forms, HTTP client, and dependency injection. For an enterprise portal with role-based access and complex forms, it reduces third-party dependency overhead compared to React."

**Q: How does your authentication work?**
> "Stateless JWT authentication. The backend issues a signed token containing the user's email and role. The Angular HTTP interceptor attaches this token to every request. The backend's JwtAuthenticationFilter validates it on every incoming request. No server-side sessions are stored."

**Q: What is the LazyInitializationException and how did you fix it?**
> "When Hibernate lazily loads a relationship (like `Resource.versions`), the data isn't fetched until you access it. If the database session is already closed by that point, it throws LazyInitializationException. I fixed it by adding `@Transactional(readOnly = true)` to my service methods, which keeps the Hibernate session open until the method returns."

**Q: Why SSE instead of WebSocket?**
> "Server-Sent Events are ideal for one-way broadcasting (server→client). WebSockets are bidirectional and heavier. Since students only receive notices (not send them via the socket), SSE is simpler, uses standard HTTP, and auto-reconnects on disconnect."

**Q: How does version control work for resources?**
> "Each `Resource` has a `OneToMany` relationship with `ResourceVersion`. When a professor uploads an updated file, the service creates a new `ResourceVersion` with an incremented version number, linked to the same parent Resource. The old version remains downloadable."

**Q: How do you handle file storage?**
> "Files are stored as binary blobs (`@Lob`) directly in PostgreSQL with lazy loading. This keeps the architecture simple and ensures atomic backups. For production scale, I'd migrate to AWS S3 and store only the URL in the database."

**Q: What happens if two admins edit the same notice simultaneously?**
> "The `Notice` entity uses `@Version` for optimistic locking. When the second admin tries to save, Hibernate detects the version mismatch and throws an `OptimisticLockException`, which my `GlobalExceptionHandler` catches and returns as a 409 Conflict response."

**Q: How do you protect admin-only routes?**
> "Two layers: (1) Frontend `RoleGuard` checks the JWT role claim before navigation. (2) Backend `SecurityConfig` restricts `/api/admin/**` to `ADMIN` role only. Even if someone bypasses the frontend guard, the backend will return 403 Forbidden."
