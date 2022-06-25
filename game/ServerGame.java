package game;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.awt.Color;

import network.ServerSide;

public class ServerGame {
    private static final Logger LOGGER = Logger.getLogger( ServerGame.class.getName() );

    public static ArrayList<Player> playerList = new ArrayList<>();
    public static ArrayList<Projectile> projectileList = new ArrayList<>();

    private ServerSide serverSide;


    public ServerGame() {
        serverSide = new ServerSide();
        serverSide.startReceivingInputThread();
    }

    public void update() {
        if (serverSide.getNewPlayerJoined()) {
            playerList.add(new Player(100, 100, Color.GREEN));
        }
        for (Player player: playerList) {
            System.out.println(player.getDownPressed());
        }
        // update all player's coordinates and whatever
        // serverSide.sendUpdateToAllClients(playerList);
        // serverSide.sendUpdateToAllClients(projectileList); eventually
        serverSide.sendUpdateToAllClients();
    }

    public static void main(String args[]) {
        ServerGame serverGame = new ServerGame();
        while (true) {
            serverGame.update();
        }
    }
}
