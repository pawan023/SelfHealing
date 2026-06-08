package com.smartqa.selfhealing.strategy;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A healing strategy that attempts to find an element by checking a list of common attributes.
 * For example, if a locator '#submit-btn' fails, it might try to find an element
 * with name='submit-btn' or data-testid='submit-btn'.
 */
public class AttributeHealingStrategy implements HealingStrategy {

    // A list of common, stable attributes to check, in order of preference.
    private static final List<String> ATTRIBUTES_TO_TRY = Arrays.asList(
            "data-testid",
            "data-test-id",
            "name",
            "aria-label",
            "id",
            "placeholder",
            "title"
    );

    // A simple regex to extract the core value from a CSS selector (e.g., '#submit' -> 'submit').
    private static final Pattern SELECTOR_VALUE_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");

    @Override
    public Locator heal(Page page, String selector, Locator brokenLocator) {
        System.out.println("Applying AttributeHealingStrategy for selector: '" + selector + "'");

        Matcher matcher = SELECTOR_VALUE_PATTERN.matcher(selector);
        if (matcher.find()) {
            String value = matcher.group();
            System.out.println("Extracted value '" + value + "' from selector.");

            for (String attribute : ATTRIBUTES_TO_TRY) {
                String newSelector = String.format("[%s='%s']", attribute, value);
                System.out.println("Attempting to heal with new selector: " + newSelector);
                Locator healedLocator = page.locator(newSelector);

                if (healedLocator.count() > 0) {
                    System.out.println("AttributeHealingStrategy successfully found element with selector: " + newSelector);
                    return healedLocator;
                }
            }
        } else {
            System.out.println("Could not extract a usable value from the selector '" + selector + "'.");
        }

        System.out.println("AttributeHealingStrategy could not heal the locator.");
        return null; // No healing performed
    }
}
