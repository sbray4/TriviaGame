import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CardLayoutFrame extends JFrame {
    private JPanel cardPanel;
    private JButton startButton;
    private JButton usernameButton;
    private JButton playButton;
    private JButton nextButton;
    private JPanel welcomePanel;
    private JPanel usernamePanel;
    private JPanel gamePanel;
    private JPanel resultPanel;
    private CardLayout cardLayout;
    private JTextField inputField;
    private String username = "";
    private JLabel questionLabel;
    private JButton[] optionButtons = new JButton[4];
    private int currentQuestionIdx;
    private List<List<String>> questionPair;
    private List<Integer> correctAnswers;
    private int correctCount = 0;

    public CardLayoutFrame() {
        currentQuestionIdx = 0;
        importQuestions();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        //Welcoming Page
        createWelcomePanel();
        //Username Page
        createUsernamePanel();
        //Game page
        createGamePanel();
        //Results screen
        // CreateResultsPanel();
        //Event handler registration
        ButtonHandler handler = new ButtonHandler();
        startButton.addActionListener(handler);
        nextButton.addActionListener(handler);
        playButton.addActionListener(handler);
        usernameButton.addActionListener(handler);
        //add all panels to cardPanel
        add(cardPanel);
    }

    private void createUsernamePanel() {
        //1. main panel creation
        usernamePanel = new JPanel(new BorderLayout());
        //2. inner username input panel
        JPanel usernameButtonPanel = new JPanel(new FlowLayout());
        inputField = new JTextField("Enter a username"); // add an inputField to the inner panel
        inputField.setFont(new Font("Arial", Font.PLAIN, 24));
        usernameButtonPanel.add(inputField);
        usernameButton = new JButton("Save the name"); // add a button to the inner panel
        usernameButtonPanel.add(usernameButton);
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH); // add the inner panel to main panel
        //3. inner play button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        playButton = new JButton("Play!"); // add a button to the inner play button panel
        buttonPanel.add(playButton);
        usernamePanel.add(buttonPanel, BorderLayout.SOUTH); // add the inner panel to main panel
        cardPanel.add(usernamePanel, "U");
    }

    private void CreateResultsPanel() {
        resultPanel = new JPanel(new GridLayout(2, 1));

        resultPanel.add(new JLabel("Results Screen - " + username, SwingConstants.CENTER));
        resultPanel.add(new JLabel( "You got " + correctCount + " out of " + questionPair.size() + " correct!", SwingConstants.CENTER));
        cardPanel.add(resultPanel, "R");
    }

    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Question #1", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gamePanel.add(questionLabel, BorderLayout.NORTH);
        // gamePanel.add(new JLabel("Quiz Screen"));
        nextButton = new JButton("Go to results");
        gamePanel.add(nextButton, BorderLayout.SOUTH);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton("Option " + (i + 1));
            optionButtons[i].addActionListener(new OptionButtonHandler(i));
            buttonPanel.add(optionButtons[i]);
        }
        gamePanel.add(buttonPanel, BorderLayout.CENTER);
        cardPanel.add(gamePanel, "G");
    }

    private void createWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Welcome Screen", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 50));
        welcomePanel.add(title, BorderLayout.CENTER);
        startButton = new JButton("Start game!");
        startButton.setFont(new Font("Arial", Font.BOLD, 50));
        welcomePanel.add(startButton, BorderLayout.SOUTH);
        cardPanel.add(welcomePanel, "W");
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == startButton) {
                cardLayout.show(cardPanel, "U");
            } else if (e.getSource() == playButton) {
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            } else if (e.getSource() == nextButton) {
                //Results screen
                CreateResultsPanel();
                cardLayout.show(cardPanel, "R");
            } else if (e.getSource() == usernameButton) {
                username = inputField.getText();
                JOptionPane.showMessageDialog(null, "Welcome " + username + "!");
            }
        }
    }

    private class OptionButtonHandler implements ActionListener {
        private int index;

        public OptionButtonHandler(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int correctAnswer = correctAnswers.get(currentQuestionIdx);

            if (index == correctAnswer) {
                correctCount++;
                JOptionPane.showMessageDialog(CardLayoutFrame.this, "Correct!");
            } else {
                JOptionPane.showMessageDialog(CardLayoutFrame.this, "Incorrect.");
            }
            currentQuestionIdx++;

            if (currentQuestionIdx < questionPair.size()) {
                loadNextQuestions();
            } else {
                CreateResultsPanel();
                cardLayout.show(cardPanel, "R");
            }
        }
    }

    private void importQuestions() {
        questionPair = new ArrayList<>();
        correctAnswers = new ArrayList<>();
        questionPair.add(List.of("Question 1: Which country won the 2026 FIFA World Cup?", "Spain", "Argentina", "Brazil", "France"));
        correctAnswers.add(0);
        questionPair.add(List.of("Question 2: How many players make up a basketball team?", "10", "4", "7", "5"));
        correctAnswers.add(3);
        questionPair.add(List.of("Question 3: How long is a marathon?", "24.2 miles", "26.2 miles", "20 miles", "25 miles"));
        correctAnswers.add(1);
        questionPair.add(List.of("Question 4: Who won the baseball World Series in 2025?", "Toronto Blue Jays", "New York Yankees", "Los Angeles Dodgers", "Texas Rangers"));
        correctAnswers.add(2);
        questionPair.add(List.of("Question 5: Recursion always needs a?", "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
        questionPair.add(List.of("Recursion always needs a?", "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
        questionPair.add(List.of("Recursion always needs a?", "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
        questionPair.add(List.of("Recursion always needs a?", "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
        questionPair.add(List.of("Recursion always needs a?", "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
        questionPair.add(List.of("Recursion always needs a?", "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
    }

    private void loadNextQuestions() {
        if (questionPair == null || currentQuestionIdx > questionPair.size() - 1) {
            cardLayout.show(cardPanel, "R");
            return;
        }
        questionLabel.setText((questionPair.get(currentQuestionIdx).get(0)));
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(questionPair.get(currentQuestionIdx).get(i + 1));
        }
    }
}
