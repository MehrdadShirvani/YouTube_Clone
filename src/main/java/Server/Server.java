package Server;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    private static final int PORT = 12345;
    private static final String LOG_FILE_ADDRESS = "src/main/java/Server/logs/Server_Log.txt";
    private ServerSocket serverSocket;
    public static ServerEncryption serverEncryption;

    public Server() {
        try {
            serverEncryption = new ServerEncryption();
            this.serverSocket = new ServerSocket(PORT);
            startServer();
        } catch (IOException e) {
            String errorLog = "Error : while Server class constructor started !";

            System.err.println(errorLog);
            writeLog(errorLog);
            throw new RuntimeException(e);
        }
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                String log = "A new client has connected !";
                System.out.println(log);
                writeLog(log);

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            String errorLog = "Error : IOException error occur while listening for new clients in startServer function";
            System.err.println(errorLog);

            writeLog(errorLog);
            closeServer();
            throw new RuntimeException(e);
        }
    }

    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            String errorLog = "Error : IOException error occur while closing the serverSocket inside closeServer function !";
            System.err.println(errorLog);

            writeLog(errorLog);
            throw new RuntimeException(e);
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

}
