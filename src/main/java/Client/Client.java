package Client;

import Shared.Api.dto.*;
import Shared.Models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

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


    public void sendRequest(Request request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writer();
            String json = objectWriter.writeValueAsString(request);
            String encryptedJson = this.clientEncryption.encryptDataRSA(json , this.serverPublicKey);

            this.bufferedWriter.write(encryptedJson);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    public Response handleResponse() {
        ObjectMapper objectMapper = new ObjectMapper();
        String encryptedJson;
        String decryptedJson;
        Response response;

        try {
            encryptedJson = this.bufferedReader.readLine();
            decryptedJson = this.clientEncryption.decryptDataRSA(encryptedJson);
            response = objectMapper.readValue(decryptedJson , Response.class);

            return response;

        } catch (JsonMappingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean sendLoginRequest(String username , String password) {
         String endpoint = "/api/account/login";
         String method = "POST";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         requestBody.setUsername(username);
         requestBody.setPassword(password);

         Request request = new Request(requestHeader, requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         if (responseBody.isSuccess()) {
             Account responseAccount = responseBody.getAccount();
             if (!Objects.equals(responseAccount , null)) {
                 this.account = responseAccount;
                 return true;
             }
         }

         return false;
    }

    public boolean sendSignupRequest(Account enteredAccount) {
        String endpoint = "/api/account/signup";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setAccount(enteredAccount);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            Account responseAccount = responseBody.getAccount();
            if (!Objects.equals(responseAccount , null)) {
                this.account = responseAccount;
                return true;
            }
        }

        return false;
    }


    public boolean sendAccountEditRequest(Account editedAccount) {
        String endpoint = "/api/account/edit";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setAccount(account);

        Request request = new Request(requestHeader, requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            Account responseAccount = responseBody.getAccount();
            if (!Objects.equals(responseAccount , null)) {
                this.account = responseAccount;
                return true;
            }
        }

        return false;
    }


    public Account getAccountInfo(Long accountId) {
        String endpoint = "/api/account/info";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setAccountId(accountId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            Account responseAccount = responseBody.getAccount();
            if (!Objects.equals(responseAccount , null)) {
                return responseAccount;
            }
        }

        return null;
    }

    public boolean sendChannelEditRequest(Channel channel) {
        String endpoint = "/api/channel/edit";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannel(channel);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return true;
        }

        return false;
    }

    public Channel getChannelInfo(Long channelId) {
        String endpoint = "/api/channel/info";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            Channel channel = responseBody.getChannel();
            return channel;
        }

        return null;
    }


    public List<Channel> getChannelSubscribers(Long channelId) {
        String endpoint = "/api/channel/subscriber";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            List<Channel> subscribersChannel = responseBody.getSubscriberChannels();
            return subscribersChannel;
        }

        return null;
    }


    public boolean sendVideoLikeAddRequest(Reaction reaction) {
        String endpoint = "/api/video/like/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setReaction(reaction);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return true;
        }

        return false;
    }

}
