   package com.smartqa.selfhealing.engine;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.smartqa.selfhealing.strategy.HealingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * The core self-healing engine responsible for intercepting locator failures
 * and applying healing strategies.
 */
public class SelfHealingEngine {

    private final Page page;
    private final List<HealingStrategy> healingStrategies;

    public SelfHealingEngine(Page page) {
        this.page = page;
        this.healingStrategies = new ArrayList<>();
    }

    /**
     * Registers a healing strategy with the engine.
     * Strategies are applied in the order they are registered.
     * @param strategy The healing strategy to register.
     */
    public void registerStrategy(HealingStrategy strategy) {
        this.healingStrategies.add(strategy);
    }

    /**
     * Attempts to find an element using the primary locator. If it fails,
     * it triggers self-healing strategies to find an alternative locator.
     *
     * @param selector The original selector string used to create the primaryLocator.
     * @param primaryLocator The initial locator to try.
     * @return A working Locator instance, potentially healed.
     */
    public Locator findElement(String selector, Locator primaryLocator) {
        System.out.println("Attempting to find element with selector: '" + selector + "' and locator: " + primaryLocator.toString());

        // Try to find the element with the primary locator
        if (primaryLocator.count() > 0) {
            System.out.println("Element found with   primary locator.");
            return primaryLocator;
        } else {
            System.out.println("Element NOT found with primary locator. Initiating self-healing process for selector: '" + selector + "'...");

            for (HealingStrategy strategy : healingStrategies) {
                System.out.println("Applying healing strategy: " + strategy.getClass().getSimpleName());
                Locator healedLocator = strategy.heal(page, selector, primaryLocator); // Pass selector to heal method
                if (healedLocator != null && healedLocator.count() > 0) {
                    System.out.println("Element healed successfully using " + strategy.getClass().getSimpleName() + ". New locator: " + healedLocator.toString());
                    // TODO: Report healing action using com.smartqa.selfhealing.reporting
                    return healedLocator;
                }
            }

            System.out.println("All healing strategies failed for selector: '" + selector + "'. Returning original broken locator.");
            // TODO: Potentially use LLM (from com.smartqa.selfhealing.llm) for new locator suggestions as a last resort.
            // TODO: Report failure to heal.
            return primaryLocator; // No healing performed, return the original (broken) locator
        }
    }
}
