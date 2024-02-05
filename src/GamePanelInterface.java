import java.awt.*;

public interface GamePanelInterface {
    void startGame();
    void newApple();
    void move();
    void checkApple();
    void checkCollisions();
    void gameOver(Graphics g);
}
