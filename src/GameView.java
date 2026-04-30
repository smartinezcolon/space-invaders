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
        
        // Draw Player (Green Rectangle)
        g.setColor(Color.GREEN);
        g.fillRect(model.getPlayerX(), model.getPlayerY(), GameModel.PLAYER_WIDTH, GameModel.PLAYER_HEIGHT);
        
        // Draw Player Bullet (Yellow Rectangle)
        GameModel.Bullet pBullet = model.getPlayerBullet();
        if (pBullet != null) {
            g.setColor(Color.YELLOW);
            g.fillRect((int) pBullet.x, (int) pBullet.y, GameModel.BULLET_WIDTH, GameModel.BULLET_HEIGHT);
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
        
        // Draw Alien Bullets (Orange Rectangles)
        g.setColor(Color.ORANGE);
        List<GameModel.Bullet> aBullets = model.getAlienBullets();
        if (aBullets != null) {
            for (GameModel.Bullet b : aBullets) {
                g.fillRect((int) b.x, (int) b.y, GameModel.BULLET_WIDTH, GameModel.BULLET_HEIGHT);
            }
        }
        
        // Draw HUD (Score and Lives)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + model.getScore(), 20, 30);
        g.drawString("Lives: " + model.getLives(), GameModel.GAME_WIDTH - 120, 30);
        
        // Draw Game Over Message
        if (model.isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            String gameOverText = "GAME OVER";
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
