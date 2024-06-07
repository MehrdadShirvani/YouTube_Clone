package Client;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class ClientEncryption {
    private final PublicKey clientRSApublicKey;
    private final PrivateKey clientRSAprivateKey;

    public ClientEncryption() {
        KeyPair keyPair = generateRSAkeyPair();
        this.clientRSApublicKey = keyPair.getPublic();
        this.clientRSAprivateKey = keyPair.getPrivate();
    }

    public KeyPair generateRSAkeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            System.err.println("ERROR : while generating KeyPair inside the client ! : ");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String encryptDataRSA(String jsonData, PublicKey serverPublicKey){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] encryptedBytes = cipher.doFinal(jsonData.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data inside the client ! :");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String decryptDataRSA(String encryptedJsonData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, this.clientRSAprivateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedJsonData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data inside the client ! :");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    public PublicKey getClientRSApublicKey() {
        return this.clientRSApublicKey;
    }
}
