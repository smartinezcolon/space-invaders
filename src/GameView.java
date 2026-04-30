import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.List;

/**
 * GameView is responsible for rendering the game state to the screen.
 * It extends JPanel and handles the visual representation of entities.
 */
public class GameView extends JPanel {
    
    private GameModel model;
    
    public GameView(GameModel model) {
        this.model = model;
        
        // Set a default background color and preferred size based on the model's dimensions
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(GameModel.GAME_WIDTH, GameModel.GAME_HEIGHT));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (model == null) return;
        
        if (!model.isGameStarted()) {
            // Draw Main Menu
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String titleText = "SPACE INVADERS";
            FontMetrics fmTitle = g.getFontMetrics();
            g.drawString(titleText, (GameModel.GAME_WIDTH - fmTitle.stringWidth(titleText)) / 2, GameModel.GAME_HEIGHT / 2 - 50);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String startText = "Press Space to Start";
            FontMetrics fmStart = g.getFontMetrics();
            g.drawString(startText, (GameModel.GAME_WIDTH - fmStart.stringWidth(startText)) / 2, GameModel.GAME_HEIGHT / 2 + 20);
            
            String hsText = "High Score: " + model.getHighScore();
            g.drawString(hsText, (GameModel.GAME_WIDTH - fmStart.stringWidth(hsText)) / 2, GameModel.GAME_HEIGHT / 2 + 60);
            return;
        }
        
        // Draw Player (Green Rectangle)
        g.setColor(Color.GREEN);
        g.fillRect(model.getPlayerX(), model.getPlayerY(), GameModel.PLAYER_WIDTH, GameModel.PLAYER_HEIGHT);
        
        // Draw Player Bullets (Yellow Rectangles)
        List<GameModel.Bullet> pBullets = model.getPlayerBullets();
        if (pBullets != null) {
            g.setColor(Color.YELLOW);
            for (GameModel.Bullet b : pBullets) {
                g.fillRect((int) b.x, (int) b.y, GameModel.BULLET_WIDTH, GameModel.BULLET_HEIGHT);
            }
        }
        
        // Draw PowerUps
        List<GameModel.PowerUp> powerUps = model.getPowerUps();
        if (powerUps != null) {
            for (GameModel.PowerUp p : powerUps) {
                switch(p.type) {
                    case SPEED: g.setColor(Color.CYAN); break;
                    case RAPID_FIRE: g.setColor(Color.YELLOW); break;
                    case MULTI_SHOT: g.setColor(Color.PINK); break;
                    case LIFE: g.setColor(Color.GREEN); break;
                }
                g.fillRect((int) p.x, (int) p.y, GameModel.POWERUP_SIZE, GameModel.POWERUP_SIZE);
                
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                String letter = "";
                switch(p.type) {
                    case SPEED: letter = "S"; break;
                    case RAPID_FIRE: letter = "R"; break;
                    case MULTI_SHOT: letter = "M"; break;
                    case LIFE: letter = "L"; break;
                }
                FontMetrics fm = g.getFontMetrics();
                int textX = (int) p.x + (GameModel.POWERUP_SIZE - fm.stringWidth(letter)) / 2;
                int textY = (int) p.y + ((GameModel.POWERUP_SIZE - fm.getHeight()) / 2) + fm.getAscent();
                g.drawString(letter, textX, textY);
            }
        }
        
        // Draw Aliens (Red Rectangles)
        g.setColor(Color.RED);
        GameModel.Alien[][] aliens = model.getAliens();
        if (aliens != null) {
            for (int r = 0; r < GameModel.ALIEN_ROWS; r++) {
                for (int c = 0; c < GameModel.ALIEN_COLS; c++) {
                    GameModel.Alien a = aliens[r][c];
                    if (a.alive) {
                        g.fillRect((int) a.x, (int) a.y, GameModel.ALIEN_WIDTH, GameModel.ALIEN_HEIGHT);
                    }
                }
            }
        }
        
        // Draw UFO
        if (model.isUfoActive()) {
            g.setColor(Color.MAGENTA);
            g.fillRect((int) model.getUfoX(), (int) model.getUfoY(), GameModel.UFO_WIDTH, GameModel.UFO_HEIGHT);
        }
        
        // Draw Alien Bullets (Orange Rectangles)
        g.setColor(Color.ORANGE);
        List<GameModel.Bullet> aBullets = model.getAlienBullets();
        if (aBullets != null) {
            for (GameModel.Bullet b : aBullets) {
                g.fillRect((int) b.x, (int) b.y, GameModel.BULLET_WIDTH, GameModel.BULLET_HEIGHT);
            }
        }
        
        // Draw HUD (Score, High Score, Level, and Lives)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + model.getScore(), 20, 30);
        g.drawString("High Score: " + model.getHighScore(), 200, 30);
        g.drawString("Level: " + model.getLevel(), GameModel.GAME_WIDTH / 2 - 40, 30);
        g.drawString("Lives: " + model.getLives(), GameModel.GAME_WIDTH - 120, 30);
        
        // Draw Level Completed Message
        if (model.isLevelCompleted()) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String levelText = "LEVEL " + model.getLevel() + " COMPLETED";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(levelText);
            int textHeight = fm.getAscent();
            g.drawString(levelText, 
                         (GameModel.GAME_WIDTH - textWidth) / 2, 
                         (GameModel.GAME_HEIGHT + textHeight) / 2 - 30);
                         
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String nextText = "Press 'N' for Next Level";
            FontMetrics fmNext = g.getFontMetrics();
            int nextWidth = fmNext.stringWidth(nextText);
            g.drawString(nextText, 
                         (GameModel.GAME_WIDTH - nextWidth) / 2, 
                         (GameModel.GAME_HEIGHT + textHeight) / 2 + 20);
            return;
        }
        
        // Draw Game Over Message
        if (model.isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 45));
            String gameOverText = model.hasAliensInvaded() ? "THE ALIENS HAVE INVADED!" : "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(gameOverText);
            int textHeight = fm.getAscent();
            g.drawString(gameOverText, 
                         (GameModel.GAME_WIDTH - textWidth) / 2, 
                         (GameModel.GAME_HEIGHT + textHeight) / 2);
                         
            // Draw Restart Instruction
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String restartText = "Press 'R' to Restart";
            FontMetrics fmRestart = g.getFontMetrics();
            int restartTextWidth = fmRestart.stringWidth(restartText);
            g.drawString(restartText, 
                         (GameModel.GAME_WIDTH - restartTextWidth) / 2, 
                         (GameModel.GAME_HEIGHT + textHeight) / 2 + 40);
        }
    }
}
