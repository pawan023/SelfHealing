package com.smartqa.tests.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URL;

public abstract class BasePlaywrightTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    protected void startBrowser() {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty("smartqa.headless", "true")));

        browser = playwright.chromium().launch(launchOptions);
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    protected void stopBrowser() {
        closePage();
        closeContext();
        closeBrowser();
        closePlaywright();
    }

    protected URL testResource(String resourcePath) {
        URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Test resource not found: " + resourcePath);
        }
        return resource;
    }

    private void closePage() {
        if (page != null) {
            page.close();
        }
    }

    private void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    private void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
    }

    private void closePlaywright() {
        if (playwright != null) {
            playwright.close();
        }
    }
}
