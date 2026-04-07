# Contributing to Whoa

Thank you for your interest in contributing! This project follows specific architectural patterns to ensure high performance and reliability.

## Error Handling Standards

All domain-specific errors must follow the assigned series to maintain a clean API contract for consumers.

### Error Code Series

| Series | Category | Description |
| :--- | :--- | :--- |
| **`WHOA-1xxx`** | **Request** | Client errors, malformed data, or validation failures. |
| **`WHOA-2xxx`** | **Resource** | Lifecycle failures such as entity not found or conflicts. |
| **`WHOA-3xxx`** | **Logic** | Business rule violations. |
| **`WHOA-4xxx`** | **System** | Database, cache, or internal infrastructure failures. |
| **`WHOA-9xxx`** | **Unknown** | Catch-all for unhandled system exceptions. |

### Creating a New Exception

1.  Create a new class in `com.subhrodip.oss.whoa.link.exceptions`.
2.  Implement the `WhoaException` interface.
3.  Assign a unique `errorCode` from the appropriate series.
4.  Define the `statusCode` (HttpStatus).

**Example:**

```kotlin
class RateLimitExceededException(
    message: String,
    override val errorCode: String = "WHOA-1003"
) : RuntimeException(message), WhoaException {
    override val statusCode: HttpStatus = HttpStatus.TOO_MANY_REQUESTS
}
```

## Coding Standards

- **Kotlin:** Follow [ktlint](https://ktlint.github.io/) standards. Run `./gradlew ktlintFormat` before committing.
- **Tests:** Maintain 95%+ test coverage. Add unit tests for every new logic branch.
- **Commits:** Follow the conventional commits specification and always include a `Signed-off-by` line (`git commit -s`).
