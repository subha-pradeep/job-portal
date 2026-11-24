# Job Portal with AI Resume Screening

A full-stack job portal where recruiters post jobs and candidates apply with their resume. An LLM reads each resume and scores how well it matches the job description, so recruiters see the best-fit candidates first.

**Tech stack:** Java 21 · Spring Boot 3 · Spring Security (JWT) · MySQL · React · Generative AI (LLM API) · Docker · Kubernetes

> 🚧 Work in progress

## Roadmap
- [x] Project setup and database models
- [x] Authentication (signup/login with JWT)
- [x] Job posting and browsing APIs
- [x] Applying to jobs with resume upload
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
| GET | `/api/jobs?keyword=java` | Public | Open jobs, newest first, optional title search |
| GET | `/api/jobs/{id}` | Public | One job |
| POST | `/api/jobs` | Recruiter | Post a job |
| PUT | `/api/jobs/{id}` | Recruiter (owner) | Edit a job |
| PATCH | `/api/jobs/{id}/close` | Recruiter (owner) | Stop accepting applications |
| GET | `/api/recruiter/jobs` | Recruiter | All jobs I posted |
| POST | `/api/jobs/{id}/apply` | Candidate | Apply with a PDF resume (multipart field `resume`, max 5 MB) |
| GET | `/api/candidate/applications` | Candidate | My applications and their status |
| GET | `/api/recruiter/jobs/{id}/applications` | Recruiter (owner) | Applicants for a job, best AI score first |
| PATCH | `/api/recruiter/applications/{id}/status` | Recruiter (owner) | `SHORTLISTED` or `REJECTED` |
| GET | `/api/recruiter/applications/{id}/resume` | Recruiter (owner) | View the resume PDF |

Send the token on protected requests as `Authorization: Bearer <token>`.

```bash
# Sign up
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","password":"secret123","role":"CANDIDATE"}'

# Who am I? (paste the token from the response above)
curl http://localhost:8080/api/users/me -H "Authorization: Bearer <token>"

# Post a job (needs a RECRUITER token)
curl -X POST http://localhost:8080/api/jobs \
  -H "Authorization: Bearer <recruiter-token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Java Backend Intern","company":"Acme","location":"Bhubaneswar","description":"Build REST APIs with Spring Boot and MySQL, write tests, and deploy with Docker.","requiredSkills":"Java, Spring Boot, MySQL, Docker"}'

# Browse jobs (no token needed)
curl "http://localhost:8080/api/jobs?keyword=java"

# Apply to job 1 with a resume (needs a CANDIDATE token)
curl -X POST http://localhost:8080/api/jobs/1/apply \
  -H "Authorization: Bearer <candidate-token>" \
  -F "resume=@/path/to/resume.pdf"
```

## Database design

| Table | Purpose |
|---|---|
| `users` | Candidates and recruiters (role column) |
| `jobs` | Jobs posted by recruiters |
| `job_applications` | A candidate's application to a job, with resume and AI score |
