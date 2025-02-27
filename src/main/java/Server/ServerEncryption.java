package Server;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.Base64;

public class ServerEncryption {
    private final PublicKey serverRSApublicKey;
    private final PrivateKey serverRSAprivateKey;
    private final SecretKey AesKey;
    private final int AES_KEY_SIZE = 128;
    private final int RSA_KEY_SIZE = 2048;

    public ServerEncryption() {
        KeyPair keyPair =  generateRSAkeyPair() ;
        this.serverRSApublicKey = keyPair.getPublic();
        this.serverRSAprivateKey = keyPair.getPrivate();
        this.AesKey = this.generateAESsecretKey();
    }

    public KeyPair generateRSAkeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(RSA_KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            System.err.println("ERROR : while generating KeyPair inside the server ! : ");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public byte[] encryptDataRSA(byte[] jsonData, PublicKey clientPublicKey){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
            return cipher.doFinal(jsonData);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data inside the server ! :");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String decryptDataRSA(String encryptedJsonData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, this.serverRSAprivateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedJsonData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data inside the server ! :");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    public String encryptDataAES(String json) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE , this.AesKey);
            String encryptedJson = Base64.getEncoder().encodeToString(cipher.doFinal(json.getBytes()));
            return encryptedJson;
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data with AES algorithm inside the server");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String decryptDataAES(String jsonEncrypted) {
        try {
            byte[] jsonEncryptedByte = Base64.getDecoder().decode(jsonEncrypted);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, this.AesKey);
            byte[] test = cipher.doFinal(jsonEncryptedByte);
            return new String(test , "UTF-8");
        } catch (Exception e) {
            System.err.println("Error : while decrypting the data with AES algorithm inside the server");
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
        return serverRSApublicKey;
    }

    public SecretKey getAesKey() {
        return AesKey;
    }
}
