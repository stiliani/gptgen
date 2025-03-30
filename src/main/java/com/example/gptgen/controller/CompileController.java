package com.example.gptgen.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.*;

import jakarta.servlet.http.*;
import javax.tools.*;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/compile")
public class CompileController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Empfange den JSON-Body der Anfrage
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parsen des JSON-Textes
        JsonObject jsonRequest = JsonParser.parseString(sb.toString()).getAsJsonObject();
        String code = jsonRequest.get("code").getAsString(); // Hole den Code aus dem JSON

        // Extrahiere den Klassennamen aus dem Code (sucht nach der ersten public class)
        String className = extractClassName(code);
        if (className == null) {
            response.getWriter().write(new Gson().toJson(Map.of("message", "Fehler: Keine öffentliche Klasse gefunden.")));
            return;
        }

        // Erstelle eine temporäre Datei mit dem richtigen Klassennamen (z.B. Main.java)
        File tempFile = new File(getServletContext().getRealPath("/" + className + ".java"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(code);  // Schreibe den Code in die Datei
        }

        // Kompiliere den Code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(tempFile);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();

        // Gebe die Kompilierergebnisse zurück
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("message", "Kompilierung erfolgreich!");
        } else {
            StringBuilder errorMessages = new StringBuilder("Kompilierungsfehler:\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                errorMessages.append(diagnostic.getMessage(null)).append("\n");
            }
            result.put("message", errorMessages.toString());
        }

        // Antwort als JSON zurücksenden
        response.getWriter().write(new Gson().toJson(result));
    }

    // Methode zum Extrahieren des Klassennamens aus dem Code
    private String extractClassName(String code) {
        String classPattern = "public class (\\w+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(classPattern);
        java.util.regex.Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            return matcher.group(1); // Gibt den Klassennamen zurück
        }
        return null; // Wenn keine öffentliche Klasse gefunden wurde
    }
}


