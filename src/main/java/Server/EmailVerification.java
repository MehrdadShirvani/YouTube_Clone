package Server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
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


    public void sendVerificationEmail() throws MessagingException {
        Properties prop = new Properties();

        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail , password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(this.senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.recipientsEmail));
        message.setSubject("Email Verification");

        String content = "<p>Dear User,</p>"
                + "<p>This is your token for verifying your email:</p>"
                + "<p><strong>Verification Token: " + token + "</strong></p>"
                + "<p>Thank you,<br>Memoli</p>";

        message.setContent(content, "text/html");

        Transport.send(message);
    }
}
