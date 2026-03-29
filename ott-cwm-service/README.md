# ott-cwm-service

Content Workflow Manager for OTT VOD workflows. The service consumes Kafka events from `content.ingested`, creates an idempotent workflow, schedules dependent jobs, simulates execution, and handles retries and completion state transitions.

## Features

- Spring Boot 4, Java 21, JPA/Hibernate, Lombok
- Kafka-based event flow
- H2 local default with PostgreSQL profile support
- Workflow/job state machine with retry and exponential backoff
- Mock execution adapter inside the service
- REST APIs for workflow inspection and manual job retry

## Package Layout

`com.cwm`

- `config`
- `controller`
- `dto`
- `event`
- `model`
- `repository`
- `scheduler`
- `service`

## Run Locally

1. Start Kafka locally or deploy the existing manifests under `ott-kafka/k8s`.
2. From `C:\Kiran\git_code_base\ott-platform-lab\ott-cwm-service`, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The service starts on `http://localhost:8083` and uses in-memory H2 by default.

## PostgreSQL Profile

Update `src/main/resources/application-postgres.yml` if needed, then run:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

## Event Flow

1. Publish `{"contentId":"movie-123"}` to topic `content.ingested`
2. Service creates one `VOD` workflow with three jobs: `ENCODE -> TRANSCODE -> PACKAGE`
3. Scheduler marks ready jobs and publishes them to `job.queue`
4. Mock executor marks jobs `RUNNING`, simulates delay, and emits a completion event
5. Completion handler updates job/workflow state and schedules downstream jobs

## Sample API Calls

```powershell
curl http://localhost:8083/workflows/{workflowId}
```

```powershell
curl http://localhost:8083/workflows/{workflowId}/jobs
```

```powershell
curl -X POST http://localhost:8083/jobs/{jobId}/retry
```

## Publish a Test Ingestion Event

With any Kafka client, send this JSON to topic `content.ingested`:

```json
{
  "contentId": "movie-123"
}
```

## Config Notes

- `cwm.workflow.failure-rate`: probability of mock job failure
- `cwm.workflow.default-max-retries`: automatic retry limit
- `cwm.workflow.initial-backoff-ms` and `cwm.workflow.backoff-multiplier`: backoff settings
- `cwm.topics.*`: topic names
