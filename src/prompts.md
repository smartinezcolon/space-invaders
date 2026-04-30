Prompt 1:
Building Space Invaders MVC
I'm building Space Invaders in Java using Swing, split into three files: GameModel.java, GameView.java, and GameController.java. GameView should extend JPanel and be hosted in a JFrame. GameController should have the main method and wire the three classes together. GameModel must have no Swing imports. For now, just create the three class shells with placeholder comments describing what each class will do. The program should compile and open a blank window.

Prompt 2:
Fill in GameModel.java. The model should track: the player's horizontal position, the alien formation (5 rows of 11), the player's bullet (one at a time), alien bullets, the score, and lives remaining (start with 3). Add logic to: move the player left and right, fire a player bullet if one isn't already in flight, advance the player's bullet each tick, move the alien formation right until the edge then down and reverse, fire alien bullets at random intervals, and detect collisions between bullets and aliens or the player. No Swing imports.

Prompt 3:
Fill in GameView.java. It should take a reference to the model and draw everything the player sees: the player, the alien formation, both sets of bullets, the score, and remaining lives. Show a centered game-over message when the game ends. The view should only read from the model — it must never change game state.

Prompt 4:
Fill in GameController.java. Add keyboard controls so the player can move left and right with the arrow keys and fire with the spacebar. Add a game loop using a Swing timer that updates the model each tick and redraws the view. Stop the loop when the game is over.

Prompt 5:
Create a separate file called ModelTester.java with a main method. It should create a GameModel, call its methods directly, and print PASS or FAIL for each check. Write tests for at least five behaviors: the player cannot move past the left or right edge, firing while a bullet is already in flight does nothing, a bullet that reaches the top is removed, destroying an alien increases the score, and losing all lives triggers the game-over state. No testing libraries — just plain Java.

Prompt 6:
Add a resetGame() method to GameModel that resets the game

Adding this reset game method added the feature of ressetting the game by clicking R on the keyboard. However, I do not like how you do not know that is a feature because it does not say anywhere in the game to select R to reset the game.

My follow up prompt to fix this: 
There is nothing stating to select R to restart the game at Gameover screen. I would like thay feature otherwise the player is not aware of selecting R to reset

This added the feature to Press 'R' to restart the game at the game over screen. 

Prompt 7:
Add a main menu before starting the game that displays "Press Space to Start"

This added a main menu before starting the game. Looks a little too basic but it will do for now. I prefer having at least a main menu, then the game starting right away when you load it up.

Prompt 8:
I noticed when I eliminate all the aliens, nothing happens afterwards. I would like to add a completed, and also would like to implement a next level.

This added a completed screen after eliminating all the aliens, and it also added a next level. 

Prompt 9: Add a UFO that crosses the top of the screen for bonus points

This added a UFO that crosses the top of the screen for bonus points.

Prompt 10: I would like to add powerups that randomly drop down from destroyed aliens, that can give me faster movement, faster bullets, shoot more bullets at once, or more lives. 

This created various types of powerups that randomly drop from destroyed aliens. 

These are the types of powerups it created:
[S] Speed (Cyan): Increases movement speed.
[R] Rapid Fire (Yellow): Increases the speed of your bullets.
[M] Multi-Shot (Pink): Upgrades your cannon to shoot double (and then triple) bullets side-by-side.
[L] Life (Green): Grants an extra life!

Prompt 11: 
I wanted to fix the aliens when reach the bottom, I want it to be gameover, but the aliens just pass through the player.

This added a gameover screen when the aliens reach the bottom. 

From this I made another prompt stating: when the aliens pass me where it triggers the gameover screen, I want it to say the aliens have invaded!

This changed the gameover screen to say "THE ALIENS HAVE INVADED!" instead of "GAME OVER" when the aliens reach the bottom.

Prompt 12:
Add a highscore feature that saves the highscore.

This implemented a highscore feature that saves the highscore. 