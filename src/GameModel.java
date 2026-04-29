import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameModel handles the logic and state of the Space Invaders game.
 * It does not contain any Swing components or dependencies.
 */
public class GameModel {

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 20;
    public static final int PLAYER_SPEED = 5;
    
    public static final int ALIEN_WIDTH = 40;
    public static final int ALIEN_HEIGHT = 30;
    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 11;
    public static final double ALIEN_SPEED_X = 2.0;
    public static final double ALIEN_DROP = 20.0;
    
    public static final int BULLET_WIDTH = 5;
    public static final int BULLET_HEIGHT = 15;
    public static final int BULLET_SPEED = 10;

    private int playerX;
    private int score;
    private int lives;
    
    private Alien[][] aliens;
    private double alienDirectionX = 1.0; // 1 for right, -1 for left
    
    private Bullet playerBullet;
    private List<Bullet> alienBullets;
    
    private Random random;

    public static class Alien {
        public double x, y;
        public boolean alive;
        public Alien(double x, double y) {
            this.x = x;
            this.y = y;
            this.alive = true;
        }
    }

    public static class Bullet {
        public double x, y;
        public boolean isAlien;
        public Bullet(double x, double y, boolean isAlien) {
            this.x = x;
            this.y = y;
            this.isAlien = isAlien;
        }
    }

    public GameModel() {
        random = new Random();
        resetGame();
    }

    public void resetGame() {
        playerX = GAME_WIDTH / 2 - PLAYER_WIDTH / 2;
        score = 0;
        lives = 3;
        playerBullet = null;
        alienBullets = new ArrayList<>();
        alienDirectionX = 1.0;
        
        initAliens();
    }

    private void initAliens() {
        aliens = new Alien[ALIEN_ROWS][ALIEN_COLS];
        int startX = 50;
        int startY = 50;
        int paddingX = 20;
        int paddingY = 20;
        
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                double ax = startX + c * (ALIEN_WIDTH + paddingX);
                double ay = startY + r * (ALIEN_HEIGHT + paddingY);
                aliens[r][c] = new Alien(ax, ay);
            }
        }
    }

    // --- Actions ---

    public void movePlayerLeft() {
        playerX -= PLAYER_SPEED;
        if (playerX < 0) {
            playerX = 0;
        }
    }

    public void movePlayerRight() {
        playerX += PLAYER_SPEED;
        if (playerX > GAME_WIDTH - PLAYER_WIDTH) {
            playerX = GAME_WIDTH - PLAYER_WIDTH;
        }
    }

    public void firePlayerBullet() {
        if (playerBullet == null) {
            // Spawn bullet just above the player
            int playerY = GAME_HEIGHT - PLAYER_HEIGHT - 10;
            playerBullet = new Bullet(playerX + PLAYER_WIDTH / 2.0 - BULLET_WIDTH / 2.0, playerY - BULLET_HEIGHT, false);
        }
    }

    // --- Game Loop Update ---

    public void update() {
        if (lives <= 0) return; // Game over state

        updateBullets();
        updateAliens();
        checkCollisions();
    }

    private void updateBullets() {
        // Player bullet
        if (playerBullet != null) {
            playerBullet.y -= BULLET_SPEED;
            if (playerBullet.y + BULLET_HEIGHT < 0) {
                playerBullet = null; // off screen
            }
        }
        
        // Alien bullets
        for (int i = 0; i < alienBullets.size(); i++) {
            Bullet b = alienBullets.get(i);
            b.y += BULLET_SPEED / 2.0; // Alien bullets move a bit slower
            if (b.y > GAME_HEIGHT) {
                alienBullets.remove(i);
                i--;
            }
        }
    }

    private void updateAliens() {
        boolean hitEdge = false;
        
        // Move horizontally
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                Alien a = aliens[r][c];
                if (a.alive) {
                    a.x += ALIEN_SPEED_X * alienDirectionX;
                    if (a.x <= 0 || a.x + ALIEN_WIDTH >= GAME_WIDTH) {
                        hitEdge = true;
                    }
                }
            }
        }
        
        // Drop down and reverse direction if edge hit
        if (hitEdge) {
            alienDirectionX *= -1;
            for (int r = 0; r < ALIEN_ROWS; r++) {
                for (int c = 0; c < ALIEN_COLS; c++) {
                    Alien a = aliens[r][c];
                    if (a.alive) {
                        a.y += ALIEN_DROP;
                    }
                }
            }
        }
        
        // Randomly fire alien bullets
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                Alien a = aliens[r][c];
                // Only alive aliens can fire, and give a low probability per frame
                if (a.alive && random.nextDouble() < 0.0005) {
                    alienBullets.add(new Bullet(a.x + ALIEN_WIDTH / 2.0 - BULLET_WIDTH / 2.0, a.y + ALIEN_HEIGHT, true));
                }
            }
        }
    }

    private void checkCollisions() {
        // Player bullet hitting alien
        if (playerBullet != null) {
            boolean hit = false;
            for (int r = 0; r < ALIEN_ROWS && !hit; r++) {
                for (int c = 0; c < ALIEN_COLS && !hit; c++) {
                    Alien a = aliens[r][c];
                    if (a.alive && rectIntersect(playerBullet.x, playerBullet.y, BULLET_WIDTH, BULLET_HEIGHT,
                                                 a.x, a.y, ALIEN_WIDTH, ALIEN_HEIGHT)) {
                        a.alive = false;
                        playerBullet = null;
                        score += 10;
                        hit = true;
                    }
                }
            }
        }
        
        // Alien bullets hitting player
        int playerY = GAME_HEIGHT - PLAYER_HEIGHT - 10; // Same as where player is rendered
        for (int i = 0; i < alienBullets.size(); i++) {
            Bullet b = alienBullets.get(i);
            if (rectIntersect(b.x, b.y, BULLET_WIDTH, BULLET_HEIGHT,
                              playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                lives--;
                alienBullets.remove(i);
                i--;
            }
        }
    }

    private boolean rectIntersect(double x1, double y1, double w1, double h1,
                                  double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    // --- Getters for View ---
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return GAME_HEIGHT - PLAYER_HEIGHT - 10; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public Alien[][] getAliens() { return aliens; }
    public Bullet getPlayerBullet() { return playerBullet; }
    public List<Bullet> getAlienBullets() { return alienBullets; }
    public boolean isGameOver() { return lives <= 0; }
}
