package OnlineExam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class ExamSystem extends JFrame {

    private CardLayout cards;
    private JPanel mainPanel;

    private String currentUser = "";
    private String currentPass = "admin123";
    private String displayName = "Student";

    private Timer timer;
    private int timeLeft = 30 * 60;
    private int currentQ = 0;
    private int score = 0;

    private int[] answers = {-1, -1, -1, -1, -1};

    private String[][] questions = {
        {"What is 2+2?", "3", "4", "5", "2"},
        {"Capital of France?", "London", "Berlin", "Paris", "Madrid"},
        {"Java is?", "Snake", "Coffee", "Language", "Island"},
        {"2*3?", "6", "5", "7", "4"},
        {"Sun rises in?", "West", "North", "East", "South"}
    };

    private int[] correct = {2, 3, 3, 1, 3};

    private boolean examRunning = false;
    private boolean examSubmitted = false;

    private JTextArea resultAreaRef;
    private JLabel timerLabel;
    private JLabel questionLabel;
    private JRadioButton[] options = new JRadioButton[4];

    public ExamSystem() {

        setTitle("Online Examination System");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if (examRunning) {
                    int choice = JOptionPane.showConfirmDialog(
                            ExamSystem.this,
                            "Are you sure you want to quit the exam?",
                            "Quit Exam",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        if (timer != null) {
                            timer.stop();
                        }
                        System.exit(0);
                    }

                } else {
                    System.exit(0);
                }
            }
        });

        cards = new CardLayout();
        mainPanel = new JPanel(cards);

        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createProfilePanel(), "profile");
        mainPanel.add(createExamPanel(), "exam");
        mainPanel.add(createResultPanel(), "result");

        add(mainPanel);

        cards.show(mainPanel, "login");
    }

    private JPanel createLoginPanel() {

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel(
                "ONLINE EXAMINATION SYSTEM",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {

            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (user.equals("admin") && pass.equals(currentPass)) {

                currentUser = user;

                if (displayName.equals("Student")) {
                    displayName = "Admin";
                }

                cards.show(mainPanel, "profile");
                passwordField.setText("");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        p.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(usernameLabel, gbc);

        gbc.gridx = 1;
        p.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        p.add(passwordLabel, gbc);

        gbc.gridx = 1;
        p.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        p.add(loginButton, gbc);

        return p;
    }

    private JPanel createProfilePanel() {

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel(
                "PROFILE UPDATE",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel nameLabel = new JLabel("Display Name:");
        JTextField nameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("New Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton saveButton = new JButton("Save & Start Exam");

        saveButton.addActionListener(e -> {

            String newName = nameField.getText().trim();
            String newPassword = new String(passwordField.getPassword());

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Display name cannot be empty!",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password cannot be empty!",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            displayName = newName;
            currentPass = newPassword;

            resetExam();

            cards.show(mainPanel, "exam");

            startTimer();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        p.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(nameLabel, gbc);

        gbc.gridx = 1;
        p.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        p.add(passwordLabel, gbc);

        gbc.gridx = 1;
        p.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        p.add(saveButton, gbc);

        return p;
    }

    private void resetExam() {

        currentQ = 0;
        score = 0;
        timeLeft = 30 * 60;
        examRunning = false;
        examSubmitted = false;

        Arrays.fill(answers, -1);
    }

    private JPanel createExamPanel() {

        JPanel p = new JPanel(new BorderLayout(10, 10));

        timerLabel = new JLabel(
                "Time: 30:00",
                SwingConstants.CENTER
        );

        timerLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        questionLabel = new JLabel(
                "",
                SwingConstants.CENTER
        );

        questionLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JPanel optionsPanel = new JPanel(
                new GridLayout(4, 1, 5, 5)
        );

        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            buttonGroup.add(options[i]);
            optionsPanel.add(options[i]);
        }

        JPanel navigationPanel = new JPanel();

        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton submitButton = new JButton("Submit");

        previousButton.addActionListener(e -> {

            saveAnswer();

            if (currentQ > 0) {
                currentQ--;
                loadQuestion();
            }
        });

        nextButton.addActionListener(e -> {

            saveAnswer();

            if (currentQ < questions.length - 1) {
                currentQ++;
                loadQuestion();
            }
        });

        submitButton.addActionListener(e -> {

            saveAnswer();

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to submit the exam?",
                    "Confirm Submission",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                finishExam();
            }
        });

        navigationPanel.add(previousButton);
        navigationPanel.add(nextButton);
        navigationPanel.add(submitButton);

        JPanel centerPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        centerPanel.add(
                questionLabel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                optionsPanel,
                BorderLayout.CENTER
        );

        p.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        p.add(timerLabel, BorderLayout.NORTH);
        p.add(centerPanel, BorderLayout.CENTER);
        p.add(navigationPanel, BorderLayout.SOUTH);

        loadQuestion();

        timer = new Timer(1000, e -> {

            timeLeft--;

            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;

            timerLabel.setText(
                    String.format(
                            "Time: %02d:%02d",
                            minutes,
                            seconds
                    )
            );

            if (timeLeft <= 0) {

                timer.stop();

                JOptionPane.showMessageDialog(
                        this,
                        "Time is up! Your exam will be submitted automatically.",
                        "Time Up",
                        JOptionPane.INFORMATION_MESSAGE
                );

                finishExam();
            }
        });

        return p;
    }

    private void loadQuestion() {

        questionLabel.setText(
                "Q" + (currentQ + 1)
                        + ": "
                        + questions[currentQ][0]
        );

        for (int i = 0; i < 4; i++) {
            options[i].setText(
                    questions[currentQ][i + 1]
            );
            options[i].setSelected(false);
        }

        if (answers[currentQ] > 0 && answers[currentQ] <= 4) {
            options[answers[currentQ] - 1].setSelected(true);
        }
    }

    private void saveAnswer() {

        for (int i = 0; i < 4; i++) {

            if (options[i].isSelected()) {
                answers[currentQ] = i + 1;
                return;
            }
        }

        answers[currentQ] = -1;
    }

    private void startTimer() {

        if (timer != null) {

            timeLeft = 30 * 60;

            timerLabel.setText("Time: 30:00");

            examRunning = true;
            examSubmitted = false;

            timer.start();
        }
    }

    private void finishExam() {

        if (examSubmitted) {
            return;
        }

        examSubmitted = true;
        examRunning = false;

        if (timer != null) {
            timer.stop();
        }

        saveAnswer();

        score = 0;

        for (int i = 0; i < questions.length; i++) {

            if (answers[i] == correct[i]) {
                score++;
            }
        }

        cards.show(mainPanel, "result");

        updateResult();
    }

    private JPanel createResultPanel() {

        JPanel p = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "EXAM RESULT",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        resultAreaRef = new JTextArea();

        resultAreaRef.setEditable(false);

        resultAreaRef.setFont(
                new Font("Monospaced", Font.PLAIN, 14)
        );

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {

            if (timer != null) {
                timer.stop();
            }

            resetExam();

            cards.show(mainPanel, "login");
        });

        p.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        p.add(title, BorderLayout.NORTH);

        p.add(
                new JScrollPane(resultAreaRef),
                BorderLayout.CENTER
        );

        p.add(
                logoutButton,
                BorderLayout.SOUTH
        );

        return p;
    }

    private void updateResult() {

        StringBuilder sb = new StringBuilder();

        sb.append(
                "Student: "
                        + displayName
                        + "\n\n"
        );

        sb.append(
                "Score: "
                        + score
                        + " / "
                        + questions.length
                        + "\n"
        );

        int timeTaken =
                (30 * 60) - timeLeft;

        int minutes =
                timeTaken / 60;

        int seconds =
                timeTaken % 60;

        sb.append(
                "Time Taken: "
                        + String.format(
                                "%02d:%02d",
                                minutes,
                                seconds
                        )
                        + "\n\n"
        );

        sb.append("Answer Breakdown:\n");
        sb.append("----------------------------\n");

        for (int i = 0; i < questions.length; i++) {

            if (answers[i] == correct[i]) {

                sb.append(
                        "Q"
                                + (i + 1)
                                + ": Correct\n"
                );

            } else {

                sb.append(
                        "Q"
                                + (i + 1)
                                + ": Incorrect\n"
                );

                sb.append(
                        "   Correct Answer: "
                                + questions[i][correct[i]]
                                + "\n"
                );

                if (answers[i] != -1) {

                    sb.append(
                            "   Your Answer: "
                                    + questions[i][answers[i]]
                                    + "\n"
                    );

                } else {

                    sb.append(
                            "   Your Answer: Not Answered\n"
                    );
                }
            }
        }

        resultAreaRef.setText(
                sb.toString()
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ExamSystem exam =
                    new ExamSystem();

            exam.setVisible(true);
        });
    }
}
