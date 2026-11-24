# Job Portal with AI Resume Screening

A full-stack job portal where recruiters post jobs and candidates apply with their resume. An LLM reads each resume and scores how well it matches the job description, so recruiters see the best-fit candidates first.

**Tech stack:** Java 21 · Spring Boot 3 · Spring Security (JWT) · MySQL · React · Generative AI (LLM API) · Docker · Kubernetes

> 🚧 Work in progress

## Roadmap
- [x] Project setup and database models
- [x] Authentication (signup/login with JWT)
- [ ] Job posting and browsing APIs
- [ ] Applying to jobs with resume upload
- [ ] AI resume screening
- [ ] React frontend
- [ ] Docker and Kubernetes deployment

## Run locally

Requirements: Java 21, Maven, Docker

```bash
# 1. Start MySQL
docker compose up -d

# 2. Start the backend
cd backend
mvn spring-boot:run
```

The API runs on http://localhost:8080.

## API

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Sign up as `CANDIDATE` or `RECRUITER`, returns a JWT |
| POST | `/api/auth/login` | Public | Log in, returns a JWT |
| GET | `/api/users/me` | JWT | The logged-in user |

Send the token on protected requests as `Authorization: Bearer <token>`.

```bash
# Sign up
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","password":"secret123","role":"CANDIDATE"}'

# Who am I? (paste the token from the response above)
curl http://localhost:8080/api/users/me -H "Authorization: Bearer <token>"
```

## Database design

| Table | Purpose |
|---|---|
| `users` | Candidates and recruiters (role column) |
| `jobs` | Jobs posted by recruiters |
| `job_applications` | A candidate's application to a job, with resume and AI score |
