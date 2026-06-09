package com.smartqa.tests.base;

import com.microsoft.playwright.*;
import com.smartqa.selfhealing.ai.AIHealingStrategy;
import com.smartqa.selfhealing.ai.AIProvider;
import com.smartqa.selfhealing.ai.providers.OllamaProvider;
import com.smartqa.selfhealing.config.HealingConfig;
import com.smartqa.selfhealing.engine.SelfHealingEngine;
import com.smartqa.selfhealing.strategy.AttributeHealingStrategy;
import com.smartqa.selfhealing.strategy.TextHealingStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URL;

public abstract class BasePlaywrightTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected SelfHealingEngine selfHealingEngine;

    @BeforeEach
    protected void startBrowser() {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty("smartqa.headless", "true")));

        browser = playwright.chromium().launch(launchOptions);
        context = browser.newContext();
        page = context.newPage();
        selfHealingEngine = new SelfHealingEngine(page);

        // Setup healing strategies
        setupHealingStrategies();
    }

    private void setupHealingStrategies() {
        HealingConfig config = new HealingConfig();

        // Register standard strategies
        selfHealingEngine.registerStrategy(new AttributeHealingStrategy());
        selfHealingEngine.registerStrategy(new TextHealingStrategy());

        // Configure and register the AI strategy
        AIProvider aiProvider = createAiProvider(config);
        if (aiProvider != null) {
            selfHealingEngine.registerStrategy(new AIHealingStrategy(aiProvider));
        }
    }

    private AIProvider createAiProvider(HealingConfig config) {
        String providerName = config.getAiProvider();
        System.out.println("Configured AI Provider: " + providerName);

        switch (providerName) {
            case "ollama":
                return new OllamaProvider();
            case "openai":
                // return new OpenAIProvider(); // To be implemented
            case "gemini":
                // return new GeminiProvider(); // To be implemented
            default:
                System.err.println("Warning: Unknown AI provider '" + providerName + "'. AI healing will be disabled.");
                return null;
        }
    }

    @AfterEach
    protected void stopBrowser() {
        closePage();
        closeContext();
        closeBrowser();
        closePlaywright();
    }

    protected URL testResource(String resourcePath) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Test resource not found: " + resourcePath);
        }
        return resource;
    }

    protected Locator findElement(String selector, Locator primaryLocator) {
        return selfHealingEngine.findElement(selector, primaryLocator);
    }

    private void closePage() {
        if (page != null) page.close();
    }

    private void closeContext() {
        if (context != null) context.close();
    }

    private void closeBrowser() {
        if (browser != null) browser.close();
    }

    private void closePlaywright() {
        if (playwright != null) playwright.close();
    }
}
