<%--
  Created by IntelliJ IDEA.
  User: Cynthia
  Date: 24.03.2025
  Time: 15:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }
        .container { width: 300px; margin: auto; padding: 20px; background: white; border-radius: 5px; }
        input, button { width: 100%; margin: 5px 0; padding: 8px; }
        .error { color: red; }
    </style>
</head>
<body>
<div class="container">
    <h2>Login</h2>
    <% String loginError = (String) request.getAttribute("loginError"); %>
    <% if (loginError != null) { %>
    <p class="error"><%= loginError %></p>
    <% } %>

    <form action="login" method="post">
        <input type="email" name="email" placeholder="E-Mail" required>
        <input type="password" name="password" placeholder="Passwort" required>
        <button type="submit">Login</button>
    </form>
    <p>Noch keinen Account? <a href="register.jsp">Jetzt registrieren</a></p>
</div>
</body>
</html>
