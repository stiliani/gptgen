import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatGptTest {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SECRET_KEY = "";

    @Test
    public void testConnection() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + SECRET_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON-Body für die Anfrage
            String jsonBody = """
                {
                  "model": "gpt-4",
                  "messages": [
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": "Hello, how are you?"}
                  ]
                }
                """;

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            assertTrue(responseCode == 200, "Verbindung zu ChatGPT war erfolgreich");

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false, "Verbindung zu ChatGPT fehlgeschlagen: " + e.getMessage());
        }
    }
}
