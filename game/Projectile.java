package game;
import java.awt.Graphics2D;

public class Projectile {
    private int moveSpeed = 8;

    private double x;
    private double y;
    private int size = 20;

    private double xMoveSpeed;
    private double yMoveSpeed;

    private double angleToMouse = 0;

    public Projectile(int x, int y, double angleToMouse) {
        this.x = x;
        this.y = y;
        this.angleToMouse = angleToMouse;

        xMoveSpeed = moveSpeed * Math.cos(angleToMouse);
        yMoveSpeed = moveSpeed * Math.sin(angleToMouse);
    }

    public boolean isOutOfBounds() {
        return x < -30 || x > Main.WIDTH + 30 || y < -30 || y > Main.HEIGHT;
    }

    public void update() {
        x += xMoveSpeed;
        y += yMoveSpeed;
    }

    public void draw(Graphics2D g) {
        g.drawRect((int) x, (int) y, size, size);
    }
}
