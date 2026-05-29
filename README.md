# SonarQube Demo — Maven Java Project

A minimal Java 17 + Maven project to test SonarQube analysis.  
It includes clean code **and** deliberate code smells so Sonar has something to flag.

---

## Project Structure

```
sonar-demo/
├── pom.xml
└── src/
    ├── main/java/com/demo/
    │   ├── App.java          # Entry point
    │   └── MathUtils.java    # Utility class (with intentional smells)
    └── test/java/com/demo/
        └── MathUtilsTest.java  # JUnit 5 tests
```

---

## Intentional Code Smells (for Sonar to catch)

| Smell | Location |
|---|---|
| Unused local variable | `MathUtils.unusedVariableExample()` |
| Empty catch block | `MathUtils.emptyCatchExample()` |
| Magic number (`3.14159`) | `MathUtils.circleArea()` |

---

## Build & Test

```bash
# Compile + run tests + generate JaCoCo coverage report
mvn verify
```

---

## Run SonarQube Analysis

### Option A — SonarQube server (local or remote)

```bash
mvn verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_SONAR_TOKEN
```

### Option B — SonarCloud

```bash
mvn verify sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=YOUR_ORG_KEY \
  -Dsonar.token=YOUR_SONAR_TOKEN
```

### Option C — Override project key

```bash
mvn verify sonar:sonar \
  -Dsonar.projectKey=my-custom-key \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_SONAR_TOKEN
```

---

## Requirements

- Java 17+
- Maven 3.8+
- A running SonarQube instance (or SonarCloud account)
