EduOps MVP — Technical Specification & Task Backlog
(Flutter + Django)
Version 0.2 | Date: January 31, 2026 | Region: Kyrgyzstan

1. Purpose
This document defines the MVP requirements, architecture, API surface, UX scope, and a granular task backlog to distribute work across a student team building EduOps — a school / course-center management system for Kyrgyzstan.
2. Product overview
EduOps helps an education organization manage: students, groups/classes, schedules, attendance, and payments.
MVP focus: operational basics (attendance + payments + announcements) with role-based access.
2.1 MVP goals
Launch a working system for 1 organization with 1 or more branches.
Allow fast onboarding (import students/groups via CSV).
Track attendance per session and produce simple reports.
Track invoices and payments and show debt/paid status per student.
Provide announcements (organization-wide or per group).
2.2 Out of scope for MVP (planned later)
Full LMS (video hosting, quizzes at scale).
Complex payroll/accounting, inventory, and procurement.
Native in-app voice/video calls (add later via compliant third-party services).
Government integrations (if needed later).
Multi-tenant white-label partner portal (post-MVP).
3. Target users and roles
Primary customers in Kyrgyzstan: private schools and private course centers.
3.1 Roles
Super Admin: system-level operator (creates organizations, manages global settings).
Org Admin (Director): manages branches, users, groups, pricing, and configuration.
Accountant: manages invoices, payments, debt lists, revenue reports.
Teacher: views own schedule, marks attendance, posts announcements, views roster.
Parent/Student: views schedule, attendance summary, announcements, and payment status.
3.2 Permission matrix (MVP)
RBAC must be enforced on the backend; frontend must hide/disable unauthorized actions.
Module
Super Admin
Org Admin
Accountant
Teacher
Parent/Student
Organization setup
R/W
R/W
R
R
—
Users & roles
R/W
R/W
—
—
—
Students & groups
R/W
R/W
R
R
R (own only)
Schedule
R/W
R/W
R
R/W (own groups)
R (own only)
Attendance
R/W
R/W
R
R/W (own groups)
R (own only)
Invoices & payments
R/W
R/W
R/W
R (read-only)
R (own only)
Announcements
R/W
R/W
R
R/W (own groups)
R (own only)
Reports
R/W
R/W
R/W
R (limited)
—

4. User journeys (MVP)
4.1 Onboarding
Org Admin logs in and creates branches (optional).
Org Admin creates groups/classes and assigns teachers.
Org Admin imports students (CSV) and links parents (phone number).
Accountant configures pricing templates (monthly / per course).
System generates invoices; parents/students can view status.
4.2 Daily operations
Teacher opens today's schedule, selects a lesson, marks attendance.
Teacher posts an announcement to a group (e.g., homework).
Accountant records a payment and the system updates debt status.
Org Admin views attendance and revenue reports for the week/month.
5. Functional requirements
5.1 Authentication & accounts
Login via phone number + password (MVP). Optional: email login later.
JWT-based authentication (access + refresh).
Password reset via admin action (MVP); OTP/SMS later.
All API endpoints require authentication except health checks and auth endpoints.
5.2 Organization & branches
An Organization contains 1..N Branches.
Each user belongs to exactly 1 Organization; optionally assigned to a Branch.
Org settings: timezone, language defaults (ru/kg), currency (KGS).
5.3 Users & roles
Org Admin can create users (teachers, accountants) with role assignment.
Teacher profile: full name, phone, status (active/inactive).
Parent/Student accounts can be auto-created when linking a phone number (MVP).
5.4 Students
Create/read/update/archive students.
Fields (MVP): full name, parent phone, branch, status, notes (optional).
CSV import with validation and row-level error report.
5.5 Groups / classes
Groups represent a class (school) or cohort (course center).
Fields: name, branch, assigned teacher(s), schedule template, pricing plan.
Enroll/unenroll students with effective date (Enrollment history).
5.6 Schedule
Weekly schedule template per group: weekday, start time, duration, room (optional).
Generate concrete Sessions for the next N weeks (default 4).
Teacher sees 'My schedule' and group schedule.
5.7 Attendance
Record attendance per session and per enrolled student.
Statuses: present, absent, late, excused.
Teacher can submit; editing allowed within a configurable window (e.g., 24h).
Parents/students view own attendance summary only.
5.8 Invoices & payments
Pricing plans: monthly fixed fee per student per group (MVP).
Generate monthly invoices for active enrollments (period = YYYY-MM).
Record payments (cash/card/transfer) with notes; support partial payments.
Debt list per student and per group.
5.9 Announcements
Post announcements by Org Admin or Teacher.
Scopes: organization-wide, branch-wide, or group-specific.
MVP: text only; attachments later.
5.10 Reports (MVP)
Attendance report per group (date range): counts and percent.
Revenue report per group/branch (month): invoiced vs paid vs debt.
Teacher workload: sessions taught in date range (simple).
6. Data model (high level)
Core entities (suggested naming; adapt to Django conventions):
Entity
Key fields
Notes
Organization
name, default_language, currency
Top-level tenant
Branch
organization, name, address(optional)
Optional multi-campus
User
phone, password_hash, role, organization, branch(optional)
Django User + profile
Student
organization, branch, full_name, parent_phone, status
Archive instead of delete
Group
organization, branch, name, teachers, pricing_plan
Class/cohort
Enrollment
student, group, start_date, end_date(optional)
Historical membership
ScheduleTemplate
group, weekday, start_time, duration_min, room(optional)
Weekly template
Session
group, date, start_datetime, duration_min
Generated from template
AttendanceRecord
session, student, status, marked_by, marked_at
One per student per session
PricingPlan
group, monthly_fee_kgs
MVP: monthly only
Invoice
student, group, period, amount, status
Generated monthly
Payment
invoice, amount, method, paid_at, note
Multiple payments per invoice
Announcement
scope, group(optional), body, created_by
Text only in MVP
AuditLog
user, action, entity, entity_id, created_at
Critical actions

7. API specification (REST, MVP)
Backend: Django + DRF. Auth: JWT. Version all endpoints under /api/v1/.
Area
Endpoint
Methods
Notes
Auth
/api/v1/auth/login
POST
phone+password -> tokens
Auth
/api/v1/auth/refresh
POST
refresh -> new access
Org
/api/v1/org/me
GET
Org settings
Branches
/api/v1/branches
GET/POST
List/create
Branches
/api/v1/branches/{id}
GET/PATCH/DELETE
Soft delete recommended
Users
/api/v1/users
GET/POST
Org Admin only
Users
/api/v1/users/{id}
GET/PATCH
Role, status
Students
/api/v1/students
GET/POST
Filters: branch,status,search
Students
/api/v1/students/import
POST
CSV import
Groups
/api/v1/groups
GET/POST
Create group, assign teachers
Groups
/api/v1/groups/{id}
GET/PATCH
Update
Enroll
/api/v1/groups/{id}/enrollments
GET/POST
Add/remove students
Schedule
/api/v1/groups/{id}/schedule-template
GET/PUT
Weekly template
Schedule
/api/v1/sessions
GET
My sessions / group sessions
Attendance
/api/v1/sessions/{id}/attendance
GET/PUT
Mark statuses
Billing
/api/v1/pricing-plans
GET/POST
MVP: monthly fee
Billing
/api/v1/invoices
GET
Filters: student,group,period,status
Billing
/api/v1/invoices/{id}/payments
GET/POST
Record payment
Announcements
/api/v1/announcements
GET/POST
Scope filters
Reports
/api/v1/reports/attendance
GET
date_from,date_to,group
Reports
/api/v1/reports/revenue
GET
month,branch/group

8. Flutter app scope (MVP)
Recommended: one Flutter app (responsive web + mobile) with role-based navigation.
8.1 Core screens
Login (phone/password), token refresh handling, logout.
Role-based Home dashboard (Admin/Teacher/Accountant/Parent).
Students: list, detail, create/edit (Admin), search, filters.
Groups: list, detail, roster, enroll/remove students (Admin).
Schedule: calendar/list view; session detail.
Attendance: session roster + status picker + submit (Teacher).
Billing: invoices list/detail + add payment (Accountant/Admin).
Announcements: list + create (Teacher/Admin) and read view.
Reports: attendance summary + revenue summary (Admin/Accountant).
Settings (minimal): language switch ru/kg, profile.
9. Non-functional requirements
Performance: list screens load within ~2 seconds on typical mobile data (when backend reachable).
Security: HTTPS, JWT, RBAC, server-side validation, audit logs for critical actions.
Reliability: daily backups; restore target within 24 hours.
Localization: Russian default; Kyrgyz ready (translate key UI strings).
10. Tech stack and architecture
Backend: Django + DRF, PostgreSQL.
Auth: JWT (simplejwt).
Optional background jobs: Celery + Redis (invoice generation); can be a management command in MVP.
Frontend: Flutter (Bloc/Cubit) + Dio.
Deployment: Docker Compose (api, db, redis optional, nginx).
11. Acceptance criteria (MVP)
Admin can create branches, users, groups, and enroll students.
Teacher can mark attendance for a session; attendance appears in reports.
Accountant can generate monthly invoices and record payments; debt status updates correctly.
Parent/Student can view own attendance summary + invoice status + announcements.
Reports work for selected date range / month.
API RBAC prevents unauthorized access (verified by tests).
12. Task backlog (granular, distributable)
Format: TASK-ID — Description | Owner | Estimate (S/M/L) | Depends on | Definition of Done (DoD).
12.1 Global Definition of Done (DoD)
Code reviewed and merged to main.
All unit/integration tests pass in CI.
Basic error handling and validation implemented.
User-facing text is in RU (KG optional in MVP) and supports Cyrillic.
Minimal documentation updated (README or user guide section).
12.2 Sprint 0 — Foundations
S0-1 — Create mono-repo structure, code style, branching rules | DevOps | M | Depends: — | DoD: CI runs lint/test
S0-2 — Docker Compose: api + db + nginx (and redis optional) | DevOps | M | Depends: — | DoD: One command brings stack up
S0-3 — Seed/demo data command for local development | Backend | S | Depends: S0-2 | DoD: Creates org, users, groups, students
S0-4 — Flutter app shell: routing, theme, environment config | Frontend | M | Depends: — | DoD: Runs on web + mobile
12.3 Backend epics
Epic B1 — Authentication & RBAC
B1-1 — Phone+password login endpoint + JWT refresh | Backend | M | Depends: S0-1 | DoD: Tokens issued/validated
B1-2 — Role/permission middleware (RBAC) + 403 responses | Backend | L | Depends: B1-1 | DoD: Unauthorized blocked
B1-3 — AuditLog for critical actions (create user, payment, attendance) | Backend | M | Depends: B1-2 | DoD: Logs stored & queryable
Epic B2 — Organization & Branches
B2-1 — Organization/Branch models + CRUD endpoints | Backend | M | Depends: B1-2 | DoD: CRUD works with RBAC
Epic B3 — Users
B3-1 — UserProfile (teacher/accountant/parent) + status | Backend | M | Depends: B1-2 | DoD: Profiles created/updated
B3-2 — Org Admin user management endpoints | Backend | M | Depends: B3-1 | DoD: Create user & assign role
Epic B4 — Students
B4-1 — Student CRUD + filters + pagination | Backend | M | Depends: B1-2 | DoD: List/search works
B4-2 — CSV import endpoint + row-level error report | Backend | M | Depends: B4-1 | DoD: Import 200 students
Epic B5 — Groups & Enrollments
B5-1 — Group CRUD + assign teacher(s) | Backend | M | Depends: B3-2 | DoD: Group endpoints
B5-2 — Enrollment model + add/remove student with dates | Backend | M | Depends: B4-1,B5-1 | DoD: Roster updates
Epic B6 — Schedule
B6-1 — ScheduleTemplate CRUD per group | Backend | M | Depends: B5-1 | DoD: Template saved
B6-2 — Session generation service for next 4 weeks | Backend | L | Depends: B6-1,B5-2 | DoD: Sessions created and queryable
Epic B7 — Attendance
B7-1 — Attendance endpoints per session (GET/PUT) | Backend | M | Depends: B6-2 | DoD: PUT updates attendance
B7-2 — Attendance editing window logic (configurable) | Backend | S | Depends: B7-1 | DoD: Edits blocked after window
Epic B8 — Billing
B8-1 — PricingPlan (monthly fee) per group | Backend | M | Depends: B5-1 | DoD: Fee stored and validated
B8-2 — Monthly invoice generation command | Backend | L | Depends: B8-1,B5-2 | DoD: Invoices created for month
B8-3 — Payment endpoint + partial payments support | Backend | M | Depends: B8-2 | DoD: Debt status correct
Epic B9 — Announcements
B9-1 — Announcement model + scoped CRUD | Backend | M | Depends: B1-2 | DoD: Group/org scopes work
Epic B10 — Reports
B10-1 — Attendance report endpoint (aggregate) | Backend | M | Depends: B7-1 | DoD: Returns counts/percent
B10-2 — Revenue report endpoint (invoiced/paid/debt) | Backend | M | Depends: B8-3 | DoD: Numbers match invoices/payments
12.4 Frontend epics (Flutter)
Epic F1 — App shell & Auth
F1-1 — Login UI + token storage + refresh interceptor | Frontend | M | Depends: B1-1 | DoD: Login works end-to-end
F1-2 — Role-based navigation and home dashboard | Frontend | M | Depends: F1-1,B1-2 | DoD: Correct menu per role
Epic F2 — Admin workflows
F2-1 — Branch management screens | Frontend | M | Depends: B2-1 | DoD: CRUD from app
F2-2 — User management screens (create teacher/accountant) | Frontend | M | Depends: B3-2 | DoD: Create user + assign role
F2-3 — Students list/detail/create/edit + filters | Frontend | L | Depends: B4-1 | DoD: CRUD from app
F2-4 — Student CSV import UI | Frontend | M | Depends: B4-2 | DoD: Shows created/errors
F2-5 — Groups list/detail + roster + enroll/remove | Frontend | L | Depends: B5-2 | DoD: Enrollment flows work
Epic F3 — Teacher workflows
F3-1 — My schedule view + session detail | Frontend | M | Depends: B6-2 | DoD: Sessions visible
F3-2 — Attendance marking UI (status picker + submit) | Frontend | L | Depends: B7-1 | DoD: Teacher can submit
F3-3 — Create announcements for own groups | Frontend | M | Depends: B9-1 | DoD: Post and see in feed
Epic F4 — Accountant workflows
F4-1 — Invoices list + filters + invoice detail | Frontend | M | Depends: B8-2 | DoD: Shows status/debt
F4-2 — Add payment form + validation | Frontend | M | Depends: B8-3 | DoD: Debt updates after payment
F4-3 — Revenue report screen (month) | Frontend | M | Depends: B10-2 | DoD: Matches backend totals
Epic F5 — Parent/Student workflows
F5-1 — Parent home: attendance summary + invoice status | Frontend | M | Depends: B7-1,B8-2 | DoD: Own-only data shown
F5-2 — Announcements feed (own scopes) | Frontend | S | Depends: B9-1 | DoD: Feed loads and filters
12.5 QA, DevOps, and Documentation
QA-1 — API tests: auth, RBAC, attendance, billing | QA/Backend | M | Depends: B1-2,B7-1,B8-3 | DoD: Coverage for key flows
QA-2 — Flutter tests: login, attendance submit, invoices | QA/Frontend | M | Depends: F1-1,F3-2,F4-2 | DoD: Runs in CI
DOP-1 — Staging deploy + environment variables + logs | DevOps | M | Depends: S0-2 | DoD: Staging reachable
DOC-1 — User guide: Admin/Teacher/Accountant/Parent | Docs | S | Depends: Core screens ready | DoD: Step-by-step documented
REL-1 — Demo script + seeded dataset + demo checklist | PM/All | S | Depends: Most features ready | DoD: Demo runs without surprises
13. Suggested sprint plan (6 weeks)
Sprint 0 (Week 1): Foundations
Repo+CI+Docker, auth scaffold, Flutter shell.
Sprint 1 (Week 2): Core data
Branches, users/roles, students CRUD + basic UI.
Sprint 2 (Week 3): Groups + schedule
Groups, enrollments, schedule template, session generation.
Sprint 3 (Week 4): Attendance
Attendance marking + attendance report.
Sprint 4 (Week 5): Billing
Pricing, invoices, payments, revenue report.
Sprint 5 (Week 6): Hardening + demo
Bugfix, permission review, tests, staging deploy, docs.
Appendix A — CSV import format (students)
Required columns (MVP): full_name, parent_phone, branch_name(optional), group_name(optional).
Optional: student_phone, birth_date(YYYY-MM-DD), note.
Backend returns row-level errors: row number, field name, message.
Appendix B — Coding standards
Backend: black + isort + flake8; DRF serializers validate inputs; service layer for business logic.
Frontend: feature folders; Bloc/Cubit per feature; repository for API; centralized error handling.
Every endpoint includes permission checks + at least smoke tests.
