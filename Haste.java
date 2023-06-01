import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Haste extends JPanel {

    // Set up the game window
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private JFrame window;

    // Set up the player character
    private static final int PLAYER_HEALTH = 100;
    private static final ArrayList<String> PLAYER_INVENTORY = new ArrayList<>();
    private static int playerX = 0;
    private static int playerY = 0;
    private static int playerHealth = PLAYER_HEALTH;
    private static ArrayList<String> playerInventory = new ArrayList<>(PLAYER_INVENTORY);

    // Set up the world
    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 1000;
    private static String[][] terrain = new String[WORLD_WIDTH][WORLD_HEIGHT];
    private static ArrayList<Monster> monsters = new ArrayList<>();

    // Set up the camera
    private static int cameraX = 0;
    private static int cameraY = 0;
    private static double cameraZoom = 1.0;

    public static void main(String[] args) {
        // Create the game window
        Haste game = new Haste();
        game.window = new JFrame("Haste");
        game.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        game.window.add(game);
        game.window.addKeyListener(new InputHandler());
        game.window.setVisible(true);
        playerX = WORLD_WIDTH / 2;
        playerY = WORLD_HEIGHT / 2;
        // Generate the terrain
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                terrain[x][y] = generateTerrain(x, y);
            }
        }

        // Start the game loop
        while (true) {
            game.update();
            game.repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String generateTerrain(int x, int y) {
        // Generate the terrain for a given location
        // This function can be customized to generate different types of terrain
        return "grass";
    }

    private void update() {
        // Move the player
        if (InputHandler.isKeyDown(KeyEvent.VK_LEFT)) {
            playerX = Math.max(0, playerX - 1);
        } else if (InputHandler.isKeyDown(KeyEvent.VK_RIGHT)) {
            playerX = Math.min(WORLD_WIDTH - 1, playerX + 1);
        } else if (InputHandler.isKeyDown(KeyEvent.VK_UP)) {
            playerY = Math.max(0, playerY - 1);
        } else if (InputHandler.isKeyDown(KeyEvent.VK_DOWN)) {
            playerY = Math.min(WORLD_HEIGHT - 1, playerY + 1);
        }
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        // Fill the top two-thirds of the screen with blue
        int topHeight = WINDOW_HEIGHT * 2 / 3;
        g2d.setColor(new Color(135, 206, 250)); // Light blue
        g2d.fillRect(0, 0, WINDOW_WIDTH, topHeight);

        // Fill the bottom one-third of the screen with dark green
        int bottomHeight = WINDOW_HEIGHT - topHeight;
        g2d.setColor(new Color(0, 100, 0)); // Dark green
        g2d.fillRect(0, topHeight, WINDOW_WIDTH, bottomHeight);
        // Update the camera
        cameraX = playerX - WINDOW_WIDTH / 2;
        cameraY = playerY - WINDOW_HEIGHT / 2;
        cameraZoom = 1.0;

        System.out.println("cameraX: " + cameraX + ", cameraY: " + cameraY);

        // Ensure that the camera doesn't show terrain outside the boundaries of the
        // world
        cameraX = Math.max(0, Math.min(WORLD_WIDTH - WINDOW_WIDTH, cameraX));
        cameraY = Math.max(0, Math.min(WORLD_HEIGHT - WINDOW_HEIGHT, cameraY));

        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                drawTerrain(g2d, x, y);
            }
        }

        // Draw the player
        drawPlayer(g2d, playerX, playerY);

        // Draw the monsters
        // for (int i = 0; i < monsters.size(); i++) {
        // drawMonster(g2d, monsters.get(i));
        // }
    }

    private void drawTerrain(Graphics2D g, int x, int y) {
        // Only draw terrain that is within the bounds of the camera view
        if (x >= cameraX && x <= cameraX + WINDOW_WIDTH &&
                y >= cameraY && y <= cameraY + WINDOW_HEIGHT) {
            // Draw the terrain for a given location
            // This function can be customized to draw different types of terrain
            g.setColor(new Color(50, 200, 50));
            g.fillRect((x - cameraX) * 50, (y - cameraY) * 50, 50, 50);

            // Check if the player is standing on this terrain block
            if (x == playerX && y == playerY) {
                drawPlayer(g, playerX, playerY);
            }
        }
    }

    private void drawPlayer(Graphics2D g, int x, int y) {
        // Draw the player character
        int screenX = (x - cameraX) + WINDOW_WIDTH / 2 - 20;
        int screenY = (y - cameraY) + WINDOW_HEIGHT / 2 - 20;
        g.setColor(Color.WHITE);
        g.fillOval(screenX, screenY, 40, 40);
    }

    private void drawMonster(Graphics2D g, Monster monster) {
        // Draw a monster
        g.setColor(new Color(200, 50, 50));
        g.fillOval((monster.getX() - cameraX) * 50 + 5, (monster.getY() - cameraY) * 50 + 5, 40, 40);
    }

    private static class InputHandler implements KeyListener {
        private static boolean[] keys = new boolean[256];

        public void keyPressed(KeyEvent e) {
            keys[e.getKeyCode()] = true;
        }

        public void keyReleased(KeyEvent e) {
            keys[e.getKeyCode()] = false;
        }

        public static boolean isKeyDown(int keyCode) {
            return keys[keyCode];
        }

        // Override unused KeyListener methods
        public void keyTyped(KeyEvent e) {
        }
    }

    private static class Monster {
        private int x;
        private int y;

        public Monster(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
