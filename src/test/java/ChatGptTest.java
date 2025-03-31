import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class contains tests to check the connectivity and interaction with the OpenAI ChatGPT API.
 */
public class ChatGptTest {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SECRET_KEY = ""; // Insert API Key here

    /**
     * Test to verify the connection to the OpenAI API and validate the response.
     */
    @Test
    public void testConnection() {
        try {
            // Creating the URL object for the API endpoint
            URL url = new URL(API_URL);

            // Opening the connection to the API
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + SECRET_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON body for the request
            String jsonBody = """
                {
                  "model": "gpt-4",
                  "messages": [
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": "Hello, how are you?"}
                  ]
                }
                """;

            // Sending the request body to the API
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code from the API
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Assert the response code is 200 (OK) for a successful request
            assertTrue(responseCode == 200, "Verbindung zu ChatGPT war erfolgreich");

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false, "Verbindung zu ChatGPT fehlgeschlagen: " + e.getMessage());
        }
    }
}
