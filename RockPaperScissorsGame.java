import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class RockPaperScissorsGame extends JFrame {
    private Player player;
    private JLabel balanceLabel, messageLabel;
    private JButton rockButton, paperButton, scissorsButton, restartButton;
    private JPanel bettingPanel;
    private int currentBet;

    public RockPaperScissorsGame() {
        player = new Player();
        setupUI();
    }

    private void setupUI() {
        setTitle("Kamień, Papier, Nożyce z obstawianiem");
        setSize(600, 400); // Zwiększone okno
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Górny panel z informacjami
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        balanceLabel = new JLabel("Stan konta: " + player.getBalance() + " $");
        messageLabel = new JLabel("");
        infoPanel.add(balanceLabel);
        infoPanel.add(messageLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Panel do obstawiania
        bettingPanel = new JPanel();
        bettingPanel.setLayout(new GridLayout(2, 3));
        createBettingButtons();
        add(bettingPanel, BorderLayout.CENTER);

        // Panel do wyboru figur
        JPanel figurePanel = new JPanel();
        figurePanel.setLayout(new FlowLayout());
        createFigureButtons();
        figurePanel.add(rockButton);
        figurePanel.add(paperButton);
        figurePanel.add(scissorsButton);
        add(figurePanel, BorderLayout.SOUTH);

        // Przycisk restartu
        restartButton = new JButton("Restart");
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> restartGame());
        add(restartButton, BorderLayout.EAST);

        setVisible(true);
    }

    private void restartGame() {
        player = new Player();
        currentBet = 0;
        updateBalance();
        messageLabel.setText("");
        restartButton.setVisible(false);
    }

    private void updateBalance() {
        balanceLabel.setText("Stan konta: " + player.getBalance() + " $");
        if (player.getBalance() == 0 && currentBet == 0) {
            restartButton.setVisible(true);
            messageLabel.setText("Nie masz już żetonów. Zrestartuj grę.");
        }
    }

    private void createBettingButtons() {
        int[] bets = {1, 5, 10, 25, 100, 500};
        String[] iconNames = {"chip1.png", "chip5.png", "chip10.png", "chip25.png", "chip100.png", "chip500.png"};

        for (int i = 0; i < bets.length; i++) {
            JButton betButton = new JButton(new ImageIcon(iconNames[i]));
            int finalI = i;
            betButton.addActionListener(e -> placeBet(bets[finalI]));
            bettingPanel.add(betButton);
        }
    }

    private void createFigureButtons() {
        rockButton = new JButton("Kamień");
        paperButton = new JButton("Papier");
        scissorsButton = new JButton("Nożyce");

        rockButton.addActionListener(e -> playRound("rock"));
        paperButton.addActionListener(e -> playRound("paper"));
        scissorsButton.addActionListener(e -> playRound("scissors"));
    }

    private void placeBet(int amount) {
        if (player.getBalance() >= amount) {
            currentBet += amount; // Dodajemy kwotę do bieżącego zakładu
            player.placeBet(amount);
            messageLabel.setText("Obstawiono łącznie " + currentBet + "$");
            updateBalance();
        } else {
            messageLabel.setText("Niewystarczający stan konta!");
        }
    }

    private void playRound(String playerChoice) {
        if (currentBet == 0) {
            messageLabel.setText("Najpierw obstaw!");
            return;
        }

        String computerChoice = getComputerChoice();
        String result = determineWinner(playerChoice, computerChoice);

        if (result.equals("win")) {
            player.winBet(currentBet);
            messageLabel.setText("Wygrałeś! Komputer wybrał: " + computerChoice);
        } else if (result.equals("lose")) {
            messageLabel.setText("Przegrałeś! Komputer wybrał: " + computerChoice);
        } else {
            player.refundBet(currentBet);
            messageLabel.setText("Remis! Komputer wybrał: " + computerChoice);
        }

        currentBet = 0; // Reset bet for next round
        updateBalance();
    }

    private String getComputerChoice() {
        String[] choices = {"rock", "paper", "scissors"};
        return choices[new Random().nextInt(choices.length)];
    }

    private String determineWinner(String playerChoice, String computerChoice) {
        if (playerChoice.equals(computerChoice)) {
            return "draw";
        } else if ((playerChoice.equals("rock") && computerChoice.equals("scissors")) ||
                (playerChoice.equals("paper") && computerChoice.equals("rock")) ||
                (playerChoice.equals("scissors") && computerChoice.equals("paper"))) {
            return "win";
        } else {
            return "lose";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RockPaperScissorsGame::new);
    }
}

class Player {
    private int balance;

    public Player() {
        balance = 100; // Startowa kwota
    }

    public int getBalance() {
        return balance;
    }

    public void placeBet(int amount) {
        balance -= amount;
    }

    public void winBet(int amount) {
        balance += amount * 2; // Podwajanie wygranej
    }

    public void refundBet(int amount) {
        balance += amount; // Zwrócenie stawki przy remisie
    }
}
