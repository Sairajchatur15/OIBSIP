import java.util.*;

public class Bank {
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Account findAccount(String id) {
        for (Account a : accounts) {
            if (a.getAccountId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }
}
