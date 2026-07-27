# 🎓 Campus Alliance - Complete Project Documentation

This document serves as the ultimate guide to understanding how the Campus Alliance project works, what technologies were used, and how the codebase is structured. It is written in a simple, easy-to-understand way, perfect for learning and interview preparation.

---

## 1. What is this project? (The Easy Explanation)
Imagine a university where students constantly miss important notices, and professors struggle to share study materials across different classes. 
**Campus Alliance** is a web platform that solves this. It acts as a central hub where:
- **Faculty/Admins** can post live announcements and upload study resources (like PDFs).
- **Students** can read these notices in real-time, comment on them, download study materials, and bookmark important items.

---

## 2. Technology Stack & Why We Used It

### 🖥️ Frontend: Angular 18 (TypeScript)
* **What it is:** The "face" of the application. It controls everything the user sees, clicks, and interacts with in the browser.
* **Why we used it:** Angular is a complete framework built by Google. Unlike React (which requires you to download many extra libraries for routing or forms), Angular comes with everything built-in. It is heavily used by massive enterprises (like banks) because it forces your code to be organized.
* **Easy Explanation:** Think of Angular as the **dashboard and steering wheel of a car**. It looks good and lets the driver control what happens.

### ⚙️ Backend: Spring Boot 3 (Java)
* **What it is:** The "brain" of the application running on a server. It receives requests from Angular, decides if the user has permission to do the action, processes the logic, and talks to the database.
* **Why we used it:** Spring Boot (Java) is famous for being incredibly secure, fast, and scalable. It uses something called "Dependency Injection" which makes managing complex logic very easy.
* **Easy Explanation:** Think of Spring Boot as the **engine of the car**. You can't see it when driving, but it's doing all the heavy lifting to make the car actually move.

### 🗄️ Database: PostgreSQL
* **What it is:** Where all the data (users, notices, resources, bookmarks) is permanently saved.
* **Why we used it:** PostgreSQL is the most advanced open-source relational database. We needed a relational database because our data is highly connected (e.g., a `User` posts a `Comment` which belongs to a `Notice`). 
* **Easy Explanation:** Think of the database as the **trunk of the car**. It safely stores all your luggage (data) so it's there the next time you drive.

---

## 3. Full Folder Structure & File Explanations

Here is a breakdown of the actual folders and files in your project and what they do.

### 📁 `frontend/` (The Angular App)
All the frontend code lives in `frontend/src/app/`.

* 📂 **`auth/`** (Authentication & Security)
  * `auth.service.ts`: The messenger that talks to the backend to log you in or register you.
  * `login.component.ts/.html`: The actual login screen UI. It checks if your email ends in `.edu` or `.ac.in`.
  * `auth.guard.ts`: A security guard that prevents users from typing `/notices` in the URL if they aren't logged in.

* 📂 **`notices/`** (The Live Notice Board)
  * `live-notices.component.ts`: The logic that fetches notices and listens for live updates using Server-Sent Events (SSE).
  * `live-notices.component.html`: The HTML structure of the notice cards you see on the screen.

* 📂 **`resources/`** (The Study Material Drive)
  * `resource-repository.component.ts`: Handles the logic for uploading files (PDFs) and downloading them.

* 📂 **`layout/`** (The App Shell)
  * `layout.component.html`: This file contains the permanent Sidebar (with the logo) that stays on the screen while you navigate between pages.

* 📂 **`bookmarks/`** (Saved Items)
  * `bookmarks.component.ts`: Fetches the items you saved. We recently updated this so you can read the full notice directly inside the bookmarks page!

---

### 📁 `backend/` (The Spring Boot App)
All the backend code lives in `backend/src/main/java/com/campusalliance/`.
The backend follows a strict "Layered Architecture". Data flows like this: `Controller` ➡️ `Service` ➡️ `Repository` ➡️ `Database`.

* 📂 **`controller/`** (The Receptionists)
  * *Purpose:* They answer the HTTP requests from Angular.
  * `NoticeController.java`: Receives a request like "Get me all notices", and asks the Service to find them.
  * `AuthController.java`: Receives login emails/passwords and returns a JWT security token.

* 📂 **`service/`** (The Managers / The Brains)
  * *Purpose:* This is where the actual business logic happens. 
  * `ResourceService.java`: This handles complex logic, like checking if a file version already exists, or fixing the "LazyInitializationException" we ran into earlier.
  * `NoticeService.java`: Manages the real-time SSE streams to push live notices to students.

* 📂 **`repository/`** (The Database Workers)
  * *Purpose:* These are simple interfaces that Spring Boot automatically translates into complex SQL queries.
  * `UserRepository.java`: Has methods like `findByEmail(String email)`. It talks directly to PostgreSQL.

* 📂 **`entity/`** (The Database Tables)
  * *Purpose:* These Java classes perfectly mirror the tables in your PostgreSQL database.
  * `User.java`: Represents a row in the `users` table.
  * `Notice.java`: Represents a row in the `notices` table.

* 📂 **`dto/`** (Data Transfer Objects / The Delivery Boxes)
  * *Purpose:* We don't want to send raw database entities to the frontend (it exposes passwords and unnecessary data). DTOs are lightweight "boxes" that only contain the exact data the frontend needs.
  * `ResourceDto.java`: Formats the resource data safely before sending it to Angular.

* 📂 **`config/`** (The Security Guards)
  * `SecurityConfig.java`: The bouncer. It checks every incoming request to make sure it has a valid JWT token, and blocks hackers from accessing your API.

---

## 4. How to Explain the Workflow in an Interview

If an interviewer asks, *"Walk me through how your application works,"* follow this exact script to sound like a Senior Developer:

**Step 1: The Login (Security)**
> "When a user logs in, the Angular frontend sends their credentials to the Spring Boot `AuthController`. The backend verifies the password using BCrypt hashing and generates a JSON Web Token (JWT). The frontend stores this token and attaches it to all future requests to prove the user's identity."

**Step 2: Role-Based Routing (UI Adaptation)**
> "Once logged in, the Angular `RoleGuard` reads the JWT. If the user is a Student, they get a read-only view. If they are a Professor or Admin, the UI dynamically unlocks hidden buttons, like 'Create Notice' or 'System Health'."

**Step 3: Real-Time Data (The Notice Board)**
> "If you look at the Notice Board, it updates instantly. Instead of using WebSockets (which are heavy), I implemented **Server-Sent Events (SSE)** in Spring Boot. It keeps a lightweight, one-way HTTP connection open so the server can push new announcements directly to the students' browsers in real-time."

**Step 4: Handling Complex Database Relations (The Fix)**
> "I also implemented a file versioning system for the resources. I actually ran into a common enterprise issue called the `LazyInitializationException` because my Service layer was trying to count file versions after the database connection closed. I solved this by strategically placing `@Transactional(readOnly = true)` annotations, which safely keeps the Hibernate session open."

**Step 5: File Storage (The Future)**
> "Right now, PDFs are securely stored in the PostgreSQL database as binary blobs. This makes backups easy. However, if this app scales to 10,000 users, my architectural plan is to migrate file storage to AWS S3 and only store the file URLs in the database to optimize performance."
