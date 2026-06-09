package com.smartqa.selfhealing.ai;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.smartqa.selfhealing.strategy.HealingStrategy;

import java.io.IOException;

/**
 * A healing strategy that uses a configurable AI Provider to suggest a new locator.
 * This class orchestrates the AI healing process, while the specific AI communication
 * is handled by an implementation of the AIProvider interface.
 */
public class AIHealingStrategy implements HealingStrategy {

    private final AIProvider provider;

    public AIHealingStrategy(AIProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("AIProvider cannot be null.");
        }
        this.provider = provider;
    }

    @Override
    public Locator heal(Page page, String selector, Locator brokenLocator) {
        System.out.println("Applying AIHealingStrategy with provider: " + provider.getClass().getSimpleName());

        try {
            // Sanitize page content to remove script/style tags and reduce token count for the AI.
            String sanitizedContent = page.content()
                    .replaceAll("<script\\b[^>]*>[\\s\\S]*?<\\/script>", "")
                    .replaceAll("<style\\b[^>]*>[\\s\\S]*?<\\/style>", "");

            String newSelector = provider.generateSelector(sanitizedContent, selector);

            if (newSelector != null && !newSelector.trim().isEmpty()) {
                System.out.println("AI provider suggested new selector: '" + newSelector + "'");
                Locator healedLocator = page.locator(newSelector);
                if (healedLocator.count() > 0) {
                    System.out.println("AIHealingStrategy successfully found element with selector: " + newSelector);
                    return healedLocator;
                } else {
                    System.out.println("AI provider's suggestion failed to find an element.");
                }
            }
        } catch (IOException e) {
            System.err.println("AIHealingStrategy: Error communicating with AI provider: " + e.getMessage());
        }

        return null;
    }
}
