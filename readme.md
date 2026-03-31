# Fujitsu internship enrty task 2026

Implemented all the core and bonusfeatures accordfing to the given task.

## About bonus features
* **Calculations based on history:** Added an optional datetime parameter to the REST interface request. If valued, delivery fee calculations are done based on business rules and weather conditions valid at that specific time.
* **CRUD:** The business rules for base fees and extra fees can be managed through a REST interface. Rules are fully decoupled from the application logic and seeded into the database on startup.

## How to execute


```bash
./gradlew bootRun
```
## On startup, the application will:
* Seed the database with the default base fee and weather fee rules.
* Execute an initial fetch of the current weather data from the Estonian Environment Agency.

### Tests can be executed:
```bash
./gradlew test
```

## API Documentation

The REST interface is fully documented using OpenAPI/Swagger. Once the application is running, it is interactable via:

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **API Docs:**  `http://localhost:8080/v3/api-docs`

Example request:
```http request
GET /api/fee?city=TARTU&vehicle=BIKE
```

