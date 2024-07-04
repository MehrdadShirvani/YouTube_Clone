package Client;

import Shared.Api.dto.*;
import Shared.Models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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

    public Client() {
        try {
            this.socket = new Socket(HOST , PORT);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientEncryption = new ClientEncryption();

            sendClientPublicKeyRSA();
            receiveAesKey();

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
                return true;
            }
        }

        return false;
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
        return null;
    }


    public void sendUnsubscribeRequest(Long subscriberChannelId , Long subscribedChannelId) {
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
    }

    public boolean sendChannelEditRequest(Channel channel) {
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
            return true;
        }

        return false;
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
        return false;
    }


    public List<Video> getHomepageVideos() {
        String endpoint = "/api/account/homepage";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();
        ArrayList<String> searchHistory = readSearchHistory();

        requestBody.setAccountId(this.account.getAccountId());
        requestBody.setSearchHistory(searchHistory);

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getHomepageVideos();
        }

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
        return null;
    }


    public void saveSearchHistory(ArrayList<String> searchHistory) {
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
            System.err.println("Error : while save search history!");
            e.printStackTrace();
        }
    }


    public ArrayList<String> readSearchHistory() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(SEARCH_HISTORY_ADDRESS);
            File fileDir = file.getParentFile();

            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
                return new ArrayList<>();
            }
            return objectMapper.readValue(file , new TypeReference<ArrayList<String>>() {});

        } catch (IOException e) {
            System.err.println("Error : while read search history!");
            e.printStackTrace();
            return new ArrayList<>();
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


    public boolean deleteVideoCategory(VideoCategory videoCategory) {
        String endpoint = "/api/videoCategory/delete";
        String method = "DELETE";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        return responseBody.isSuccess();
    }


    public List<Video> getWatchHistory() {
         String endpoint = "/api/account/" + this.account.getChannelId() + "/watch-history";
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
            return getCategories();
        }

        return null;
    }

    public Boolean isChannelNameUnique() {
        String endpoint = "/api/isUnique/channelName";
        String method = "GET";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.isChannelNameUnique();
        }

        return null;
    }


    public Comment editComment(Comment comment) {
        String endpoint = "/api/comment/edit";
        String method = "PUT";
        Header requestHeader = new Header(method , endpoint);
        Body requestBody = new Body();

        Request request = new Request(requestHeader , requestBody);

        sendRequest(request);
        Response response = handleResponse();

        Body responseBody = response.getBody();

        if (responseBody.isSuccess()) {
            return responseBody.getComment();
        }

        return null;
    }


    public Playlist addPlaylist(Playlist playlist) {
         String endpoint = "/api/playlist/add";
         String method = "POST";
         Header requestHeader = new Header(method , endpoint);
         Body requestBody = new Body();

         Request request = new Request(requestHeader , requestBody);

         sendRequest(request);
         Response response = handleResponse();

         Body responseBody = response.getBody();

         if (responseBody.isSuccess()) {
             return responseBody.getPlaylist();
         }

         return null;
    }

    public Account getAccount()
    {
        return account;
    }
}
