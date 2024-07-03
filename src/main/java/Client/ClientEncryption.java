package Client;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

public class ClientEncryption {
    private final PublicKey clientRSApublicKey;
    private final PrivateKey clientRSAprivateKey;
    private SecretKey AesKey;
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

    public byte[] decryptDataRSA(byte[] encryptedJsonData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, this.clientRSAprivateKey);
            return cipher.doFinal(encryptedJsonData);
        } catch (Exception e) {
            System.err.println("Error : while encrypting the data inside the client ! :");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    public String decryptDataAES(String jsonEncrypted) {
        try {
            byte[] jsonEncryptedByte = Base64.getDecoder().decode(jsonEncrypted);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipher.DECRYPT_MODE , this.AesKey);
            return new String(cipher.doFinal(jsonEncryptedByte) , "UTF-8");
        } catch (Exception e) {
            System.err.println("Error : while decrypting the data with AES algorithm inside the client !");
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
            System.err.println("Error : while encrypting the data with AES algorithm inside the client !");
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
            throw new RuntimeException();
        }
    }


    public PublicKey getClientRSApublicKey() {
        return this.clientRSApublicKey;
    }

    public SecretKey getAesKey() {
        return AesKey;
    }

    public void setAesKey(SecretKey aesKey) {
        AesKey = aesKey;
    }

}
