package Server;

import Server.Database.DatabaseManager;
import Shared.Api.dto.*;
import Shared.Models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.binding.NumberExpressionBase;
import javafx.scene.chart.PieChart;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClientHandler implements Runnable {
    private static final String LOG_FILE_ADDRESS = "src/main/java/Server/logs/ClientHandler_Log.txt";
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private PublicKey clientPublicKey;
    private ServerEncryption serverEncryption;


    public ClientHandler(Socket socket) {
        serverEncryption = new ServerEncryption();

        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientHandlers.add(this);


        } catch (IOException e) {
            String errorLog = "Error : while ClientHandler constructor runs !";
            System.err.println(errorLog);
            writeLog(errorLog);
            closeEverything(socket , bufferedReader , bufferedWriter);
        }
    }


    @Override
    public void run() {
        ObjectMapper objectMapper = new ObjectMapper();
        String encryptedJson;
        String json;
        Request request;
        Header header;
        String endpoint;

//        sendServerPublicKeyRSA();
//        receiveClientPublicKeyRSA();


        sendAesKey();


        while (socket.isConnected()) {
            try {
                encryptedJson =  this.bufferedReader.readLine();
                json = this.serverEncryption.decryptDataAES(encryptedJson);
                request = objectMapper.readValue(json , Request.class);
                header = request.getHeader();
                endpoint = header.endpointParser()[1];

                if (endpoint.equals("api")) {
                    handleApiRequests(request);

                } else {
                    handleBadRequest(header);
                }
            } catch (IOException e) {
                String errorLog = "Error : while reading request json in ClientHandler !";
                System.err.println(errorLog);
                e.printStackTrace();
                writeLog(errorLog);
            }
        }
    }

    public void writeLog(String log) {
        try {
            File logFile = new File(LOG_FILE_ADDRESS);
            File logDir = logFile.getParentFile();

            if (!logDir.exists()) {
                logDir.mkdir();
            }

            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_ADDRESS , true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.write("[" + timestamp + "] " + log);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error : while logging inside writeLog function !");
            e.printStackTrace();
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        clientHandlers.remove(this);
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            String errorLog = "Error : while closeEverything called : \n\t";

            System.err.println(errorLog);
            writeLog(errorLog);
            e.printStackTrace();
        }
    }


    public void handleApiRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[2];

        if (endpoint.equals("account")) {
            handleAccountRequests(request);

        } else if (endpoint.equals("channel")) {
            handleChannelRequests(request);

        } else if (endpoint.equals("video")) {
            handleVideoRequests(request);

        } else if (endpoint.equals("comment")) {
            handleCommentRequests(request);

        } else {
            handleBadRequest(header);
        }
    }


    public void handleAccountRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (header.getMethod().equals("POST")) {
            if (endpoint.equals("login")) {
                handleLoginRequests(request);

            } else if (endpoint.equals("signup")) {
                handleSignupRequests(request);

            } else if (endpoint.equals("edit")) {
                handleAccountEditRequests(request);

            } else if (endpoint.equals("subscribe")) {
                handleAccountSubscribeRequests(request);

            } else if (endpoint.equals("unsubscribe")) {
                handleAccountUnsubscribeRequests(request);

            } else {
                handleBadRequest(header);

            }
        } else if (header.getMethod().equals("GET")) {
            try {
                Long accountId = Long.parseLong(endpoint);
                handleAccountInfoRequests(request , accountId);
            } catch (NumberFormatException e) {
                handleBadRequest(header);
            }
        }
    }


    public void handleChannelRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (header.getMethod().equals("POST")) {
            if (endpoint.equals("edit")) {
                handleChannelEditRequests(request);

            } else {
                handleBadRequest(header);

            }
        } else if (header.getMethod().equals("GET")) {
            if (endpoint.equals("subscribers")) {
                String channelIdString = header.endpointParser()[4];
                try {
                    Long channelId = Long.parseLong(channelIdString);
                    handleChannelSubscribersRequests(request , channelId);
                } catch (NumberFormatException e) {
                    handleBadRequest(header);
                }
            } else {
                try {
                    Long channelId = Long.parseLong(endpoint);
                    handleChannelInfoRequests(request , channelId);
                } catch (NumberFormatException e) {
                    handleBadRequest(header);
                }
            }
        }
    }


    public void handleVideoRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (endpoint.equals("like")) {
            handleVideoLikeRequests(request);

        } else {
            handleBadRequest(header);
        }
    }

    public void handleCommentRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (endpoint.equals("add")) {
            handleCommentAddRequests(request);

        } else if (endpoint.equals("delete")) {
            handleCommentDeleteRequests(request);

        } else if (endpoint.equals("like")) {
            handleCommentLikeRequests(request);

        } else {
            handleBadRequest(header);

        }
    }


    public void sendResponse(Response response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writer();
            String json = objectWriter.writeValueAsString(response);
            String encryptedJson = this.serverEncryption.encryptDataAES(json);

            this.bufferedWriter.write(encryptedJson);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

        } catch (JsonProcessingException e) {
            String errorLog = "Error : while serialize the response client in ClientHandler (sendResponse function)";
            e.printStackTrace();
            writeLog(errorLog);

        } catch (IOException e) {
            String errorLog = "Error : while sending the encryptedJson to client in sendResponse function !";
            e.printStackTrace();
            writeLog(errorLog);
        }
    }


    public void handleBadRequest(Header header) {
        Body body = new Body();
        body.setSuccess(false);
        body.setMessage("400 Bad Request : " + header.getEndpoint());

        Response response = new Response(header , body);
        sendResponse(response);
    }


    public void handleLoginRequests(Request request) {
        Response response;
        Body body = request.getBody();
        Header header = request.getHeader();
        String username = body.getUsername();
        String password = body.getPassword();

        if (Objects.equals(username , null) || Objects.equals(password , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("Username or Password is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        Account account = DatabaseManager.getAccount(username , password);

        if (Objects.equals(account , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("Username or Password is wrong !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setAccount(account);

        response = new Response(header , body);

        sendResponse(response);
    }


    public void handleSignupRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        Account account = body.getAccount();

        if (Objects.equals(account , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("Requested account is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }
        account = DatabaseManager.addAccount(account);
        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setAccount(account);

        response = new Response(header , body);

        sendResponse(response);
    }


    public void handleAccountEditRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        Account account = body.getAccount();

        if (Objects.equals(account , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("Requested account is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        //TODO make editAccount return the account
        DatabaseManager.editAccount(account);

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");

        response = new Response(header , body);

        sendResponse(response);
    }


    public void handleAccountInfoRequests(Request request , Long accountId) {
        Response response;
        Header header = request.getHeader();

        if (Objects.equals(accountId , null)) {
            Body body = new Body();
            body.setSuccess(false);
            body.setMessage("The accountId is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        Account account = DatabaseManager.getAccount(accountId);

        if (Objects.equals(account , null)) {
            Body body = new Body();
            body.setSuccess(false);
            body.setMessage("There is no Account with this accountId ! [" + accountId + "]");

            response = new Response(header , body);
            sendResponse(response);
            return;

        }

        Body body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setAccount(account);

        response = new Response(header , body);

        sendResponse(response);
    }


    public void handleAccountSubscribeRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        Long subscriberChannelId = body.getSubscriberChannelId();
        Long subscribedChannelId = body.getSubscribedChannelId();

        Subscription subscription = DatabaseManager.addSubscription(subscriberChannelId , subscribedChannelId);

        if (Objects.equals(subscription , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("subscriber channel id or subscribed channel id isn't valid !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setSubscription(subscription);

        response = new Response(header , body);

        sendResponse(response);
    }


    public void handleAccountUnsubscribeRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        Long subscriberChannelId = body.getSubscriberChannelId();
        Long subscribedChannelId = body.getSubscribedChannelId();

        DatabaseManager.deleteSubscription(subscriberChannelId , subscribedChannelId);

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");

        response = new Response(header , body);

        sendResponse(response);
    }


    public void handleChannelEditRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        Channel channel = body.getChannel();

        if (Objects.equals(channel , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("The channel that sent is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        //TODO make editChannel return channel
        DatabaseManager.editChannel(channel);

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");

        response = new Response(header , body);

        sendResponse(response);
    }


    public void handleChannelInfoRequests(Request request , Long channelId) {
        Response response;
        Header header = request.getHeader();

        if (Objects.equals(channelId , null)) {
            Body body = new Body();
            body.setSuccess(false);
            body.setMessage("The channel id that sent is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        Channel channel = DatabaseManager.getChannel(channelId);

        if (Objects.equals(channel , null)) {
            Body body = new Body();
            body.setSuccess(false);
            body.setMessage("There is no channel with this channelId ! [" + channelId + "]");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        Body body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setChannel(channel);

        response = new Response(header , body);
        sendResponse(response);
    }


    public void handleChannelSubscribersRequests(Request request , Long channelId) {
        Response response;
        Header header = request.getHeader();
        Body body;

        if (Objects.equals(channelId , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("The channelId that sent is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        List<Channel> subscriberChannels = DatabaseManager.getSubscriberChannels(channelId);

        if (Objects.equals(subscriberChannels , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("There is no channel with this channelId ! [" + channelId + "]");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setSubscriberChannels(subscriberChannels);

        response = new Response(header , body);
        sendResponse(response);
    }


    public void handleVideoLikeRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        String endpoint = header.endpointParser()[4];

        if (endpoint.equals("add")) {
            Reaction reaction = body.getReaction();

            if (Objects.equals(reaction , null)) {
                body = new Body();
                body.setSuccess(false);
                body.setMessage("The reaction that sent is null !");

                response = new Response(header , body);
                sendResponse(response);
                return;
            }

            Reaction reactionInDB = DatabaseManager.getReaction(reaction.getChannelId() , reaction.getVideoId());
            if (Objects.equals(reactionInDB, null)) {
                reaction = DatabaseManager.addReaction(reaction);
                body = new Body();
                body.setSuccess(true);
                body.setMessage("200 Ok");
                body.setReaction(reaction);

                response = new Response(header , body);
                sendResponse(response);

            } else {
                //TODO make editReaction return Reaction
                DatabaseManager.editReaction(reaction);
                body = new Body();
                body.setSuccess(true);
                body.setMessage("200 Ok");

                response = new Response(header , body);
                sendResponse(response);
            }


        } else if (endpoint.equals("delete")) {
            Long reactionId = body.getReactionId();

            if (Objects.equals(reactionId , null)) {
                body = new Body();
                body.setSuccess(false);
                body.setMessage("The reactionId that sent is null !");

                response = new Response(header , body);
                sendResponse(response);
                return;
            }

            DatabaseManager.deleteReaction(reactionId);

            body = new Body();
            body.setSuccess(true);
            body.setMessage("200 Ok");

            response = new Response(header , body);
            sendResponse(response);

        } else {
            handleBadRequest(header);
        }
    }


    public void handleVideoGetReactionRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();

        Long channelId = body.getChannelId();
        Long videoId = body.getVideoId();

        Reaction reaction = DatabaseManager.getReaction(channelId , videoId);

        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setReaction(reaction);

        response = new Response(header , body);
        sendResponse(response);
    }


    public void handleCommentAddRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        Comment comment = body.getComment();

        if (Objects.equals(comment , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("The comment that sent is null !");

            response = new Response(header , body);
            sendResponse(response);
            return;
        }

        comment = DatabaseManager.addComment(comment);

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");
        body.setComment(comment);

        response = new Response(header , body);
        sendResponse(response);
    }

    public void handleCommentDeleteRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        Long commentId = body.getCommentId();

        if (Objects.equals(commentId , null)) {
            body = new Body();
            body.setSuccess(false);
            body.setMessage("The commentId that send is null !");

            response = new Response(header , body);
            sendResponse(response);

        }

        DatabaseManager.deleteComment(commentId);

        body = new Body();
        body.setSuccess(true);
        body.setMessage("200 Ok");

        response = new Response(header , body);
        sendResponse(response);
    }

    public void handleCommentLikeRequests(Request request) {
        Response response;
        Header header = request.getHeader();
        Body body = request.getBody();
        String endpoint = header.endpointParser()[4];

        if (endpoint.equals("add")) {
            CommentReaction commentReaction = body.getCommentReaction();

            if (Objects.equals(commentReaction, null)) {
                body = new Body();
                body.setSuccess(false);
                body.setMessage("The comment reaction that sent is null !");

                response = new Response(header , body);
                sendResponse(response);
                return;
            }

            CommentReaction commentReactionInDB = DatabaseManager.getCommentReaction(commentReaction.getChannelId() , commentReaction.getCommentId());
            if (Objects.equals(commentReactionInDB, null)) {
                commentReaction = DatabaseManager.addCommentReaction(commentReaction);
                body = new Body();
                body.setSuccess(true);
                body.setMessage("200 Ok");
                body.setCommentReaction(commentReaction);

                response = new Response(header , body);
                sendResponse(response);

            } else {
                //TODO make editCommentReaction return Reaction
                DatabaseManager.editCommentReaction(commentReaction);
                body = new Body();
                body.setSuccess(true);
                body.setMessage("200 Ok");

                response = new Response(header , body);
                sendResponse(response);
            }


        } else if (endpoint.equals("delete")) {
            Long commentReactionId = body.getCommentReactionId();

            if (Objects.equals(commentReactionId, null)) {
                body = new Body();
                body.setSuccess(false);
                body.setMessage("The reactionId that sent is null !");

                response = new Response(header , body);
                sendResponse(response);
                return;
            }

            DatabaseManager.deleteCommentReaction(commentReactionId);

            body = new Body();
            body.setSuccess(true);
            body.setMessage("200 Ok");

            response = new Response(header , body);
            sendResponse(response);

        } else {
            handleBadRequest(header);
        }
    }


    public void sendServerPublicKeyRSA() {
        try {
            PublicKey serverPublicKey = this.serverEncryption.getServerRSApublicKey();
            String encodedServerPublicKey = Base64.getEncoder().encodeToString(serverPublicKey.getEncoded());
            this.bufferedWriter.write(encodedServerPublicKey);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException e) {
            String errorLog = "Error : while encoding the RSA public key and send to client in sendServerPublicKeyRSA function";
            System.err.println(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void sendAesKey() {
        try {
            SecretKey AesKey = this.serverEncryption.getAesKey();
//            String encodedAesKey = this.serverEncryption.encryptDataRSA(Base64.getEncoder().encodeToString(AesKey.getEncoded()) , this.clientPublicKey);
            String encodedAesKey = Base64.getEncoder().encodeToString(AesKey.getEncoded());
            this.bufferedWriter.write(encodedAesKey);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();


        } catch (IOException e) {
            String errorLog = "Error : while encoding the Aes key and send to client in sendAesKey function";
            System.err.println(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void receiveClientPublicKeyRSA() {
        try {
            String encodedClientPublicKey = this.bufferedReader.readLine();
            byte[] decodedClientPublicKey = Base64.getDecoder().decode(encodedClientPublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedClientPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.clientPublicKey = keyFactory.generatePublic(keySpec);

        } catch (IOException e) {
            String errorLog = "Error : while reading data from client in recive ClientPublicKeyRSA function !";
            writeLog(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (NoSuchAlgorithmException e) {
            String errorLog = "Error : while getting instance of RSA Algorithm throws NoSuchAlgorithmException in receiveClientPublicKeyRSA function !";
            writeLog(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);

        } catch (InvalidKeySpecException e) {
            String errorLog = "Error : while generatePublic throws InvalidKeySpecException in receiveClientPublicKeyRSA function !";
            writeLog(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
