package Server;


import java.security.*;

public class ServerEncryption {
    private final PublicKey serverPublicKey;
    private final PrivateKey serverPrivateKey;

    public ServerEncryption() {
        KeyPair keyPair =  generateKeyPair() ;
        this.serverPublicKey = keyPair.getPublic();
        this.serverPrivateKey = keyPair.getPrivate();
    }

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            System.err.println("ERROR : while generating KeyPair inside the server ! : ");
            e.printStackTrace();
        }

        return null;
    }
}
