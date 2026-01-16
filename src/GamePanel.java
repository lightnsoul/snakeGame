import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600; // Breite des Spielfensters
    static final int SCREEN_HEIGHT = 600; // Höhe des Spielfensters
    static final int UNIT_SIZE  = 25; // Größe einer Einheit (Kasten) im Spiel
    static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE); // Gesamtanzahl der Einheiten im Spiel
    static final int DELAY = 80; // Geschwindigkeit/Tempo des Spiels
    final int x[] = new int[GAME_UNITS]; // x-Koordinaten der Schlange
    final int y[] = new int[GAME_UNITS]; // y-Koordinaten der Schlange
    int bodyParts = 3; // Anfangslänge der Schlange

    int applesEaten;
    int appleCount = 3;
    int[] appleX = new int[appleCount]; // x-Koordinaten der Äpfel
    int[] appleY = new int[appleCount]; // y-Koordinaten der Äpfel

    char direction = 'R'; // Anfangsrichtung der Schlange
    boolean running = false;
    boolean started = false;
    Timer timer;
    Random random;

    final Color headColor = new Color(72, 87, 33); // Dunkelgrün für den Kopf der Schlange
    final Color[] bodyColors = new Color[GAME_UNITS]; // Farben für den Körper der Schlange

    GamePanel(){ 
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true); // Damit das Panel KeyEvents empfangen kann
        this.addKeyListener(new MyKeyAdapter()); // Fügt den KeyListener hinzu
    } 
    public void startGame() {
        x[0] = UNIT_SIZE * 5; // Startposition der Schlange in der Mitte des Bildschirms
        y[0] = SCREEN_HEIGHT / 2; // Startposition der Schlange in der Mitte des Bildschirms
        for (int i = 1; i < bodyParts; i++) {
            x[i] = x[0] - i * UNIT_SIZE; // Initiale Positionierung des Körpers hinter dem Kopf
            y[i] = y[0]; // Initiale Positionierung des Körpers hinter dem Kopf
        }

        bodyColors[0] = headColor;
        for (int i = 1; i < GAME_UNITS; i++) bodyColors[i] = randomGreen(); // Initiale Farben für den Körper

        for (int i = 0; i < appleCount; i++) {
            newApple(i);
        }
        running = true;
        timer = new Timer(DELAY, this); // Timer initialisieren
        timer.start();
    }
    @Override // Paint-Methode zum Zeichnen des Spiels
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Ruft die übergeordnete Methode auf
        draw(g);
    }

    public void draw(Graphics g) { // Zeichnet das Spiel
        if(!started) {
            // Starte Spiel
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            FontMetrics fm = getFontMetrics(g.getFont());
            String title = "Drücke X zum Starten";
            g.drawString(title, (SCREEN_WIDTH - fm.stringWidth(title)) / 2, SCREEN_HEIGHT / 2 - 20);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            FontMetrics fm2 = getFontMetrics(g.getFont());
            String hint = "Nutze Arrow Keys oder WASD zur Steuerung";
            g.drawString(hint, (SCREEN_WIDTH - fm2.stringWidth(hint)) / 2, SCREEN_HEIGHT / 2 + 10);
            return;
        }

        if(running) {
            for(int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++){
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            g.setColor(Color.red);
            for (int i = 0; i < appleCount; i++) {
                g.fillOval(appleX[i], appleY[i], UNIT_SIZE, UNIT_SIZE);
            }

            for(int i = 0; i < bodyParts; i++){
                if(i==0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(bodyColors[i]);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 26));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }

    public void newApple(int index) { // Generiert eine neue Position für einen Apfel
        appleX[index] = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY[index] = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for(int i = bodyParts; i > 0; i--){ // Verschiebt den Körper der Schlange
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        for (int i = bodyParts; i > 1; i--) {
            bodyColors[i] = bodyColors[i-1];
        }
        // introduce a new random green right behind the head
        if (bodyParts >= 1) bodyColors[1] = randomGreen(); // Neue Farbe direkt hinter dem Kopf

        switch(direction){ // Bewegt den Kopf der Schlange in die aktuelle Richtung
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
        for (int i = 0; i < appleCount; i++) {
            if ((x[0] == appleX[i]) && (y[0] == appleY[i])) { // Überprüft ob die Schlange den Apfel isst
                bodyParts++; // Erhöht die Länge der Schlange
                applesEaten++; // Erhöht den Punktestand
                if (bodyParts < bodyColors.length) bodyColors[bodyParts] = randomGreen(); // Neue Farbe für den neuen Körperteil
                newApple(i);
            }
        }
    }

    public void checkCollisions() {
        // Überprüft ob die Schnecke in sich selbst reingelaufen ist
        for(int i = bodyParts; i > 0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; // Spiel beenden
            }
        }
        //Überprüft ob den Schneckenkopf linken Rand berührt
        if(x[0] < 0) {
            running = false; 
        }
        //Überprüft ob den Schneckenkopf rechten Rand berührt
        if(x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //Überprüft ob den Scheckenhopf obere Grenze berührt
        if(y[0] < 0) {
            running = false;
        }
        //Überprüft ob den Scheckenhopf untere Grenze berührt
        if(y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if(!running) { // Wenn das Spiel beendet ist, stoppt der Timer
            timer.stop();
        }
    }

    public void gameOver(Graphics g) { // Anzeige des Game Over Bildschirms
        //Score Display
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

        //Spiel vorbei Text
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over!", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT / 2);
    }

    @Override // Aktion bei Timer-Event
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint(); // Ruft die paintComponent-Methode erneut auf
    }

    public class MyKeyAdapter extends KeyAdapter { // KeyListener für Tastatureingaben
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(!started) {
                if(key == KeyEvent.VK_X) { // Startet das Spiel bei Drücken von 'X'
                    started = true;
                    startGame();
                }
                return;
            }
            switch(key) {
                case KeyEvent.VK_LEFT: // Linke Pfeiltaste
                case KeyEvent.VK_A: // 'A' Taste
                    if(direction != 'R') direction = 'L'; // Verhindert das Umdrehen der Schlange
                    break; // Beendet den switch-Block
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if(direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if(direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if(direction != 'U') direction = 'D';
                    break;
            }

        }
    }

    private Color randomGreen() { // Generiert eine zufällige Grüntonfarbe
        float hue = 0.27f + random.nextFloat() * 0.18f; // 0.27..0.45 (Grüntöne)
        float sat = 0.6f + random.nextFloat() * 0.4f;   // 0.6..1.0 (Sättigung)
        float bri = 0.5f + random.nextFloat() * 0.5f;   // 0.5..1.0 (Helligkeit)
        return Color.getHSBColor(hue, sat, bri); // Gibt die generierte Farbe zurück
    }
}
