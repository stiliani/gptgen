package com.example.gptgen.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controller class responsible for handling user logout requests.
 * This class invalidates the user's session and redirects them to the home page after logout.
 */
@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    /**
     * Handles HTTP GET requests for user logout.
     * This method invalidates the user's session and redirects to the home page.
     * @param request The HTTP request to retrieve the session.
     * @param response The HTTP response to redirect the user to the home page.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the current session, if it exists
        HttpSession session = request.getSession(false);

        // Log session invalidation attempt
        System.out.println("XXX Attempting to log out user. Session exists: " + (session != null));

        if (session != null) {
            session.invalidate();  // Invalidate the session to log out the user
            System.out.println("XXX User logged out successfully.");
        }
        response.sendRedirect("index.jsp");
    }
}
