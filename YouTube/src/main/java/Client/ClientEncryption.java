package Client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ClientEncryption {
    private final PublicKey clientPublicKey;
    private final PrivateKey clientPrivateKey;

    public ClientEncryption() {
        KeyPair keyPair = generateKeyPair();
        this.clientPublicKey = keyPair.getPublic();
        this.clientPrivateKey = keyPair.getPrivate();
    }

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
