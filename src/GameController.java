import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * GameController contains the main method and wires the Model and View together.
 * It handles user input, initializes the game loop, and updates the view.
 */
public class GameController {
    
    private GameModel model;
    private GameView view;
    private JFrame frame;
    
    public GameController() {
        // Initialize the Model and View
        model = new GameModel();
        view = new GameView();
        
        // Set up the JFrame to host the GameView
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setResizable(false);
    }
    
    public void start() {
        // Make the window visible and start the game loop
        frame.setVisible(true);
        
        // TODO: Implement a game loop (e.g., using javax.swing.Timer or a dedicated thread)
    }
    
    public static void main(String[] args) {
        // Run UI initialization on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.start();
        });
    }
}
