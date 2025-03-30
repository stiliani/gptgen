<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Automatische Code-Generierung mit GPT</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            color: #333;
            margin: 0;
            padding: 0;
        }
        h1 {
            color: #0056b3;
            text-align: center;
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
        textarea {
            width: 100%;
            height: 200px;
            padding: 10px;
            margin: 10px 0;
            border-radius: 5px;
            border: 1px solid #ccc;
            font-family: monospace;
            background-color: #f5f5f5;
            resize: vertical;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }
        #response {
            margin-top: 20px;
            padding: 10px;
            background-color: #e7f7e7;
            border: 1px solid #d1f1d1;
            border-radius: 5px;
        }
        pre {
            background-color: #2e2e2e;
            color: #f8f8f2;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            white-space: pre-wrap;
            min-height: 200px;
            max-height: 400px;
            position: relative;
        }
        code {
            font-family: monospace;
        }
        .copy-btn {
            background-color: #007bff;
            color: white;
            padding: 5px 10px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 10px;
        }
        .copy-btn:hover {
            background-color: #0056b3;
        }
        #compilation-result {
            margin-top: 20px;
            padding: 10px;
            background-color: #f7f7f7;
            border: 1px solid #ccc;
            border-radius: 5px;
            display: none; /* Zunächst nicht anzeigen */
        }

        #code-review-result {
            margin-top: 20px;
            padding: 10px;
            background-color: #f7f7f7;
            border: 1px solid #ccc;
            border-radius: 5px;
            display: none; /* Zunächst nicht anzeigen */
        }

        #review-result {
            margin-top: 20px;
            padding: 10px;
            background-color: #f7f7f7;
            border: 1px solid #ccc;
            border-radius: 5px;
            display: none; /* Zunächst nicht anzeigen */
        }
    </style>
</head>

<body>

<div id="nav">
    <%
        HttpSession loginsession = request.getSession(false);
        if (loginsession != null && loginsession.getAttribute("username") != null) {
            String username = (String) loginsession.getAttribute("username");
    %>
    <span>Hallo, <%= username %>!</span>
    <a href="konto.jsp">Konto</a> | <a href="logout">Abmelden</a>
    <%
    } else {
    %>
    <a href="login.jsp">Anmelden</a> | <a href="register.jsp">Registrieren</a>
    <%
        }
    %>
</div>

<h1>ChatGPT - Anfrage</h1>

<div id="container">
    <form action="chat" method="POST">
        <select name="model">
            <option value="gpt-4" <%= "gpt-4".equals(request.getParameter("model")) ? "selected" : "" %>>GPT-4</option>
            <option value="gpt-3.5-turbo" <%= "gpt-3.5-turbo".equals(request.getParameter("model")) ? "selected" : "" %>>GPT-3.5-Turbo</option>
            <option value="gpt-3.5-turbo-16k" <%= "gpt-3.5-turbo-16k".equals(request.getParameter("model")) ? "selected" : "" %>>GPT-3.5-Turbo-16k</option>
        </select><br>
        <textarea name="message" placeholder="Gib deine Frage an ChatGPT ein..."><%= request.getParameter("message") != null ? request.getParameter("message") : "" %></textarea><br>
        <button type="submit">Senden</button>
    </form>

    <%
        String responseMessage = (String) request.getAttribute("responseMessage");
        if (responseMessage != null) {
            if (responseMessage.contains("```java")) {
                String[] parts = responseMessage.split("```java");
                for (String part : parts) {
                    if (part.contains("```")) {
                        String code = part.split("```")[0].trim();
                        String textAfterCode = part.split("```")[1].trim();
                        out.println("<div id='response'>");
                        out.println("<button onclick='compileCode()'>Code Kompilieren</button>");
                        out.println("<button onclick='reviewCode()'>Code Review</button>");
                        out.println("<pre contenteditable='true' id='editable-code'>" + code + "</pre>");
                        out.println("<button class='copy-btn' onclick='copyCode()'>Copy</button>");
                        out.println(textAfterCode);
                        out.println("</div>");
                    }
                }
            } else {
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

<script>
    function copyCode() {
        const codeText = document.getElementById("editable-code").innerText;
        navigator.clipboard.writeText(codeText).then(() => {
            alert("Code wurde in die Zwischenablage kopiert!");
        }).catch(err => {
            console.error("Fehler beim Kopieren", err);
        });
    }

    function compileCode() {
        const codeText = document.getElementById("editable-code").innerText;

        fetch('compile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ code: codeText })
        })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById("compilation-result");
                resultDiv.style.display = "block";
                resultDiv.innerHTML = "<strong>Kompilierungsergebnis:</strong><br>" + data.message;
            })
            .catch((error) => {
                console.error('Fehler beim Kompilieren:', error);
            });
    }

    function reviewCode() {
        const contentDiv = document.getElementById("code-review-content");
        contentDiv.innerHTML = "Code wird analysiert..."; // Platzhaltertext setzen
        const codeText = document.getElementById("editable-code").innerText;

        fetch('reviewCode?code=' + encodeURIComponent(codeText), {
            method: 'POST'
        })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById("code-review-result");

                resultDiv.style.display = "block";

                let codeMatch = data.match(/`java([\s\S]*?)`/);
                let codeBlock = codeMatch ? codeMatch[1].trim() : null;

                let reviewText = data.replace(/`java[\s\S]*?`/, '').trim();
                let formattedReview = reviewText.replace(/(\d+\.)/g, '<br>$1');

                contentDiv.innerHTML = formattedReview; // Ergebnis von ChatGPT anzeigen

                if (codeBlock) {
                    let codeContainer = document.getElementById("separate-code");
                    if (!codeContainer) {
                        codeContainer = document.createElement("div");
                        codeContainer.id = "separate-code";
                        codeContainer.innerHTML = "<strong><br>Überarbeiteter Code:</strong><br><pre contenteditable='true' id='editable-code'>" + codeBlock + "</pre>";
                        resultDiv.appendChild(codeContainer);
                    } else {
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
</script>

</body>
</html>



