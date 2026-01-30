# Edupage - School Management System

A full-stack school management application built with React, Spring Boot, and PostgreSQL.

## Features

### Students
- View weekly schedule
- Track attendance
- View grades and averages
- Read announcements

### Teachers
- View teaching schedule
- Mark student attendance
- Add and manage grades
- Create announcements

### Admins
- Manage all schedules (create/edit/auto-generate)
- Manage users (students, teachers, admins)
- Create system-wide announcements
- View all data

## Tech Stack

- **Frontend**: React 18, Vite, React Router
- **Backend**: Spring Boot 3.2, Spring Security, JWT
- **Database**: PostgreSQL 15
- **Containerization**: Docker, Docker Compose

## Quick Start

### Using Docker Compose (Recommended)

```bash
# Clone the repository
cd edupagel

# Start all services
docker-compose up --build

# Access the application
# Frontend: http://localhost:5173
# Backend API: http://localhost:8080

### Local Development

#### Backend

```bash
    cd C:\Users\Adilkan\Documents\EdupageProject
    .\manage.ps1 start
```


## Demo Credentials

After starting the application, the database will be seeded with demo data:

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@edupage.com | admin123 |
| Teacher | john.smith@edupage.com | teacher123 |
| Teacher | jane.doe@edupage.com | teacher123 |
| Teacher | bob.wilson@edupage.com | teacher123 |
| Student | alice.johnson@edupage.com | student123 |
| Student | charlie.brown@edupage.com | student123 |
| Student | emma.davis@edupage.com | student123 |

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - Register new user (Admin only)

### Schedule
- `GET /api/schedule/week` - Get weekly schedule
- `POST /api/schedule` - Create schedule entry (Admin)
- `POST /api/schedule/generate` - Auto-generate schedule (Admin)

### Attendance
- `GET /api/attendance` - Get student's attendance
- `POST /api/attendance` - Mark attendance (Teacher)

### Grades
- `GET /api/grades` - Get student's grades
- `POST /api/grades` - Add grade (Teacher)

### Announcements
- `GET /api/announcements` - Get announcements
- `POST /api/announcements` - Create announcement (Teacher/Admin)

## Project Structure

```
edupagel/
├── backend/
│   ├── src/main/java/com/edu/edupage/
│   │   ├── config/         # Security, CORS configuration
│   │   ├── controller/     # REST controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # JPA repositories
│   │   ├── security/       # JWT authentication
│   │   └── service/        # Business logic
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/     # Reusable UI components
│   │   ├── context/        # React context
│   │   ├── pages/          # Page components
│   │   ├── services/       # API services
│   │   └── App.jsx
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
└── README.md
```

## License

MIT License

<!-- Quick start -->

```bash
    cd C:\Users\Adilkan\Documents\EdupageProject
    .\manage.ps1 start
```

<!-- Swagger -->

http://localhost:8080/swagger-ui.html