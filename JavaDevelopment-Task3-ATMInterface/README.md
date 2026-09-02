# Task 3: ATM Interface

## Objective

Develop an object-oriented console ATM that authenticates a user and supports common banking transactions.

## Steps Performed

1. Modelled accounts, transactions, and the bank with separate Java classes.
2. Added User ID and PIN login with a three-attempt limit.
3. Implemented withdrawal, deposit, transfer, transaction history, and quit options.
4. Validated amounts, recipients, and available balance before processing transactions.

## Tools Used

- Java
- Object-oriented programming
- `ArrayList` for transaction history
- `Scanner` for console input

## Outcome

The application records successful transactions for the active session and gives clear feedback for invalid input or insufficient funds. Sample User IDs are `U001` and `U002`; the default PIN is `1234`.

## Run

```bash
javac *.java
java Main
```
