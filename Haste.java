import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
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
    private static final int WORLD_WIDTH = WINDOW_WIDTH;
    private static final int WORLD_HEIGHT = WINDOW_HEIGHT;
    private static String[][] terrain = new String[WORLD_WIDTH][WORLD_HEIGHT];
    // private static ArrayList<Monster> monsters = new ArrayList<>();

    // Set up the camera
    private static int cameraX = 0;
    private static int cameraY = 0;
    private static double cameraZoom = 1.0;

    // Jump variables
    private static boolean isJumping = false;
    private static int jumpHeight = 100;
    private static int jumpSpeed = 5;
    private static int currentJump = 0;

    // Background image
    private BufferedImage backgroundImage;

    public Haste() {
        try {
            backgroundImage = ImageIO.read(new File("dojo.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        } else if (InputHandler.isKeyDown(KeyEvent.VK_SPACE) && !isJumping) {
            jump();
        }

        // Update the jump
        if (isJumping) {
            currentJump += jumpSpeed;
            playerY = playerY - jumpSpeed;
            if (currentJump >= jumpHeight) {
                isJumping = false;
            }
        } else {
            // Apply gravity when not jumping
            playerY = Math.min(playerY + jumpSpeed, WORLD_HEIGHT - 1);
        }
    }

    private void jump() {
        isJumping = true;
        currentJump = 0;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the background image
        g2d.drawImage(backgroundImage, 0, 0, null);

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

    // private void drawMonster(Graphics2D g, Monster monster) {
    // // Draw a monster
    // g.setColor(new Color(200, 50, 50));
    // g.fillOval((monster.getX() - cameraX) + WINDOW_WIDTH / 2 - 20,
    // (monster.getY() - cameraY) + WINDOW_HEIGHT / 2 - 20, 40, 40);
    // }
}

class InputHandler implements KeyListener {

    private static boolean[] keys = new boolean[256];

    public static boolean isKeyDown(int keyCode) {
        if (keyCode >= 0 && keyCode < 256) {
            return keys[keyCode];
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() >= 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() >= 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
