package Server;


import com.google.zxing.WriterException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.io.IOException;

public class TwoFactorAuthentication {

    private GoogleAuthenticator googleAuthenticator;
    private String secretKey;
    private final String APP_NAME = "MemoliYT";
    private String email;
    private int code;

    public TwoFactorAuthentication(String email) throws IOException, WriterException {
        googleAuthenticator = new GoogleAuthenticator();
        generateSecretKey();
        this.email = email;
    }

    public TwoFactorAuthentication(String secretKey , int code) {
        googleAuthenticator = new GoogleAuthenticator();
        //TODO : Get secret key from database manager
        this.secretKey = secretKey;
        this.code = code;
    }


    public void generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        this.secretKey = key.getKey();
    }

    public boolean verifyCode() {
        return googleAuthenticator.authorize(this.secretKey, this.code);
    }

    public String generateQrCodeData() {
        return "otpauth://totp/" + this.APP_NAME + ":" + this.email + "?secret=" + this.secretKey + "&issuer=" + this.APP_NAME;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
