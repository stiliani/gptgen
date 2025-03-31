package com.example.gptgen.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

/**
 * Controller class responsible for handling account deletion requests.
 * This class receives a request to delete a user account, removes the account from the database,
 * invalidates the session, and redirects the user back to the home page.
 */
@WebServlet("/deleteAccount")
public class DeleteAccountController extends HttpServlet {
    /**
     * Handles HTTP POST requests for account deletion.
     * @param request The HTTP request containing the user's session data.
     * @param response The HTTP response to redirect or provide an error message.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session and retrieve the username from it
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gptgenlogin", "root", "")) {
            // Prepare and execute the SQL statement to delete the user from the database
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username = ?");
            ps.setString(1, username);
            ps.executeUpdate();

            int rowsAffected = ps.executeUpdate();
            // Debugging output
            if (rowsAffected > 0) {
                System.out.println("XXX Account for username: " + username + " deleted successfully.");
            } else {
                System.out.println("XXX No account found for username: " + username);
            }

            // Invalidate the session after the account is deleted
            session.invalidate();
            response.sendRedirect("index.jsp");  // Redirect to the index.jsp
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("konto.jsp?error=Fehler beim Löschen des Accounts");
        }
    }
}

