<%--
  Created by IntelliJ IDEA.
  User: Cynthia
  Date: 24.03.2025
  Time: 15:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<html>
<head>
    <title>Registrierung</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }
        .container { width: 300px; margin: auto; padding: 20px; background: white; border-radius: 5px; }
        input, button { width: 100%; margin: 5px 0; padding: 8px; }
        .error { color: red; }
    </style>
</head>
<body>
<div class="container">
    <h2>Registrieren</h2>
    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
    <p class="error"><%= error %></p>
    <%
        }
    %>

    <form action="register" method="post">
        <input type="text" name="username" placeholder="Benutzername" required>
        <input type="email" name="email" placeholder="E-Mail" required>
        <input type="password" name="password" placeholder="Passwort" required>
        <button type="submit">Registrieren</button>
    </form>
    <p>Bereits registriert? <a href="login.jsp">Hier einloggen</a></p>
</div>
</body>
</html>


