# Campus Alliance 🎓

A full-stack Academic Resource and Notice Management platform designed for seamless collaboration between university faculty, administration, and students.

## 🌟 Features
- **Role-Based Access Control (RBAC):** Distinct permissions for Students (read), Faculty (read/write), and Admins (full control).
- **Secure Authentication:** JWT-based stateless authentication.
- **Resource Repository:** A central hub for uploading, managing, and downloading academic files with built-in version control (e.g. keeping multiple iterations of a syllabus).
- **Live Notice Board:** Real-time announcement broadcasting using Server-Sent Events (SSE). No page refreshes required!
- **System Health Monitoring:** Built-in Spring Boot Actuator integration for live database and storage service health tracking.

## 🛠 Tech Stack
- **Backend:** Java 23, Spring Boot 3.3, Spring Security, Spring Data JPA, Hibernate, PostgreSQL.
- **Frontend:** Angular 18, RxJS, Vanilla CSS (Custom Design System).
- **Deployment:** Docker, Docker Compose, Nginx.

## 🚀 Getting Started (Docker)

The absolute easiest way to run the entire application stack is via Docker Compose.

1. Ensure [Docker Desktop](https://www.docker.com/products/docker-desktop/) is installed and running.
2. Clone this repository.
3. Open a terminal in the project root directory.
4. Run the following command:
   ```bash
   docker-compose up --build -d
   ```

This will automatically:
1. Spin up a **PostgreSQL 15** database container.
2. Build and launch the **Spring Boot API** on port `8080`.
3. Build and launch the **Angular UI** on port `80` (via Nginx).

### Accessing the Application
- **Frontend:** Open your browser to [http://localhost](http://localhost)
- **Backend API:** Available at `http://localhost:8080/api`
- **Actuator Health:** Available at `http://localhost:8080/actuator/health`

## 👨‍💻 Local Development

If you prefer to run the components independently for development:

### Database Setup
Ensure PostgreSQL is running locally on port `5432` with a database named `campus_connect`, user `postgres`, and password `password`. Alternatively, spin up just the DB using docker:
```bash
docker-compose up db -d
```

### Backend Setup
1. Navigate to the `backend` folder: `cd backend`
2. Run Maven wrapper: `./mvnw spring-boot:run`
3. The API will start on `http://localhost:8080`.

### Frontend Setup
1. Navigate to the `frontend` folder: `cd frontend`
2. Install dependencies: `npm install`
3. Start the dev server: `npm run dev` (or `ng serve`)
4. The UI will start on `http://localhost:4200`.

## 🧪 Testing
The backend features comprehensive unit tests focusing on core business logic. Mockito is heavily utilized to isolate the Service layer from the database layer, ensuring fast and robust test execution.
To run the tests:
```bash
cd backend
./mvnw test
```

## 📐 Architecture Decisions
- **Vanilla CSS:** To ensure complete control over the bespoke, premium UI mockups and to demonstrate fundamental CSS mastery, no heavy UI frameworks (like Bootstrap/Material) or utility classes (like Tailwind) were used.
- **SSE vs WebSockets:** Server-Sent Events were chosen for the Notice Board as it is a unidirectional stream (Server -> Client), making it significantly lighter and easier to implement over standard HTTP than a full bidirectional WebSocket connection.
- **Soft Deletes:** Removed from the backend API for simplicity and data cleanliness, but the UI mockups for the Recycle Bin remain active (via mock data) to fulfill visual requirements.
