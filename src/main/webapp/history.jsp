<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>History of Prompts</title>
  <style>

    /* General page style */
    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: linear-gradient(135deg, #a8c0ff, #3f2b96);
      color: #fff;
      margin: 0;
      padding: 0;
      text-align: center;
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    /* Navigation bar style */
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

    /* Main heading style */
    h1 {
      font-size: 32px;
      margin-bottom: 20px;
      text-align: center;
    }

    /* Button style */
    button {
      padding: 12px 24px;
      background-color: #8D8FDC;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      margin-bottom: 25px;
      transition: background-color 0.3s ease;
      font-size: 16px;
    }

    button:hover {
      background-color: #7274c1; /* Button hover color */
    }

    /* Container for history list */
    .container {
      max-width: 80%;
      margin: 30px auto;
      background: rgba(255, 255, 255, 0.1);
      padding: 30px;
      border-radius: 10px;
      box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
      display: flex;
      flex-direction: column;
      justify-content: center;
    }

    /* History table style */
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
      background: rgba(255, 255, 255, 0.1);
      color: #ddd;
      border-radius: 10px;
      overflow: hidden;
    }

    /* Table header and cell styles */
    table td:nth-child(1) {
      color: black;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    th, td {
      padding: 14px;
      text-align: left;
      border-bottom: 1px solid rgba(255,255,255,0.3);
    }

    td {
      vertical-align: top;
    }

    th {
      background-color: #8D8FDC;
      color: white;
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    /* Zebra striping for rows */
    tr:nth-child(even) {
      background-color: rgba(255, 255, 255, 0.05);
    }

    tr:nth-child(odd) {
      background-color: rgba(255, 255, 255, 0.1);
    }

    /* Message style for no entries */
    .message {
      font-size: 18px;
      font-weight: bold;
      padding: 15px;
      background: rgba(255, 255, 255, 0.3);
      border-radius: 8px;
      display: inline-block;
      margin-top: 30px;
    }

    /* Style for displaying responses */
    .response-container {
      background-color: #686A97;
      padding: 12px;
      border-radius: 8px;
      border: 1px solid rgba(255, 255, 255, 0.3); /* Light, transparent border */
      font-size: 14px;
      line-height: 1.6;
      color: #fff;
      white-space: pre-wrap;
      overflow-x: auto;
      max-width: 970px;
      word-wrap: break-word;
      margin: 0 auto;
    }

    /* Style for preformatted text and code blocks */
    pre {
      background-color: #686A97;
      color: #fff;
      padding: 10px;
      border-radius: 5px;
      overflow-x: auto;
      margin: 0;
      font-size: 13px;
    }

    code {
      font-family: "Courier New", monospace;
      font-size: 14px;
      color: #fff;
    }

  </style>
</head>
<body>
  <!-- Navigation bar -->
  <div id="nav">
    <%
      // Check if a session exists and if a user is logged in
      HttpSession loginsession = request.getSession(false);
      if (loginsession != null && loginsession.getAttribute("username") != null) {
        String username = (String) loginsession.getAttribute("username");
    %>
    <span>Hallo, <%= username %>!</span>  <!-- Output of the logged-in user -->
    <a href="index.jsp">Home</a> | <a href="konto.jsp">Konto</a> | <a href="logout">Abmelden</a>
    <%
      }
    %>
  </div>

  <h1>JavaGen - History of Prompts</h1>

  <div class="container">
    <!-- Check if there is a list of history -->
    <c:if test="${not empty historyList}">
      <table>
        <thead>
        <tr>
          <th>Prompt</th>
          <th>Response</th>
          <th>Aktion</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="history" items="${historyList}">
          <tr>
            <td>${history.prompt}</td>
            <td>
              <div class="response-container">
                  <%-- Remove markdown code blocks from the response --%>
                <c:set var="cleanResponse" value="${fn:replace(fn:replace(history.response, '```java', ''), '```', '')}" />
                <c:choose>
                  <c:when test="${fn:contains(history.response, '```java')}">
                    <%-- If the response contains Java code, format it as a code block --%>
                    <pre><code class="java">${fn:escapeXml(cleanResponse)}</code></pre>
                  </c:when>
                  <c:otherwise>
                    <%-- If it is not Java code, display it as normal text --%>
                    <p>${fn:escapeXml(cleanResponse)}</p>
                  </c:otherwise>
                </c:choose>
              </div>
            </td>
            <td class="action-column">
              <!-- Delete button with confirmation prompt -->
              <button onclick="confirmDelete(${history.id}, event)">Löschen</button>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:if>
  </div>

    <!-- Display message if no entries are found -->
    <c:if test="${empty historyList}">
      <p class="message">No history entries found for this user.</p>
    </c:if>
  </div>

  <script>

    // Show a confirmation prompt before deleting an entry
    function confirmDelete(promptid, event) {
      if (confirm('Wirklich löschen?')) {
        deleteHistory(promptid, event);
      }
    }

    // Sends a DELETE request to the server to remove an entry
    function deleteHistory(promptid, event) {
      event.preventDefault();
      fetch('history?promptid=' + promptid, {
        method: 'DELETE',
      })
              .then(response => {
                if (response.ok) {
                  // Remove the row from the table
                  event.target.closest('tr').remove();
                  console.log('History entry deleted successfully.');
                  location.reload();
                } else {
                  console.error('Failed to delete history entry.');
                }
              })
              .catch(error => {
                console.error('Error:', error);
              });
    }
  </script>
</body>
</html>
