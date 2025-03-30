package com.example.gptgen.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/updateAccount")
public class UpdateAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String email = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword.equals(confirmPassword)) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gptgenlogin", "root", "")) {
                // Benutzer aus der Datenbank abrufen
                PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    // Überprüfen, ob das eingegebene alte Passwort mit dem gehashten Passwort übereinstimmt
                    String storedPasswordHash = rs.getString("password");
                    if (BCrypt.checkpw(oldPassword, storedPasswordHash)) {
                        // Passwort ändern (neues Passwort wird ebenfalls gehasht)
                        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                        ps = conn.prepareStatement("UPDATE users SET email = ?, password = ? WHERE username = ?");
                        ps.setString(1, email);
                        ps.setString(2, newPasswordHash); // Gehashtes neues Passwort speichern
                        ps.setString(3, username);
                        ps.executeUpdate();

                        // Erfolgreich gespeichert
                        session.setAttribute("email", email);
                        response.sendRedirect("konto.jsp?success=true");
                    } else {
                        response.sendRedirect("konto.jsp?error=Falsches Passwort");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect("konto.jsp?error=Fehler bei der Datenbankverbindung");
            }
        } else {
            response.sendRedirect("konto.jsp?error=Passwörter stimmen nicht überein");
        }
    }
}
