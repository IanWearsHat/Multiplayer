package game;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.event.*;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable {

    private final long FRAME_DELAY = 1000/60L; //30 fps

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private boolean animate = true;
    private Font basic = new Font("TimesRoman", Font.PLAIN, 30);

    private static Game game;
    public static int mouseX = 0;
    public static int mouseY = 0;

    public Main() {
        game = new Game();
        addBindings();
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

    private void addBindings() {
        Action left = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setLeftPressed(true);
            }
        };
    
        Action leftReleased = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setLeftPressed(false);
            }
        };
    
    
        Action right = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setRightPressed(true);
            }
        };
    
        Action rightReleased = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setRightPressed(false);
            }
        };

        Action up = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setUpPressed(true);
            }
        };

        Action upReleased = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setUpPressed(false);
            }
        };

        Action down = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setDownPressed(true);
            }
        };

        Action downReleased = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setDownPressed(false);
            }
        };

        Action shoot = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setShootPressed(true);
            }
        };

        Action shootReleased = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.setShootPressed(false);
            }
        };

        Action startServer = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.startServer();
            }
        };

        Action startClient = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                game.startClient();
            }
        };
    
        InputMap inputMap = getInputMap();
        ActionMap actionMap = getActionMap();

        KeyStroke key = KeyStroke.getKeyStroke("A");
        inputMap.put(key, "left");
        actionMap.put("left", left);

        key = KeyStroke.getKeyStroke("released A");
        inputMap.put(key, "leftReleased");
        actionMap.put("leftReleased", leftReleased);


        key = KeyStroke.getKeyStroke("D");
        inputMap.put(key, "right");
        actionMap.put("right", right);

        key = KeyStroke.getKeyStroke("released D");
        inputMap.put(key, "rightReleased");
        actionMap.put("rightReleased", rightReleased);


        key = KeyStroke.getKeyStroke("W");
        inputMap.put(key, "up");
        actionMap.put("up", up);

        key = KeyStroke.getKeyStroke("released W");
        inputMap.put(key, "upReleased");
        actionMap.put("upReleased", upReleased);

        key = KeyStroke.getKeyStroke("S");
        inputMap.put(key, "down");
        actionMap.put("down", down);

        key = KeyStroke.getKeyStroke("released S");
        inputMap.put(key, "downReleased");
        actionMap.put("downReleased", downReleased);

        key = KeyStroke.getKeyStroke("V");
        inputMap.put(key, "shoot");
        actionMap.put("shoot", shoot);

        key = KeyStroke.getKeyStroke("released V");
        inputMap.put(key, "shootReleased");
        actionMap.put("shootReleased", shootReleased);

        // debug for starting a game as a server or client
        key = KeyStroke.getKeyStroke("O");
        inputMap.put(key, "startServer");
        actionMap.put("startServer", startServer);

        key = KeyStroke.getKeyStroke("P");
        inputMap.put(key, "startClient");
        actionMap.put("startClient", startClient);
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

        graphic.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent me) {
                mouseX = me.getX();
                mouseY = me.getY();
            }
        });

        frame.add(graphic);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();

        graphic.run();
    }
}