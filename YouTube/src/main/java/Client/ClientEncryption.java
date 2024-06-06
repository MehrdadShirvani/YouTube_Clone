package Client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class ClientEncryption {

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            System.err.println("ERROR : while generating KeyPair inside the server ! : ");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
