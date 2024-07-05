package Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

public class EmailVerification {
    private final String FILE_PATH = "src/main/java/Server/.EmailData.txt";
    private String senderEmail;
    private String password;
    private final String recipientsEmail;
    private final String token;

    public EmailVerification(String recipientsEmail) {
        this.recipientsEmail = recipientsEmail;
        this.token = verificationToken();
        readConfigFile();
    }

    private void readConfigFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length >= 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (key.equals("email")) {
                        this.senderEmail= value;
                    } else if (key.equals("password")) {
                        this.password = value;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String verificationToken() {
        return UUID.randomUUID().toString();
    }
}
