package com.example.gptgen.controller;

import com.example.gptgen.model.User;
import com.example.gptgen.model.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller class responsible for handling user login requests.
 * This class processes the login credentials, authenticates the user, and creates a session if authentication is successful.
 * In case of invalid credentials, it redirects the user back to the login page with an error message.
 */
@WebServlet("/login")
public class LoginController extends HttpServlet{
    /**
     * Handles HTTP POST requests for user login.
     * @param request The HTTP request containing the user's email and password.
     * @param response The HTTP response to redirect or forward based on the login result.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("XXX Attempting login with email: " + email);

        // Create a UserDAO instance to authenticate the user
        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticateUser(email, password);

        // Check if the user is authenticated successfully
        if (user != null) {
            // Create a new session and set user details as session attributes
            HttpSession session = request.getSession();
            session.setAttribute("id", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("password", user.getPassword());

            // Log successful login
            System.out.println("XXX User authenticated successfully: " + user.getUsername());

            // Redirect to the home page after successful login
            response.sendRedirect("index.jsp");
        } else {
            request.setAttribute("loginError", "Falsche E-Mail oder falsches Passwort.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
