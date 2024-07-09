package Client;

import Shared.Api.dto.*;
import Shared.Models.*;
import Shared.Utils.CacheUtil;
import Shared.Utils.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bytedeco.javacv.FrameFilter;
import org.w3c.dom.ls.LSOutput;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private static final int PORT = 12345;
    private static final String HOST = "localhost";
    private final ClientEncryption clientEncryption;
    private PublicKey serverPublicKey;
    private Account account;
    private final String SEARCH_HISTORY_ADDRESS = ".cache/Search_History.json" ;
    public static final int LIKE_ID = 1;
    public static final int DISLIKE_ID = -1;

    public Client() throws Exception {
        try {
            this.socket = new Socket(HOST , PORT);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientEncryption = new ClientEncryption();

            sendClientPublicKeyRSA();
            receiveAesKey();

        } catch (Exception e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
            throw new Exception (e);
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

    public void receiveAesKey() {
        try {
            String encodedEncryptedAesKey = this.bufferedReader.readLine();
            byte[] encryptedAesKey = Base64.getDecoder().decode(encodedEncryptedAesKey);
            byte[] decodedAesKey = this.clientEncryption.decryptDataRSA((encryptedAesKey));
            SecretKey AesKey = new SecretKeySpec(decodedAesKey, "AES");
            this.clientEncryption.setAesKey(AesKey);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void sendRequest(Request request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writer();
            String json = objectWriter.writeValueAsString(request);
            String encryptedJson = this.clientEncryption.encryptDataAES(json);
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
            decryptedJson = this.clientEncryption.decryptDataAES(encryptedJson);
            response = objectMapper.readValue(decryptedJson , Response.class);

            if (Objects.equals(response.getHeader().getEndpoint(), "/api/notifications/poll")) {
                //TODO : update notification ui
                Video uploadedVideo = response.getBody().getVideo();
                Notification.sendNotification(uploadedVideo.getName() , uploadedVideo.getDescription());
                return handleResponse();
            }


            return response;

        } catch (Exception e) {
            String viewName = "elements/retry-page.fxml";
            YouTube.changeScene(viewName);

            System.out.println("THERE IS NO LONGER CONNECTION !");
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
         System.out.println(responseBody.getMessage());
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

        System.out.println(responseBody.getMessage());
        return false;
    }


    public Account sendAccountEditRequest(Account editedAccount) {
        String endpoint = "/api/account/edit";
        String method = "PUT";
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

                 try {
                     CacheUtil.cacheAccount(this.account);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }

                return responseAccount;
            }
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Account getAccountInfo(Long accountId) {
        String endpoint = "/api/account/" + accountId;
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

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

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Subscription sendSubscribeRequest(Long subscriberChannelId , Long subscribedChannelId) {
        String endpoint = "/api/account/subscribe";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setSubscriberChannelId(subscriberChannelId);
        requestBody.setSubscribedChannelId(subscribedChannelId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            Subscription subscription = responseBody.getSubscription();
            return subscription;
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public boolean sendUnsubscribeRequest(Long subscriberChannelId , Long subscribedChannelId) {
        String endpoint = "/api/account/unsubscribe";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setSubscriberChannelId(subscriberChannelId);
        requestBody.setSubscribedChannelId(subscribedChannelId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();
        return responseBody.isSuccess();
    }

    public Channel sendChannelEditRequest(Channel channel) {
        String endpoint = "/api/channel/edit";
        String method = "PUT";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannel(channel);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getChannel();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }

    public Channel getChannelInfo(Long channelId) {
        String endpoint = "/api/channel/" + channelId;
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            Channel channel = responseBody.getChannel();
            return channel;
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Channel> getChannelSubscribers(Long channelId) {
        String endpoint = "/api/channel/subscribers/" + channelId;
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            List<Channel> subscribersChannel = responseBody.getSubscriberChannels();
            return subscribersChannel;
        }

        System.out.println(responseBody.getMessage());
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

        System.out.println(responseBody.getMessage());
        return false;
    }

    public boolean sendVideoLikeDeleteRequest(Long reactionId) {
        String endpoint = "/api/video/like/delete";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setReactionId(reactionId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return true;
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public Reaction sendVideoGetReactionRequest(Long channelId , Long videoId) {
        String endpoint = "/api/video/like/reaction";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);
        requestBody.setVideoId(videoId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getReaction();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public boolean sendCommentAddRequest(Comment comment) {
        String endpoint = "/api/comment/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setComment(comment);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return true;
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public boolean sendCommentDeleteRequest(Long commentId) {
        String endpoint = "/api/comment/delete";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setCommentId(commentId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return true;
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public boolean sendCommentLikeAddRequest(CommentReaction commentReaction) {
        String endpoint = "/api/comment/like/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setCommentReaction(commentReaction);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return true;
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public boolean sendCommentLikeDeleteRequest(Long commentId) {
        String endpoint = "/api/comment/like/delete";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setCommentId(commentId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return true;
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public boolean sendCheckUsernameUnique(String username) {
        String endpoint = "/api/isUnique/username";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setUsername(username);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            if (responseBody.isUsernameUnique()) {
                return true;
            }
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public boolean sendCheckEmailUnique(String emailAddress) {
        String endpoint = "/api/isUnique/email";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setEmailAddress(emailAddress);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            if (responseBody.isEmailUnique()) {
                return true;
            }
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public List<Video> getHomepageVideos(int perPage , int pageNumber) {
        String endpoint = "/api/account/homepage";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();
        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);
        requestBody.setChannelId(this.account.getChannelId());

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getHomepageVideos();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Comment> getCommentsOfVideo(Long videoId) {
        String endpoint = "/api/video/comments";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getComments();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Channel> getSubscriptions() {
        String endpoint = "/api/account/subscriptions";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(this.account.getChannelId());

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getSubscriptions();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Video getVideo(Long videoId) {
        String endpoint = "/api/video/info";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getVideo();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Video> searchVideo(List<Category> categories , String searchKeywords , int perPage , int pageNumber) {
        String searchKeywordsUrlForm = searchKeywords.replace(" " , "%20");
        String endpoint = "/api/video/search?query=" + searchKeywordsUrlForm;
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        //TODO -> get this back to normal
//        requestBody.setChannelId(this.account.getChannelId());
        requestBody.setChannelId(1L);
        requestBody.setCategories(categories);
        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getSearchVideos();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public void saveSearchHistory(String[] searchHistory) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(SEARCH_HISTORY_ADDRESS);
            File fileDir = file.getParentFile();

            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            objectMapper.writeValue(file , searchHistory);
        } catch (IOException e) {
            System.err.println("Error: while saving search history!");
            e.printStackTrace();
        }
    }


    public String[] readSearchHistory() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(SEARCH_HISTORY_ADDRESS);
            File fileDir = file.getParentFile();

            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
                return new String[0];
            }

            return objectMapper.readValue(file, new TypeReference<String[]>() {});
        } catch (IOException e) {
            System.err.println("Error: while reading search history!");
            e.printStackTrace();
            return new String[0];
        }
    }


    public boolean isSubscribedToChannel(Long targetChannelId) {
        String endpoint = "/api/account/is-subscribed?channelId=" + targetChannelId;
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(this.account.getChannelId());

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.isSubscribedToChannel();
        }

        System.out.println(responseBody.getMessage());
        return false;
    }


    public HashMap<Boolean , Short> isVideoLiked(Long videoId) {
       String endpoint = "/api/video/is-liked?channelId=" + this.account.getChannelId();
       String method = "GET";
       Header requestHeader = new Header(method , endpoint);
       Body requestBody = new Body();

       requestBody.setVideoId(videoId);

       Request request = new Request(requestHeader , requestBody);

       sendRequest(request);
       Response response = handleResponse();

       Body responseBody = response.getBody();

       if (responseBody.isSuccess()) {
            return responseBody.getIsVideoLiked();
       }

       System.out.println(responseBody.getMessage());
       return null;
    }

    public HashMap<Boolean , Short> isCommentLiked(Long commentId) {
        String endpoint = "/api/comment/is-liked?channelId=" + this.account.getChannelId();
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setCommentId(commentId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getIsCommentLiked();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }

    public Long getViewsOfVideo(Long videoId) {
        String endpoint = "/api/video/views";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getNumberOfViews();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Long getLikesOfVideo(Long videoId) {
         String endpoint = "/api/video/likes";
         String method = "GET";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         requestBody.setVideoId(videoId);

         Request request = new Request(requestHeader , requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         if (responseBody.isSuccess()) {
             return responseBody.getNumberOfLikes();
         }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Long getDislikesOfVideo(Long videoId) {
        String endpoint = "/api/video/dislikes";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getNumberOfDislikes();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Comment> getRepliesOfComment(Long commentId) {
        String endpoint = "/api/comment/replies";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody  = new Body();

        requestBody.setCommentId(commentId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getComments();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Long getLikesOfComment(Long commentId) {
        String endpoint = "/api/comment/likes";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setCommentId(commentId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getNumberOfCommentLikes();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Long getDislikesOfComment(Long commentId) {
        String endpoint = "/api/comment/dislikes";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setCommentId(commentId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getNumberOfCommentDislikes();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Category> getCategoriesOfVideo(Long videoId) {
        String endpoint = "/api/video/" + videoId + "/categories";
        String method = "GET";
        Header requestHeader= new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getCategories();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Category> getMostViewedCategoriesOfUser() {
        String endpoint = "/api/account/" + this.account.getChannelId() + "most-views-categories";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getCategories();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Video addVideo(Video video) {
        String endpoint = "/api/video/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideo(video);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getVideo();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public boolean deleteVideo(Long videoId) {
        String endpoint = "/api/video/" + videoId + "/delete";
        String method = "DELETE";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        return responseBody.isSuccess();
    }


    public boolean deleteVideoCategory(Long videoId, Integer categoryId) {
        String endpoint = "/api/videoCategory/delete";
        String method = "DELETE";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);
        requestBody.setCategoryId(categoryId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        return responseBody.isSuccess();
    }


    public List<Video> getWatchHistoryVideo(int perPage , int pageNumber) {
         String endpoint = "/api/account/" + this.account.getChannelId() + "/watch-history/video";
         String method = "GET";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         Request request = new Request(requestHeader , requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         if (responseBody.isSuccess()) {
             return responseBody.getWatchHistoryVideos();
         }

        System.out.println(responseBody.getMessage());
        return null;
    }



    public List<Date> getWatchHistoryDates(int perPage , int pageNumber) {
        String endpoint = "/api/account/" + this.account.getChannelId() + "/watch-history/timestamp";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getWatchHistoryDates();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Category> getCategories() {
        String endpoint = "/api/categories";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getCategories();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }

    public Boolean isChannelNameUnique(String channelName) {
        String endpoint = "/api/isUnique/channelName";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelName(channelName);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.isChannelNameUnique();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Comment editComment(Comment comment) {
        String endpoint = "/api/comment/edit";
        String method = "PUT";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setComment(comment);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getComment();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Playlist addPlaylist(Playlist playlist) {
         String endpoint = "/api/playlist/add";
         String method = "POST";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         requestBody.setPlaylist(playlist);

         Request request = new Request(requestHeader , requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         if (responseBody.isSuccess()) {
             return responseBody.getPlaylist();
         }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Playlist editPlaylist(Playlist playlist) {
         String endpoint = "/api/playlist/edit";
         String method = "PUT";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         requestBody.setPlaylist(playlist);

         Request request = new Request(requestHeader , requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         if (responseBody.isSuccess()) {
             return responseBody.getPlaylist();
         }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Video> getVideosOfPlaylist(Long playlistId) {
        String endpoint = "/api/playlist/" + playlistId + "/videos";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getPlaylistVideos();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Channel> getChannelsOfPlaylist(Long playlistId) {
       String endpoint = "/api/playlist/" + playlistId + "/channels";
       String method = "GET";
       Header requestHeader = new Header(method , endpoint);
       Body requestBody = new Body();

       Request request = new Request(requestHeader , requestBody);

       sendRequest(request);
       Response response = handleResponse();

       Body responseBody = response.getBody();

       if (responseBody.isSuccess()) {
            return responseBody.getPlaylistChannels();
       }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public VideoPlaylist addVideoPlaylist(Long videoId , Long playlistId) {
         String endpoint = "/api/playlist/" + playlistId + "/video/add";
         String method = "POST";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         requestBody.setVideoId(videoId);

         Request request = new Request(requestHeader , requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         if (responseBody.isSuccess()) {
             return responseBody.getVideoPlaylist();
         }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public boolean deleteVideoPlaylist(Long videoId , Long playlistId) {
        String endpoint = "/api/playlist/video/delete";
        String method = "DELETE";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);
        requestBody.setPlaylistId(playlistId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        return responseBody.isSuccess();
    }


    public ChannelPlaylist addChannelPlaylist(Long channelId , Long playlistId) {
        String endpoint = "/api/playlist/" + playlistId + "/channel/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getChannelPlaylist();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public boolean deleteChannelPlaylist(Long channelId , Long playlistId) {
         String endpoint = "/api/playlist/channel/delete";
         String method = "DELETE";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         requestBody.setChannelId(channelId);
         requestBody.setPlaylistId(playlistId);

         Request request = new Request(requestHeader , requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         return responseBody.isSuccess();
    }


    public VideoView addVideoView(VideoView videoView) {
        String endpoint = "/api/video/add-view";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody =new Body();

        requestBody.setVideoView(videoView);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getVideoView();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Video> searchAd(Long channelId , List<Category> categories , String searchTerms , int perPage , int pageNumber) {
        String endpoint = "/api/video/search-ad";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);
        requestBody.setCategories(categories);
        requestBody.setSearchTerms(searchTerms);
        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return requestBody.getSearchVideos();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Video> searchShortVideo(Long channelId , List<Category> categories , String searchTerms , int perPage , int pageNumber) {
        String endpoint = "/api/video/search-short";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);
        requestBody.setCategories(categories);
        requestBody.setSearchTerms(searchTerms);
        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return requestBody.getSearchVideos();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<VideoCategory> addVideoCategories(Long videoId , List<Integer> categoryId) {
        String endpoint = "/api/video/category/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);
        requestBody.setCategoryIds(categoryId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return requestBody.getVideoCategories();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public boolean deleteVideoCategories(Long  videoId) {
        String endpoint = "/api/video/category/delete";
        String method = "DELETE";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        System.out.println(responseBody.isSuccess() ? "" : responseBody.getMessage());
        return responseBody.isSuccess();
    }


    public boolean deleteVideoPlaylists(Long videoId) {
        String endpoint = "/api/playlist/delete";
        String method = "DELETE";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        System.out.println(responseBody.isSuccess() ? "" : responseBody.getMessage());
        return responseBody.isSuccess();
    }


    public List<VideoPlaylist> addVideoPlaylists(Long videoId , List<Long> playlistIds) {
        String endpoint = "/api/videoPlaylists/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setVideoId(videoId);
        requestBody.setPlaylistIds(playlistIds);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return requestBody.getVideoPlaylists();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Playlist> getPlaylistsOfChannel(Long channelId , boolean isSelf) {
        String endpoint = "/api/channel/" + channelId + "/playlists";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setSelf(isSelf);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getPlaylists();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Playlist> getPublicPlaylistsForUser(Long channelId) {
        String endpoint = "/api/channel/" + channelId + "/public-playlists";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getPlaylists();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }

    public List<Video> searchPlaylist(String searchKeywords , int perPage , int pageNumber , Long channelId) {
        String endpoint = "/api/playlist/search";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);
        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);
        requestBody.setSearchTerms(searchKeywords);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getSearchVideos();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Video> getVideosOfChannel(Long channelId , int perPage , int pageNumber) {
        String endpoint = "/api/channel/" + channelId + "/videos/all";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getVideosOfChannel();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Video> getRecentVideosOfChannel(Long channelId , int perPage , int pageNumber) {
        String endpoint = "/api/channel/" + channelId + "/videos/recent";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getVideosOfChannel();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }



    public List<Video> getMostPopularVideosOfChannel(Long channelId , int perPage , int pageNumber) {
        String endpoint = "/api/channel/" + channelId + "/videos/popular";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setPerPage(perPage);
        requestBody.setPageNumber(pageNumber);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getVideosOfChannel();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }



    public Long getAllViewsOfChannel(Long channelId) {
        String endpoint = "/api/channel/" + channelId + "/videos/all-views";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getNumberOfViews();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public CommentReaction getCommentReaction(Long channelId , Long commentId) {
        String endpoint = "/api/comment/" + commentId + "/reaction";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setChannelId(channelId);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getCommentReaction();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Long getCountOfVideosOfChannel(Long channelId) {
        String endpoint = "/api/channel/" + channelId+ "/videos/count";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getNumberOfVideos();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Integer verifyEmail() {
        String endpoint = "/api/account/email/verify";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setUsername(this.account.getUsername());
        requestBody.setRecipientsEmail(this.account.getEmail());

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getTwoFactorDigit();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Integer twoFactorEmailSend() {
        String endpoint = "/api/2fa/email/send";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setUsername(this.account.getUsername());
        requestBody.setRecipientsEmail(this.account.getEmail());

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getTwoFactorDigit();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public HashMap<String , String> authenticatorAdd() {
        String endpoint = "/api/2fa/authenticator/add";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setRecipientsEmail(this.account.getEmail());

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getTwoFactorData();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public Boolean authenticatorVerify(int code) {
        String endpoint = "/api/2fa/authenticator/verify";
        String method = "POST";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        requestBody.setCode(code);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.isVerified();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }


    public List<Playlist> getPlaylistsOfVideo(Long videoId) {
        String endpoint = "/api/video/" + videoId + "/playlists";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getPlaylists();
        }

        System.out.println(responseBody.getMessage());
        return null;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
