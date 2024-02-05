import javax.swing.*;

/**
 *JFrame este o o clasa care este folosita pentru a crea ferestre grafice in GUI.
 * Fereastra(Panel) poate contine butoane, text field, panouri(panel)...
 */
public class GameFrame extends JFrame {
    GameFrame()
    {
        this.add(new GamePanel());//adaug panel GamePanel in cadrul ferestrei GameFrame
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); //determina fereastra sa se ajusteze automat la dimensiunile optime pentru a incadra componentele adaugate.
        this.setVisible(true);
        this.setLocationRelativeTo(null);//pozitioneaza fereastra in mijloucl ecranului
    }
}
