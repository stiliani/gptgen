package com.example.gptgen.controller;

import com.example.gptgen.model.User;
import com.example.gptgen.model.PromptDAO;
import com.example.gptgen.view.TitleGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.example.gptgen.service.CodeGenerator;
import com.example.gptgen.service.CodeReviewer;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/deine_datenbank";
    private static final String DB_USER = "dein_benutzer";
    private static final String DB_PASSWORD = "dein_passwort";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("generate".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("username") != null) {
                String username = (String) session.getAttribute("username");
                String message = request.getParameter("message");
                String responseMessage = (String) request.getAttribute("responseMessage");

                // Titel aus den ersten 5 Wörtern erstellen
                String title = createTitle(message);

                // Benutzer-ID aus der Datenbank abrufen
                int userId = getUserId(username);

                // Daten in der Datenbank speichern
                if (userId != -1) {
                    saveMessage(userId, title, message, responseMessage);
                } else {
                    System.err.println("Benutzer-ID nicht gefunden.");
                }
            }
        }

        request.getRequestDispatcher("chat.jsp").forward(request, response);
    }

    private String createTitle(String message) {
        String[] words = message.split("\\s+");
        StringBuilder title = new StringBuilder();
        for (int i = 0; i < Math.min(5, words.length); i++) {
            title.append(words[i]).append(" ");
        }
        return title.toString().trim() + "...";
    }

    private int getUserId(String username) {
        int userId = -1;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    private void saveMessage(int userId, String title, String message, String responseMessage) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages (user_id, title, question, answer) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.setString(4, responseMessage);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}










