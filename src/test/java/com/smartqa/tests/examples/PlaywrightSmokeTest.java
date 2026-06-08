package com.smartqa.tests.examples;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.smartqa.tests.base.BasePlaywrightTest;
import org.junit.jupiter.api.Test;

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
        // 1. Navigate to a page designed for this test.
        page.navigate(testResource("demo-pages/healing-test.html").toExternalForm());

        // 2. Define a broken selector. The 'login-input' element in the HTML
        //    does NOT have an ID, so this will fail.
        String brokenSelector = "#login-input";
        String healingValue = "login-input"; // The value our strategy should extract.

        // 3. Call findElement. The SelfHealingEngine should be triggered.
        //    - The primary locator (page.locator("#login-input")) will fail.
        //    - The AttributeHealingStrategy will then try page.locator("[name='login-input']").
        //    - This will succeed, and the healed locator will be returned.
        var healedLocator = findElement(healingValue, page.locator(brokenSelector));

        // 4. Perform an action and assert to prove it worked.
        healedLocator.fill("This proves healing worked!");
        assertThat(healedLocator).hasValue("This proves healing worked!");
    }
}
