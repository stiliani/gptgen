package com.example.gptgen.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * This servlet handles account updates, specifically for updating the user's email and password.
 * It verifies the old password, compares it with the stored hash, and updates the user's email and/or password in the database.
 */
@WebServlet("/updateAccount")
public class UpdateAccountController extends HttpServlet {
    /**
     * Handles HTTP POST requests for updating the user's account information.
     * This method processes the change of email and/or password after validating the provided old password.
     * @param request The HTTP request containing the form data for the email and password update.
     * @param response The HTTP response that will confirm the success or failure of the update operation.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve session data and form inputs
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String email = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Check if the new and confirm passwords match
        if (newPassword.equals(confirmPassword)) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gptgenlogin", "root", "")) {
                // Prepare a statement to fetch the stored password for the user
                PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();

                // Check if the user was found in the database
                if (rs.next()) {
                    // Retrieve the stored password hash
                    String storedPasswordHash = rs.getString("password");

                    // Check if the old password matches the stored hash
                    if (BCrypt.checkpw(oldPassword, storedPasswordHash)) {
                        // Hash the new password
                        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                        // Update the email and password in the database
                        ps = conn.prepareStatement("UPDATE users SET email = ?, password = ? WHERE username = ?");
                        ps.setString(1, email);
                        ps.setString(2, newPasswordHash); // Gehashtes neues Passwort speichern
                        ps.setString(3, username);
                        ps.executeUpdate();

                        // Log the success of the operation
                        System.out.println("XXX Account update successful for user.");

                        // Update session with new email
                        session.setAttribute("email", email);

                        // Redirect to account page with success message
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
