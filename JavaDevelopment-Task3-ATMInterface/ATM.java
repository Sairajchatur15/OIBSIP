import java.util.*;

public class ATM {
    private Bank bank;
    private Account currentAccount;
    private List<Transaction> history = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public void start() {
        System.out.println("=== ATM INTERFACE ===");
        int attempts = 0;
        while (currentAccount == null && attempts < 3) {
            System.out.print("Enter User ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter PIN (default 1234): ");
            String pin = scanner.nextLine().trim();
            Account acc = bank.findAccount(id);
            if (acc != null && pin.equals("1234")) {
                currentAccount = acc;
                System.out.println("Login successful. Welcome, " + acc.getHolderName());
            } else {
                attempts++;
                System.out.println("Access denied. Attempts left: " + (3 - attempts));
            }
        }
        if (currentAccount == null) {
            System.out.println("Too many failed attempts. Card retained.");
            return;
        }
        mainMenu();
    }

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": showHistory(); break;
                case "2": withdraw(); break;
                case "3": deposit(); break;
                case "4": transfer(); break;
                case "5": System.out.println("Goodbye! Thank you for using our ATM."); running = false; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private void showHistory() {
        System.out.println("\n--- Transaction History ---");
        if (history.isEmpty()) System.out.println("No transactions yet.");
        for (Transaction t : history) System.out.println(t);
    }

    private void withdraw() {
        System.out.print("Enter amount to withdraw: ");
        String in = scanner.nextLine().trim();
        if (!in.matches("\\d+(\\.\\d+)?")) { System.out.println("Invalid amount."); return; }
        double amt = Double.parseDouble(in);
        if (currentAccount.withdraw(amt)) {
            history.add(new Transaction("Withdraw", amt, "Account: " + currentAccount.getAccountId()));
            System.out.println("Withdrawal successful. New balance: ₹" + currentAccount.getBalance());
        } else {
            System.out.println("Insufficient Funds.");
        }
    }

    private void deposit() {
        System.out.print("Enter amount to deposit: ");
        String in = scanner.nextLine().trim();
        if (!in.matches("\\d+(\\.\\d+)?")) { System.out.println("Invalid amount."); return; }
        double amt = Double.parseDouble(in);
        currentAccount.deposit(amt);
        history.add(new Transaction("Deposit", amt, "Account: " + currentAccount.getAccountId()));
        System.out.println("Deposit successful. New balance: ₹" + currentAccount.getBalance());
    }

    private void transfer() {
        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine().trim();
        Account recipient = bank.findAccount(recipientId);
        if (recipient == null || recipient == currentAccount) {
            System.out.println("Invalid recipient account."); return;
        }
        System.out.print("Enter amount to transfer: ");
        String in = scanner.nextLine().trim();
        if (!in.matches("\\d+(\\.\\d+)?")) { System.out.println("Invalid amount."); return; }
        double amt = Double.parseDouble(in);
        if (currentAccount.transferTo(recipient, amt)) {
            history.add(new Transaction("Transfer", amt, "To: " + recipient.getAccountId()));
            System.out.println("Transfer successful. New balance: ₹" + currentAccount.getBalance());
        } else {
            System.out.println("Insufficient Funds.");
        }
    }
}
