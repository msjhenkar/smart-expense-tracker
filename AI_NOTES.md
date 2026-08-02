# AI Notes

Done by me
- Planned and initial set up of spring boot project.
- drafted folder structure, 
- outlined the end point list, 
- Didnt fully used the ,odel, service and controller architecture but instead i implemented Repository layer and following interface in service. 
- Made some changes in Service Layer (separate `ExpenseService` Interface + `ExpenseServiceImpl`)
- made some structural changes different from AI adding constructure based injection, for-llops instead of strems
-

Given By AI
- suggested additinonal dependencies
- drafted code for model, service and controller
- Wrote the `ExpenseCOntrollerTest` File 

This document explains how I used AI tools (primarily ChatGPT and Claude) while building this project.
## 1. What was AI-generated vs. written by me

**AI-generated (used as a starting point, then reviewed/modified):**
- Suggested some additional spring dependency 
- Modifying ExpenseController REST endpoints.
- `GlobalExceptionHandler` (`@RestControllerAdvice`) structure for validation and not-found errors
- `ExpenseControllerTest` — the majority of the MockMvc test cases were AI-drafted, then fixed by me to compile and pass against my actual implementation
- README.md structure and initial content

**Written/implemented by me:**
- `Project Intial Setup` - created spring boot project and added required dependencies(also took help from AI).
- `Intial Project Structure` - planned and set up the Controller / Service / Repository layered architecture
- Initial `Expense` model structure
- Initial `ExpenseController` REST endpoint skeleton (`POST`, `GET`, `GET /total`, `DELETE`) — later modified by AI

- `AI_NOTES.md` and final README verification

## 2. What I validated, tested, or changed — and why
I reviewed every AI-generated suggestion before integrating it, rather than copying it directly. In general, this meant:
- AI suggested using BigDecimal for monetary values, and I adopted it to avoid floating-point precision issues.
- Testing all REST API endpoints using Postman to verify correct functionality and expected HTTP status codes.
- Running unit tests after implementing AI-suggested test cases and fixing any that failed
- Checking the final implementation against the assignment requirements before committing.

**Spring Boot 4 / Jackson 3 package mismatch.**
AI's initial test code used `com.fasterxml.jackson.databind.ObjectMapper` and manually registered `JavaTimeModule`. This project runs Spring Boot 4.1.0, which ships Jackson 3.x — the package was renamed to `tools.jackson.databind`, and `JavaTimeModule` registration is no longer needed since JSR-310 support is built in. I caught this from `Cannot resolve symbol` errors in IntelliJ, confirmed the actual dependency tree with `mvnw dependency:tree`, and verified the correct import path before fixing it. Same issue occurred again with `@WebMvcTest`'s import path, which also moved in Spring Boot 4.

**Method name mismatch between service and test.**
The AI-generated test called `expenseService.getAllExpenses()` and `getExpensesByCategory(...)`, but my own `ExpenseServiceImpl` (written separately) had `getAllExpense()` and `getExpenseByCategory(...)` — missing the trailing "s". This wasn't an IDE caching issue as I initially assumed; it was a real naming inconsistency between two independently written pieces of code. Fixed by renaming the service methods to match the test (and updating the interface).

**Model field naming mismatch (`name`/`localDate` vs `title`/`date`).**
My `Expense` model used `name` and `localDate` as field names, but the assignment spec and the AI-generated test both used `title` and `date`. Actual JSON output confirmed the mismatch: `{"id":1,"name":"Groceries",...,"localDate":"2026-07-30"}`. I chose to rename the model fields to `title`/`date` rather than change the test, since `localDate` in particular leaks the Java type name into the API's JSON contract, which is bad API design — the field should describe what it represents, not its underlying Java type.

**Missing `@Valid` on the controller parameter.**
Two tests expected `400 Bad Request` for a negative amount and a missing title, but both returned `201 Created`. I traced this to `@Valid` being missing on the `@RequestBody ExpenseRequest` parameter in `addExpense` — without it, Spring never triggers Bean Validation, so `MethodArgumentNotValidException` was never thrown and `GlobalExceptionHandler`'s validation handler never ran. Adding `@Valid` fixed both failing tests.

**Package casing inconsistency.**
AI-generated code initially had a mismatch between package declarations and imports (e.g. `controller` vs `Controller`). I standardized all packages to lowercase, since Java convention expects this and mixed-case packages risk breaking on case-sensitive filesystems (e.g. Linux-based CI), even though they compiled fine on Windows.

**README accuracy.**
I ran `.\mvnw.cmd -version` and `java -version` myself rather than trusting AI's assumed version numbers in the initial README draft. This surfaced a real discrepancy: the project targets Java 17 (`pom.xml` compile target) but actually runs on JDK 25.0.3 locally — both are true and worth stating precisely rather than rounding to "Java 17" alone.

**Second-opinion README review.**
I had a second AI tool review the README from a "beginner/CI agent" perspective. I incorporated its suggestions to add the `git clone` step, explicit working-directory guidance, `java -version` verification, and IntelliJ run instructions, since these genuinely reduce ambiguity for an automated reviewer or unfamiliar developer.

## 3. AI suggestions I did not use, and why

- **Field injection (`@Autowired` on a field) in the service layer.** AI's initial suggestion used field injection. I use constructor injection instead, since it allows the dependency to be `final`, is easier to unit test without reflection, and is the currently recommended Spring practice.
- **Stream-based aggregation (`.stream().map().reduce()`) for totals.** AI suggested this for `getTotal()`/`getTotalByCategory()`. I kept explicit for-loops instead — functionally identical, but more readable to me and not meaningfully different in performance at this scale.
- **Hardcoding an exact test count in the README** (e.g. "Tests run: 12"), suggested during the README review pass. I used a placeholder (`Tests run: X`) instead, since the exact number wasn't final at the time of writing and a wrong hardcoded number would be worse than a generic one.
- **A top-level `tests/` folder**, as literally described in the assignment's suggested structure. Maven's build tooling depends on the `src/main/java` / `src/test/java` convention — forcing a separate top-level `tests/` folder would either break `mvn test` or require fragile workarounds (e.g. symlinks) for no real benefit. I kept standard Maven structure and explained this deviation explicitly in the README rather than silently ignoring the instruction.
- **`spring-boot-starter-webmvc-test` / `spring-boot-starter-validation-test` artifact names looked non-standard at first glance** (they don't match older Spring Boot 3.x naming conventions I initially expected). Before assuming they were AI-invented package names, I confirmed via web search and the actual resolved dependency tree that Spring Boot 4.x genuinely renamed these starters — so I kept them rather than "fixing" something that wasn't broken.