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

@WebServlet("/reviewCode")
public class ReviewController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Empfange den JSON-Body der Anfrage
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parsen des JSON-Textes
        JsonObject jsonRequest = JsonParser.parseString(sb.toString()).getAsJsonObject();
        String code = jsonRequest.get("code").getAsString(); // Hole den Code aus dem JSON

        // OpenAI API Anfrage
        String openAiApiKey = ""; // Dein OpenAI API-Schlüssel
        String url = "https://api.openai.com/v1/chat/completions";

        // Anfragekörper (JSON)
        String body = "{"
                + "\"model\": \"gpt-4\","
                + "\"messages\": [{"
                + "\"role\": \"user\","
                + "\"content\": \"Bitte analysiere den folgenden Code und gib Verbesserungsvorschläge: " + code + "\""
                + "}]"
                + "}";

        // Anfrage an OpenAI senden
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + openAiApiKey);
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Antwort von OpenAI lesen
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String inputLine;
            StringBuilder responseStr = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
            // Die Antwort von OpenAI wird hier zurückgegeben
            String chatGptResponse = responseStr.toString();
            JsonObject jsonResponse = JsonParser.parseString(chatGptResponse).getAsJsonObject();
            String message = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            // Antwort an den Client senden
            JsonObject jsonResponseObj = new JsonObject();
            jsonResponseObj.addProperty("message", message);
            response.getWriter().write(jsonResponseObj.toString());
        }
    }
}

