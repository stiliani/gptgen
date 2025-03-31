<%@ page import="jakarta.servlet.http.*, jakarta.servlet.*, jakarta.servlet.annotation.*, java.sql.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Konto - Benutzerverwaltung</title>
  <style>
    /* General body styling */
    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: linear-gradient(135deg, #a8c0ff, #3f2b96);
      color: #fff;
      margin: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    /* Header styling */
    h1 {
      text-align: center;
      margin: 20px 0;
      color: #fff;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
    }

    /* Navigation bar styling */
    #nav {
      background-color: rgba(0, 0, 0, 0.2);
      padding: 10px 20px;
      text-align: right;
    }

    #nav a {
      color: #fff;
      text-decoration: none;
      margin: 0 15px;
      font-weight: 600;
      transition: color 0.3s ease;
    }

    #nav a:hover {
      color: #ddd;
    }

    /* Container for account settings */
    #container {
      width: 70%;
      max-width: 800px;
      margin: 30px auto;
      padding: 30px;
      background-color: rgba(255, 255, 255, 0.1);
      border-radius: 15px;
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
      backdrop-filter: blur(10px);
    }

    /* Input fields styling */
    input[type="text"], input[type="email"], input[type="password"] {
      width: calc(100% - 22px);
      padding: 10px;
      margin: 10px 0;
      border-radius: 8px;
      border: 1px solid rgba(255, 255, 255, 0.3);
      font-size: 16px;
    }

    #oldPassword {
      background-color: rgba(255, 255, 255, 0.05);
    }

    input:focus {
      outline-color: #8D8FDC;
    }

    /* Button styling */
    button {
      background-color: transparent;
      color: #fff;
      padding: 10px;
      border: 1px solid white;
      border-radius: 8px;
      cursor: pointer;
      font-size: 16px;
      transition: background-color 0.3s ease, border-color 0.3s ease;
      margin: 10px 0;
      display: block;
      width: calc(100% - 2px);
      text-align: center;
    }

    button:hover {
      background-color: rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.5);
    }

    /* Specific style for the "Delete Account" button */
    .btn-danger {
      background-color: #686A97;
    }

    /* Modal confirmation dialog styling */
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
      background-color: rgba(255, 255, 255, 0.1);
      padding: 20px;
      border-radius: 10px;
      width: 400px;
      color: #fff;
    }

    /* Button styling within the modal */
    .confirmation-modal button {
      width: 48%;
      margin: 10px 1%;
      background-color: transparent;
      color: #fff;
      padding: 8px 12px;
      border: 1px solid white;
      border-radius: 8px;
      cursor: pointer;
      font-size: 14px;
      transition: background-color 0.3s ease, border-color 0.3s ease;
    }

    .confirmation-modal button:hover {
      background-color: rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.5);
    }
  </style>
</head>
<body>
  <!-- Navigation bar -->
  <div id="nav">
    <a href="index.jsp">Startseite</a>
    <a href="logout">Abmelden</a>
  </div>

  <h1>Mein Konto</h1>

  <!-- Main container for the account form -->
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

    <!-- Form to delete user account -->
    <form action="deleteAccount" method="POST">
      <button type="submit" class="btn-danger" onclick="showConfirmationModal(event)">Account löschen</button>
    </form>
  </div>

  <!-- Confirmation modal for account deletion -->
  <div id="confirmationModal" class="confirmation-modal">
    <div class="modal-content">
      <h2>Sind Sie sicher?</h2>
      <p>Ihr Konto wird dauerhaft gelöscht.</p>
      <button onclick="confirmDeletion()">Ja, löschen</button>
      <button onclick="cancelDeletion()">Abbrechen</button>
    </div>
  </div>

<script>
  // Show confirmation modal when user clicks "Delete Account"
  function showConfirmationModal(event) {
    event.preventDefault();  // Prevent form submission
    document.getElementById('confirmationModal').style.display = 'flex';
  }

  // Confirm account deletion
  function confirmDeletion() {
    document.querySelector('form[action="deleteAccount"]').submit();  // Submit the form to delete the account
  }

  // Cancel account deletion
  function cancelDeletion() {
    document.getElementById('confirmationModal').style.display = 'none';
  }
</script>
</body>
</html>

