package com.smartqa.selfhealing.strategy;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Interface for self-healing strategies.
 * Implementations of this interface will attempt to find an alternative locator
 * when the primary locator fails.
 */
public interface HealingStrategy {

    /**
     * Attempts to heal a broken locator by finding an alternative.
     *
     * @param page The Playwright Page instance.
     * @param selector The original selector string that was used.
     * @param brokenLocator The original locator that failed to find an element.
     * @return A new, working Locator if healing is successful, or null if the strategy cannot find an alternative.
     */
    Locator heal(Page page, String selector, Locator brokenLocator);
}
