# Sonar Violations Demo — Tasks

Each task deliberately introduces **exactly one** SonarQube violation. After the task executes, the `fix-sonar-issues-post-task-hook` fires automatically and removes the violation. This produces the self-healing loop that is the core of the demo.

All changes are confined to the two demo files:
- `src/main/java/com/skylink/crewscheduler/service/DemoSonarFindings.java`
- `src/main/java/com/skylink/crewscheduler/service/DemoSonarFindingsExtra.java`

---

## Tasks

- [x] 1. **[S1481 — Sonar Way] Add method with an unused local variable**

  Open `DemoSonarFindings.java`. Add the following method at the bottom of the class (before the closing `}`):

  ```java
  // --- S1481: Unused local variable -------------------------------------------
  public static int countRejected(List<TripAssignment> assignments) {
      int skipped = 0;   // S1481: declared and incremented, never read after this method
      int rejected = 0;
      for (TripAssignment a : assignments) {
          if (a.getStatus() == AssignmentStatus.CANCELLED) {
              rejected++;
          } else {
              skipped++;   // incremented but the caller never sees skipped
          }
      }
      return rejected;
  }
  ```

  Do not change any other code. The variable `skipped` is assigned but its value is never returned or used, which is the exact pattern Sonar rule S1481 flags.

  **Files to modify:** `DemoSonarFindings.java`

- [x] 2. **[S1192 — Sonar Way] Add method with duplicated string literals**

  Open `DemoSonarFindings.java`. Add the following method at the bottom of the class (before the closing `}`):

  ```java
  // --- S1192: String literals duplicated ≥ 3 times ----------------------------
  public static String buildAuditTag(AssignmentStatus status) {
      if (status == AssignmentStatus.ASSIGNED) {
          return "crew-audit-tag";   // S1192: same literal appears 4 times
      }
      if (status == AssignmentStatus.CONFIRMED) {
          return "crew-audit-tag";
      }
      if (status == AssignmentStatus.COMPLETED) {
          return "crew-audit-tag";
      }
      return "crew-audit-tag";
  }
  ```

  Do not change any other code. The literal `"crew-audit-tag"` appears four times, crossing the S1192 threshold of 3.

  **Files to modify:** `DemoSonarFindings.java`

- [ ] 3. **[S3776 — Sonar Way] Add method with high Cognitive Complexity**

  Open `DemoSonarFindings.java`. Add the following method at the bottom of the class (before the closing `}`). This method intentionally nests conditionals well beyond the default S3776 threshold of 15:

  ```java
  // --- S3776: Cognitive Complexity exceeds threshold ---------------------------
  public static String classifyAssignment(TripAssignment assignment, boolean strictMode,
                                           boolean includeDetails) {
      StringBuilder result = new StringBuilder();
      if (assignment != null) {
          if (assignment.getCrewMember() != null) {
              if (assignment.getCrewMember().getCrewRole() != null) {
                  if (strictMode) {
                      if (assignment.getCrewMember().getCrewRole().name().equals("PILOT")) {
                          if (assignment.getStatus() == AssignmentStatus.CONFIRMED) {
                              if (includeDetails) {
                                  result.append("confirmed-pilot-detail:");
                                  if (assignment.getCrewMember().getSeniorityNumber() != null) {
                                      if (assignment.getCrewMember().getSeniorityNumber() < 200) {
                                          result.append("senior");
                                      } else if (assignment.getCrewMember().getSeniorityNumber() < 500) {
                                          result.append("mid");
                                      } else {
                                          result.append("junior");
                                      }
                                  }
                              } else {
                                  result.append("confirmed-pilot");
                              }
                          } else if (assignment.getStatus() == AssignmentStatus.ASSIGNED) {
                              if (includeDetails) {
                                  result.append("assigned-pilot-detail");
                              } else {
                                  result.append("assigned-pilot");
                              }
                          }
                      } else {
                          if (assignment.getStatus() == AssignmentStatus.CONFIRMED) {
                              result.append("confirmed-cabin");
                          } else {
                              result.append("cabin");
                          }
                      }
                  } else {
                      result.append(assignment.getCrewMember().getCrewRole().name().toLowerCase());
                  }
              }
          } else {
              result.append("unassigned");
          }
      }
      return result.toString();
  }
  ```

  **Files to modify:** `DemoSonarFindings.java`

- [ ] 4. **[S1135 — Sonar Way] Create DemoSonarFindingsExtra.java with a TODO comment**

  Create the file `src/main/java/com/skylink/crewscheduler/service/DemoSonarFindingsExtra.java` with the content below. The `// TODO` comment on line 18 is the S1135 violation:

  ```java
  package com.skylink.crewscheduler.service;

  import com.skylink.crewscheduler.model.AssignmentStatus;
  import com.skylink.crewscheduler.model.CrewMember;
  import com.skylink.crewscheduler.model.Trip;
  import com.skylink.crewscheduler.model.TripAssignment;

  import java.util.ArrayList;
  import java.util.List;
  import java.util.stream.Collectors;

  /**
   * DEMO-ONLY — INTENTIONAL SONARQUBE FINDINGS (EXTRA SET).
   * Safe to delete after the demo. Nothing in the application depends on this class.
   */
  public class DemoSonarFindingsExtra {

      // TODO: implement full validation logic for crew eligibility   <-- S1135

      private DemoSonarFindingsExtra() {
          // utility class — no instantiation
      }
  }
  ```

  **Files to create:** `DemoSonarFindingsExtra.java`

- [ ] 5. **[S106 — Sonar Way] Add method that uses System.out.println**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S106: Use of System.out.println instead of a logger --------------------
  public static void logTripStart(Trip trip) {
      System.out.println("Trip started: " + trip.getTripNumber()); // S106
  }
  ```

  Do not change any other code. Using `System.out` directly instead of a logging framework (SLF4J/Logback) is what S106 flags.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 6. **[S1172 — Sonar Way] Add method with an unused parameter**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1172: Unused method parameter -----------------------------------------
  public static String formatCrewId(String id, int unusedOption) { // S1172: unusedOption never read
      return "CREW-" + id.toUpperCase();
  }
  ```

  Do not change any other code. The parameter `unusedOption` is declared but never referenced inside the method body, which is what S1172 flags.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 7. **[S2583 — Sonar Way] Add method with a condition that is always true**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S2583: Condition always true / dead code --------------------------------
  public static String alwaysTrue(AssignmentStatus status) {
      boolean active = true;
      if (active) {           // S2583: condition is always true — else branch is dead code
          return status.name();
      } else {
          return "INACTIVE";  // dead code — unreachable
      }
  }
  ```

  Do not change any other code. The `if (active)` branch where `active` is unconditionally `true` is the S2583 pattern.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 8. **[S1168 — Sonar Way] Add method that returns null instead of an empty collection**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1168: Return empty collection instead of null -------------------------
  public static List<Long> getActiveCrewIds(List<TripAssignment> assignments) {
      if (assignments == null) {
          return null;   // S1168: should return Collections.emptyList()
      }
      List<Long> ids = new ArrayList<>();
      for (TripAssignment a : assignments) {
          if (a.getStatus() == AssignmentStatus.CONFIRMED) {
              ids.add(a.getCrewMember().getId());
          }
      }
      return ids;
  }
  ```

  Do not change any other code. Returning `null` from a method whose return type is a `List` is the exact pattern S1168 flags.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 9. **[S112 — Sonar Way] Add method that throws RuntimeException directly**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S112: Generic RuntimeException thrown -----------------------------------
  public static void validateSeniority(CrewMember crew) {
      if (crew.getSeniorityNumber() == null) {
          throw new RuntimeException("Seniority number is required"); // S112
      }
  }
  ```

  Do not change any other code. Throwing `RuntimeException` directly instead of a more specific exception type is what S112 flags.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 10. **[S1854 — Sonar Way] Add method with a dead store**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1854: Dead store — value assigned but never used before reassignment ---
  public static int computeLayoverHours(Trip trip) {
      int hours = 12;                                  // S1854: this value is immediately overwritten
      hours = trip.getEndDate().getDayOfYear()
              - trip.getStartDate().getDayOfYear();
      return hours * 24;
  }
  ```

  Do not change any other code. Assigning `hours = 12` and then immediately reassigning `hours` without ever reading the first value is the S1854 dead-store pattern.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 11. **[S2201 — Sonar Way] Add method that ignores the return value of a pure function**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S2201: Return value of method not used ----------------------------------
  public static String normalizeAirportCode(String code) {
      code.trim();        // S2201: trim() returns a new String; the result is discarded
      return code.toUpperCase();
  }
  ```

  Do not change any other code. Calling `String.trim()` (a pure function) and discarding the result is what S2201 flags.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 12. **[S1144 — Sonar Way] Add an unused private method**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1144: Unused private method --------------------------------------------
  private static boolean internalCheck(TripAssignment assignment) { // S1144: never called
      return assignment.getStatus() != AssignmentStatus.CANCELLED;
  }
  ```

  Do not change any other code. A `private` method that is never called anywhere in the class is the S1144 pattern.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [x] 13. **[S1698 — Custom Profile] Add method using == on a String field (equals is overridden)**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1698 (Custom): == used where equals() should be used ------------------
  public static boolean isSameTrip(Trip a, Trip b) {
      return a.getTripNumber() == b.getTripNumber(); // S1698: String == instead of .equals()
  }
  ```

  Do not change any other code. Comparing two `String` references with `==` when `String` overrides `equals` is the S1698 violation. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [x] 14. **[S1874 — Custom Profile] Add method that calls a deprecated API**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1874 (Custom): @Deprecated API used ------------------------------------
  @SuppressWarnings("deprecation")
  public static String legacyCrewFormat(CrewMember crew) {
      java.util.Date assignmentDate = new java.util.Date(); // S1874: Date() constructor is deprecated
      return crew.getEmployeeId() + "@" + assignmentDate.toLocaleString(); // toLocaleString also deprecated
  }
  ```

  Do not change any other code. Using `new java.util.Date()` and `Date.toLocaleString()` — both deprecated since Java 1.1 — is what S1874 flags. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [x] 15. **[S2203 — Custom Profile] Add method that uses forEach + list::add instead of collect**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S2203 (Custom): list::add with forEach instead of collect ---------------
  public static List<Long> collectPilotIds(List<TripAssignment> assignments) {
      List<Long> ids = new ArrayList<>();
      assignments.stream()
              .filter(a -> a.getAssignmentRole() != null)
              .forEach(a -> ids.add(a.getCrewMember().getId())); // S2203: use collect() instead
      return ids;
  }
  ```

  Do not change any other code. Using `.forEach(list::add)` on a stream instead of `.collect(Collectors.toList())` is the S2203 violation. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [x] 16. **[S2221 — Custom Profile] Add method that catches the base Exception type unnecessarily**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S2221 (Custom): Exception caught when not required by called methods ----
  public static int parseSeniority(String value) {
      try {
          return Integer.parseInt(value);
      } catch (Exception e) {  // S2221: parseInt only throws NumberFormatException
          return -1;
      }
  }
  ```

  Do not change any other code. `Integer.parseInt` throws only `NumberFormatException`; catching the broad `Exception` base type is the S2221 violation. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [ ] 17. **[S1186 — Custom Profile] Add method with an empty body**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1186 (Custom): Empty method body ---------------------------------------
  public static void onTripCancelled(Trip trip) {
      // S1186: method body is empty — Sonar expects at least a comment explaining why
  }
  ```

  Do not change any other code. A method with an empty body (even with a single comment that explains nothing semantically) is what S1186 flags. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [x] 18. **[S2259 — Custom Profile] Add method with a potential null dereference**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S2259 (Custom): Null dereference ----------------------------------------
  public static String getCrewBadge(CrewMember crew) {
      String role = crew.getCrewRole() != null ? crew.getCrewRole().name() : null;
      return role.toUpperCase(); // S2259: role can be null — calling toUpperCase() risks NPE
  }
  ```

  Do not change any other code. Calling `.toUpperCase()` on `role` which can be `null` from the ternary expression is the S2259 null-dereference pattern. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [x] 19. **[S1066 — Custom Profile] Add method with collapsible if statements**

  Open `DemoSonarFindingsExtra.java`. Add the following method inside the class (before the closing `}`):

  ```java
  // --- S1066 (Custom): Collapsible if statements --------------------------------
  public static String resolvePriority(CrewMember crew) {
      if (crew.getSeniorityNumber() != null) {             // S1066: can be merged with inner if
          if (crew.getSeniorityNumber() < 100) {
              return "HIGH";
          }
      }
      return "NORMAL";
  }
  ```

  Do not change any other code. The outer `if` and inner `if` share no `else` branch and can be combined into a single condition — this is the S1066 collapsible-if pattern. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`

- [x] 20. **[S1118 — Custom Profile] Remove the private constructor to make it a utility class without one**

  Open `DemoSonarFindingsExtra.java`. Delete the private constructor block:

  ```java
  private DemoSonarFindingsExtra() {
      // utility class — no instantiation
  }
  ```

  After this deletion, `DemoSonarFindingsExtra` becomes a non-instantiable utility class (all methods are `static`) **without** a private constructor, which is the exact pattern S1118 flags. This rule is enabled in the custom "Southwest Way" profile.

  **Files to modify:** `DemoSonarFindingsExtra.java`
