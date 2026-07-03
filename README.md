# TailTown

Monorepo for the TailTown pet-care platform: a native Android app and its Spring Boot backend.

## Layout

```
TailTown/
├── frontend/       Android app (Kotlin, Jetpack Compose)
├── backend/        Spring Boot API (Kotlin, PostgreSQL, Redis)
└── TailTownDocs/   Architecture, API contracts, and deployment docs
```

## Frontend

```
cd frontend
./gradlew assembleDebug
```

## Backend

```
cd backend
./gradlew bootRun
```

See `backend/docker-compose.yml` for local Postgres/Redis, and `TailTownDocs/` for architecture and API contract details.
