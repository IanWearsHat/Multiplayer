package game;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.Color;


/*
simply make a rectangle that moves by wasd or arrow keys
Add some way to shoot projectiles with the mouse and a key press

Then use the sockets to make a multiplayer game. 
*/
public class Game {

    private Player player1;

    public static ArrayList<Projectile> projectileList = new ArrayList<>();

    public Game() {
        player1 = new Player(200, 200, new Color(45, 247, 95, 255));
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
        ArrayList<Projectile> projectilesToDelete = new ArrayList<Projectile>();

        player1.update();
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
