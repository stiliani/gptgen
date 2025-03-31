<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <style>
        /* General body styling */
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #a8c0ff, #3f2b96); /* Gradient background */
            color: #fff;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            justify-content: center; /* Centers the container vertically */
            align-items: center; /* Centers the container horizontally */
        }

        /* Container for the login form */
        .container {
            width: 70%;
            max-width: 400px;
            margin: 30px auto;
            padding: 30px;
            background-color: rgba(255, 255, 255, 0.1); /* Semi-transparent background */
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); /* Shadow effect */
            backdrop-filter: blur(10px); /* Background blur effect */
            text-align: center; /* Centers the content within the container */
        }

        /* Header styling */
        h2 {
            color: #fff;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3); /* Shadow for the header */
        }

        /* Styling for input fields and buttons */
        input, button {
            width: calc(100% - 22px); /* Ensures inputs and buttons fill the width of the container */
            padding: 10px;
            margin: 10px 0;
            border-radius: 8px;
            border: 1px solid rgba(255, 255, 255, 0.3); /* Border styling */
            background-color: rgba(255, 255, 255, 0.1); /* Semi-transparent background */
            color: #fff; /* White text color */
        }

        /* Focus effect for input fields */
        input:focus {
            outline-color: #8D8FDC; /* Outline color when focused */
        }

        /* Button styling */
        button {
            background-color: transparent;
            color: #fff;
            padding: 10px 20px;
            border: 1px solid white;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s ease, border-color 0.3s ease; /* Smooth transition for hover effect */
        }

        /* Button hover effect */
        button:hover {
            background-color: rgba(255, 255, 255, 0.1); /* Background color on hover */
            border-color: rgba(255, 255, 255, 0.5); /* Border color on hover */
        }

        /* Error message styling */
        .error {
            color: red;
            margin-top: 10px;
        }

        /* Link styling */
        a {
            color: #fff;
            text-decoration: underline; /* Underline for links */
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Login</h2>

        <%
            // Display login error message if there's one
            String loginError = (String) request.getAttribute("loginError");
        %>

        <% if (loginError != null) { %>
        <p class="error"><%= loginError %></p>
        <% } %>

        <!-- Login form -->
        <form action="login" method="post">
            <input type="email" name="email" placeholder="E-Mail" required>
            <input type="password" name="password" placeholder="Passwort" required>
            <button type="submit">Login</button>
        </form>
        <p>Noch keinen Account? <a href="register.jsp">Jetzt registrieren</a></p>
    </div>
</body>
</html>
