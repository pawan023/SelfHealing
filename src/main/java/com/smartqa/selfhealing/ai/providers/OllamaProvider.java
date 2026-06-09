package com.smartqa.selfhealing.ai.providers;

import com.smartqa.selfhealing.ai.AIProvider;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An AI Provider that connects to a local Ollama server.
 * Requires Ollama to be running locally with a model installed (e.g., 'ollama pull qwen').
 */
public class OllamaProvider implements AIProvider {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Generous timeout for local model loading
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final String OLLAMA_MODEL = "qwen";

    // Regex to capture a CSS selector. It tries to be comprehensive but stops at common non-selector characters.
    private static final Pattern CSS_SELECTOR_EXTRACTOR = Pattern.compile("^\\s*([a-zA-Z0-9\\s\\.#\\[\\]=\\-'\"~|^*$:>+]+?)(?=\\s*\\{|\\n|$)", Pattern.DOTALL);


    @Override
    public String generateSelector(String htmlContent, String originalSelector) throws IOException {
        System.out.println("OllamaProvider: Generating selector with model '" + OLLAMA_MODEL + "'...");

        String prompt = createEnhancedPrompt(htmlContent, originalSelector);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", OLLAMA_MODEL);
        jsonBody.put("prompt", prompt);
        jsonBody.put("stream", false);
        jsonBody.put("options", new JSONObject().put("temperature", 0.0)); // For deterministic output

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(OLLAMA_API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected API response from Ollama: " + response.code() + " Body: " + response.body().string());
            }
            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);

            String rawResponse = responseJson.getString("response").trim();
            System.out.println("OllamaProvider: Raw response from AI: " + rawResponse);

            return sanitizeResponse(rawResponse);
        }
    }

    private String createEnhancedPrompt(String htmlContent, String originalIntent) {
        return "You are an expert Playwright test automation engineer. " +
                "Given the following HTML content, your task is to find the element that matches the intent: \"" + originalIntent + "\". " +
                "Provide a single, robust CSS selector that *exactly targets this element using ONLY the existing tags, classes, IDs, and attributes found directly within the provided HTML*. " +
                "You MUST NOT invent any new attributes, classes, or IDs. Your response MUST be ONLY the CSS selector string, with no additional text, explanations, or code blocks. The selector should be as specific as possible to uniquely identify the element.\n\n" +
                "HTML:\n" +
                htmlContent;
    }

    private String sanitizeResponse(String rawResponse) {
        Matcher matcher = CSS_SELECTOR_EXTRACTOR.matcher(rawResponse);
        if (matcher.find()) {
            String extractedSelector = matcher.group(1);
            if (extractedSelector != null && !extractedSelector.trim().isEmpty()) {
                return extractedSelector.trim();
            }
        }

        int firstCurlyBrace = rawResponse.indexOf('{');
        int firstNewline = rawResponse.indexOf('\n');

        if (firstCurlyBrace != -1 && (firstNewline == -1 || firstCurlyBrace < firstNewline)) {
            return rawResponse.substring(0, firstCurlyBrace).trim();
        } else if (firstNewline != -1) {
            return rawResponse.substring(0, firstNewline).trim();
        }

        return rawResponse.trim();
    }
}
