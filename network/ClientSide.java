package network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import game.Player;
import network.packet.*;

public class ClientSide implements Runnable {
    private static final Logger LOGGER = Logger.getLogger( ClientSide.class.getName() );

    private volatile boolean kill = false;

    public final int PORT = 9696;

    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    private volatile boolean joined = false;

    private Player player;

    public ClientSide(Player player) {
        
        try {
            this.player = player;

            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        }
        catch (Exception e) {

        }
    }

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
            // System.out.print("Enter IP: ");
            // String remoteAddress = stdIn.readLine();

            /* Attempts to connect to the remote address at the port specified.
            Port is the same as the port specified in port forwarding for router. */
            LOGGER.log(Level.INFO, "Attempting connection to server...\n");

            Thread receiveInit = new Thread(() -> {
                while (!joined) {
                    try {
                        byte[] buffer = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
                        ObjectInputStream ois = new ObjectInputStream(baos);
                        ClientInitializationPacket received = (ClientInitializationPacket) ois.readObject();

                        LOGGER.log(Level.INFO, "ID: " + received.getID());
                        joined = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveInit.start();

            sendInitToServer();


            // data sent by clients:
            // the player's id
            // 4 booleans: left, right, up, down
            Thread sendInputs = new Thread(() -> {
                PlayerInputPacket playerInfo;
                while (true) {
                    try {
                        // playerInfo = new PlayerInputPacket(1, true, false, true, false);
                         playerInfo = new PlayerInputPacket(1, player.getLeftPressed(), player.getRightPressed(), player.getUpPressed(), player.getDownPressed());

                        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
                        
                        objectOutStream.writeObject(playerInfo);
                        objectOutStream.flush();

                        buf = byteOutStream.toByteArray();
                        
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
                        socket.send(packet);

                        // buf = new byte[256];
                        // packet = new DatagramPacket(buf, buf.length);
                        // socket.receive(packet);

                        // ByteArrayInputStream baos = new ByteArrayInputStream(buf);
                        // ObjectInputStream ois = new ObjectInputStream(baos);
                        // PlayerPacket received = (PlayerPacket) ois.readObject();
                    }
                    catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "input thread stopped", e);
                    }
                }
            });

            Thread receivePlayerStates = new Thread(() -> {
                while (true) {
                    try {
                        byte[] buffer = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
                        ObjectInputStream ois = new ObjectInputStream(baos);
                        PlayerStatePacket received = (PlayerStatePacket) ois.readObject();

                        LOGGER.log(Level.INFO, received.x + " " + received.y);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            sendInputs.start();
            receivePlayerStates.start();

            // LOGGER.log(Level.INFO, 
            //     "\nReceived from player " + received.clientID + "\n" +
            //     "Left: " + received.left + "\n" +
            //     "Right: " + received.right + "\n" +
            //     "Up: " + received.up + "\n" +
            //     "Down: " + received.down + "\n"
            // );
            

            /* Creates input and output streams for the socket */
            // ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream()); 
            // ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            //this thread has to run so that the client can wait for a message from the server
            //while also waiting for an input from the user.
            // new Thread(() -> {
            //     while (!kill) {
            //         try {
            //             Packet receivedPacket = (Packet) in.readObject();
            //             if (receivedPacket instanceof MessagePacket) {
            //                 System.out.println(((MessagePacket) receivedPacket).message);
            //             }
            //             else if (receivedPacket instanceof PlayerPacket) {
            //                 System.out.println(((PlayerPacket) receivedPacket).x);
            //                 // this is how the client would update other players' positions on the client's screen
            //                 // it would receive other clients' positions from the server and render them accordingly on its screen.
            //             }
            //         }
            //         catch (ClassNotFoundException e) {
            //             LOGGER.log(Level.SEVERE, "Packet wasn't a Packet object, killing input reader thread.", e);
            //             kill = true;
            //         } 
            //         catch (IOException e) {
            //             LOGGER.log(Level.SEVERE, "Could not read packet, killing input reader thread.", e);
            //             kill = true;
            //         }
            //     }
            // }).start();

            /* first thing you need to do is type in your name*/
            // String userInput;
            // userInput = stdIn.readLine();
            // out.writeObject(new MessagePacket(userInput));

            /* Waits for the user to input something in the terminal. 
            When the user hits the return key, the input is sent to the server through the out stream (out.println(userInput)). */
            // while ((userInput = userPrompt(stdIn)) != null) {
            //     out.writeObject(new MessagePacket(userInput));
            //     // out.reset();
            // }

        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "BRUH", e);
        }
        
        kill = true;
    }

    private void sendInitToServer() {
        try {
            InitializePacket initPacket = new InitializePacket();
            while (!joined) {
                ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
                
                objectOutStream.writeObject(initPacket);
                objectOutStream.flush();

                buf = byteOutStream.toByteArray();
                
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
                socket.send(packet);
            }
        }
        catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    @Override
    public void run() {
        startClient();
    }
    
}