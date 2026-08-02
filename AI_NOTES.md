# AI Notes

This document explains how I used AI tools (primarily Claude, with a second AI tool used for a README review pass) while building this project: what it generated, what I validated or changed, and what I chose not to use.

## 1. What was AI-generated vs. written by me

**AI-generated (used as a starting point, then reviewed/modified):**
- Initial `Expense` model structure (fields, constructor, plain getters/setters)
- Initial `ExpenseController` REST endpoint skeleton (`POST`, `GET`, `GET /total`, `DELETE`)
- Initial `ExpenseRepository` interface and `InMemoryExpenseRepository` implementation
- `GlobalExceptionHandler` (`@RestControllerAdvice`) structure for validation and not-found errors
- `ExpenseControllerTest` - the majority of the MockMvc test cases were AI-drafted, then fixed by me to compile and pass against my actual implementation
- README.md structure and initial content

**Written/implemented by me:**
- Project setup - generated the Spring Boot project via Spring Initializr and configured `pom.xml`
- `ExpenseServiceImpl` - I designed and wrote this independently, structured differently from AI's initial suggestion: a separate `ExpenseService` interface + `ExpenseServiceImpl`, constructor-based dependency injection instead of field injection, and explicit for-loops instead of stream-based aggregation for the totals
- Rewrote the `Expense` model to use Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`) instead of the AI-drafted plain getters/setters, and renamed its fields from `name`/`localDate` to `title`/`date` to match the assignment spec (see below)
- Diagnosed and fixed every bug listed in section 2 - the fixes themselves were implemented by me after AI helped identify the root cause
- Manual testing of all endpoints via Postman
- AI_NOTES.md and final README verification (version numbers, working directory, run commands — all confirmed by running them myself)

## 2. What I validated, tested, or changed — and why

- I reviewed every AI-generated suggestion before integrating it rather than copying it directly, and used Postman to manually verify each endpoint's behavior and HTTP status codes alongside the automated test suite.
- **`BigDecimal` for monetary values** - AI suggested this over `double`/`float` to avoid floating-point rounding errors, and I adopted it directly since the reasoning was sound.
- **Spring Boot 4 / Jackson 3 package mismatch.** AI's initial test code used `com.fasterxml.jackson.databind.ObjectMapper` and manually registered `JavaTimeModule`. This project runs Spring Boot 4.1.0, which ships Jackson 3.x — the package was renamed to `tools.jackson.databind`, and `JavaTimeModule` registration is no longer needed since JSR-310 support is now built in. I caught this from `Cannot resolve symbol` errors in IntelliJ, confirmed the actual dependency tree with `mvnw dependency:tree`, and verified the correct import path before fixing it. The same class of issue occurred again with `@WebMvcTest`'s import path, which also moved in Spring Boot 4.
- **Model field naming mismatch (`name`/`localDate` vs `title`/`date`).** My rewritten `Expense` model used `name` and `localDate`, but the assignment spec and the AI-generated test both used `title` and `date`. Actual JSON output confirmed the mismatch: `{"id":1,"name":"Groceries",...,"localDate":"2026-07-30"}`. I chose to rename the model fields to `title`/`date` rather than change the test, since `localDate` in particular leaks the Java type name into the API's JSON contract — the field should describe what it represents, not its underlying Java type.
- **Missing `@Valid` on the controller parameter.** Two tests expected `400 Bad Request` for a negative amount and a missing title, but both returned `201 Created`. I traced this to `@Valid` being missing on the `@RequestBody ExpenseRequest` parameter in `addExpense` — without it, Spring never triggers Bean Validation, so `MethodArgumentNotValidException` was never thrown and `GlobalExceptionHandler`'s validation handler never ran. Adding `@Valid` fixed both failing tests.
- **Package casing inconsistency.** Early code had a mismatch between package declarations and imports (e.g. `controller` vs `Controller`). I standardized all packages to lowercase, since Java convention expects this and mixed-case packages risk breaking on case-sensitive filesystems (e.g. Linux-based CI), even though they compiled fine on Windows.
- **README accuracy.** I ran `.\mvnw.cmd -version` and `java -version` myself rather than trusting AI's assumed version numbers in the initial README draft. This surfaced a real discrepancy: the project targets Java 17 (`pom.xml` compile target) but actually runs on JDK 25.0.3 locally — both are true and worth stating precisely rather than rounding to "Java 17" alone.
- **Second-opinion README review.** I had a second AI tool review the README from a beginner/CI-agent perspective. I incorporated its suggestions to add the `git clone` step, explicit working-directory guidance, `java -version` verification, and IntelliJ run instructions, since these genuinely reduce ambiguity for an automated reviewer or unfamiliar developer.

## 3. AI suggestions I did not use, and why

- **Field injection (`@Autowired` on a field) in the service layer.** AI's initial suggestion used field injection. I used constructor injection instead, since it allows the dependency to be `final`, is easier to unit test without reflection, and is the currently recommended Spring practice.
- **Stream-based aggregation (`.stream().map().reduce()`) for totals.** AI suggested this for `getTotal()`/`getTotalByCategory()`. I kept explicit for-loops instead — functionally identical, but more readable to me and not meaningfully different in performance at this scale.
- **Plain Java getters/setters on the model.** AI's initial draft avoided Lombok to keep the code fully visible. I went the other direction and used Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`) to cut boilerplate — a deliberate reversal of AI's default suggestion.
- **Hardcoding an exact test count in the README** (e.g. "Tests run: 12"), suggested during the README review pass. I used a placeholder (`Tests run: X`) instead, since the exact number wasn't final at the time of writing and a wrong hardcoded number would be worse than a generic one.
- **A top-level `tests/` folder**, as literally described in the assignment's suggested structure. Maven's build tooling depends on the `src/main/java` / `src/test/java` convention — forcing a separate top-level `tests/` folder would either break `mvn test` or require fragile workarounds (e.g. symlinks) for no real benefit. I kept standard Maven structure and explained this deviation explicitly in the README rather than silently ignoring the instruction.
