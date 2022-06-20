package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.packet.*;

public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger( ClientHandler.class.getName() );

    private int clientID;
    private String username;
    
    private Socket clientSocket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    
    public ClientHandler(int id, Socket client) {
        clientID = id;
        clientSocket = client;
    }

    /**
     * Prints a message to the client that the client handler is handling, whether that be 
     * from the server or from another client. 
     * 
     * @param outStream The client's output stream, so we keep using only one output stream
     * @param serverMessage True prints a message as it is, false is for when another client is chatting 
     * @param inputName The username of the client that sent the message if serverMessage is false. Should usually just be username (ex. printMessageToClient(out, false, username, "play val"))
     * @param message The message that is sent, whether from server or otherwise
     */
    private void printMessageToClient(boolean serverMessage, String inputName, String message) {
        try {
            if (serverMessage) {
                outStream.writeObject(new MessagePacket(message));
            }
            else {
                outStream.writeObject(new MessagePacket(inputName + " says: " + message));
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not send message.", e);
        }
    }
    

    /**
     * Broadcasts a message to every client that isn't the client broadcasting.
     * @param serverMessage If true, sends a message from the server. if false, the client is saying something that's shared with everyone
     * @param message The message being sent
     */
    private void broadcast(boolean serverMessage, String message) {
        for (int i = 0; i < ServerSide.handlerList.size(); i++) {
            if (i != clientID) {
                ServerSide.handlerList.get(i).printMessageToClient(serverMessage, username, message);
            }
        }
    }
    
    public void run() {
        try {
            outStream = new ObjectOutputStream(clientSocket.getOutputStream()); 
            inStream = new ObjectInputStream(clientSocket.getInputStream());
            
            outStream.writeObject(new MessagePacket("Please enter your username: ")); // first thing the handler does is ask for a username, which will identify who has said what later on
            Packet receivedPacket = (Packet) inStream.readObject();
            username = ((MessagePacket) receivedPacket).message;
            LOGGER.log(Level.INFO, username + " has joined.");
            
            outStream.reset(); 
            /* so the client doesn't throw a StreamCorruptedException.
            https://stackoverflow.com/questions/2393179/streamcorruptedexception-invalid-type-code-ac
            This didn't have the same problem as me, but someone threw the idea that the stream should run .reset() to forget everything in the stream, and that worked.
            Their explanation was that the outStream was being created even though the server socket's (called clientSocket here for convenience) 
            output stream was already used, but these previous lines are the first instance of an outstream being used, so that doesn't work.  */

            String inputLine;
            boolean running = true;
            while (running) {
                receivedPacket = (Packet) inStream.readObject();
                inputLine = ((MessagePacket) receivedPacket).message;

                if (inputLine != null ) {
                    broadcast(false, inputLine);
                    System.out.println(username + " says: " + inputLine);
                }
                else {
                    running = false;
                }
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not connect to client.", e);
        } 
        catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not read packet.", e);
        }

        //tell every client that a certain client has left. This only runs when the try block finishes, meaning the client leaves. 
        broadcast(true, username + " has left the game.");
        LOGGER.log(Level.INFO, username + " has lost connection. Handler " + clientID + " closing."); // server message to server terminal to indicate that a client has lost connection. 
    }

    public String getName() {
        return username;
    }

    public int getClientID() {
        return clientID;
    }

}