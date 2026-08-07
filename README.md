# SkyLink Crew Scheduler

A Spring Boot demo application for assigning flight crew (pilots and flight
attendants) to trips, with a calendar view of the logged-in crew member's
schedule. Built as a self-contained demo target: H2 in-memory database,
seeded sample data, and a small static frontend, so it runs with a single
command and needs no external services.

Built for the **Kong API + AI Summit 2026**  to demonstrate the Kiro
self-healing hook and SonarQube IDE plugin working together against a real
SonarCloud-analyzed codebase.

## Stack

- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring Security (session/form login)
- H2 (in-memory)
- JUnit 5, Mockito, AssertJ, Spring Security Test
- JaCoCo for coverage, Sonar Maven Scanner for SonarCloud/SonarQube

## Domain model

| Entity | Purpose |
|---|---|
| `CrewMember` | A pilot or flight attendant. Doubles as the login identity (username/password) for the crew portal. |
| `Trip` | A pairing of one or more flight legs, with a start/end date. |
| `Flight` | A single flight leg belonging to a trip. |
| `TripAssignment` | Links a crew member to a trip in a specific role (`CAPTAIN`, `FIRST_OFFICER`, `PURSER`, `FLIGHT_ATTENDANT`) with a lifecycle status (`ASSIGNED`, `CONFIRMED`, `COMPLETED`, `CANCELLED`). |

Business rules enforced in `TripAssignmentService`:
- A pilot can only be assigned as `CAPTAIN` or `FIRST_OFFICER`; a flight
  attendant can only be assigned as `PURSER` or `FLIGHT_ATTENDANT`.
- A crew member cannot be double-booked on two trips with overlapping date
  ranges (cancelled assignments are ignored for this check).

## Running locally

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080** (it will redirect to the login page).

### Demo logins

All seeded crew members share the password `CrewDemo#2026`:

| Username | Name | Role |
|---|---|---|
| `jmorrison` | Jordan Morrison | Captain |
| `asingh` | Amara Singh | First Officer |
| `kwilliams` | Keisha Williams | Purser |
| `rortega` | Rafael Ortega | Flight Attendant |
| `tnguyen` | Thao Nguyen | Flight Attendant (base HOU) |

Sample trips are seeded across the current month (plus one trip into next
month) so the calendar view has visible content immediately - no manual data
entry needed before a demo.

The H2 console is available at `/h2-console` (JDBC URL `jdbc:h2:mem:crewscheduler`,
user `sa`, blank password) if you want to show the underlying data live.

## REST API

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/auth/me` | Current logged-in crew member |
| `GET` | `/api/crew-members` | List crew members |
| `POST` | `/api/crew-members` | Create a crew member |
| `GET` | `/api/trips` | List trips (with flight legs) |
| `POST` | `/api/trips` | Create a trip with its flight legs |
| `GET` | `/api/trip-assignments` | List all assignments |
| `GET` | `/api/trip-assignments/crew-member/{id}` | Assignments for one crew member |
| `GET` | `/api/trip-assignments/my-calendar?start=YYYY-MM-DD&end=YYYY-MM-DD` | Calendar feed for the logged-in crew member |
| `POST` | `/api/trip-assignments` | Assign a crew member to a trip |
| `PATCH` | `/api/trip-assignments/{id}/status` | Update assignment status |

## Testing

```bash
mvn test
```

Tests cover the role-compatibility and overlap-detection business logic in
`TripAssignmentService`, `CrewMemberService`, and a MockMvc test of the
`my-calendar` endpoint (authenticated and unauthenticated cases).

JaCoCo generates `target/site/jacoco/jacoco.xml`, which is what both the
Sonar Maven plugin and your local SonarQube IDE plugin read for coverage.

## Sonar Way vs. "Southwest Way": running the two-profile demo

**Why local, not SonarCloud:** SonarQube Cloud's Free plan only supports the
built-in Sonar Way profile - custom quality profiles are a Team/Enterprise
feature there. To show a custom "Southwest Way" profile next to Sonar Way
for free, run **SonarQube Community Build** locally instead. This also means
the demo has zero dependency on conference wifi or on your company's
corporate SonarQube instance - everything runs on your own laptop.

**Do this before you travel**, not at the venue (pull the image once, over a
real connection):

```bash
docker pull sonarqube:community
docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:community
```

Then:

1. Open `http://localhost:9000`, log in as `admin`/`admin`, and set a new password.
2. **Create new project** &rarr; project key `flight-crew-scheduler` &rarr; **Generate a token** (save it - you'll pass it as `sonar.token`).
3. **Quality Profiles** &rarr; find the built-in Java **Sonar way** profile &rarr; **Extend** &rarr; name the new profile `Southwest Way`. Extending (not copying) means it inherits everything from Sonar Way, so the "we build on the industry standard, plus our own bar" narrative is literally true.
4. In `Southwest Way`, tighten a couple of parameters so the two profiles visibly diverge:
   - **Cognitive Complexity** (`java:S3776`) - lower the `Threshold` parameter from its default down to something aggressive, e.g. `4`.
   - **String literals should not be duplicated** (`java:S1192`) - lower the minimum-occurrences parameter if exposed.
5. Run analysis against your local instance:
   ```bash
   mvn clean verify sonar:sonar \
     -Dsonar.projectKey=flight-crew-scheduler \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.token=<your local token>
   ```
6. In the SonarQube UI, assign the project's Java quality profile to **Sonar way** first and note the findings, then reassign it to **Southwest Way** and re-run step 5 - `DemoSonarFindings.seniorityBand()` (see below) is calibrated to be clean under Sonar Way and flagged under Southwest Way once the complexity threshold is tightened, giving you a provable, live "same file, two profiles, two results."

### Connected Mode in VS Code

Install **SonarQube for IDE** (this is SonarLint's current name - renamed in
v4.13) from the VS Code Marketplace. In the **SONARQUBE SETUP &rarr; CONNECTED
MODE** panel: *Add SonarQube Server Connection* &rarr; `http://localhost:9000`
+ your token &rarr; bind the `flight-crew-scheduler` project. Connected Mode
syncs whichever quality profile is assigned to the project server-side.

There's no manual "run analysis" button for regular files - SonarQube for
IDE analyzes automatically on file open/save (with Autosave on, as you type).
So the live demo moment is: reassign the project's profile in the SonarQube
UI (step 6 above), then re-save the file in VS Code (or use **Analyze Changed
Files with SonarQube** from the Source Control view) - the **Problems panel**
updates with the new findings under the new profile.

### Seeded findings for the demo

- **`DemoSonarFindings.java`** (`service` package) - deliberately unused at
  runtime, carrying four labeled findings: three standard Sonar Way findings
  (cognitive complexity, duplicated string literal, unused local variable),
  plus `seniorityBand()`, calibrated to only fail once Southwest Way's
  Cognitive Complexity threshold is tightened. Safe to delete once it's
  served its purpose - nothing else depends on it.
- **CSRF disabled in `SecurityConfig`** - flagged as security hotspot
  `S4502` under both profiles. Commented in place with the reasoning and the
  production fix (re-enable CSRF, serve the token via
  `CookieCsrfTokenRepository` to the static frontend).

Everything else in the codebase is written clean on purpose, so the demo
reads as "a normal, mostly-healthy repo with a few real, labeled findings,"
not a codebase riddled with bugs.

### Optional: SonarCloud for ongoing CI (after the conference)

`.github/workflows/build-and-sonar.yml` is set up to run against SonarCloud
if you want continuous analysis on the public GitHub repo afterward. Note
the Free plan there only offers the Sonar Way profile (see above) - the
Southwest Way comparison is a local-only demo unless you upgrade to a paid
SonarCloud plan. To wire it up: create a SonarCloud org, add repo secret
`SONAR_TOKEN` and repo variables `SONAR_PROJECT_KEY` / `SONAR_ORGANIZATION`.

## Project structure

```
src/main/java/com/skylink/crewscheduler/
  config/       Security config, UserDetailsService adapter, demo data seeder
  model/        JPA entities and enums
  repository/   Spring Data JPA repositories
  dto/          Request/response records
  service/      Business logic
  controller/   REST controllers
  exception/    Centralized error handling
src/main/resources/
  static/       Login page, calendar page, CSS, JS (no build step - plain files)
```
