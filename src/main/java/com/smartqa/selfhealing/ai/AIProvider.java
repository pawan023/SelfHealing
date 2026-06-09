package com.smartqa.selfhealing.ai;

import java.io.IOException;

/**
 * An interface for AI providers that can suggest new locators.
 * This allows for a plug-and-play architecture (Ollama, OpenAI, Gemini, etc.).
 */
public interface AIProvider {

    /**
     * Generates a new CSS selector based on the provided HTML content and the original, broken selector.
     *
     * @param htmlContent The full HTML content of the page.
     * @param originalSelector The selector that failed to find the element.
     * @return A new, suggested CSS selector string, or null if no suggestion can be made.
     * @throws IOException If there is an error communicating with the AI provider's API.
     */
    String generateSelector(String htmlContent, String originalSelector) throws IOException;
}
