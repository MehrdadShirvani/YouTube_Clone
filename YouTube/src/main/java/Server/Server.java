package Server;


import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Error : while Server class constructor started !");
            throw new RuntimeException(e);
        }
    }

}
