package in.ashokit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetApiExample {
    public static String callAPI() {
        StringBuilder response = new StringBuilder();
        int responseCode = 0;
        try {
            // Specify the URL of the API you want to call
            String apiUrl = "https://dummyjson.com/products/1";

            // Create a URL object with the API URL
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            responseCode = connection.getResponseCode();
//            System.out.println("Response Code: " + responseCode);

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;


            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Close the BufferedReader
            reader.close();

            // Print the response from the API
//            System.out.println("Response: " + response.toString());

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return responseCode + "";
    }
}
