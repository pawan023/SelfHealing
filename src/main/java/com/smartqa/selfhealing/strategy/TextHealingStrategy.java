package com.smartqa.selfhealing.strategy;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * A healing strategy that attempts to find an element by its text content.
 */
public class TextHealingStrategy implements HealingStrategy {

    @Override
    public Locator heal(Page page, String selector, Locator brokenLocator) {
        System.out.println("Applying TextHealingStrategy for broken locator with selector: '" + selector + "'");

        // Simple heuristic: if the selector doesn't start with common CSS/XPath prefixes,
        // assume it might be text content. This is a very basic check.
        if (!selector.startsWith("#") && !selector.startsWith(".") && !selector.startsWith("/") && !selector.contains("[") && !selector.contains("=")) {
            System.out.println("Attempting to heal by text: " + selector);
            Locator healedLocator = page.getByText(selector);
            if (healedLocator.count() > 0) {
                System.out.println("TextHealingStrategy successfully found element by text: " + selector);
                return healedLocator;
            }
        }
        System.out.println("TextHealingStrategy could not heal the locator.");
        return null; // No healing performed
    }
}
