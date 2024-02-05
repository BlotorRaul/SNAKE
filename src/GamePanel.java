import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.*;
import java.util.Collections;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener,GamePanelInterface {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    /**
     * fiecare unitate a jocului este de 25 de pixeli
     */
    static final int UNIT_SIZE = 25;
    /**
     * Game_units = ne spune cate astfel de blocuri exista in fereastra noastra
     */
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    /**
     * Delay = frecventa cu care se actualizeaza starea jocului
     * delay mic -> aplicatia se actualizeaza mai des
     * delay mare ->actualizarile sunt mai putin frecvente
     */
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int appleEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    /**
     * constructorul: jocul incepe sa porneasca datorita functiei startGame();
     */
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        /**
         * setFocusable(true) =panoul curent este pregatit sa primeasca informatii de la tastatura
         * Astfel Panoul reactioneaza la tastele pe care le apas.
         */
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        startGame();
    }

    /**
     * se creeaza un mar
     * incepe jocul: Timer este activ
     */
    public void startGame() {
        newApple();
        running = true;
        //aici this se refera la instanta curenta care specifica ca ea
        //este ascultatorul care va raspunde la evenmintele de Timer
        timer = new Timer(DELAY, this);
        timer.start();
    }


    /**
     * nu este apelata de mine pentru ca Swing o apeleaza automat
     * atunci cnad trebuie sa se redeseneze panoul
     * Sau cand se chema explicit functia repaint()(se cheama in actionPerformence)
     * @param g the <code>Graphics</code> object to protect
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * aici se deseneaza totul
     * @param g
     */
    public void draw(Graphics g) {
        if (running) { //daca ruleaza programul
            //desenam patratele: Unit de dimensiune UNIT_SIZE
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            //desenam marul
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            //desenam capul si corpul
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { //capul snake
                    g.setColor(Color.green);
                    //PARAMETRII : poz X, poz Y, latime UNIT_SIZE, inaltime UNIT_SIZE
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else { //corpul snake
                    g.setColor(new Color(45, 180, 0));
                    //culoare random ptr fiecare unitate a snake
                    //g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            //draw the score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            //FontMetrics ofera informatii despre caracteristicile fontului(inaltimea/latimea literelor...)
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + appleEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }

    }

    public void newApple() {
        //sa fie un numar de la 0 la cate unitati is pe orizontala
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        //sa fie un numar de la 0 la cate unitati is pe verticala
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        //corpul se misca in fata(pe urmele capului)
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        //mutam capul in functie de tasta inserata
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;


        }
    }

    public void checkApple() {
        //daca capul are aceleasi coordonate ca marul
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            appleEaten++;
            //appleX si appleY fiind doar o coordonata actualizam aceasta coordonata si astfel
            //face sa se stearga marul 'mancat' si sa mearga altundeva
            newApple();
        }
    }

    public void checkCollisions() {
        //capu isi atinge corpul
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //head touches left border
        if (x[0] < 0) {
            running = false;
        }
        //head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //head touches top border
        if (y[0] < 0) {
            running = false;
        }
        //head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }

    }


    /**
     * background va fi negru si va aparea mare Game OVer si scorul pe care l-ai facut
     * @param g
     */
    public void gameOver(Graphics g) {
        //draw the score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + appleEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + appleEaten)) / 2, g.getFont().getSize());


        //Game over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("GameOver")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    /**
     * MyKeyAdapter este configurat pentru a asculta evenimentele tastaturii și va
     * executa codul din metoda keyPressed atunci când o tastă este apăsată.
     * Aceasta permite jocului să reacționeze la acțiunile utilizatorului la tastatură.
     */
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') { // aceasta linie de cod asigura ca srpele nu se intoarce brusc la 180
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
