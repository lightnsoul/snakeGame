import javax.swing.*;

public class GameFrame extends JFrame {
    GameFrame() {
        this.add(new GamePanel()); // Fügt das GamePanel zum Frame hinzu
        this.setTitle("Snake Game: The Return of The Ominous Green Longbodied Creature"); // Setzt den Titel des Fensters
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Beendet das Programm beim Schließen des Fensters
        this.setResizable(false); // Verhindert das Ändern der Fenstergröße
        this.pack(); // Passt die Fenstergröße an den Inhalt an
        this.setVisible(true); // Macht das Fenster sichtbar
        this.setLocationRelativeTo(null); // Zentriert das Fenster auf dem Bildschirm
    }
}
