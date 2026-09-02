public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.addAccount(new Account("U001", "Alice", 5000));
        bank.addAccount(new Account("U002", "Bob", 3000));
        ATM atm = new ATM(bank);
        atm.start();
    }
}
