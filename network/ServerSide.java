package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import network.packet.Packet;
import network.packet.PlayerInputPacket;
import network.packet.PlayerStatePacket;

public class ServerSide implements Runnable {
    //TODO: needs to kill threads properly
    private static final Logger LOGGER = Logger.getLogger( ServerSide.class.getName() );

    private final int PORT = 9696;

    // https://www.baeldung.com/udp-in-java 
    public static DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    private ArrayList<InetAddress> playerList;

    public ServerSide() {
        playerList = new ArrayList<>();
    }

    private void startServer() {
        try {
            //also, thread for networking has to be made so it starts in Game class

            /* Creates a ServerSocket bound to the port specified.
            Port is the same as the port specified in port forwarding for router. */
            socket = new DatagramSocket(PORT);

            /* All this class does is wait for a client to attempt connection. When the client successfully connects,
            a socket is created on the server's end that connects to the client socket. The server
            socket (called "clientSocket" here for readability) is stored and the code proceeds. */

            //TODO: this loop needs to be exited somehow, meaning it can't just be while(true)
            
            running = true;
            while (running) {
                LOGGER.log(Level.INFO, "Listening for message...\n");

                buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                ByteArrayInputStream baos = new ByteArrayInputStream(buf);
                ObjectInputStream ois = new ObjectInputStream(baos);
                Packet receivedPacket = (Packet) ois.readObject();

                if (receivedPacket instanceof PlayerInputPacket) {
                    PlayerInputPacket received = (PlayerInputPacket) receivedPacket;

                    if (!playerList.contains(clientAddress)) {
                        playerList.add(clientAddress);
                    }

                    // LOGGER.log(Level.INFO, "Player " + received.clientID + " sent a message from " + address + " at port " + port + "\n");
                    LOGGER.log(Level.INFO, "Player " + received.clientID + "\n" +
                    "Left: " + received.left + "\n" +
                    "Right: " + received.right + "\n" +
                    "Up: " + received.up + "\n" +
                    "Down: " + received.down + "\n");
                }


                PlayerStatePacket playerState = new PlayerStatePacket(0, 100);

                ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
                
                objectOutStream.writeObject(playerState);
                objectOutStream.flush();

                byte[] buffer = byteOutStream.toByteArray();
                
                packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(packet);
            }
            socket.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not connect to client.", e);
        }
        catch (ClassNotFoundException e) {
            
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