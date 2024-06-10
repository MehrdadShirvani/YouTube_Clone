package Server;

import Server.Database.DatabaseManager;
import Shared.Api.dto.*;
import Shared.Models.*;

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
    private ServerEncryption serverEncryption;
    private PublicKey clientPublicKey;


    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.serverEncryption = new ServerEncryption();
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
        String endpoint = request.getHeader().endpointParser()[1];

        if (endpoint == "account") {
            handleAccountRequests(request);

        } else if (endpoint == "channel") {
            handleChannelRequests(request);

        } else if (endpoint == "video") {
            handleVideoRequests(request);

        } else if (endpoint == "comment") {
            handleCommentRequests(request);

        } else {
            handleBadRequest();
        }
    }


    public void handleAccountRequests(Request request) {
        Body body = request.getBody();
        String endpoint = request.getHeader().endpointParser()[2];

        if (endpoint == "login") {
            handleLoginRequests(request);

        } else if (endpoint == "signup") {
            handleSignupRequests(request);

        } else if (endpoint == "edit") {
            handleAccountEditRequests(request);

        } else if (endpoint == "info") {
            handleAccountInfoRequests(request);

        } else {
            handleBadRequest();
        }
    }


    public void handleChannelRequests(Request request) {
        Body body = request.getBody();
        String endpoint = request.getHeader().endpointParser()[2];

        if (endpoint == "edit") {
            handleChannelEditRequests(request);

        } else if (endpoint == "info") {
            handleChannelInfoRequests(request);

        } else if (endpoint == "subscribers") {
            handleChannelSubscribersRequests(request);

        } else {
            handleBadRequest();
        }
    }
}
