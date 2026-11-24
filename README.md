# Job Portal with AI Resume Screening

A full-stack job portal where recruiters post jobs and candidates apply with their resume. An LLM reads each resume and scores how well it matches the job description, so recruiters see the best-fit candidates first.

**Tech stack:** Java 21 · Spring Boot 3 · Spring Security (JWT) · MySQL · React · Generative AI (LLM API) · Docker · Kubernetes

> 🚧 Work in progress

## Roadmap
- [x] Project setup and database models
- [ ] Authentication (signup/login with JWT)
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

## Database design

| Table | Purpose |
|---|---|
| `users` | Candidates and recruiters (role column) |
| `jobs` | Jobs posted by recruiters |
| `job_applications` | A candidate's application to a job, with resume and AI score |
