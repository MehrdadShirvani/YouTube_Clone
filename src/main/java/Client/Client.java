package Client;

import Shared.Models.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private static final int PORT = 12345;
    private static final String HOST = "localhost";
    private final ClientEncryption clientEncryption;
    private PublicKey serverPublicKey;
    private Account account;

    public Client() {
        try {
            this.socket = new Socket(HOST , PORT);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientEncryption = new ClientEncryption();

            receiveServerPublicKeyRSA();
            sendClientPublicKeyRSA();

        } catch (UnknownHostException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
            throw new RuntimeException(e);

        } catch (IOException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
            throw new RuntimeException(e);
        }
    }


    public void closeEverything(Socket socket , BufferedReader bufferedReader , BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendClientPublicKeyRSA() {
         try {
             PublicKey clientPublicKey = this.clientEncryption.getClientRSApublicKey();
             String encodedClientPublicKey = Base64.getEncoder().encodeToString(clientPublicKey.getEncoded());
             this.bufferedWriter.write(encodedClientPublicKey);
             this.bufferedWriter.newLine();
             this.bufferedWriter.flush();
         } catch(IOException e) {
             e.printStackTrace();
             throw new RuntimeException(e);
         }
    }


    public void receiveServerPublicKeyRSA() {
        try {
            String encodedServerPublicKey = this.bufferedReader.readLine();
            byte[] decodedServerPublicKey = Base64.getDecoder().decode(encodedServerPublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedServerPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.serverPublicKey = keyFactory.generatePublic(keySpec);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
