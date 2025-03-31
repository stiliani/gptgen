package com.example.gptgen.controller;

import com.example.gptgen.model.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import com.example.gptgen.model.History;
import com.example.gptgen.model.HistoryDAO;
import java.util.List;

/**
 * Controller class responsible for handling history-related requests.
 * This class provides methods for retrieving, saving, and deleting user history entries.
 * It interacts with the HistoryDAO to manage the history data and responds with appropriate actions.
 */
@WebServlet("/history")
public class HistoryController extends HttpServlet {

    private HistoryDAO historyDAO = new HistoryDAO();

    /**
     * Handles HTTP GET requests for retrieving the user's history.
     * @param request The HTTP request containing the username for which to retrieve the history.
     * @param response The HTTP response that will contain the retrieved history as a JSON response.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameParam = request.getParameter("username"); // Retrieve the username parameter from the request

        UserDAO userDAO = new UserDAO();

        // Check if the username is provided
        if (usernameParam == null || usernameParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
            return;
        }

        // Get the user ID from the username
        Long userId = userDAO.getUserIdByUsername(usernameParam);

        // Retrieve the history entries for this user
        List<History> histories = historyDAO.getHistoriesByUserId(userId);

        // Set the response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Log the size of the retrieved history list for debugging purposes
        System.out.println("XXX Retrieved " + histories.size() + " history entries for userId: " + userId);

        // Set the history list as a request attribute and forward to the JSP
        request.setAttribute("historyList", histories);

        RequestDispatcher dispatcher = request.getRequestDispatcher("history.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Handles HTTP POST requests for saving a new history entry.
     * @param request The HTTP request containing the prompt and response text.
     * @param response The HTTP response to redirect after saving the history.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the prompt and response text from the request
        String prompt = request.getParameter("prompt");
        String responseText = request.getParameter("response");

        // Retrieve the userId from the session
        Long userId = (Long) request.getSession().getAttribute("userId");

        // Log the prompt and response for debugging purposes
        System.out.println("XXX Saving history entry for userId: " + userId + " with prompt: " + prompt);

        // Create a new History object
        History history = new History(prompt, responseText, userId);

        // Persist the History object in the database
        historyDAO.saveHistory(history);

        // Redirect to the history view page after saving
        response.sendRedirect("history");  // Assuming you have a history view page
    }

    /**
     * Handles HTTP DELETE requests for deleting a history entry by prompt ID.
     * @param request The HTTP request containing the prompt ID to delete.
     * @param response The HTTP response indicating the result of the deletion.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String promptIdParam = request.getParameter("promptid");

        // Check if the promptId is provided
        if (promptIdParam != null && !promptIdParam.isEmpty()) {
            try {
                Long promptid = Long.parseLong(promptIdParam);

                // Log the promptId for debugging purposes
                System.out.println("XXX Deleting history entry with promptId: " + promptid);

                // Delete the history entry by ID
                historyDAO.deleteHistoryById(promptid);

                // Set the status to OK after successful deletion
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid promptid");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "promptid is required");
        }
    }
}