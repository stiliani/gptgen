<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Automatische Code-Generierung mit GPT</title>
    <style>
        /* Basic styling for the page */
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #a8c0ff, #3f2b96); /* Smooth blue gradient */
            color: #fff;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        /* Main title styling */
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

        /* Main content container */
        #container {
            width: 70%;
            max-width: 800px;
            margin: 30px auto;
            padding: 30px;
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(10px); /* Blurred background effect */
            flex: 1;
        }

        /* Styling for input fields */
        textarea, select {
            width: calc(100% - 22px);
            padding: 10px;
            margin: 10px 0;
            border-radius: 8px;
            border: 1px solid rgba(255, 255, 255, 0.3);
            background-color: rgba(255, 255, 255, 0.1);
            color: #fff;
            font-size: 16px;
            resize: vertical;
        }

        /* Focus style for textarea */
        textarea:focus {
            outline-color: white;
            outline-style: solid;
            outline-width: 2px;
        }

        /* Response area styling */
        #response {
            margin-top: 20px;
            padding: 20px;
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            border: 1px solid rgba(255, 255, 255, 0.3);
            color: #fff;
        }

        /* Compilation and code review results (hidden by default) */
        #compilation-result, #code-review-result {
            margin-top: 20px;
            padding: 20px;
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            border: 1px solid rgba(255, 255, 255, 0.3);
            color: #fff;
            display: none; /* Fields are hidden by default */
        }

        /* Style for preformatted text blocks */
        pre {
            background-color: rgba(0, 0, 0, 0.2);
            padding: 15px;
            border-radius: 8px;
            overflow-x: auto;
            white-space: pre-wrap;
            min-height: 200px;
            max-height: 400px;
            border: 1px solid transparent; /* Transparent border by default */
        }

        /* Focus style for pre element */
        pre:focus {
            border-color: white;
            outline: none;
        }

        /* Form styling */
        form {
            display: flex;
            flex-direction: column; /* Arrange elements vertically */
        }

        /* Button container styling */
        form > div {
            display: flex; /* Align buttons horizontally */
            justify-content: flex-start; /* Align buttons to the left */
            margin-top: 10px;
        }

        /* Button styling */
        button {
            background-color: transparent;
            color: #fff;
            padding: 8px 12px;
            border: 1px solid white;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s ease, border-color 0.3s ease;
            margin: 5px;
            display: inline-block;
            width: 130px;
            text-align: center;
        }

        /* Button hover effect */
        button:hover {
            background-color: rgba(255, 255, 255, 0.1);
            border-color: rgba(255, 255, 255, 0.5);
        }

        /* Copy button styling */
        .copy-btn {
            background-color: #8D8FDC;
            color: #fff;
            padding: 8px 15px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            position: absolute; /* Absolute positioning inside pre element */
            top: 360px; /* Adjust distance from top */
            right: 70px; /* Adjust distance from right */
            width: auto;
        }

        /* Copy button hover effect */
        .copy-btn:hover {
            border: white;
        }

        /* Display history button styling */
        #displayHistoryButton {
            position: absolute;
            top: 210px;
            left: 180px;
            background-color: transparent;
            color: #fff;
            padding: 8px 12px;
            border: 1px solid white;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s ease, border-color 0.3s ease;
            margin: 5px;
            display: inline-block;
            width: 130px;
            text-align: center;
        }

        /* Select dropdown styling */
        select {
            width: 100%; /* Match width of textarea */
            padding: 10px;
            margin: 10px 0;
            border-radius: 8px;
            border: 1px solid rgba(255, 255, 255, 0.3);
            background: linear-gradient(135deg, #a8c0ff, #3f2b96);
            color: #fff;
            font-size: 16px;
            appearance: none;
            background-image: url('images/selectpic.png');
            background-repeat: no-repeat;
            background-position: right 10px center;
            background-size: 30px;
        }

        /* Select focus style */
        select:focus {
            border-color: rgba(255, 255, 255, 0.5);
            outline: none;
        }

        select option {
            background-color: #8D8FDC;
            color: #fff;
        }

        /* Form submission actions */
        form[action="submit"],
        form[action="history"] {
            display: inline-flex; /* Flexbox for button arrangement */
            margin-right: 10px; /* Optional margin between forms */
        }

        footer {
            background-color: rgba(0, 0, 0, 0.2);
            padding: 10px 20px;
            text-align: center;
            color: white;
            font-size: 12px;
        }

        footer a {
            color: lightblue;
        }
    </style>
</head>
<body>
<!-- Navigation bar -->
    <div id="nav">
        <%
            // Check if a session exists and if the "username" attribute is present in the session
            HttpSession loginsession = request.getSession(false);
            if (loginsession != null && loginsession.getAttribute("username") != null) {
                String username = (String) loginsession.getAttribute("username");
        %>
        <!-- Display the username and provide links for account and logout -->
        <span>Hallo, <%= username %>!</span>
        <!-- If username, show konto and logout links -->
        <a href="konto.jsp">Konto</a> | <a href="logout">Abmelden</a>
        <%
        } else {
        %>
        <!-- If no session or username, show login and registration links -->
        <a href="login.jsp">Anmelden</a> | <a href="register.jsp">Registrieren</a>
        <%
            }
        %>
    </div>

    <h1>JavaGen</h1>
    <div id="container">
        <%
            // Check if the user is logged in and get the username if available
            String username = null;
            if (loginsession != null && loginsession.getAttribute("username") != null) {
                username = (String) loginsession.getAttribute("username");
            }
        %>

        <!-- Form for submitting a question to ChatGPT -->
        <form action="chat" method="POST">
            <input type="hidden" name="action" value="generate">
            <select name="model">
                <!-- Dynamically select the model based on the user's choice -->
                <option value="gpt-4" <%= "gpt-4".equals(request.getParameter("model")) ? "selected" : "" %>>GPT-4</option>
                <option value="gpt-3.5-turbo" <%= "gpt-3.5-turbo".equals(request.getParameter("model")) ? "selected" : "" %>>GPT-3.5-Turbo</option>
                <option value="gpt-3.5-turbo-16k" <%= "gpt-3.5-turbo-16k".equals(request.getParameter("model")) ? "selected" : "" %>>GPT-3.5-Turbo-16k</option>
            </select><br>
            <!-- Text area for user input -->
            <textarea name="message" placeholder="Gib deine Frage an ChatGPT ein..."><%= request.getParameter("message") != null ? request.getParameter("message") : "" %></textarea><br>
            <button type="submit">Senden</button>
        </form>
        <%
            // If the user is logged in, show a button to display the chat history
            if (username != null) {
        %>
        <form action="history" method="GET">
            <input type="hidden" name="username" value="<%= username %>">
            <button type="submit" id="displayHistoryButton">Display History</button>
        </form>
        <% } %>

        <%
            // Handle the response from the backend (ChatGPT response)
            String responseMessage = (String) request.getAttribute("responseMessage");
            if (responseMessage != null) {
                // If the response contains Java code (formatted in markdown), split and display accordingly
                if (responseMessage.contains("```java")) {
                    String[] parts = responseMessage.split("```java");
                    for (String part : parts) {
                        if (part.contains("```")) {
                            String code = part.split("```")[0].trim();
                            String textAfterCode = part.split("```")[1].trim();
                            out.println("<div id='response'>");

                            // Provide buttons for compiling, reviewing, and saving the code
                            out.println("<button class='btn' onclick='compileCode()'>Kompilieren</button>");
                            out.println("<button class='btn' onclick='reviewCode()'>Code Review</button>");
                            out.println("<button class='btn' onclick='saveResponse()'>Speichern</button>");

                            // Display the code in a contenteditable area for user interaction
                            out.println("<pre contenteditable='true' id='editable-code'>" + code + "</pre>");
                            out.println("<button class='copy-btn' onclick='copyCode()'>Copy</button>");
                            out.println(textAfterCode);
                            out.println("</div>");
                        }
                    }
                } else {
                    // If no code, just display the response as is
                    out.println("<div id='response'>" + responseMessage + "</div>");
                }
            }
        %>

    <div id="compilation-result"></div>

    <div id="code-review-result">
        <strong>Code-Analyse und Verbesserungsvorschläge:</strong><br>
        <div id="code-review-content">Code wird analysiert...</div>
    </div>
</div>

<footer>
    <p>&copy; 2025 Cynthia Blank | Icon von <a href="https://de.freepik.com/icon/wasser_7540293">bearicons</a> auf Freepik</p>
</footer>

<script>
    // Function to copy code to clipboard
    function copyCode() {
        // Get the text content of the element with id "editable-code"
        const codeText = document.getElementById("editable-code").innerText;

        // Create a temporary textarea element to hold the code text
        const tempInput = document.createElement("textarea");
        tempInput.value = codeText;  // Set the value of the textarea to the code text
        document.body.appendChild(tempInput);  // Append the textarea to the document body

        tempInput.select();

        // Execute the copy command to copy the selected text to clipboard
        document.execCommand("copy");

        // Remove the temporary textarea element from the document
        document.body.removeChild(tempInput);

        // Show an alert confirming the code has been copied to the clipboard
        alert("Code wurde in die Zwischenablage kopiert!");
    }

    // Function to compile the code
    function compileCode() {
        const codeText = document.getElementById("editable-code").innerText;

        // Send a POST request to the 'compile' endpoint with the code as JSON
        fetch('compile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ code: codeText })
        })
            .then(response => response.json())  // Parse the response as JSON
            .then(data => {
                // Get the div with id "compilation-result" to display the compilation result
                const resultDiv = document.getElementById("compilation-result");
                resultDiv.style.display = "block";
                resultDiv.innerHTML = "<strong>Kompilierungsergebnis:</strong><br>" + data.message;
            })
            .catch((error) => {
                console.error('Fehler beim Kompilieren:', error);
            });
    }

    // Function to review and analyze the code
    function reviewCode() {
        // Get the content div where the review will be displayed
        const contentDiv = document.getElementById("code-review-content");
        contentDiv.innerHTML = "Code wird analysiert..."; // Platzhaltertext setzen
        const codeText = document.getElementById("editable-code").innerText;

        const modelSelect = document.querySelector('select[name="model"]');
        const selectedModel = modelSelect.value;

        // Send a POST request to the 'reviewCode' endpoint with the code as a query parameter
        fetch('reviewCode', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded', // Change to form url encoded
            },
            body: 'code=' + encodeURIComponent(codeText) + '&model=' + encodeURIComponent(selectedModel)
        })
            .then(response => response.json())
            .then(data => {
                // Get the div where the code review result will be displayed
                const resultDiv = document.getElementById("code-review-result");
                resultDiv.style.display = "block";

                // Extract the code block from the response using regex
                let codeMatch = data.match(/`java([\s\S]*?)`/);
                let codeBlock = codeMatch ? codeMatch[1].trim() : null;

                // Clean up the review text and format it
                let reviewText = data.replace(/`java[\s\S]*?`/, '').trim();
                let formattedReview = reviewText.replace(/(\d+\.)/g, '<br>$1');

                contentDiv.innerHTML = formattedReview; // Ergebnis von ChatGPT anzeigen

                // If a code block was found, display it in a separate section
                if (codeBlock) {
                    let codeContainer = document.getElementById("separate-code");
                    if (!codeContainer) {
                        // If the code container doesn't exist, create it
                        codeContainer = document.createElement("div");
                        codeContainer.id = "separate-code";
                        codeContainer.innerHTML = "<strong><br>Überarbeiteter Code:</strong><br><pre contenteditable='true' id='editable-code'>" + codeBlock + "</pre>";
                        resultDiv.appendChild(codeContainer);
                    } else {
                        // If the code container exists, update its content
                        codeContainer.innerHTML = "<strong><br>Überarbeiteter Code:</strong><br><pre contenteditable='true' id='editable-code'>" + codeBlock + "</pre>";
                    }
                }

                if (data.includes("Fehler:")) {
                    console.error("Fehler von OpenAI: " + data);
                }
            })
            .catch((error) => {
                contentDiv.innerHTML = "Fehler bei der Code-Analyse."; // Fehlermeldung anzeigen
                console.error('Fehler bei der Code-Analyse:', error);
            });
    }

    // Function to save the reviewed or generated code as a text file
    function saveResponse() {
        const codeText = document.getElementById("editable-code").innerText;
        const data = codeText; // Nur den Code speichern

        // Create a Blob containing the code text
        const blob = new Blob([data], { type: 'text/plain' });
        const link = document.createElement('a');

        // Create a link element to trigger the file download
        link.href = URL.createObjectURL(blob); // Create a URL for the Blob
        link.download = 'generated_code.txt'; // Set the file name for the download
        link.click(); // Trigger the file download
    }
</script>
</body>
</html>



