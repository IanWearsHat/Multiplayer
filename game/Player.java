package game;
import java.awt.Graphics2D;
import java.awt.Color;

public class Player {

    private int moveSpeed = 8;

    private int x;
    private int y;
    private int width = 20;
    private int height = 40;

    private Color color;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean shootPressed = false;

    private int mouseX = 0;
    private int mouseY = 0;

    private double prevTime = System.currentTimeMillis();
    private double hitSpeed = 0.2;
    private double angleToMouse = 0;

    public Player(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void setLeftPressed(boolean pressed) {
        leftPressed = pressed;
    }

    public void setRightPressed(boolean pressed) {
        rightPressed = pressed;
    }

    public void setUpPressed(boolean pressed) {
        upPressed = pressed;
    }

    public void setDownPressed(boolean pressed) {
        downPressed = pressed;
    }

    public void setShootPressed(boolean pressed) {
        shootPressed = pressed;
    }

    public void update() {
        mouseX = Main.mouseX;
        mouseY = Main.mouseY;

        if (shootPressed && System.currentTimeMillis() - prevTime > hitSpeed * 1000) {
            angleToMouse = calculateAngleToMouse();
            shoot(angleToMouse);

            prevTime = System.currentTimeMillis();
        }

        if (leftPressed) {
            x -= moveSpeed;
        }
        if (rightPressed) {
            x += moveSpeed;
        }
        if (upPressed) {
            y -= moveSpeed;
        }
        if (downPressed) {
            y += moveSpeed;
        }
    }

    public double calculateAngleToMouse() {
        double theta = Math.atan(
            (double) (mouseY - y) / 
            (double) (mouseX - x)
        );
        if ((mouseX - x) < 0) {
            theta += Math.PI;
        }
        return theta;
    }

    private void shoot(double angle) {
        Game.projectileList.add(new Projectile(x, y, angle));
    }

    private void drawPlayer(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    private void drawCursor(Graphics2D g) {
        int radius = 12;
        g.setColor(Color.BLACK);
        g.drawOval(mouseX - radius, mouseY - radius, radius * 2, radius * 2);
    }

    public void draw(Graphics2D g) {
        drawPlayer(g);
        drawCursor(g);
    }
}
