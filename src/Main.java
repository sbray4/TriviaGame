import javax.swing.WindowConstants;

public class Main {
    public static void main(String[] args) {
        CardLayoutFrame frame = new CardLayoutFrame();
        frame.setTitle("Trivia Game");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
