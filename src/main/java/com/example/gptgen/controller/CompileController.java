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

@WebServlet("/compile")
public class CompileController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        long startTime = System.currentTimeMillis(); // Startzeit erfassen

        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JsonObject jsonRequest = JsonParser.parseString(sb.toString()).getAsJsonObject();
        String code = jsonRequest.get("code").getAsString();

        String className = extractClassName(code);
        if (className == null) {
            response.getWriter().write(new Gson().toJson(Map.of("message", "Error: No public class found.")));
            return;
        }

        File tempFile = new File(getServletContext().getRealPath("/" + className + ".java"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(code);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(tempFile);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();

        long endTime = System.currentTimeMillis(); // Endzeit erfassen
        long compilationTime = endTime - startTime; // Dauer berechnen

        System.out.println("XXXXXXXX Kompilierungszeit (ms): " + compilationTime);

        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("message", "Kompilierung erfolgreich!");
            result.put("compilationTime", compilationTime); // Zeit hinzufügen
            String executionResult = executeCode(className, getServletContext().getRealPath("/"));
            result.put("executionResult", executionResult);
        } else {
            StringBuilder errorMessages = new StringBuilder("Kompilierungsfehler:\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                long lineNumber = diagnostic.getLineNumber();
                long columnNumber = diagnostic.getColumnNumber();
                String errorMessage = diagnostic.getMessage(null);
                errorMessages.append("Zeile ").append(lineNumber).append(", Spalte ").append(columnNumber).append(": ").append(errorMessage).append("\n");
            }
            result.put("message", errorMessages.toString());
            result.put("compilationTime", compilationTime); // Zeit hinzufügen
        }

        response.getWriter().write(new Gson().toJson(result));
    }

    private String extractClassName(String code) {
        String classPattern = "public class (\\w+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(classPattern);
        java.util.regex.Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String executeCode(String className, String path) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", className);
        processBuilder.directory(new File(path));

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}

