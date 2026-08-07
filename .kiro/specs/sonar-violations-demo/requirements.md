# Sonar Violations Demo — Requirements

## Overview

This spec exists purely for a live demo. Each task deliberately introduces **one Sonar violation** into the `DemoSonarFindings.java` file (or a companion `DemoSonarFindingsExtra.java` file). After each task executes, the `fix-sonar-issues-post-task-hook` fires automatically and repairs the violation it just introduced, demonstrating Kiro's self-healing loop end-to-end.

**Nothing in this spec should affect production code.** All changes are confined to the two demo files listed below.

---

## Target Files

| File | Purpose |
|---|---|
| `src/main/java/com/skylink/crewscheduler/service/DemoSonarFindings.java` | Existing demo file — already has S3776 / S1192 / S1481 violations |
| `src/main/java/com/skylink/crewscheduler/service/DemoSonarFindingsExtra.java` | New companion file created by this spec — holds the additional ~17 violations |

---

## Rule Coverage

### Sonar Way (built-in Java quality profile)

| # | Rule | Short description |
|---|---|---|
| 1 | **S1481** | Unused local variable |
| 2 | **S1192** | String literals duplicated ≥ 3 times |
| 3 | **S3776** | Cognitive Complexity too high |
| 4 | **S1135** | `TODO` comment — work in progress |
| 5 | **S106** | Use of `System.out.println` instead of a logger |
| 6 | **S1172** | Unused method parameter |
| 7 | **S2583** | Condition always true / dead code |
| 8 | **S1168** | Return empty collection instead of `null` |
| 9 | **S112** | `RuntimeException` thrown instead of a specific exception type |
| 10 | **S1854** | Dead store — value assigned but never used before reassignment |
| 11 | **S2201** | Return value of method not used |
| 12 | **S1144** | Unused private method |

### Custom Profile ("Southwest Way" — extends Sonar Way)

| # | Rule | Short description |
|---|---|---|
| 13 | **S1698** | `==` / `!=` used on objects where `equals` is overridden |
| 14 | **S1874** | Use of `@Deprecated` API |
| 15 | **S2203** | `list::add` in a Stream instead of `collect` |
| 16 | **S2221** | `Exception` caught when no called method declares it |
| 17 | **S1186** | Method body is empty (no-op method) |
| 18 | **S2259** | Null dereference — method called on a potentially null reference |
| 19 | **S1066** | Collapsible `if` statements |
| 20 | **S1118** | Utility class without a `private` constructor |

---

## Acceptance Criteria

1. Each task touches **exactly one Sonar rule**, in one newly written code block.
2. The `DemoSonarFindingsExtra.java` file compiles without errors (violations are code-quality issues, not compile errors).
3. The existing `DemoSonarFindings.java` file is only touched by tasks 1–3 (which add new methods to it); tasks 4–20 only touch `DemoSonarFindingsExtra.java`.
4. After the hook fires on each task, `getDiagnostics` should report zero new issues in the modified file.
5. No production service, controller, model, or repository files are modified.
