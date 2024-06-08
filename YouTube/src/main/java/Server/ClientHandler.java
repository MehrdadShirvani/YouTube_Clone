package Server;

import Client.ClientEncryption;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;


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
}
