# Naukri Auto Apply (Java + Spring Boot REST API)

This project now includes a **real Selenium-based automation integration** that logs into Naukri, searches jobs, and attempts quick apply actions.

## What is implemented

- REST API to start an auto-apply run with required and optional filters.
- Background execution model for long-running runs.
- Status endpoint to track each run.
- Real automation client (`SeleniumNaukriAutomationClient`) enabled by default.
- Simulated automation mode retained for local development/testing.
- Clean architecture split into `api`, `application`, `domain`, and `infrastructure` packages.

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

## Automation modes

Set `automation.mode` in `application.yml`:

- `real` (default): uses Selenium + ChromeDriver and tries to apply for jobs.
- `simulated`: keeps the old safe simulation.

## Real mode prerequisites

- Google Chrome installed on the host.
- Selenium Manager ability to provision/connect a compatible ChromeDriver.
- Naukri account credentials in API request.
- Stable selectors on Naukri pages (UI changes can require selector updates).

## Key configuration

```yaml
automation:
  mode: real
  naukri:
    login-url: https://www.naukri.com/nlogin/login
    search-url: https://www.naukri.com/jobs-in-india
    headless: true
    implicit-wait-seconds: 20
    page-load-timeout-seconds: 60
```

## Important notes for real automation

- Captcha, OTP, and anti-bot checks may block unattended runs.
- Some jobs require multi-step forms and may not be quick-apply compatible.
- Always review platform terms and your own account safety before high-volume automation.

## Run locally

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```
