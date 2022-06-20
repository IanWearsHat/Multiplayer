package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSide implements Runnable {
    //TODO: needs to kill threads properly
    private static final Logger LOGGER = Logger.getLogger( ServerSide.class.getName() );

    private final int PORT = 9696;

    public static ArrayList<Socket> clientList;
    public static ArrayList<ClientHandler> handlerList;
    public ClientHandler handler;

    public ServerSide() {
        clientList = new ArrayList<Socket>();
        handlerList = new ArrayList<ClientHandler>();
    }

    private void startServer() {
        try {
            //also, thread for networking has to be made so it starts in Game class

            /* Creates a ServerSocket bound to the port specified.
            Port is the same as the port specified in port forwarding for router. */
            ServerSocket serverSocket = new ServerSocket(PORT);

            /* All this class does is wait for a client to attempt connection. When the client successfully connects,
            a socket is created on the server's end that connects to the client socket. The server
            socket (called "clientSocket" here for readability) is stored and the code proceeds. */

            //TODO: this loop needs to be exited somehow, meaning it can't just be while(true)
            
            while (true) {
                LOGGER.log(Level.INFO, "Listening for connection...");
                Socket clientSocket = serverSocket.accept();
                LOGGER.log(Level.INFO, "Established connection to client from " + clientSocket.getInetAddress());
                
                clientList.add(clientSocket);

                int playerNumber = clientList.size() - 1;
                handler = new ClientHandler(playerNumber, clientSocket);
                handlerList.add(handler);

                LOGGER.log(Level.INFO, "Starting client handler thread " + playerNumber);
                new Thread(handler).start();
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not connect to client.", e);
        }
    }

    @Override
    public void run() {
        startServer();
    }

    public static void main(String[] args) {
        new Thread(
            new ServerSide()
        ).start();
    }
    
}