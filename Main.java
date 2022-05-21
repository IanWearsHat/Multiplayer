import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable {

    private final long FRAME_DELAY = 1000/60L; //30 fps

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private boolean animate = true;
    private Font basic = new Font("TimesRoman", Font.PLAIN, 30);

    private static Game game;

    public Main() {
        game = new Game();
    }

     /*  Called by repaint() in the run method, meaning it's called every frame. */
     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(basic);
        game.draw(g2);
    }

    /** Enables periodic repaint calls. */
    public synchronized void start() {
        animate = true;
    }

    /** Pauses animation. */
    public synchronized void stop() {
        animate = false;
    }

    private synchronized boolean animationEnabled() {
        return animate;
    }

    @Override
    public void run() {
        while (true) {
            if (animationEnabled()) {
                requestFocusInWindow(); 
                repaint();
            }
            try {
                Thread.sleep(FRAME_DELAY);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame();
        Main graphic = new Main();

        frame.add(graphic);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();

        graphic.run();
    }
}