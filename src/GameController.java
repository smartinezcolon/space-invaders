import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * GameController contains the main method and wires the Model and View together.
 * It handles user input, initializes the game loop, and updates the view.
 */
public class GameController {
    
    private GameModel model;
    private GameView view;
    private JFrame frame;
    private Timer gameLoop;
    
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    public GameController() {
        // Initialize the Model and View
        model = new GameModel();
        view = new GameView(model);
        
        // Set up the JFrame to host the GameView
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setResizable(false);
        
        setupInput();
        
        // Setup game loop targeting ~60 FPS (16ms per tick)
        gameLoop = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Apply continuous movement based on key state
                if (leftPressed) {
                    model.movePlayerLeft();
                }
                if (rightPressed) {
                    model.movePlayerRight();
                }
                
                // Advance the game logic by one tick
                model.update();
                
                // Repaint the screen with the new state
                view.repaint();
                
                if (model.isGameOver()) {
                    gameLoop.stop();
                }
            }
        });
    }
    
    private void setupInput() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!model.isGameStarted()) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        model.startGame();
                        if (!gameLoop.isRunning()) {
                            gameLoop.start();
                        }
                    }
                } else {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        model.resetGame();
                        if (!gameLoop.isRunning()) {
                            gameLoop.start();
                        }
                    } else if (model.isLevelCompleted()) {
                        if (e.getKeyCode() == KeyEvent.VK_N) {
                            model.nextLevel();
                        }
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                            leftPressed = true;
                        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            rightPressed = true;
                        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                            model.firePlayerBullet();
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftPressed = false;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightPressed = false;
                }
            }
        });
        
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }
    
    public void start() {
        // Make the window visible and start the game loop
        frame.setVisible(true);
        gameLoop.start();
    }
    
    public static void main(String[] args) {
        // Run UI initialization on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.start();
        });
    }
}
