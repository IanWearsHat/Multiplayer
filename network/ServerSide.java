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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import network.packet.*;

public class ServerSide {
    //TODO: needs to kill threads properly
    private static final Logger LOGGER = Logger.getLogger( ServerSide.class.getName() );

    private final int PORT = 9696;

    // https://www.baeldung.com/udp-in-java 
    public static DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public volatile HashMap<InetAddress, Integer> addressPortMap;

    private volatile boolean newPlayerHasJoined = false;
    private int playerID = 0;

    public ServerSide() {
        addressPortMap = new HashMap<>();
    }

    public void startReceivingInputThread() {
        new Thread(() -> {
            try {
                /* Creates a ServerSocket bound to the port specified.
                Port is the same as the port specified in port forwarding for router. */
                socket = new DatagramSocket(PORT);

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

                        // LOGGER.log(Level.INFO, "Player " + received.clientID + " sent a message from " + address + " at port " + port + "\n");
                        // LOGGER.log(Level.INFO, "Player " + received.clientID + "\n" +
                        // "Left: " + received.left + "\n" +
                        // "Right: " + received.right + "\n" +
                        // "Up: " + received.up + "\n" +
                        // "Down: " + received.down + "\n");
                        
                        // for (Map.Entry<InetAddress, Integer> entry : addressPortMap.entrySet()) {
                        //     LOGGER.log(Level.INFO, entry.getKey() + "/" + entry.getValue());
                        // }
                    }
                    else if (receivedPacket instanceof InitializePacket) {
                        InitializePacket received = (InitializePacket) receivedPacket;
                        // if a new address starts connecting, then signal to the serverGame that a new player has to be created
                        if (!addressPortMap.containsKey(clientAddress)) {
                            addressPortMap.put(clientAddress, clientPort);
                            newPlayerHasJoined = true;

                            ClientInitializationPacket initPacket = new ClientInitializationPacket(playerID);
                            sendPacketToAddressAndPort(initPacket, clientAddress, clientPort);

                            playerID++;
                        }
                    }
                }
                socket.close();
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not connect to client.", e);
            }
            catch (ClassNotFoundException e) {
                
            }
        }).start();
    }

    // maybe takes as input the list of player objects. 
    // It loops through this list and sends each player's id and coordinates in a player state packet
    public void sendUpdateToAllClients() {
        try {
            for (Map.Entry<InetAddress, Integer> entry : addressPortMap.entrySet()) {
                // LOGGER.log(Level.INFO, entry.getKey() + "/" + entry.getValue());

                InetAddress clientAddress = entry.getKey();
                int clientPort = entry.getValue();

                // for (player in playerList) { send player's coordintes and id in playerStatePacket to the address and port right above }
                PlayerStatePacket playerState = new PlayerStatePacket(0, 100);

                sendPacketToAddressAndPort(playerState, clientAddress, clientPort);
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "oops", e);
        }
    }

    private void sendPacketToAddressAndPort(Packet packet, InetAddress address, int port) {
        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
            
            objectOutStream.writeObject(packet);
            objectOutStream.flush();

            byte[] buffer = byteOutStream.toByteArray();
            
            DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(dataPacket);
        }
        catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public boolean getNewPlayerJoined() {
        return newPlayerHasJoined;
    }

    public void setNewPlayerJoined(boolean joined) {
        newPlayerHasJoined = joined;
    }

    public HashMap<InetAddress, Integer> getAddressPortMap() {
        return addressPortMap;
    }
    
}