import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

/**
 * GameView is responsible for rendering the game state to the screen.
 * It extends JPanel and handles the visual representation of entities.
 */
public class GameView extends JPanel {
    
    public GameView() {
        // Set a default background color and preferred size
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
        
        // TODO: Add rendering logic in paintComponent(Graphics g)
    }
}
