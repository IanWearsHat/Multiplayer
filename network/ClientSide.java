package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.packet.*;

public class ClientSide implements Runnable {
    private static final Logger LOGGER = Logger.getLogger( ClientSide.class.getName() );

    private volatile boolean kill = false;

    public ClientSide() {}

    // will be expanded eventually to fit Jframe so that messages from the server won't mix up with your input
    private String userPrompt(BufferedReader in) {
        try {
            System.out.print("You: ");
            String input = in.readLine();
            return input;
        }
        catch (Exception e) {}
        return null;
    }

    public void startClient() {

        try {
            // must setup port forwarding so networks can connect instead of only local networks
            //also, thread for networking has to be made so it starts in Game class

            // creates an input stream from the keyboard so the user can provide input
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter IP: ");
            String remoteAddress = stdIn.readLine();

            /* Creates a new socket and binds it to the client's address with an open port */
            Socket clientSocket = new Socket();

            /* Local address is just the address of the client
            remote address is the open ip of the server that the client connects to */
            InetAddress localAddress = InetAddress.getLocalHost();
            clientSocket.bind(new InetSocketAddress(localAddress, 0));

            /* Attempts to connect to the remote address at the port specified.
            Port is the same as the port specified in port forwarding for router. */
            LOGGER.log(Level.INFO, "Attempting connection to server...");

            int timeout = 10 * 1000;
            clientSocket.connect(new InetSocketAddress(remoteAddress, 9696), timeout);
            
            LOGGER.log(Level.INFO, "Connected to server!");

            /* Creates input and output streams for the socket */
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream()); 
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            //this thread has to run so that the client can wait for a message from the server
            //while also waiting for an input from the user.
            new Thread(() -> {
                while (!kill) {
                    try {
                        Packet receivedPacket = (Packet) in.readObject();
                        if (receivedPacket instanceof MessagePacket) {
                            System.out.println(((MessagePacket) receivedPacket).message);
                        }
                        else if (receivedPacket instanceof PlayerPacket) {
                            System.out.println(((PlayerPacket) receivedPacket).x);
                            // this is how the client would update other players' positions on the client's screen
                            // it would receive other clients' positions from the server and render them accordingly on its screen.
                        }
                    }
                    catch (ClassNotFoundException e) {
                        LOGGER.log(Level.SEVERE, "Packet wasn't a Packet object, killing input reader thread.", e);
                        kill = true;
                    } 
                    catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Could not read packet, killing input reader thread.", e);
                        kill = true;
                    }
                }
            }).start();

            /* first thing you need to do is type in your name*/
            String userInput;
            userInput = stdIn.readLine();
            out.writeObject(new MessagePacket(userInput));

            /* Waits for the user to input something in the terminal. 
            When the user hits the return key, the input is sent to the server through the out stream (out.println(userInput)). */
            while ((userInput = userPrompt(stdIn)) != null) {
                out.writeObject(new MessagePacket(userInput));
                // out.reset();
            }

        }
        catch (ConnectException | SocketTimeoutException e) {
            LOGGER.log(Level.SEVERE, "No server found. Exiting...", e);
        }
        catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Invalid IP.", e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        kill = true;
    }

    @Override
    public void run() {
        startClient();
    }
    
}