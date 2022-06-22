package game;
import java.awt.Graphics2D;
import java.util.ArrayList;

import network.ClientSide;
import network.ServerSide;

import java.awt.Color;

public class Game {

    private Player player1;

    public static ArrayList<Projectile> projectileList = new ArrayList<>();

    private boolean networkStarted = false;
    private boolean server = false;

    public Game() {
        player1 = new Player(200, 200, new Color(45, 247, 95, 255));
    }

    public void startServer() {
        if (!networkStarted) {
            new Thread(
                new ServerSide()
            ).start();
            server = true;
        }
        networkStarted = true;
    }

    public void startClient() {
        if (!networkStarted) {
            new Thread(
                new ClientSide(player1)
            ).start();
            server = false;
        }
        networkStarted = true;
    }

    public void setLeftPressed(boolean pressed) {
        player1.setLeftPressed(pressed);
    }

    public void setRightPressed(boolean pressed) {
        player1.setRightPressed(pressed);
    }

    public void setUpPressed(boolean pressed) {
        player1.setUpPressed(pressed);
    }

    public void setDownPressed(boolean pressed) {
        player1.setDownPressed(pressed);
    }

    public void setShootPressed(boolean pressed) {
        player1.setShootPressed(pressed);
    }

    public void update() {
        player1.update();

        ArrayList<Projectile> projectilesToDelete = new ArrayList<Projectile>();

        for (Projectile projectile: projectileList) {
            projectile.update();

            if (projectile.isOutOfBounds()) {
                projectilesToDelete.add(projectile);
            }
        }

        projectileList.removeAll(projectilesToDelete);
    }

    public void draw(Graphics2D g) {
        update();
        player1.draw(g);
        for (Projectile projectile: projectileList) {
            projectile.draw(g);
        }
    }
}
