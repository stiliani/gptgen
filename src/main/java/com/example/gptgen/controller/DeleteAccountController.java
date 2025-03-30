package com.example.gptgen.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/deleteAccount")
public class DeleteAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gptgenlogin", "root", "")) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username = ?");
            ps.setString(1, username);
            ps.executeUpdate();

            session.invalidate();  // Session ungültig machen
            response.sendRedirect("index.jsp");  // Zurück zur Startseite
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("konto.jsp?error=Fehler beim Löschen des Accounts");
        }
    }
}

