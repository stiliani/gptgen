<%--
  Created by IntelliJ IDEA.
  User: Cynthia
  Date: 24.03.2025
  Time: 16:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="jakarta.servlet.http.*, jakarta.servlet.*, jakarta.servlet.annotation.*, java.sql.*" %>
<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Konto - Benutzerverwaltung</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f0f0f0;
      color: #333;
      margin: 0;
      padding: 0;
    }
    #nav {
      background-color: #0056b3;
      padding: 10px;
      text-align: right;
    }
    #nav a {
      color: white;
      text-decoration: none;
      margin: 0 15px;
      font-size: 16px;
      font-weight: bold;
    }
    #nav a:hover {
      text-decoration: underline;
    }
    #container {
      width: 60%;
      margin: 20px auto;
      padding: 20px;
      background-color: white;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }
    h1 {
      color: #0056b3;
      text-align: center;
    }
    input[type="text"], input[type="email"], input[type="password"] {
      width: 100%;
      padding: 10px;
      margin: 10px 0;
      border-radius: 5px;
      border: 1px solid #ccc;
    }
    button {
      background-color: #4CAF50;
      color: white;
      padding: 10px 20px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      width: 100%;
    }
    button:hover {
      background-color: #45a049;
    }
    .btn-danger {
      background-color: #ff4444;
    }
    .btn-danger:hover {
      background-color: #ff0000;
    }
    .confirmation-modal {
      display: none;
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.7);
      justify-content: center;
      align-items: center;
      z-index: 999;
    }
    .confirmation-modal .modal-content {
      background-color: white;
      padding: 20px;
      border-radius: 10px;
      width: 400px;
    }
    .confirmation-modal button {
      width: 48%;
      margin: 10px 1%;
    }
  </style>
</head>
<body>

<div id="nav">
  <a href="index.jsp">Startseite</a>
  <a href="logout">Abmelden</a>
</div>

<h1>Mein Konto</h1>

<div id="container">
  <form action="updateAccount" method="POST">
    <label for="username">Benutzername</label>
    <input type="text" id="username" name="username" value="<%= session.getAttribute("username") %>" disabled>

    <label for="email">E-Mail</label>
    <input type="email" id="email" name="email" value="<%= session.getAttribute("email") %>" required>

    <label for="oldPassword">Altes Passwort</label>
    <input type="password" id="oldPassword" name="oldPassword" required>

    <label for="newPassword">Neues Passwort</label>
    <input type="password" id="newPassword" name="newPassword" required>

    <label for="confirmPassword">Neues Passwort wiederholen</label>
    <input type="password" id="confirmPassword" name="confirmPassword" required>

    <button type="submit">Daten speichern</button>
  </form>

  <form action="deleteAccount" method="POST">
    <button type="submit" class="btn-danger" onclick="showConfirmationModal(event)">Account löschen</button>
  </form>
</div>

<div id="confirmationModal" class="confirmation-modal">
  <div class="modal-content">
    <h2>Sind Sie sicher?</h2>
    <p>Ihr Konto wird dauerhaft gelöscht.</p>
    <button onclick="confirmDeletion()">Ja, löschen</button>
    <button onclick="cancelDeletion()">Abbrechen</button>
  </div>
</div>

<script>
  // Modal anzeigen, wenn der Benutzer auf "Account löschen" klickt
  function showConfirmationModal(event) {
    event.preventDefault();
    document.getElementById('confirmationModal').style.display = 'flex';
  }

  // Bestätigung des Löschvorgangs
  function confirmDeletion() {
    document.querySelector('form[action="deleteAccount"]').submit();
  }

  // Abbrechen des Löschvorgangs
  function cancelDeletion() {
    document.getElementById('confirmationModal').style.display = 'none';
  }
</script>

</body>
</html>

