package com.example.gptgen.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This servlet handles the review of code sent by the client.
 * It processes the request containing code to be reviewed,
 * sends the code to the OpenAI API, and returns suggestions for improvement.
 */
@WebServlet("/reviewCode")
public class ReviewController extends HttpServlet {
    /**
     * Handles HTTP POST requests for reviewing code.
     * This method receives a request containing the code to be reviewed,
     * sends the code to the OpenAI API for analysis, and returns the improvement suggestions.
     * @param request The HTTP request containing the code to be reviewed.
     * @param response The HTTP response that will contain the improvement suggestions from OpenAI.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read the JSON body from the incoming request
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON request
        JsonObject jsonRequest = JsonParser.parseString(sb.toString()).getAsJsonObject();
        String code = jsonRequest.get("code").getAsString(); // Extract code from the JSON request

        // Log the received code for debugging
        System.out.println("XXX Received code for review: " + code);

        // Prepare OpenAI API request
        String openAiApiKey = ""; // Insert API Key here
        String url = "https://api.openai.com/v1/chat/completions";

        // Prepare the JSON body for the OpenAI API request
        String body = "{"
                + "\"model\": \"gpt-4\","
                + "\"messages\": [{"
                + "\"role\": \"user\","
                + "\"content\": \"Bitte analysiere den folgenden Code und gib Verbesserungsvorschläge: " + code + "\""
                + "}]"
                + "}";

        // Log the request to OpenAI
        System.out.println("XXX Sending request to OpenAI API...");

        // Send the request to OpenAI API
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + openAiApiKey);
        con.setDoOutput(true);

        // Send the JSON body to OpenAI API
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response from OpenAI
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String inputLine;
            StringBuilder responseStr = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }

            // Log the response from OpenAI
            System.out.println("XXX Response from OpenAI received.");

            // Parse the response from OpenAI
            String chatGptResponse = responseStr.toString();
            JsonObject jsonResponse = JsonParser.parseString(chatGptResponse).getAsJsonObject();
            String message = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            // Send the analysis result back to the client
            JsonObject jsonResponseObj = new JsonObject();
            jsonResponseObj.addProperty("message", message);
            response.getWriter().write(jsonResponseObj.toString());
        }
    }
}

