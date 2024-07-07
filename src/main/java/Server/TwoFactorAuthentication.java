package Server;


import com.google.zxing.WriterException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.io.IOException;

public class twoFactorAuthentication {

    private GoogleAuthenticator googleAuthenticator;
    private String secretKey;
    private final String APP_NAME = "MemoliYT";
    private String email;

    public twoFactorAuthentication(String email) throws IOException, WriterException {
        googleAuthenticator = new GoogleAuthenticator();
        generateSecretKey();
        this.email = email;
    }

    public twoFactorAuthentication(String secretKey , String email) {
        googleAuthenticator = new GoogleAuthenticator();
        //TODO : Get secret key from database manager
        this.secretKey = secretKey;
    }


    public void generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        this.secretKey = key.getKey();
    }

    public boolean verifyCode(int code) {
        return googleAuthenticator.authorize(this.secretKey, code);
    }

    public String generateQrCodeData() {
        return "otpauth://totp/" + this.APP_NAME + ":" + this.email + "?secret=" + this.secretKey + "&issuer=" + this.APP_NAME;
    }
}
