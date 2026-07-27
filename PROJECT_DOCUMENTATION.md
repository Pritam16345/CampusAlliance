# 🎓 Campus Alliance - Ultimate Project Documentation

This document is the **complete, in-depth guide** to the Campus Alliance project. It breaks down every single component, workflow, and architectural decision. Use this guide to deeply understand the codebase and perfectly present your project during your deep skilling course and in professional job interviews.

---

## 1. Executive Summary
**Campus Alliance** is a modern, full-stack Academic Management Portal. Large universities often struggle with fragmented communication—students miss important emails, and professors have a hard time distributing and updating study materials. 

Campus Alliance centralizes this ecosystem by providing:
- **Role-Based Authentication:** Distinct capabilities for Students, Faculty, and Admins.
- **Real-Time Notice Board:** Live announcements pushed instantly to users without page reloads.
- **Version-Controlled Resource Drive:** A secure repository for academic files (PDFs) with built-in version tracking.
- **Administrative Oversight:** Comprehensive audit logging to track every action taken within the system.

---

## 2. System Architecture & Tech Stack Details

The application is built using a modern **Three-Tier Architecture**:

### Tier 1: Client (Frontend) - Angular 18 & TypeScript
* **Core Function:** Handles the User Interface (UI) and User Experience (UX). It captures user input, displays data, and manages the client-side routing.
* **Why Angular?** Angular is a comprehensive framework. It provides built-in tools for HTTP requests (`HttpClient`), complex form validation (`ReactiveForms`), and route protection (`AuthGuard`). This makes it highly suitable for enterprise applications compared to lightweight libraries.
* **Styling:** Custom Vanilla CSS utilizing modern CSS Variables (`var(--primary-color)`) to implement a clean, cohesive, and professional design system without relying on heavy external UI frameworks like Bootstrap.

### Tier 2: Application Server (Backend) - Java 23 & Spring Boot 3
* **Core Function:** The brain of the operation. It exposes a RESTful API that the frontend consumes. It handles business logic, security, and data validation.
* **Why Spring Boot?** Spring Boot is the industry standard for Java enterprise development. It provides "Inversion of Control" (IoC) and "Dependency Injection", meaning it automatically wires together your database connections and services so you don't have to write boilerplate code.
* **Security:** Secured using **Spring Security** and **JWT (JSON Web Tokens)**. Every request is intercepted, and the token is verified before any data is returned.

### Tier 3: Database - PostgreSQL
* **Core Function:** Persistent storage of all application data.
* **Why PostgreSQL?** Our data is highly relational. A `User` creates a `Notice`. A `User` can bookmark a `Notice`. A `Resource` has multiple `ResourceVersions`. PostgreSQL handles these complex relationships, foreign keys, and cascading deletes efficiently.

---

## 3. Deep Dive into the Folder Structure & Files

### 📁 Frontend: `frontend/src/app/`
Angular uses a Component-Based Architecture. Every piece of the screen is an independent component.

#### 🔐 Authentication (`/auth`)
- **`login.component.ts`**: Contains the logic for the login and registration form. It uses `FormBuilder` to enforce strict validation rules (e.g., ensuring the email ends in `.edu` or `.ac.in`).
- **`auth.service.ts`**: The bridge to the backend. It takes the email/password, sends a `POST` request to the backend `/api/auth/login`, and saves the returned JWT token to the browser's `localStorage`.
- **`auth.guard.ts` & `role.guard.ts`**: Security guards for the router. If a user tries to access `/admin/users` but their JWT token says they are a "STUDENT", the `role.guard.ts` blocks the navigation and redirects them.

#### 📢 Notice Board (`/notices`)
- **`live-notices.component.ts`**: This component connects to the backend using an `EventSource` object to listen for **Server-Sent Events (SSE)**. When a new notice is published, it instantly pushes the notice into the UI array, making it appear in real-time.
- **`live-notices.component.html`**: Uses Angular structural directives like `*ngFor` to loop through the notices array and render a beautiful card for each one, including a dynamic "Read More" button for long texts.

#### 📚 Resource Repository (`/resources`)
- **`resource-repository.component.ts`**: Handles fetching resources and managing file uploads using the `FormData` object. It includes logic to handle different file states (like uploading vs. downloading).

#### 🗂️ Bookmarks (`/bookmarks`)
- **`bookmarks.component.ts`**: Fetches the user's personalized saved items. It receives complex DTOs (Data Transfer Objects) from the backend that contain both the bookmark metadata and the actual content of the bookmarked notice/resource.

---

### 📁 Backend: `backend/src/main/java/com/campusalliance/`
Spring Boot uses a Layered Architecture.

#### 🚦 Controllers (`/controller`)
Controllers are the entry points. They map HTTP URLs to Java methods.
- **`NoticeController.java`**: Listens for `GET /api/notices/stream`. When called, it returns an `SseEmitter` object, which keeps the HTTP connection open indefinitely to stream data.
- **`ResourceController.java`**: Uses `@PostMapping` with `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` to accept physical file uploads from the frontend.

#### 🧠 Services (`/service`)
Services contain the heavy business logic. Controllers should never talk directly to the database; they ask Services to do it.
- **`ResourceService.java`**: When uploading a file, this service checks if the resource already exists. If it does, it increments the version number. It uses `@Transactional(readOnly = true)` to safely fetch lazy-loaded database relationships without crashing the system (preventing `LazyInitializationException`).

#### 🗄️ Repositories (`/repository`)
Repositories are interfaces that extend `JpaRepository`.
- **`UserRepository.java`**: By simply writing a method signature like `Optional<User> findByEmail(String email);`, Spring Data JPA automatically writes the complex SQL query required to fetch a user by their email.

#### 📦 Entities & DTOs (`/entity` & `/dto`)
- **Entities (`Notice.java`, `User.java`)**: These define the exact schema of your PostgreSQL tables. They use annotations like `@Entity` and `@OneToMany` to define foreign key relationships.
- **DTOs (`ResourceDto.java`)**: Data Transfer Objects. We use DTOs to format our data safely before sending it over the internet. For example, the `User` entity contains a hashed password. We map the `User` to a `UserDto` which *excludes* the password so it doesn't accidentally get sent to the frontend.

---

## 4. Detailed System Workflows

To truly master the project, you need to understand how data flows through the system.

### Workflow A: The Authentication Flow
1. **User Action:** User enters `sneha@kiit.ac.in` and `password123` and clicks "Sign In".
2. **Frontend:** `auth.service.ts` makes a POST request to `/api/auth/login`.
3. **Backend Controller:** `AuthController` receives the payload.
4. **Backend Security:** `AuthenticationManager` looks up the user in PostgreSQL. It hashes the provided password using BCrypt and compares it to the stored hash.
5. **Token Generation:** If they match, `JwtService` generates a JSON Web Token (JWT) encoding the user's Email and Role, signs it with a secret cryptographic key, and returns it.
6. **Frontend Storage:** Angular receives the JWT and stores it in `localStorage`.
7. **Subsequent Requests:** Angular's `JwtInterceptor` intercepts every future outgoing HTTP request and injects `Authorization: Bearer <token>` into the header.

### Workflow B: The Real-Time Notice Flow (SSE)
1. **Connection:** When a student opens the Notice Board, Angular creates an `EventSource` connection to `/api/notices/stream`.
2. **Backend Setup:** Spring Boot returns an `SseEmitter` and saves it in a list of active connections inside `NoticeService`. The connection remains open.
3. **Trigger:** A Professor submits a new Notice.
4. **Broadcast:** The `NoticeService` saves the notice to PostgreSQL, then loops through all active `SseEmitter` connections, pushing the new Notice JSON directly to the clients.
5. **UI Update:** The Angular component receives the event and pushes it to the top of the screen instantly.

### Workflow C: The Resource Versioning Flow
1. **Upload:** A Professor uploads an updated syllabus PDF.
2. **Backend Processing:** `ResourceService` extracts the raw bytes of the file.
3. **Database Check:** It queries PostgreSQL: "Does a resource named 'Syllabus' for course 'CS101' already exist?"
4. **Versioning:** It finds the existing resource. Instead of overwriting it, it creates a new `ResourceVersion` entity (e.g., v2), attaches the new file bytes to it, and links it to the parent `Resource`.
5. **Save:** Both are saved. When students view the repository, they see the resource has "2 Versions" available.

---

## 5. Interview Presentation Guide

When demonstrating this project to an interviewer or professor, use this step-by-step narrative:

> *"Hello, I'd like to present Campus Alliance, a full-stack Academic Management Portal built with Angular, Spring Boot, and PostgreSQL."*

**Step 1: Security & Validation (Show Login)**
> *"I'll start at the authentication layer. I implemented strict client-side validation using Angular ReactiveForms to ensure only institutional emails (.edu/.ac.in) are accepted. When I log in, the Spring Boot backend issues a stateless JWT token, which Angular intercepts and attaches to all future API calls."*

**Step 2: Real-Time Architecture (Show Notice Board)**
> *"Now I'm logged in as a Student. This Notice Board is fully real-time. Instead of heavy WebSockets, I implemented Server-Sent Events (SSE) in Spring Boot. It's highly optimized for one-way broadcasting. If a faculty member posts a notice, it appears here instantly without the student needing to refresh."*

**Step 3: Relational Data & Bug Fixing (Show Bookmarks & Resources)**
> *"The database relies on complex JPA relationships. For example, inside Bookmarks, I had to ensure the backend efficiently fetches both the bookmark metadata and the parent Notice content. Similarly, in the Resource Repository, I implemented a robust version-control system for uploaded PDFs. While building this, I overcame a complex `LazyInitializationException` in Hibernate by implementing proper `@Transactional` boundaries in my Service layer to manage the database session lifecycle."*

**Step 4: Role-Based Access Control (Log out, Log in as Admin/Faculty)**
> *"If I log out and sign in as an Admin, you'll see the Angular `RoleGuard` dynamically unlocks administrative routes. The backend Spring Security filter chain also enforces this—if a student tries to hack the API to view System Health, the server will return a 403 Forbidden error."*

**Step 5: Audit Logging (Show Audit Logs)**
> *"Finally, for enterprise compliance, every critical action (like logins, uploads, and notice creations) is recorded in an Audit Log table using Spring Data JPA, providing a full security trail for administrators."*
