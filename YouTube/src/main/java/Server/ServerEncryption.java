package Server;


import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

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
            throw new RuntimeException();
        }
    }

    public static String encryptData(String jsonData, PublicKey clientPublicKey){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
            byte[] encryptedBytes = cipher.doFinal(jsonData.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data inside the server ! :");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
