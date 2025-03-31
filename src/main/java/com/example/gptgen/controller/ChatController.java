package com.example.gptgen.controller;

import com.example.gptgen.model.History;
import com.example.gptgen.model.HistoryDAO;
import com.example.gptgen.model.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Controller class responsible for handling chat-related requests and communicating with OpenAI API.
 */
@WebServlet("/chat")
public class ChatController extends HttpServlet {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SECRET_KEY = ""; // Insert API Key here

    // DAO objects for interacting with the database
    private HistoryDAO historyDAO = new HistoryDAO();
    private UserDAO userDAO = new UserDAO();

    /**
     * Processes HTTP POST requests for generating code or handling chat actions.
     * @param request The HTTP request containing the user message and action.
     * @param response The HTTP response to be sent back to the client.
     * @throws ServletException If an error occurs during the request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("XXX DOPost XXX");

        // Setting character encoding to UTF-8 to handle special characters
        request.setCharacterEncoding("UTF-8");

        // Retrieving parameters from the HTTP request
        String action = request.getParameter("action");
        String userMessage = request.getParameter("message");
        String model = request.getParameter("model");
        HttpSession session = request.getSession(false);
        String username = (session != null && session.getAttribute("username") != null) ? (String) session.getAttribute("username") : "anonymous";

        // Debugging: Log the received action and user message
        System.out.println("XXX Received action: " + action);
        System.out.println("XXX Received userMessage: " + userMessage);
        System.out.println("XXX Received model: " + model);

        if (action == null || action.isEmpty()) {
            System.out.println("XXX Error: Action parameter is missing or empty.");
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Action missing.");
            return;
        }

        if (userMessage == null || userMessage.isEmpty()) {
            System.out.println("XXX Error: User message is missing or empty.");
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Message missing.");
            return;
        }

        String result;
        try {
            // If the action is 'generate', proceed to generate the code
            if ("generate".equals(action)) {
                result = generateCode(userMessage, model);
            } else {
                System.out.println("XXX Error: Invalid action.");
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
                return;
            }

            // Debugging: Log the processed action and generated result
            System.out.println("XXX Action processed successfully. Generating result...");

            // Retrieve the user ID and save the result history in the database
            Long userId = userDAO.getUserIdByUsername(username);
            System.out.println("XXXXXXXXX USERID: " + userId);
            System.out.println("XXXXXXXXX result: " + result);
            System.out.println("XXXXXXXXX userMessage: " + userMessage);

            // Create a new history entry with the user's message and the generated result
            History history = new History(userMessage, result, userId);
            historyDAO.saveHistory(history);

            // Debugging: Confirm the history has been saved
            System.out.println("XXX History saved for user: " + username);

            // Forward the response message to the JSP page for display
            request.setAttribute("responseMessage", result);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("XXX Exception occurred: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred.");
        }
    }

    /**
     * Generates code based on the user input and model selected.
     * @param userMessage The message provided by the user to generate code.
     * @param model The model chosen by the user for generating code.
     * @return The generated code as a result.
     * @throws IOException If an I/O error occurs while communicating with the OpenAI API.
     */
    private String generateCode(String userMessage, String model) throws IOException {
        String systemMessage = "Du bist ein Experte für Java-Code-Generierung. Generier den Code immer so das man ihn Kompilieren kann."; // Define a system message to guide the OpenAI model
        return sendOpenAIRequest(systemMessage, userMessage, model);
    }

    /**
     * Sends a request to the OpenAI API to generate a response based on the user's message.
     * @param systemMessage The message sent to the API to inform its behavior.
     * @param userMessage The message from the user for which the code should be generated.
     * @param model The model used for generating the response.
     * @return The response content from the OpenAI API.
     * @throws IOException If an I/O error occurs while communicating with the OpenAI API.
     */
    private String sendOpenAIRequest(String systemMessage, String userMessage, String model) throws IOException {
        // Construct the request body as JSON
        String jsonBody = String.format("""
        {
            "model": "%s",
            "messages": [
                {"role": "system", "content": "%s"},
                {"role": "user", "content": "%s"}
            ]
        }
        """, model, systemMessage, userMessage);

        System.out.println("XXX Sending request to OpenAI API with body: " + jsonBody);

        // Open a connection to the OpenAI API endpoint
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + SECRET_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the JSON body to the API
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.getBytes("utf-8"));
        }

        // Get the response code from the API
        int responseCode = connection.getResponseCode();
        System.out.println("XXX OpenAI API response code: " + responseCode);

        // If the request is successful (HTTP 200), read the response content
        if (responseCode == 200) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder responseContent = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                // Parse the JSON response and extract the generated content
                JSONObject jsonResponse = new JSONObject(responseContent.toString());
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        } else {
            throw new IOException("Fehler bei der OpenAI-Anfrage: HTTP " + responseCode);
        }
    }

    /**
     * Sends an error response to the client with the specified status code and message.
     * @param response The HttpServletResponse to which the error message will be sent.
     * @param statusCode The HTTP status code for the error response.
     * @param message The error message to be included in the response.
     * @throws IOException If an I/O error occurs while sending the response.
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        // Set the response content type and status code
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);

        // Create a JSON object with the error message
        JSONObject errorJson = new JSONObject();
        errorJson.put("error", message);

        // Write the error response to the output stream
        response.getWriter().write(errorJson.toString());

        System.out.println("XXX Sent error response: " + message);
    }
}









