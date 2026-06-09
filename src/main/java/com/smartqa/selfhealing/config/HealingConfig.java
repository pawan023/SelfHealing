  package com.smartqa.selfhealing.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and provides access to configuration for the self-healing framework.
 */
public class HealingConfig {

    private static final String CONFIG_FILE = "self-healing.properties";
    private final Properties properties = new Properties();

    public HealingConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("HealingConfig: No '" + CONFIG_FILE + "' found. Using default settings.");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            System.err.println("HealingConfig: Error loading configuration file. Using default settings. Error: " + ex.getMessage());
        }
    }

    /**
     * Gets the configured AI provider.
     *
     * @return The name of the AI provider (e.g., "ollama"). Defaults to "ollama".
     */
    public String getAiProvider() {
        return properties.getProperty("ai.provider", "ollama").toLowerCase().trim();
    }
}
