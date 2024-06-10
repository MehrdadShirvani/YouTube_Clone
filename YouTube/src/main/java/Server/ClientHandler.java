package Server;

import Server.Database.DatabaseManager;
import Shared.Api.dto.*;
import Shared.Models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.scene.chart.PieChart;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private static final String LOG_FILE_ADDRESS = "src/main/java/Server/logs/ClientHandler_Log.txt";
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private PublicKey clientPublicKey;


    public ClientHandler(Socket socket) {
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
        try {
            sendServerPublicKeyRSA();
            receiveClientPublicKeyRSA();

        } catch (IOException e) {
            String errorLog = "Error : while running ClientHandler ";
            System.err.println(errorLog);
            e.printStackTrace();
            writeLog(errorLog);
            throw new RuntimeException();
        }
    }

    public void writeLog(String log) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_ADDRESS, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + log);
            writer.newLine();
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
        String endpoint = header.endpointParser()[3];

        if (endpoint == "account") {
            handleAccountRequests(request);

        } else if (endpoint == "channel") {
            handleChannelRequests(request);

        } else if (endpoint == "video") {
            handleVideoRequests(request);

        } else if (endpoint == "comment") {
            handleCommentRequests(request);

        } else {
            handleBadRequest(header);
        }
    }


    public void handleAccountRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (endpoint == "login") {
            handleLoginRequests(request);

        } else if (endpoint == "signup") {
            handleSignupRequests(request);

        } else if (endpoint == "edit") {
            handleAccountEditRequests(request);

        } else if (endpoint == "info") {
            handleAccountInfoRequests(request);

        } else {
            handleBadRequest(header);
        }
    }


    public void handleChannelRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (endpoint == "edit") {
            handleChannelEditRequests(request);

        } else if (endpoint == "info") {
            handleChannelInfoRequests(request);

        } else if (endpoint == "subscribers") {
            handleChannelSubscribersRequests(request);

        } else {
            handleBadRequest(header);
        }
    }


    public void handleVideoRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (endpoint == "like") {
            handleVideoLikeRequests(request);

        } else {
            handleBadRequest(header);
        }
    }

    public void handleCommentRequests(Request request) {
        Header header = request.getHeader();
        String endpoint = header.endpointParser()[3];

        if (endpoint == "add") {
            handleCommentAddReqeuests(request);

        } else if (endpoint == "delete") {
            handleCommentDeleteRequests(request);

        } else if (endpoint == "like") {
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
            String encryptedJson = Server.serverEncryption.encryptDataRSA(json , this.clientPublicKey);

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
}
