package Client;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class ClientEncryption {
    private final PublicKey clientRSApublicKey;
    private final PrivateKey clientRSAprivateKey;
    private final int AES_KEY_SIZE = 128;
    private final int RSA_KEY_SIZE = 2048;

    public ClientEncryption() {
        KeyPair keyPair = generateRSAkeyPair();
        this.clientRSApublicKey = keyPair.getPublic();
        this.clientRSAprivateKey = keyPair.getPrivate();
    }

    public KeyPair generateRSAkeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(RSA_KEY_SIZE);
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


    public byte[] decryptDataAES(byte[] encryptedFileBytes , SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipher.DECRYPT_MODE , secretKey);
            return cipher.doFinal(encryptedFileBytes);
        } catch (Exception e) {
            System.err.println("Error : while decrypting the data with AES algorithm inside the client !");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    public PublicKey getClientRSApublicKey() {
        return this.clientRSApublicKey;
    }
}
