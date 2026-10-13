import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class CardLayoutFrame extends JFrame {
    private JPanel cardPanel;
    private JButton startButton;
    private JButton usernameButton;
    private JButton playButton;
    private JButton playAgainButton;
//    private JButton nextButton; // don't need "Go to results" button anymore
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
    private JComboBox<QuestionCategory> categoryBox;
    private QuestionCategory selectedCategory = QuestionCategory.Sports; // Default value
    private Timer questionTimer;
    private JLabel timerLabel;
    private int timeLeft;
    private TimerHandler timerHandler;

    private static final Color BACKGROUND_COLOR = new Color(190, 201, 212);
    private static final Color FOREGROUND_COLOR = new Color(242, 245, 247);
    private static final Color BUTTON_COLOR = new Color(205, 208, 210);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BUTTON_TEXT_COLOR = new Color(35, 42, 50);

    public CardLayoutFrame() {
        applyTheme();
        currentQuestionIdx = 0;
        //importQuestions(); // do later because it relies on the selected category
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        //Welcoming Page
        createWelcomePanel();
        //Username Page
        createUsernamePanel();
        //Game page
        createGamePanel();
        //Results screen
        // CreateResultsPanel(); // do later because it depends on the questions being added already
        // Instantiate playAgainButton here so it's not null when adding ActionListener
        playAgainButton = new JButton("Play Again!");
        //Event handler registration
        ButtonHandler handler = new ButtonHandler();
        startButton.addActionListener(handler);
//        nextButton.addActionListener(handler);
        playButton.addActionListener(handler);
        usernameButton.addActionListener(handler);
        playAgainButton.addActionListener(handler);
        timerHandler = new TimerHandler();
        //add all panels to cardPanel
        add(cardPanel);
    }

    private enum QuestionCategory {
        Sports,
        Math,
    }

    private void createUsernamePanel() {
        //1. main panel creation
        usernamePanel = new JPanel(new BorderLayout());
        //2. inner username input panel
        JPanel usernameButtonPanel = new JPanel(new FlowLayout());
        inputField = new JTextField("Enter a username"); // add an inputField to the inner panel
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputField.addFocusListener(new UsernameFocusAdapter());
        usernameButtonPanel.add(inputField);
        usernameButton = new JButton("Save the name"); // add a button to the inner panel
        usernameButtonPanel.add(usernameButton);
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH); // add the inner panel to main panel
        //3. inner play button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        playButton = new JButton("Play!"); // add a button to the inner play button panel
        playButton.setFont(new Font("Arial", Font.BOLD, 36));
        buttonPanel.add(playButton);
        usernamePanel.add(buttonPanel, BorderLayout.SOUTH); // add the inner panel to main panel

        //4. question category dropdown
        JPanel categoryPanel = new JPanel(new FlowLayout());
        categoryBox = new JComboBox<>(QuestionCategory.values());
        categoryPanel.add(categoryBox);
        usernamePanel.add(categoryPanel, BorderLayout.CENTER);

        cardPanel.add(usernamePanel, "U");
    }

    private void CreateResultsPanel() {
        stopQuestionTimer();

        // Remove old resultPanel
        if (resultPanel != null) {
            cardPanel.remove(resultPanel);
        }

        resultPanel = new JPanel(new GridLayout(3, 1));

        resultPanel.add(new JLabel("Results - " + username, SwingConstants.CENTER));
        resultPanel.add(new JLabel( "You got " + correctCount + " out of " + questionPair.size() + " correct!", SwingConstants.CENTER));
        cardPanel.add(resultPanel, "R");

        // Play again button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        //playAgainButton = new JButton("Play Again!"); // need to instantiate earlier
        buttonPanel.add(playAgainButton);
        resultPanel.add(buttonPanel); // add the inner panel to main panel
    }

    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Question #1", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gamePanel.add(questionLabel, BorderLayout.NORTH);
        // gamePanel.add(new JLabel("Quiz Screen"));
//         nextButton = new JButton("Go to results");
//         gamePanel.add(nextButton, BorderLayout.SOUTH);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton("Option " + (i + 1));
            optionButtons[i].addActionListener(new OptionButtonHandler(i));
            buttonPanel.add(optionButtons[i]);
        }
        gamePanel.add(buttonPanel, BorderLayout.CENTER);
        cardPanel.add(gamePanel, "G");

        timerLabel = new JLabel("", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        gamePanel.add(timerLabel, BorderLayout.SOUTH);
    }

    private void createWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));

        JLabel title = new JLabel("Brainblast Triva ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 75));
        titlePanel.add(title);

        JLabel subtitle = new JLabel("It's your brain against the clock!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 32));
        titlePanel.add(subtitle);

        welcomePanel.add(titlePanel, BorderLayout.CENTER);
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
                selectedCategory = (QuestionCategory) categoryBox.getSelectedItem();

                // Load questions
                importQuestions();
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
//            } else if (e.getSource() == nextButton) {
//                //Results screen
//                CreateResultsPanel();
//                cardLayout.show(cardPanel, "R");
            } else if (e.getSource() == usernameButton) {
                username = inputField.getText();
                JOptionPane.showMessageDialog(null, "Welcome " + username + "!");
            } else if (e.getSource() == playAgainButton) {
                // Reset questions
                currentQuestionIdx = 0;
                correctCount = 0;
                clearQuestions();

                cardLayout.show(cardPanel, "U");
            }
        }
    }

    private class TimerHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);

            if (timeLeft <= 0) {
                // -1 means the player didn't choose an answer
                processAnswer(-1);
            }
        }
    }

    private void processAnswer(int index) {
        stopQuestionTimer();

        int correctAnswer = correctAnswers.get(currentQuestionIdx);

        if (index == -1) {
            JOptionPane.showMessageDialog(CardLayoutFrame.this, "Time's up! Incorrect.");
        }
        else if (index == correctAnswer) {
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

    private class OptionButtonHandler implements ActionListener {
        private int index;

        public OptionButtonHandler(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            processAnswer(index);
        }
    }

    private void clearQuestions() {
        if (questionPair != null) questionPair.clear();
        if (correctAnswers != null) correctAnswers.clear();
    }

    private void importQuestions() {
        questionPair = new ArrayList<>();
        correctAnswers = new ArrayList<>();

        switch (selectedCategory) {
            case Sports:
                questionPair.add(List.of("Question 1: Which country won the 2026 FIFA World Cup?", "Spain", "Argentina", "Brazil", "France"));
                correctAnswers.add(0);
                questionPair.add(List.of("Question 2: How many players make up a basketball team?", "10", "4", "7", "5"));
                correctAnswers.add(3);
                questionPair.add(List.of("Question 3: How long is a marathon?", "24.2 miles", "26.2 miles", "20 miles", "25 miles"));
                correctAnswers.add(1);
                questionPair.add(List.of("Question 4: Who won the baseball World Series in 2025?", "Toronto Blue Jays", "New York Yankees", "Los Angeles Dodgers", "Texas Rangers"));
                correctAnswers.add(2);
                questionPair.add(List.of("Question 5: In American football, how many points is a field goal worth?", "7", "6", "10", "5"));
                correctAnswers.add(1);
                questionPair.add(List.of("Question 6: How many holes are played in a standard round of golf?", "9", "12", "18", "24"));
                correctAnswers.add(2);
                questionPair.add(List.of("Question 7: How many players are on a volleyball team?", "6", "4", "8", "5"));
                correctAnswers.add(0);
                questionPair.add(List.of("Question 8: Who has the most amount of gold medals in the Olympics?", "Lebron James", "Usain Bolt", "Lionel Messi", "Michael Phelps"));
                correctAnswers.add(3);
                questionPair.add(List.of("Question 9: How many outs are there in baseball?", "3", "1", "4", "5"));
                correctAnswers.add(0);
                questionPair.add(List.of("Question 10: How long is a soccer game in minutes?", "60", "120", "90", "100"));
                correctAnswers.add(2);
                break;
            case Math:
                questionPair.add(List.of("Question 1: What is 12x12?", "12", "144", "122", "132"));
                correctAnswers.add(1);
                questionPair.add(List.of("Question 2: What is 6x7?", "42", "46", "48", "56"));
                correctAnswers.add(0);
                questionPair.add(List.of("Question 3: What is the solution to 3x+6=21?", "x=4", "x=3", "x=5", "x=6"));
                correctAnswers.add(2);
                questionPair.add(List.of("Question 4: What is the derivative of 2x+1?", "2x", "1", "2x^2", "2"));
                correctAnswers.add(3);
                questionPair.add(List.of("Question 5: What is 15x15?", "225", "205", "150", "155"));
                correctAnswers.add(0);
                questionPair.add(List.of("Question 6: How many degrees are in a full circle?", "180", "90", "270", "360"));
                correctAnswers.add(3);
                questionPair.add(List.of("Question 7: What is the value of pi rounded to two decimal places?", "3.14", "3.16", "2.34", "3.15"));
                correctAnswers.add(0);
                questionPair.add(List.of("Question 8: What is the square root of 81?", "27", "18", "9", "8"));
                correctAnswers.add(2);
                questionPair.add(List.of("Question 9: What is 21-7?", "13", "15", "12", "14"));
                correctAnswers.add(3);
                questionPair.add(List.of("Question 10: What is 1+1?", "4", "11", "2", "10"));
                correctAnswers.add(2);
                break;
        }
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

        startQuestionTimer();
    }

    private class UsernameFocusAdapter extends FocusAdapter {
        @Override
        public void focusGained(FocusEvent e) {
            if (inputField.getText().equals("Enter a username")) {
                inputField.setText("");
            }
        }
    }

    private void startQuestionTimer() {
        stopQuestionTimer();

        // interval to update the timer in ms
        int delay = 1000;

        questionTimer = new Timer(delay, timerHandler);

        timeLeft = 15;
        timerLabel.setText("Time: " + timeLeft);

        questionTimer.start();
    }

    private void stopQuestionTimer() {
        if (questionTimer != null) {
            questionTimer.stop();
        }
    }

    private void applyTheme() {
        // JPanel colors
        UIManager.put("Panel.background", BACKGROUND_COLOR);

        // JButton colors
        UIManager.put("Button.background", BUTTON_COLOR);
        UIManager.put("Button.foreground", BUTTON_TEXT_COLOR);

        // JLabel colors
        UIManager.put("Label.foreground", TEXT_COLOR);

        // JTextField colors
        UIManager.put("TextField.background", FOREGROUND_COLOR);
        UIManager.put("TextField.foreground", TEXT_COLOR);

        // JOptionPane colors
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("OptionPane.buttonBackground", BUTTON_COLOR);
        UIManager.put("OptionPane.buttonForeground", BUTTON_TEXT_COLOR);
    }
}
