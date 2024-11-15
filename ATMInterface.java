import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class BankAccount {
    private double balance;
    private List<String> transactionHistory;

    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add("Deposited: $" + amount);
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactionHistory.add("Withdrew: $" + amount);
            return true;
        }
        return false;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void clearTransactionHistory() {
        transactionHistory.clear();
    }
}

public class ATMInterface extends JFrame {
    private BankAccount account;
    private JTextArea displayArea;
    private JTextField amountField;
    private JPasswordField pinField;
    private JPanel actionPanel, pinPanel, amountPanel;
    private String correctPin = "1234";  // Default pin for simplicity
    private boolean isAuthenticated = false;

    public ATMInterface() {
        // Initialize bank account with a starting balance of $1000
        account = new BankAccount(1000.0);

        // Frame settings
        setTitle("Advanced ATM Interface");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Dark theme colors
        Color backgroundColor = new Color(18, 18, 18); // Dark gray
        Color textColor = new Color(240, 240, 240); // Light gray
        Color accentColor = new Color(70, 130, 180); // Samsung blue

        // Header
        JLabel header = new JLabel("ATM Interface", SwingConstants.CENTER);
        header.setForeground(accentColor);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setOpaque(true);
        header.setBackground(backgroundColor);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Padding for the header
        add(header, BorderLayout.NORTH);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around edges
        mainPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 rows (including amount input)

        // Display area for ATM messages
        displayArea = new JTextArea(6, 30);
        displayArea.setBackground(new Color(40, 40, 40)); // Dark gray background
        displayArea.setForeground(textColor); // Light text color
        displayArea.setEditable(false);
        displayArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        displayArea.setBorder(BorderFactory.createLineBorder(accentColor));
        mainPanel.add(new JScrollPane(displayArea));

        // Pin input field (Initially hidden)
        pinPanel = new JPanel();
        pinPanel.setBackground(backgroundColor);
        pinPanel.setLayout(new FlowLayout());

        JLabel pinLabel = new JLabel("Enter PIN:");
        pinLabel.setForeground(textColor);
        pinPanel.add(pinLabel);

        pinField = new JPasswordField(10);
        pinField.setBackground(new Color(40, 40, 40));
        pinField.setForeground(textColor);
        pinPanel.add(pinField);

        mainPanel.add(pinPanel);

        // Amount input field (Initially hidden)
        amountPanel = new JPanel();
        amountPanel.setBackground(backgroundColor);
        JLabel amountLabel = new JLabel("Enter Amount: ");
        amountLabel.setForeground(textColor);
        amountField = new JTextField(10);
        amountField.setBackground(new Color(40, 40, 40));
        amountField.setForeground(textColor);
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);
        amountPanel.setVisible(false);  // Initially hidden until authenticated
        mainPanel.add(amountPanel);

        // Add main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        // Button panel for actions (withdraw, deposit, check balance, etc.)
        actionPanel = new JPanel();
        actionPanel.setBackground(backgroundColor);

        JButton withdrawButton = createActionButton("Withdraw");
        JButton depositButton = createActionButton("Deposit");
        JButton checkBalanceButton = createActionButton("Check Balance");
        JButton viewHistoryButton = createActionButton("Transaction History");
        JButton authenticateButton = createActionButton("Authenticate");

        actionPanel.add(authenticateButton);
        actionPanel.add(withdrawButton);
        actionPanel.add(depositButton);
        actionPanel.add(checkBalanceButton);
        actionPanel.add(viewHistoryButton);

        add(actionPanel, BorderLayout.SOUTH);

        // Initialize with greeting
        displayArea.setText("Welcome to ATM. Please authenticate with your PIN.");
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAction(e.getActionCommand());
            }
        });
        return button;
    }

    private void handleAction(String action) {
        if (action.equals("Authenticate")) {
            authenticateUser();
            return;
        }

        if (!isAuthenticated) {
            displayArea.setText("Please authenticate first with your PIN.");
            return;
        }

        String amountText = amountField.getText();
        double amount = 0;

        if (!action.equals("Check Balance") && !action.equals("Transaction History")) {
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException ex) {
                displayArea.setText("Invalid input. Please enter a valid amount.");
                return;
            }

            if (amount <= 0) {
                displayArea.setText("Amount must be greater than zero.");
                return;
            }
        }

        switch (action) {
            case "Withdraw":
                if (account.withdraw(amount)) {
                    displayArea.setText("Withdrawal of $" + amount + " successful.\nBalance: $" + account.getBalance());
                } else {
                    displayArea.setText("Insufficient funds for withdrawal.\nBalance: $" + account.getBalance());
                }
                break;

            case "Deposit":
                account.deposit(amount);
                displayArea.setText("Deposit of $" + amount + " successful.\nBalance: $" + account.getBalance());
                break;

            case "Check Balance":
                displayArea.setText("Current Balance: $" + account.getBalance());
                break;

            case "Transaction History":
                List<String> history = account.getTransactionHistory();
                if (history.isEmpty()) {
                    displayArea.setText("No transactions yet.");
                } else {
                    displayArea.setText("Transaction History:\n" + String.join("\n", history));
                }
                break;
        }
    }

    private void authenticateUser() {
        String enteredPin = new String(pinField.getPassword());  // Converts char[] to String

        if (enteredPin.equals(correctPin)) {
            isAuthenticated = true;
            pinPanel.setVisible(false);  // Hide PIN input after authentication
            displayArea.setText("Authentication successful. Please choose a transaction.");

            // Show amount input field after authentication
            amountPanel.setVisible(true);  // Show the amount field
        } else {
            displayArea.setText("Invalid PIN. Please try again.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATMInterface atm = new ATMInterface();
            atm.setVisible(true);
        });
    }
}
