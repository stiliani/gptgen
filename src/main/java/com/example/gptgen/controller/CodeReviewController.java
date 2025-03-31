package com.example.gptgen.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Controller class responsible for handling code review requests and communicating with the OpenAI API.
 * This class receives Java code via HTTP POST, sends the code to OpenAI for analysis, and returns the analysis results.
 */
@WebServlet("/reviewCode")
public class CodeReviewController extends HttpServlet {

    private static final String OPENAI_API_KEY = ""; // Insert API Key here
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * Processes HTTP POST requests for generating code reviews.
     * @param request The HTTP request containing the user message and code to analyze.
     * @param response The HTTP response to be sent back to the client.
     * @throws ServletException If an error occurs during the request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String model = request.getParameter("model");

        System.out.println("XXX Code received: " + (code != null ? code : "No code provided"));

        // Check if the code parameter is null or empty
        if (code == null || code.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Code is required.");
            return;
        }

        // Create an HTTP client for sending the request
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .header("Authorization", "Bearer " + OPENAI_API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(buildOpenAiPrompt(code, model))) // Pass the model
                    .build();

            System.out.println("XXX HTTP request created. Sending request to OpenAI...");

            // Send the request and get the response
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String openAiResponse = httpResponse.body();

            System.out.println("XXX OpenAI API Response: " + openAiResponse);

            // Create a new instance of Gson to handle JSON parsing
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(openAiResponse, JsonObject.class);

            // Check if the response contains choices and process them
            if (jsonResponse.getAsJsonArray("choices") != null && jsonResponse.getAsJsonArray("choices").size() > 0) {
                String analysis = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();

                System.out.println("XXX Analysis received: " + analysis);

                // Prepare and send the response to the client
                response.setContentType("application/json;charset=UTF-8"); // Set UTF-8 encoding
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(analysis));
                out.flush();
            } else {
                // If no response from OpenAI, send error message
                response.setContentType("application/json;charset=UTF-8"); // Set UTF-8 encoding
                PrintWriter out = response.getWriter();
                out.print(gson.toJson("Error: No response received from OpenAI."));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during code analysis.");
        }
    }

    /**
     * Builds the OpenAI prompt with the provided code and model for the API request.
     * @param code The Java code to analyze.
     * @param model The model to use for the code analysis.
     * @return The JSON body of the request to OpenAI.
     */
    private String buildOpenAiPrompt(String code, String model) {
        Gson gson = new Gson();
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model); // Use the provided model

        JsonArray messages = new JsonArray();

        // Create system message
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "Du bist ein Code-Review-Experte.");
        messages.add(systemMessage);

        // Create user message with the provided code
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "Analysiere diesen Java-Code und gib Verbesserungsvorschläge:\\n\\n" + code);
        messages.add(userMessage);

        requestBody.add("messages", messages);

        // Debugging: Print the request body being sent to OpenAI
        System.out.println("XXX Request body sent to OpenAI: " + gson.toJson(requestBody));

        return gson.toJson(requestBody);
    }
}
