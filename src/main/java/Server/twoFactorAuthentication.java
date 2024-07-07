package Server;


import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
public class twoFactorAuthentication {

    private GoogleAuthenticator googleAuthenticator;
    private String secretKey;

    public twoFactorAuthentication() {
        googleAuthenticator = new GoogleAuthenticator();
        generateSecretKey();
    }

    public twoFactorAuthentication(String secretKey) {
        googleAuthenticator = new GoogleAuthenticator();
        this.secretKey = secretKey;
    }


    public void generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        this.secretKey = key.getKey();
    }
}
