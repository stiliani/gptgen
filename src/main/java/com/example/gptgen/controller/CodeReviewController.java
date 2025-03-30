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
import java.util.logging.Logger;

@WebServlet("/reviewCode")
public class CodeReviewController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String openAiApiKey = "";
        Logger logger = Logger.getLogger(CodeReviewController.class.getName());

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(buildOpenAiPrompt(code)))
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String openAiResponse = httpResponse.body();
            int statusCode = httpResponse.statusCode();

            logger.info("OpenAI API Response: " + openAiResponse);
            logger.info("OpenAI API Status Code: " + statusCode);

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(openAiResponse, JsonObject.class);

            if (jsonResponse.getAsJsonArray("choices") != null && jsonResponse.getAsJsonArray("choices").size() > 0) {
                String analysis = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
                response.setContentType("application/json;charset=UTF-8"); // UTF-8 setzen
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(analysis));
                out.flush();
            } else {
                response.setContentType("application/json;charset=UTF-8"); // UTF-8 setzen
                PrintWriter out = response.getWriter();
                out.print(gson.toJson("Fehler: Keine Antwort von OpenAI erhalten."));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Fehler bei der Code-Analyse.");
        }
    }

    private String buildOpenAiPrompt(String code) {
        Gson gson = new Gson();
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "Du bist ein Code-Review-Experte.");
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "Analysiere diesen Java-Code und gib Verbesserungsvorschläge:\\n\\n" + code);
        messages.add(userMessage);

        requestBody.add("messages", messages);

        return gson.toJson(requestBody);
    }
}
