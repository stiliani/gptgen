package com.example.gptgen.controller;

import com.example.gptgen.model.User;
import com.example.gptgen.model.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = new User(username, email, password);
        UserDAO userDAO = new UserDAO();

        // Überprüfen, ob Benutzername oder E-Mail bereits existieren
        if (userDAO.isUsernameTaken(username)) {
            request.setAttribute("error", "Username existiert schon.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else if (userDAO.isEmailTaken(email)) {
            request.setAttribute("error", "Es existiert bereits ein Konto mit dieser E-Mail.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else {
            // Registrierung des Benutzers
            if (userDAO.registerUser(user)) {
                response.sendRedirect("login.jsp?success=registered");
            } else {
                response.sendRedirect("register.jsp?error=unknown");
            }
        }
    }
}
