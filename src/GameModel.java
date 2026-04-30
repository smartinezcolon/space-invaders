import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * GameModel handles the logic and state of the Space Invaders game.
 * It does not contain any Swing components or dependencies.
 */
public class GameModel {

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 20;
    public static final int BASE_PLAYER_SPEED = 5;
    
    public static final int ALIEN_WIDTH = 40;
    public static final int ALIEN_HEIGHT = 30;
    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 11;
    public static final double BASE_ALIEN_SPEED_X = 2.0;
    public static final double ALIEN_DROP = 20.0;
    
    public static final int UFO_WIDTH = 60;
    public static final int UFO_HEIGHT = 20;
    public static final double UFO_SPEED = 3.0;
    
    public static final int BULLET_WIDTH = 5;
    public static final int BULLET_HEIGHT = 15;
    public static final int BASE_BULLET_SPEED = 10;
    
    public static final int POWERUP_SIZE = 15;
    public static final double POWERUP_SPEED = 2.5;

    private boolean gameStarted;
    private int playerX;
    private int score;
    private int highScore;
    private boolean hasSavedHighScore;
    private int lives;
    private int level = 1;
    private boolean levelCompleted;
    private double currentAlienSpeedX;
    private double currentAlienFireRate;
    
    private int playerSpeed;
    private int playerBulletSpeed;
    private int multiShotLevel;
    private int fireCooldown;
    
    private double ufoX;
    private double ufoY;
    private boolean ufoActive;
    private int ufoDirection;
    
    private boolean aliensInvaded;
    
    private Alien[][] aliens;
    private double alienDirectionX = 1.0; // 1 for right, -1 for left
    
    private List<Bullet> playerBullets;
    private List<Bullet> alienBullets;
    private List<PowerUp> powerUps;
    
    private Random random;
    
    public enum PowerUpType { SPEED, RAPID_FIRE, MULTI_SHOT, LIFE }
    
    public static class PowerUp {
        public double x, y;
        public PowerUpType type;
        public PowerUp(double x, double y, PowerUpType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

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
        loadHighScore();
        resetGame();
    }

    private void loadHighScore() {
        try (Scanner scanner = new Scanner(new File("highscore.txt"))) {
            if (scanner.hasNextInt()) {
                highScore = scanner.nextInt();
            }
        } catch (IOException e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (PrintWriter out = new PrintWriter(new FileWriter("highscore.txt"))) {
            out.println(highScore);
        } catch (IOException e) {
            System.err.println("Failed to save highscore");
        }
    }

    public void resetGame() {
        gameStarted = false;
        level = 1;
        levelCompleted = false;
        playerX = GAME_WIDTH / 2 - PLAYER_WIDTH / 2;
        score = 0;
        lives = 3;
        playerSpeed = BASE_PLAYER_SPEED;
        playerBulletSpeed = BASE_BULLET_SPEED;
        multiShotLevel = 1;
        fireCooldown = 0;
        playerBullets = new ArrayList<>();
        alienBullets = new ArrayList<>();
        powerUps = new ArrayList<>();
        alienDirectionX = 1.0;
        ufoActive = false;
        aliensInvaded = false;
        hasSavedHighScore = false;
        
        updateDifficulty();
        initAliens();
    }
    
    public void nextLevel() {
        level++;
        levelCompleted = false;
        playerBullets.clear();
        alienBullets.clear();
        powerUps.clear();
        alienDirectionX = 1.0;
        ufoActive = false;
        aliensInvaded = false;
        
        updateDifficulty();
        initAliens();
    }

    private void updateDifficulty() {
        currentAlienSpeedX = BASE_ALIEN_SPEED_X + (level - 1) * 0.5;
        currentAlienFireRate = 0.0005 + (level - 1) * 0.0002;
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

    public void startGame() { gameStarted = true; }
    public boolean isGameStarted() { return gameStarted; }
    public boolean isLevelCompleted() { return levelCompleted; }
    public boolean hasAliensInvaded() { return aliensInvaded; }
    public int getLevel() { return level; }
    public int getHighScore() { return highScore; }
    
    public boolean isUfoActive() { return ufoActive; }
    public double getUfoX() { return ufoX; }
    public double getUfoY() { return ufoY; }
    
    public List<Bullet> getPlayerBullets() { return playerBullets; }
    public List<PowerUp> getPowerUps() { return powerUps; }

    // --- Actions ---

    public void movePlayerLeft() {
        playerX -= playerSpeed;
        if (playerX < 0) {
            playerX = 0;
        }
    }

    public void movePlayerRight() {
        playerX += playerSpeed;
        if (playerX > GAME_WIDTH - PLAYER_WIDTH) {
            playerX = GAME_WIDTH - PLAYER_WIDTH;
        }
    }

    public void firePlayerBullet() {
        if (fireCooldown <= 0) {
            int playerY = GAME_HEIGHT - PLAYER_HEIGHT - 10;
            if (multiShotLevel == 1) {
                playerBullets.add(new Bullet(playerX + PLAYER_WIDTH / 2.0 - BULLET_WIDTH / 2.0, playerY - BULLET_HEIGHT, false));
            } else if (multiShotLevel == 2) {
                playerBullets.add(new Bullet(playerX, playerY - BULLET_HEIGHT, false));
                playerBullets.add(new Bullet(playerX + PLAYER_WIDTH - BULLET_WIDTH, playerY - BULLET_HEIGHT, false));
            } else {
                playerBullets.add(new Bullet(playerX, playerY - BULLET_HEIGHT, false));
                playerBullets.add(new Bullet(playerX + PLAYER_WIDTH / 2.0 - BULLET_WIDTH / 2.0, playerY - BULLET_HEIGHT, false));
                playerBullets.add(new Bullet(playerX + PLAYER_WIDTH - BULLET_WIDTH, playerY - BULLET_HEIGHT, false));
            }
            fireCooldown = 15; // 15 ticks cooldown (~250ms)
        }
    }

    // --- Game Loop Update ---

    public void update() {
        if (!gameStarted) return;
        if (lives <= 0) {
            if (!hasSavedHighScore) {
                if (score > highScore) {
                    highScore = score;
                }
                saveHighScore();
                hasSavedHighScore = true;
            }
            return; // Game over state
        }
        if (levelCompleted) return; // Waiting to go to next level

        if (fireCooldown > 0) fireCooldown--;

        updateBullets();
        updateAliens();
        updateUFO();
        updatePowerUps();
        checkCollisions();
    }
    
    private void updateUFO() {
        if (!ufoActive) {
            if (random.nextDouble() < 0.002) { // 0.2% chance per tick to spawn
                ufoActive = true;
                ufoY = 20;
                if (random.nextBoolean()) {
                    ufoX = -UFO_WIDTH;
                    ufoDirection = 1;
                } else {
                    ufoX = GAME_WIDTH;
                    ufoDirection = -1;
                }
            }
        } else {
            ufoX += UFO_SPEED * ufoDirection;
            if (ufoX > GAME_WIDTH + UFO_WIDTH || ufoX < -UFO_WIDTH - UFO_WIDTH) {
                ufoActive = false;
            }
        }
    }

    private void updatePowerUps() {
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp p = powerUps.get(i);
            p.y += POWERUP_SPEED;
            if (p.y > GAME_HEIGHT) {
                powerUps.remove(i);
                i--;
            }
        }
    }

    private void updateBullets() {
        // Player bullets
        for (int i = 0; i < playerBullets.size(); i++) {
            Bullet b = playerBullets.get(i);
            b.y -= playerBulletSpeed;
            if (b.y + BULLET_HEIGHT < 0) {
                playerBullets.remove(i);
                i--;
            }
        }
        
        // Alien bullets
        for (int i = 0; i < alienBullets.size(); i++) {
            Bullet b = alienBullets.get(i);
            b.y += BASE_BULLET_SPEED / 2.0; // Alien bullets move a bit slower
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
                    a.x += currentAlienSpeedX * alienDirectionX;
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
                        if (a.y + ALIEN_HEIGHT >= GAME_HEIGHT - PLAYER_HEIGHT - 10) {
                            aliensInvaded = true;
                            lives = 0; // Aliens reached the bottom, game over
                        }
                    }
                }
            }
        }
        
        // Randomly fire alien bullets
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                Alien a = aliens[r][c];
                // Only alive aliens can fire, and give a low probability per frame
                if (a.alive && random.nextDouble() < currentAlienFireRate) {
                    alienBullets.add(new Bullet(a.x + ALIEN_WIDTH / 2.0 - BULLET_WIDTH / 2.0, a.y + ALIEN_HEIGHT, true));
                }
            }
        }
    }

    private void checkCollisions() {
        // Player bullets hitting alien
        for (int i = 0; i < playerBullets.size(); i++) {
            Bullet pb = playerBullets.get(i);
            boolean hit = false;
            for (int r = 0; r < ALIEN_ROWS && !hit; r++) {
                for (int c = 0; c < ALIEN_COLS && !hit; c++) {
                    Alien a = aliens[r][c];
                    if (a.alive && rectIntersect(pb.x, pb.y, BULLET_WIDTH, BULLET_HEIGHT,
                                                 a.x, a.y, ALIEN_WIDTH, ALIEN_HEIGHT)) {
                        a.alive = false;
                        playerBullets.remove(i);
                        i--;
                        score += 10;
                        if (score > highScore) highScore = score;
                        hit = true;
                        
                        // Spawn powerup? (10% chance)
                        if (random.nextDouble() < 0.10) {
                            PowerUpType type = PowerUpType.values()[random.nextInt(PowerUpType.values().length)];
                            powerUps.add(new PowerUp(a.x + ALIEN_WIDTH / 2.0 - POWERUP_SIZE / 2.0, a.y + ALIEN_HEIGHT, type));
                        }
                    }
                }
            }
        }
        
        // Player bullets hitting UFO
        for (int i = 0; i < playerBullets.size(); i++) {
            Bullet pb = playerBullets.get(i);
            if (ufoActive && rectIntersect(pb.x, pb.y, BULLET_WIDTH, BULLET_HEIGHT,
                              ufoX, ufoY, UFO_WIDTH, UFO_HEIGHT)) {
                ufoActive = false;
                playerBullets.remove(i);
                i--;
                score += (random.nextInt(3) + 1) * 50; // Bonus points
                if (score > highScore) highScore = score;
            }
        }
        
        // Player catching powerups
        int playerY = GAME_HEIGHT - PLAYER_HEIGHT - 10;
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp p = powerUps.get(i);
            if (rectIntersect(p.x, p.y, POWERUP_SIZE, POWERUP_SIZE, playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                switch(p.type) {
                    case SPEED: playerSpeed = Math.min(playerSpeed + 2, 10); break;
                    case RAPID_FIRE: playerBulletSpeed = Math.min(playerBulletSpeed + 5, 20); break;
                    case MULTI_SHOT: multiShotLevel = Math.min(multiShotLevel + 1, 3); break;
                    case LIFE: lives++; break;
                }
                powerUps.remove(i);
                i--;
            }
        }
        
        // Alien bullets hitting player
        for (int i = 0; i < alienBullets.size(); i++) {
            Bullet b = alienBullets.get(i);
            if (rectIntersect(b.x, b.y, BULLET_WIDTH, BULLET_HEIGHT,
                              playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                lives--;
                alienBullets.remove(i);
                i--;
            }
        }
        
        checkLevelCompletion();
    }

    private void checkLevelCompletion() {
        boolean allDead = true;
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliens[r][c].alive) {
                    allDead = false;
                    break;
                }
            }
        }
        if (allDead) {
            levelCompleted = true;
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
    public List<Bullet> getAlienBullets() { return alienBullets; }
    public boolean isGameOver() { return lives <= 0; }
}
