package Server;

import Server.Database.DatabaseManager;
import Shared.Api.dto.*;
import Shared.Models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.beans.binding.NumberExpressionBase;
import javafx.scene.chart.PieChart;
import org.hibernate.dialect.Database;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        receiveClientPublicKeyRSA();
        sendAesKey();


        while (socket.isConnected()) {
            try {
                encryptedJson =  this.bufferedReader.readLine();

                if (encryptedJson == null) {
                    String log = "A client has disconnected";
                    System.out.println(log);
                    writeLog(log);
                    closeEverything(this.socket , this.bufferedReader , this.bufferedWriter);
                }

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

        } else if (endpoint.equals("isUnique")) {
            handleIsUniqueRequests(request);

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

            } else if (endpoint.equals("subscribe")) {
                handleAccountSubscribeRequests(request);

            } else if (endpoint.equals("unsubscribe")) {
                handleAccountUnsubscribeRequests(request);

            } else {
                handleBadRequest(header);

            }
        } else if (header.getMethod().equals("GET")) {
            if (endpoint.equals("homepage")) {
                handleHomepageVideosRequest(request);

            } else if (endpoint.equals("subscriptions")) {
                handleGetSubscriptionsRequest(request);

            } else if (header.isValidAccountInfoQuery()){
                Long accountId = header.parseAccountId();
                if (!accountId.equals(null)) {
                    handleAccountInfoRequests(request , accountId);
                } else {
                    handleBadRequest(header);
                }
            } else if (header.isValidSubscribedToChannelQuery()) {
                Long channelId = header.parseIsSubscribedChannelId();
                if (!channelId.equals(null)) {
                    handleIsSubscribedToChannelRequest(request , channelId);
                } else {
                    handleBadRequest(header);
                }
            }
        } else if (header.getMethod().equals("PUT")) {
            if (endpoint.equals("edit")) {
                handleAccountEditRequests(request);
            }
        }
    }


    public void handleChannelRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (header.getMethod().equals("PUT")) {
            if (endpoint.equals("edit")) {
                handleChannelEditRequests(request);

            } else {
                handleBadRequest(header);

            }
        } else if (header.getMethod().equals("GET")) {
            if (endpoint.equals("subscribers")) {
                if (header.isValidSubscribersQuery()) {
                   Long channelId = header.parseChannelIdInSubscribers();
                   if (channelId != null) {
                        handleChannelSubscribersRequests(request , channelId);
                   } else {
                        handleBadRequest(header);
                   }
                }
            } else if (header.isValidChannelInfoQuery()){
                Long channelId = header.parseChannelId();
                if (channelId != null) {
                    handleChannelInfoRequests(request , channelId);
                } else {
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

        } else if (endpoint.equals("comments")) {
            handleCommentsOfVideoRequest(request);

        } else if (endpoint.equals("views")) {
            handleGetViewsOfVideo(request);

        } else if (endpoint.equals("likes")) {
            handleGetLikesOfVideo(request);

        } else if (endpoint.equals("dislikes")) {
            handleGetDislikesOfVideo(request);

        } else if (header.isValidSearchQuery()) {
            handleSearchVideoRequest(request);

        } else if (header.isValidVideoLikedQuery()) {
            Long channelId = header.parseVideoLikedChannelId();
            handleIsVideoLikedRequest(request , channelId);
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

        } else if (endpoint.equals("replies")) {
            handleGetRepliesOfComment(request);

        } else if (endpoint.equals("likes")) {
            handleGetLikesOfComment(request);

        } else if (endpoint.equals("dislikes")) {
            handleGetDislikesOfComment(request);

        } else if (header.isValidCommentLikedQuery()) {
            Long channelId = header.parseCommentLikedChannelId();
            handleIsCommentLikedRequest(request , channelId);

        } else {
            handleBadRequest(header);

        }
    }


    public void handleIsUniqueRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (endpoint.equals("username")) {
            handleCheckUsernameUnique(request);

        } else if (endpoint.equals("email")) {
            handleCheckEmailUnique(request);

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
                Reaction editedReaction = DatabaseManager.editReaction(reaction);
                body = new Body();
                body.setSuccess(true);
                body.setMessage("200 Ok");
                body.setReaction(editedReaction);

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

        } else if (endpoint.equals("reaction")) {
            handleVideoGetReactionRequests(request);

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


    public void handleCheckUsernameUnique(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        String username = requestBody.getUsername();

        Body responseBody = new Body();

        if (username.equals(null)) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The username that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        boolean isUsernameUnique = DatabaseManager.isUsernameUnique(username);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setUsernameUnique(isUsernameUnique);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleCheckEmailUnique(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        String emailAddress = requestBody.getEmailAddress();

        Body responseBody = new Body();

        if (emailAddress.equals(null)) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The email address that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        boolean isEmailUnique = DatabaseManager.isEmailUnique(emailAddress);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setEmailUnique(isEmailUnique);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleHomepageVideosRequest(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        ArrayList<String> searchHistory = requestBody.getSearchHistory();
        Long accountId = requestBody.getAccountId();

        Body responseBody = new Body();

        if (accountId.equals(null) | searchHistory.equals(null)) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The account id or searchHistory that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        //TODO implement some random function for basic home page videos

        ArrayList<Video> homepageVideos = new ArrayList<>();

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setHomepageVideos(homepageVideos);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleCommentsOfVideoRequest(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();

        Body responseBody = new Body();

        if (videoId.equals(null)) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The video id that sent is null");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Comment> commentsOfVideo = DatabaseManager.getVideoComments(videoId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setComments(commentsOfVideo);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetSubscriptionsRequest(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long channelId = requestBody.getChannelId();

        Body responseBody = new Body();

        if (channelId.equals(null)) {
            responseBody.setSuccess(false);
            responseBody.setMessage("the channel id that sent is null");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Channel> subscriptions = DatabaseManager.getSubscribedChannels(channelId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setSubscriptions(subscriptions);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetVideoRequest(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();

        Body responseBody = new Body();

        if (videoId.equals(null)) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The video id that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        Video video = DatabaseManager.getVideo(videoId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setVideo(video);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleSearchVideoRequest(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long channelId = requestBody.getChannelId();
        List<Category> categories = requestBody.getCategories();
        int perPage = requestBody.getPerPage();
        int pageNumber = requestBody.getPageNumber();
        String searchKeywords;

        Body responseBody = new Body();

        try {
            searchKeywords = requestHeader.parseSearchKeywords();
        } catch (UnsupportedEncodingException e) {
            responseBody.setSuccess(false);
            responseBody.setMessage("error while running regex on a endpoint !");

            String errorLog = "Error : error while running regex on a endpoint for finding search keywords !";
            System.err.println(errorLog);
            writeLog(errorLog);
            throw new RuntimeException(e);
        }

        //TODO use database search function
        List<Video> searchVideos = DatabaseManager.searchVideo(channelId , categories , searchKeywords , perPage , pageNumber);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setSearchVideos(searchVideos);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
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
            byte[] encryptedAesKey = this.serverEncryption.encryptDataRSA(AesKey.getEncoded() , this.clientPublicKey);
            String encodedEncryptedAesKey = Base64.getEncoder().encodeToString(encryptedAesKey);
            this.bufferedWriter.write(encodedEncryptedAesKey);
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


    public void handleIsSubscribedToChannelRequest(Request request , Long targetChannelId) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long userChannelId = requestBody.getChannelId();

        Body responseBody = new Body();

        if (userChannelId.equals(null) | targetChannelId.equals(null)) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The UserChannelId  or targetChannelId is null!");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        boolean isSubscribed = DatabaseManager.isSubscribedToChannel(userChannelId , targetChannelId);
        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setSubscribedToChannel(isSubscribed);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleIsVideoLikedRequest(Request request , Long channelId) {
       Response response;
       Header requestHeader = request.getHeader();
       Body requestBody = request.getBody();
       Long videoId = requestBody.getVideoId();

       Body responseBody = new Body();

       if (videoId == null | channelId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The videoId or channelId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
       }

       Reaction reaction = DatabaseManager.getReaction(channelId, videoId);
       HashMap<Boolean , Short> isVideoLiked = new HashMap<>();

       if (reaction != null) {
            isVideoLiked.put(true , reaction.getReactionTypeId());
       } else {
           isVideoLiked.put(false , null);
       }

       responseBody.setSuccess(true);
       responseBody.setMessage("200 Ok");
       responseBody.setIsVideoLiked(isVideoLiked);

       response = new Response(requestHeader , responseBody);
       sendResponse(response);
    }


    public void handleIsCommentLikedRequest(Request request , Long channelId) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long commentId = requestBody.getCommentId();

        Body responseBody = new Body();

        if (commentId == null | channelId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The commentId or channelId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        CommentReaction commentReaction = DatabaseManager.getCommentReaction(channelId, commentId);
        HashMap<Boolean , Short> isCommentLiked = new HashMap<>();

        if (commentReaction != null) {
            isCommentLiked.put(true , commentReaction.getCommentReactionTypeId());
        } else {
            isCommentLiked.put(false , null);
        }

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setIsCommentLiked(isCommentLiked);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }

    public void handleGetViewsOfVideo(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();

        Body responseBody = new Body();

        if (videoId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The videoId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        Long numberOfViews = DatabaseManager.getNumberOfViews(videoId);
        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setNumberOfViews(numberOfViews);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetLikesOfVideo(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();

        Body responseBody = new Body();

        if (videoId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The videoId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Reaction> videoReactions = DatabaseManager.getVideoReactions(videoId);
        Long numberOfLikes = videoReactions.stream().filter(videoReaction -> videoReaction.getReactionTypeId() == 1).count();

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setNumberOfLikes(numberOfLikes);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetDislikesOfVideo(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();

        Body responseBody = new Body();

        if (videoId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The videoId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Reaction> videoReactions = DatabaseManager.getVideoReactions(videoId);
        Long numberOfLikes = videoReactions.stream().filter(videoReaction -> videoReaction.getReactionTypeId() == -1).count();

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setNumberOfDislikes(numberOfLikes);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetRepliesOfComment(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long commentId = requestBody.getCommentId();

        Body responseBody = new Body();

        if (commentId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The commentId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Comment> comments = DatabaseManager.getCommentsRepliedToComment(commentId);
        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setComments(comments);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetLikesOfComment(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long commentId = requestBody.getCommentId();

        Body responseBody = new Body();

        if (commentId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The commentId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<CommentReaction> commentReactions = DatabaseManager.getCommentReactionsOfComment(commentId);
        Long numberOfCommentLikes = commentReactions.stream().filter(commentReaction -> commentReaction.getCommentReactionTypeId() == 1).count();

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setNumberOfCommentLikes(numberOfCommentLikes);


        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetDislikesOfComment(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long commentId = requestBody.getCommentId();

        Body responseBody = new Body();

        if (commentId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The commentId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<CommentReaction> commentReactions = DatabaseManager.getCommentReactionsOfComment(commentId);
        Long numberOfCommentLikes = commentReactions.stream().filter(commentReaction -> commentReaction.getCommentReactionTypeId() == -1).count();

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setNumberOfCommentLikes(numberOfCommentLikes);


        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetCategoriesOfVideo(Request request , Long videoId) {
        Response response;
        Header requestHeader = request.getHeader();

        Body responseBody = new Body();

        if (videoId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The videoId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Category> categories = DatabaseManager.getCategoriesOfVideo(videoId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setCategories(categories);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetMostViewedCategoriesOfUser(Request request , Long channelId) {
        Response response;
        Header requestHeader = request.getHeader();

        Body responseBody = new Body();

        if (channelId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The channelId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Category> categories = DatabaseManager.getMostViewedCategoriesOfUsers(channelId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setCategories(categories);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleAddVideo(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Video video = requestBody.getVideo();

        Body responseBody = new Body();

        if (video == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The video object that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        Video addedVideo = DatabaseManager.addVideo(video);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setVideo(addedVideo);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleDeleteVideo(Request request , Long videoId) {
        Response response;
        Header requestHeader = request.getHeader();

        Body responseBody = new Body();

        if (videoId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The videoId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        DatabaseManager.deleteVideo(videoId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleDeleteVideoCategory(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();
        Integer categoryId = requestBody.getCategoryId();

        Body responseBody = new Body();

        if (videoId == null | categoryId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The videoId or categoryId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        DatabaseManager.deleteVideoCategory(videoId , categoryId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetWatchHistory(Request request , Long channelId) {
        Response response;
        Header requestHeader = request.getHeader();

        Body responseBody = new Body();

        if (channelId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The channelId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Video> watchHistoryVideos = DatabaseManager.getWatchHistory(channelId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setWatchHistoryVideos(watchHistoryVideos);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetCategories(Request request) {
        Response response;
        Header requestHeader = request.getHeader();

        Body responseBody = new Body();

        List<Category> categories = DatabaseManager.getCategories();

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setCategories(categories);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleIsChannelNameUnique(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        String channelName = requestBody.getChannelName();

        Body responseBody = new Body();

        if (channelName == null | Objects.equals(channelName, "")) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The channelId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        boolean isChannelNameUnique = DatabaseManager.isChannelNameUnique(channelName);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setChannelNameUnique(isChannelNameUnique);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleEditComment(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Comment comment = requestBody.getComment();

        Body responseBody = new Body();

        if (comment == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The comment object that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        Comment editedComment = DatabaseManager.editComment(comment);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setComment(editedComment);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleAddPlaylist(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Playlist playlist = requestBody.getPlaylist();

        Body responseBody = new Body();

        if (playlist == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlist object that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        Playlist addedPlaylist = DatabaseManager.addPlaylist(playlist);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setPlaylist(addedPlaylist);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleEditPlaylist(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Playlist playlist = requestBody.getPlaylist();

        Body responseBody = new Body();

        if (playlist == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlist object that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        Playlist editedPlaylist = DatabaseManager.editPlaylist(playlist);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setPlaylist(editedPlaylist);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetVideosOfPlaylist(Request request , Long playlistId) {
        Response response;
        Header requestHeader = request.getHeader();

        Body responseBody = new Body();

        if (playlistId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlistId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Video> playlistVideos = DatabaseManager.getVideosOfPlaylist(playlistId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setPlaylistVideos(playlistVideos);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleGetChannelsOfPlaylist(Request request , Long playlistId) {
        Response response;
        Header requestHeader = request.getHeader();

        Body responseBody = new Body();

        if (playlistId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlistId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        List<Channel> playlistChannels = DatabaseManager.getChannelsOfPlaylist(playlistId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setPlaylistChannels(playlistChannels);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleAddVideoPlaylist(Request request , Long playlistId) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();

        Body responseBody = new Body();

        if (videoId == null | playlistId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlistId or videoId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        VideoPlaylist videoPlaylist = DatabaseManager.addVideoPlaylist(videoId , playlistId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setVideoPlaylist(videoPlaylist);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleDeleteVideoPlaylist(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long videoId = requestBody.getVideoId();
        Long playlistId = requestBody.getPlaylistId();

        Body responseBody = new Body();

        if (videoId == null | playlistId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlistId or videoId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        DatabaseManager.deleteVideoPlaylist(videoId , playlistId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleAddChannelPlaylist(Request request , Long playlistId) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long channelId = requestBody.getChannelId();

        Body responseBody = new Body();

        if (channelId == null | playlistId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlistId or channelId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        ChannelPlaylist channelPlaylist = DatabaseManager.addChannelPlaylist(channelId , playlistId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");
        responseBody.setChannelPlaylist(channelPlaylist);

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public void handleDeleteChannelPlaylist(Request request) {
        Response response;
        Header requestHeader = request.getHeader();
        Body requestBody = request.getBody();
        Long channelId = requestBody.getChannelId();
        Long playlistId = requestBody.getPlaylistId();

        Body responseBody = new Body();

        if (channelId == null | playlistId == null) {
            responseBody.setSuccess(false);
            responseBody.setMessage("The playlistId or channelId that sent is null !");

            response = new Response(requestHeader , responseBody);
            sendResponse(response);
            return;
        }

        DatabaseManager.deleteChannelPlaylist(channelId , playlistId);

        responseBody.setSuccess(true);
        responseBody.setMessage("200 Ok");

        response = new Response(requestHeader , responseBody);
        sendResponse(response);
    }


    public HashMap<String , Double> dataConversion(HashMap<String , Integer>  data, double percentage) throws Exception {
        HashMap<String, Double> result = new HashMap<>();
        for (Map.Entry<String, Integer> set : data.entrySet()) {
            result.put(set.getKey(), (set.getValue() / (double) data.get("sum")) * percentage);

        }
        return result;
    }

    public HashMap<String , Double> videoSuggest() {
        //TODO complete here after database manager method is created
        try {
            HashMap<String , Integer> daily = new HashMap<String , Integer>();
            HashMap<String , Double> normalizedDailyValue = dataConversion(daily , 0.5);

            HashMap<String , Integer> weekly = new HashMap<String , Integer>();
            HashMap<String , Double> normalizedWeeklyValue = dataConversion(weekly , 0.2);

            HashMap<String , Integer> monthly = new HashMap<String , Integer>();
            HashMap<String , Double> normalizedMonthlyValue = dataConversion(monthly , 0.2);

            HashMap<String , Integer> allTime = new HashMap<String , Integer>();
            HashMap<String , Double> normalizedAllTimeValue = dataConversion(allTime , 0.1);

            HashMap<String , Double> result = new HashMap<>();

            for (Map.Entry<String , Double> entry : normalizedAllTimeValue.entrySet()) {
                String key = entry.getKey();
                double sum = entry.getValue() + normalizedDailyValue.get(key) + normalizedWeeklyValue.get(key) + normalizedMonthlyValue.get(key);
                result.put(entry.getKey() , sum);
            }

            return result;

        } catch (Exception e) {
            String errorLog = "Error : while converting data in videoSuggest !";
            System.err.println(errorLog);
            writeLog(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public List<Video> homepageRecommendation() {
        final int NUMBER_OF_SUBSCRIBED_AND_INTERESTED_VIDEO = 6;
        final int NUMBER_OF_NOT_SUBSCRIBED_AND_INTERESTED_VIDEO = 2;
        final int NUMBER_OF_SUBSCRIBED_AND_TRENDING_VIDEO = 2;

        List<Video> recommendedVideos = new ArrayList<>();
        // subbed and intrested
        HashMap<String , Double> videoSuggestionMap = videoSuggest();
        List<Map.Entry<String , Double>> videoSuggestEntry = new ArrayList<>(videoSuggestionMap.entrySet());

        Collections.sort(videoSuggestEntry , Map.Entry.comparingByValue());

        int totalVideos= 0;
        for (Map.Entry<String , Double> entry : videoSuggestEntry.reversed()) {
            if (entry.getKey() == "key") {
                continue;
            }

            int numberOfVideo = (int)Math.ceil(entry.getValue() * NUMBER_OF_SUBSCRIBED_AND_INTERESTED_VIDEO);
            totalVideos += numberOfVideo;

            if (numberOfVideo == 0 | totalVideos > NUMBER_OF_SUBSCRIBED_AND_INTERESTED_VIDEO) {
                break;
            }

            //TODO : add database manager method
            //OUTPUT of video category based must be sorted with trending
            List<Video> videoBasedCategoryAndSubbed = new ArrayList<>();

            for (int i = 0 ; i < numberOfVideo ; i++) {
                recommendedVideos.add(videoBasedCategoryAndSubbed.get(i));
            }
        }

        totalVideos = 0;
        for (Map.Entry<String , Double> entry : videoSuggestEntry.reversed()) {
            if (entry.getKey() == "key") {
                continue;
            }

            int numberOfVideo = (int)Math.ceil(entry.getValue()) * NUMBER_OF_NOT_SUBSCRIBED_AND_INTERESTED_VIDEO;
            totalVideos += numberOfVideo;

            if (numberOfVideo == 0 | totalVideos > NUMBER_OF_NOT_SUBSCRIBED_AND_INTERESTED_VIDEO) {
                break;
            }

            //TODO : add database manager method
            //Must be sorted !
            List<Video> videoBasedCategoryAndNotSubbed = new ArrayList<>();

            for (int i = 0 ; i < numberOfVideo ; i++) {
                recommendedVideos.add(videoBasedCategoryAndNotSubbed.get(i));
            }
        }

        //TODO : use trending function
        List<Video> videoTrending = new ArrayList<>();
        recommendedVideos.addAll(videoTrending);

        return recommendedVideos;
    }
}
