# SmartQA Self-Healing Framework

SmartQA is a Playwright Java automation framework that will recover from locator failures by using stored locator metadata, fallback strategies, DOM analysis, and eventually an LLM-assisted healing step.

## Phase 1 Goal

Build the self-healing foundation before adding visual testing or AI test generation.

The first working flow will be:

1. A test asks the framework to act on a logical element key such as `login.button`.
2. The framework reads locator metadata for that key.
3. It tries the primary locator.
4. If the primary locator fails, it tries configured fallback locators.
5. If deterministic fallbacks fail, it captures page context for a future AI healing step.
6. It reports what failed, what recovered, and what still needs review.

## Package Structure

```text
src/main/java/com/smartqa/selfhealing
  config       Framework configuration and runtime settings
  core         Shared framework contracts and high-level orchestration
  locator      Locator metadata models and locator value objects
  repository   Storage access for locator metadata
  strategy     Recovery algorithms such as CSS, text, XPath, ARIA, and LLM
  engine       Self-healing decision flow
  playwright   Wrapper layer around Playwright APIs
  llm          AI provider abstraction, prompt building, and response parsing
  reporting    Healing events, reports, and diagnostics
  exception    Framework-specific exceptions

src/test/java/com/smartqa/tests
  base         Common test setup
  login        Login-focused test cases
  examples     Small framework usage examples

src/test/resources
  locators     Locator metadata files
  config       Test and framework config files
  testdata     Test input data
  demo-pages   Local HTML pages used to prove healing behavior
```

## Design Principles

- Keep tests readable. Test classes should say what the user does, not how healing works internally.
- Keep Playwright behind a wrapper. Direct `page.locator(...)` calls cannot self-heal.
- Keep locator storage behind a repository. JSON is fine first, but the engine should not depend on JSON directly.
- Keep recovery logic as strategies. Each healing approach should be replaceable and testable.
- Keep AI as the final fallback. Deterministic recovery should work before LLM recovery is added.

## Phase 1 Sub-Phases

1. Project skeleton and Maven setup.
2. Basic Playwright test execution.
3. Locator metadata stored in JSON.
4. `SmartPage` wrapper for logical element actions.
5. Deterministic fallback strategies.
6. Healing report.
7. DOM capture.
8. LLM-based locator suggestion.
9. Healed locator persistence.
10. CI-ready execution.
