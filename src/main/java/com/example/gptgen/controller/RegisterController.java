package com.example.gptgen.controller;

import com.example.gptgen.model.User;
import com.example.gptgen.model.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller class responsible for handling user registration requests.
 * This class processes the registration form, validates the username and email,
 * and creates a new user in the database if the inputs are valid.
 */
@WebServlet("/register")
public class RegisterController extends HttpServlet {
    /**
     * Handles HTTP POST requests for user registration.
     * This method processes the registration form data, validates the inputs,
     * and attempts to register the user in the database.
     * @param request The HTTP request containing the registration form data.
     * @param response The HTTP response to redirect the user or show an error message.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve form parameters
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Create a new User object
        User user = new User(username, email, password);
        UserDAO userDAO = new UserDAO();

        // Check if the username or email already exists in the database
        if (userDAO.isUsernameTaken(username)) {
            request.setAttribute("error", "Username existiert schon.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else if (userDAO.isEmailTaken(email)) {
            request.setAttribute("error", "Es existiert bereits ein Konto mit dieser E-Mail.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else {
            // Attempt to register the user
            if (userDAO.registerUser(user)) {
                response.sendRedirect("login.jsp?success=registered");
            } else {
                response.sendRedirect("register.jsp?error=unknown");
            }
        }
    }
}
