# Sonar Violations Demo — Design

## Architecture

Two demo-only Java files live in the `com.skylink.crewscheduler.service` package:

```
src/main/java/com/skylink/crewscheduler/service/
├── DemoSonarFindings.java          ← already exists; tasks 1–3 add new methods
└── DemoSonarFindingsExtra.java     ← new file created by task 4; tasks 4–20 add blocks
```

Neither file is referenced by any `@Service`, `@Component`, or `@Configuration` bean, so they never affect runtime behaviour. They exist only to produce SonarQube findings.

---

## File Stubs

### DemoSonarFindingsExtra.java (initial state, created by task 4)

```java
package com.skylink.crewscheduler.service;

/**
 * ============================================================================
 * DEMO-ONLY FILE — INTENTIONAL SONARQUBE FINDINGS (EXTRA SET)
 * ============================================================================
 * Each method in this class deliberately trips one Sonar rule.
 * The fix-sonar-issues-post-task-hook repairs each violation after its task runs.
 * Safe to delete once the demo is finished.
 * ============================================================================
 */
public class DemoSonarFindingsExtra {
    // populated by spec tasks 4–20
}
```

---

## Violation Design Table

Each row shows which code construct triggers the rule, the exact change made, and which file/method it lives in.

| Task | Rule | File | Method / Change |
|------|------|------|-----------------|
| 1 | S1481 | DemoSonarFindings | Add new method `countRejected` — declares `int skipped = 0` but never reads it |
| 2 | S1192 | DemoSonarFindings | Add new method `buildAuditTag` — returns the literal `"crew-audit-tag"` in 4 branches |
| 3 | S3776 | DemoSonarFindings | Add new method `classifyAssignment` — deep nested `if`/`else` chain exceeding complexity 15 |
| 4 | S1135 | DemoSonarFindingsExtra | **Create file**; stub class has `// TODO: implement validation logic` comment |
| 5 | S106 | DemoSonarFindingsExtra | Add `logTripStart` — calls `System.out.println(...)` |
| 6 | S1172 | DemoSonarFindingsExtra | Add `formatCrewId(String id, int unusedOption)` — `unusedOption` never read |
| 7 | S2583 | DemoSonarFindingsExtra | Add `alwaysTrue` — contains `if (true) { ... }` dead-code branch |
| 8 | S1168 | DemoSonarFindingsExtra | Add `getActiveCrewIds` — returns `null` instead of empty list |
| 9 | S112 | DemoSonarFindingsExtra | Add `validateSeniority` — throws `new RuntimeException(...)` directly |
| 10 | S1854 | DemoSonarFindingsExtra | Add `computeLayoverHours` — assigns `hours = 12` then immediately reassigns before reading |
| 11 | S2201 | DemoSonarFindingsExtra | Add `normalizeAirportCode` — calls `s.trim()` without capturing the return value |
| 12 | S1144 | DemoSonarFindingsExtra | Add `private` method `internalCheck` that is never called from within the class |
| 13 | S1698 | DemoSonarFindingsExtra | Add `isSameTrip(Trip a, Trip b)` — compares with `a.getTripNumber() == b.getTripNumber()` |
| 14 | S1874 | DemoSonarFindingsExtra | Add `legacyCrewFormat` — calls `new java.util.Date()` (deprecated constructor) |
| 15 | S2203 | DemoSonarFindingsExtra | Add `collectPilotIds` — uses `stream().forEach(list::add)` instead of `collect` |
| 16 | S2221 | DemoSonarFindingsExtra | Add `parseSeniority` — wraps pure arithmetic in `catch (Exception e)` |
| 17 | S1186 | DemoSonarFindingsExtra | Add `onTripCancelled()` — method with empty body, no comment |
| 18 | S2259 | DemoSonarFindingsExtra | Add `getCrewBadge` — calls `.toUpperCase()` on a reference that can be `null` |
| 19 | S1066 | DemoSonarFindingsExtra | Add `resolvePriority` — has nested `if` inside another `if` that can be merged |
| 20 | S1118 | DemoSonarFindingsExtra | Remove the private constructor stub that was in the class, making it a utility class without one |

---

## Hook Integration

The hook `fix-sonar-issues-post-task-hook` fires on `PostTaskExec`. Its command instructs the agent to:

1. Run `git diff --name-only` to identify only files touched by this task.
2. Run `getDiagnostics` on those files.
3. Read each file, understand each issue, and fix it in place without changing business logic.
4. Re-run `getDiagnostics` to confirm resolution.

Because each task touches only one or two methods, the hook has a narrow, predictable surface to fix — making the demo reliable and fast.

---

## Demo Flow (per violation)

```
Spec task runs  →  violation added  →  PostTaskExec fires hook
    →  hook reads git diff  →  getDiagnostics finds 1 issue
    →  hook fixes the issue  →  getDiagnostics shows 0 issues
    →  next task runs
```
