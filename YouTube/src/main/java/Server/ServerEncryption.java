package Server;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.Base64;

public class ServerEncryption {
    private final PublicKey serverRSApublicKey;
    private final PrivateKey serverRSAprivateKey;
    private final int AES_KEY_SIZE = 128;

    public ServerEncryption() {
        KeyPair keyPair =  generateKeyPair() ;
        this.serverRSApublicKey = keyPair.getPublic();
        this.serverRSAprivateKey = keyPair.getPrivate();
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

    public String encryptData(String jsonData, PublicKey clientPublicKey){
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

    public String decryptData(String encryptedJsonData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, this.serverPrivateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedJsonData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data inside the server ! :");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    public static byte[] encryptDataAES(byte[] fileBytes , SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE , secretKey);
            return cipher.doFinal(fileBytes);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data with AES algorithm inside the server");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public SecretKey generateAESsecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            System.err.println("Error : while generating AES keys inside the server");
            e.printStackTrace();
            throw  new RuntimeException();
        }
    }

    public PublicKey getServerRSApublicKey() {
        return serverPublicKey;
    }
}
