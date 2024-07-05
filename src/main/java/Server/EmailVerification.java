package Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EmailVerification {
    private final String FILE_PATH = "src/main/java/Server/.EmailData.txt";
    private String email;
    private String password;

    public EmailVerification() {
        readConfigFile();
    }

    public void readConfigFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length >= 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (key.equals("email")) {
                        this.email = value;
                    } else if (key.equals("password")) {
                        this.password = value;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
