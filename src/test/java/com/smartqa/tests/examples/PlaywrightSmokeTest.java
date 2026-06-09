package com.smartqa.tests.examples;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.smartqa.tests.base.BasePlaywrightTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

final class PlaywrightSmokeTest extends BasePlaywrightTest {

    @Test
    void opensLocalDemoPageAndSubmitsLogin() {
        page.navigate(testResource("demo-pages/login.html").toExternalForm());

        assertThat(page).hasTitle("SmartQA Demo Login");

        findElement("Username", page.getByLabel("Username")).fill("pawan");
        findElement("Password", page.getByLabel("Password")).fill("secret");
        findElement("Sign in", page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in"))).click();

        assertThat(findElement("#login-status", page.locator("#login-status"))).containsText("Welcome, pawan");
    }

    @Test
    void attributeHealingStrategyWorksWhenIdIsBroken() {
        page.navigate(testResource("demo-pages/healing-test.html").toExternalForm());

        String brokenSelector = "#login-input";
        String healingValue = "login-input";

        var healedLocator = findElement(healingValue, page.locator(brokenSelector));

        healedLocator.fill("This proves healing worked!");
        assertThat(healedLocator).hasValue("This proves healing worked!");
    }

    @Test
    @EnabledIfSystemProperty(named = "smartqa.runAiTests", matches = "true")
    void aiHealingStrategyWorksWithOllama() {
        // 1. Navigate to a page designed for this test.
        page.navigate(testResource("demo-pages/ai-healing-test.html").toExternalForm());

        // 2. Define a truly broken selector that will NOT find anything.
        String trulyBrokenSelector = "#nonexistent-button-id";

        // 3. Provide a simple, direct semantic description of the element to the AI.
        //    Focus on the most unambiguous identifier: the button's text.
        String semanticDescription = "Confirm button";

        // 4. Call findElement. The SelfHealingEngine will trigger the AI strategy.
        //    - Attribute and Text strategies will fail.
        //    - AIHealingStrategy will call OllamaProvider with the semanticDescription.
        //    - Ollama should analyze the DOM and suggest a better selector like '.button-container .action-btn.primary' or 'text=Confirm'.
        var healedLocator = findElement(semanticDescription, page.locator(trulyBrokenSelector));

        // 5. Assert that the correct element was found and is visible.
        assertThat(healedLocator).isVisible();
        assertThat(healedLocator).containsText("Confirm");
    }
}
