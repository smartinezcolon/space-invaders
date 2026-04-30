public class ModelTester {

    public static void main(String[] args) {
        System.out.println("Running Model Tests...\n");
        
        testPlayerBounds();
        testBulletRateLimiting();
        testBulletRemoval();
        testAlienDestruction();
        testGameOver();
    }

    private static void assertTest(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
        } else {
            System.out.println("[FAIL] " + testName);
        }
    }

    private static void testPlayerBounds() {
        GameModel model = new GameModel();
        model.startGame();
        
        // Move left far beyond 0
        for (int i = 0; i < 200; i++) model.movePlayerLeft();
        boolean leftBoundOk = model.getPlayerX() == 0;
        
        // Move right far beyond GAME_WIDTH
        for (int i = 0; i < 200; i++) model.movePlayerRight();
        int expectedMaxX = GameModel.GAME_WIDTH - GameModel.PLAYER_WIDTH;
        boolean rightBoundOk = model.getPlayerX() == expectedMaxX;
        
        assertTest("Player cannot move past left or right edge", leftBoundOk && rightBoundOk);
    }

    private static void testBulletRateLimiting() {
        GameModel model = new GameModel();
        model.startGame();
        model.firePlayerBullet();
        GameModel.Bullet bullet1 = model.getPlayerBullet();
        
        // Attempt to fire again
        model.firePlayerBullet();
        GameModel.Bullet bullet2 = model.getPlayerBullet();
        
        assertTest("Firing while bullet is already in flight does nothing", bullet1 != null && bullet1 == bullet2);
    }

    private static void testBulletRemoval() {
        GameModel model = new GameModel();
        model.startGame();
        
        // Move player to the far left edge to avoid hitting any aliens
        for (int i = 0; i < 200; i++) model.movePlayerLeft();
        
        model.firePlayerBullet();
        boolean bulletExisted = (model.getPlayerBullet() != null);
        
        // Advance time so bullet goes off the top
        for (int i = 0; i < 100; i++) {
            model.update();
        }
        
        // Score should be 0 because we didn't hit any aliens
        assertTest("Bullet reaching top is removed", bulletExisted && model.getPlayerBullet() == null && model.getScore() == 0);
    }

    private static void testAlienDestruction() {
        GameModel model = new GameModel();
        model.startGame();
        int initialScore = model.getScore();
        
        // Player starts at x=375. Move left 55 times to x=100.
        // This ensures the fired bullet will intercept the first column of aliens as they move right.
        for (int i = 0; i < 55; i++) {
            model.movePlayerLeft();
        }
        
        model.firePlayerBullet();
        
        // Tick until score changes or timeout
        int ticks = 0;
        while (model.getScore() == initialScore && ticks < 100) {
            model.update();
            ticks++;
        }
        
        assertTest("Destroying an alien increases the score", model.getScore() > initialScore);
    }

    private static void testGameOver() {
        GameModel model = new GameModel();
        model.startGame();
        
        // Simulate getting hit 3 times
        for (int i = 0; i < 3; i++) {
            // Add a bullet right above the player
            model.getAlienBullets().add(new GameModel.Bullet(model.getPlayerX() + 20, model.getPlayerY() - 5, true));
            
            // Update a few times to let the bullet hit
            for (int j = 0; j < 5; j++) {
                model.update();
            }
        }
        
        assertTest("Losing all lives triggers the game-over state", model.isGameOver());
    }
}
