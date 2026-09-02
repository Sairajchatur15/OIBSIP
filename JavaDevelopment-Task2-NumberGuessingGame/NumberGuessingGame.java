import java.util.Scanner;
import java.util.Random;

public class NumberGuessingGame {
    private Scanner scanner = new Scanner(System.in);
    private Random random = new Random();
    private int targetNumber;
    private int attempts;
    private int maxAttempts = 7;
    private int round = 1;
    private int totalRounds = 0;

    public void play() {
        System.out.println("=== NUMBER GUESSING GAME ===");
        boolean playAgain = true;
        while (playAgain) {
            totalRounds++;
            startRound();
            System.out.print("Play again? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            playAgain = response.equals("yes") || response.equals("y");
        }
        System.out.println("Thanks for playing! Rounds played: " + totalRounds);
    }

    private void startRound() {
        targetNumber = random.nextInt(100) + 1;
        attempts = 0;
        System.out.println("\n--- Round " + round + " ---");
        System.out.println("I'm thinking of a number between 1 and 100. You have " + maxAttempts + " attempts.");

        boolean guessed = false;
        while (attempts < maxAttempts && !guessed) {
            System.out.print("Enter your guess: ");
            String input = scanner.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            int guess = Integer.parseInt(input);
            attempts++;
            System.out.println("Attempt " + attempts + "/" + maxAttempts);
            if (guess == targetNumber) {
                System.out.println("Correct! You guessed it in " + attempts + " attempts.");
                System.out.println("Round " + round + " — guessed in " + attempts + " attempts");
                guessed = true;
            } else if (guess > targetNumber) {
                System.out.println("Too High!");
            } else {
                System.out.println("Too Low!");
            }
        }
        if (!guessed) {
            System.out.println("You Lost! The number was: " + targetNumber);
            System.out.println("Round " + round + " — lost (0 correct guesses)");
        }
        round++;
    }
}
