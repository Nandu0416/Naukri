# Naukri Auto Apply (Java + Spring Boot REST API)

This project provides a **production-friendly base** to automate Naukri job applications using Java and Spring Boot.

## What is implemented

- REST API to start an auto-apply run with required and optional filters.
- Background execution model for long-running runs.
- Status endpoint to track each run.
- Clean architecture split into `api`, `application`, `domain`, and `infrastructure` packages.
- In-memory run repository for quick setup (easy to swap with Redis/PostgreSQL).
- Simulated automation client to keep the service testable and safe by default.

## Request fields

### Required
- `username`
- `password`
- `role`
- `skills`
- `noticePeriod`
- `applicationsToSubmit`

### Optional
- `expectedPackage`
- `location`
- `freshnessInDays`

## API

### Start an auto-apply run

`POST /api/v1/job-applications/runs`

```json
{
  "username": "name@email.com",
  "password": "secret",
  "role": "Java Backend Developer",
  "skills": ["Java", "Spring Boot", "Microservices"],
  "noticePeriod": "30 days",
  "applicationsToSubmit": 20,
  "expectedPackage": "18 LPA",
  "location": "Bangalore",
  "freshnessInDays": 3
}
```

### Get run status

`GET /api/v1/job-applications/runs/{runId}`

## Run locally

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## Microservices-ready evolution path

If you want to split this into microservices later, this code is already prepared for that transition:

1. **Profile Service**
   - Stores user job preferences securely.
2. **Automation Service**
   - Owns browser/workflow automation.
3. **Orchestrator Service**
   - Accepts run request and tracks state.
4. **Notification Service**
   - Sends run summary to email/Slack.
5. **Queue/Event Bus**
   - Kafka/RabbitMQ to decouple orchestration from execution.

You can extract the current `application` and `infrastructure` layers into individual services with minimal API changes.
