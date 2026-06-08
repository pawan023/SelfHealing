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
}
