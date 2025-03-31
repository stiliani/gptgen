package com.example.gptgen.controller;

import java.io.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.tools.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Controller class responsible for handling code compilation and execution requests.
 * This class receives Java code via HTTP POST, compiles the code, executes it, and returns the results.
 */
@WebServlet("/compile")
public class CompileController extends HttpServlet {

    /**
     * Processes HTTP POST requests for code compilation and execution.
     * @param request The HTTP request containing the code to compile.
     * @param response The HTTP response to send back the result.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException If an I/O error occurs during request or response handling.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Receive the JSON body of the request
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        System.out.println("Received JSON: " + sb.toString()); // Log the received JSON

        // Parse the JSON text
        JsonObject jsonRequest = JsonParser.parseString(sb.toString()).getAsJsonObject();
        String code = jsonRequest.get("code").getAsString(); // Extract the code from the JSON

        System.out.println("Extracted Code: " + code); // Log the extracted code

        // Extract the class name from the code (searches for the first public class)
        String className = extractClassName(code);
        if (className == null) {
            response.getWriter().write(new Gson().toJson(Map.of("message", "Error: No public class found.")));
            return;
        }

        // Create a temporary file with the correct class name (e.g., Main.java)
        File tempFile = new File(getServletContext().getRealPath("/" + className + ".java"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(code);   // Write the code to the file
        }

        // Compile the code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(tempFile);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();

        // Return the compilation results
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("message", "Kompilierung erfolgreich!");
            // Execute the code
            String executionResult = executeCode(className, getServletContext().getRealPath("/"));
            result.put("executionResult", executionResult);

        } else {
            StringBuilder errorMessages = new StringBuilder("Kompilierungsfehler:\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                errorMessages.append(diagnostic.getMessage(null)).append("\n");
            }
            result.put("message", errorMessages.toString());
        }

        // Send the response as JSON
        response.getWriter().write(new Gson().toJson(result));
    }

    /**
     * Extracts the class name from the provided Java code.
     * @param code The Java code to extract the class name from.
     * @return The class name, or null if no public class is found.
     */
    private String extractClassName(String code) {
        String classPattern = "public class (\\w+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(classPattern);
        java.util.regex.Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            return matcher.group(1); // Return the class name
        }
        return null; // If no public class is found
    }

    /**
     * Executes the compiled Java code using a process builder.
     * @param className The name of the class to execute.
     * @param path The directory to execute the code in.
     * @return The output of the execution as a string.
     * @throws IOException If an I/O error occurs while executing the code.
     */
    private String executeCode(String className, String path) throws IOException{
        System.out.println("XXX Preparing to execute the compiled code for class: " + className);

        ProcessBuilder processBuilder = new ProcessBuilder("java", className);
        processBuilder.directory(new File(path));

        Process process = processBuilder.start();

        // Read the output of the process
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}


