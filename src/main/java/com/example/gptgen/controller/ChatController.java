package com.example.gptgen.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebServlet("/chat")
public class ChatController extends HttpServlet {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SECRET_KEY = "";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gptgenlogin"; // Ersetzen
    private static final String DB_USER = "root"; // Ersetzen
    private static final String DB_PASSWORD = ""; // Ersetzen

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String userMessage = request.getParameter("message");
        String model = request.getParameter("model");
        Logger logger = Logger.getLogger(ChatController.class.getName());
        HttpSession session = request.getSession(false);
        String username = (session != null && session.getAttribute("username") != null) ? (String) session.getAttribute("username") : "anonymous";

        if (action == null || action.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Aktion fehlt.");
            return;
        }

        if (userMessage == null || userMessage.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Nachricht fehlt.");
            return;
        }

        String result;
        try {
            if ("generate".equals(action)) {
                result = generateCode(userMessage, model);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Ungültige Aktion.");
                return;
            }

            Logger.getLogger(ChatController.class.getName()).info("saveChatLog wird aufgerufen...");
            // Daten in die Datenbank speichern und Erfolg prüfen
            boolean savedSuccessfully = saveChatLog(username, userMessage, result, model);

            if (savedSuccessfully) {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(result);
            } else {
                // Fehlermeldung an den Benutzer senden
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Fehler beim Speichern der Daten.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ein interner Fehler ist aufgetreten.");
        }
    }

    private String generateCode(String userMessage, String model) throws IOException {
        String systemMessage = "Du bist ein Code-Generator.";
        return sendOpenAIRequest(systemMessage, userMessage, model);
    }

    private String sendOpenAIRequest(String systemMessage, String userMessage, String model) throws IOException {
        String jsonBody = String.format("""
        {
            "model": "%s",
            "messages": [
                {"role": "system", "content": "%s"},
                {"role": "user", "content": "%s"}
            ]
        }
        """, model, systemMessage, userMessage);

        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + SECRET_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.getBytes("utf-8"));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder responseContent = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                JSONObject jsonResponse = new JSONObject(responseContent.toString());
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        } else {
            throw new IOException("Fehler bei der OpenAI-Anfrage: HTTP " + responseCode);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        JSONObject errorJson = new JSONObject();
        errorJson.put("error", message);
        response.getWriter().write(errorJson.toString());
    }

    private boolean saveChatLog(String userId, String inputMessage, String generatedOutput, String modelUsed) {
        Logger logger = Logger.getLogger(ChatController.class.getName());
        logger.info("Versuche, Chat-Log zu speichern: userId=" + userId + ", inputMessage=" + inputMessage + ", generatedOutput=" + generatedOutput + ", modelUsed=" + modelUsed);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO chat_logs (user_id, input_message, generated_output, model_used) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, inputMessage);
            preparedStatement.setString(3, generatedOutput);
            preparedStatement.setString(4, modelUsed);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                logger.info("Datenbankeintrag erfolgreich.");
                return true; // Erfolg
            } else {
                logger.severe("Fehler beim Speichern des Chat-Logs: Betroffene Zeilen: " + rowsAffected);
                return false; // Fehler
            }

        } catch (SQLException e) {
            logger.severe("Fehler beim Speichern des Chat-Logs: " + e.getMessage());
            e.printStackTrace();
            return false; // Fehler
        }
    }
}









