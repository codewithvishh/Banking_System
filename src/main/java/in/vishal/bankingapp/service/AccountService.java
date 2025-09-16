package in.vishal.bankingapp.service;

import in.vishal.bankingapp.model.Account;
import in.vishal.bankingapp.model.Customer;
import in.vishal.bankingapp.model.Transaction;
import in.vishal.bankingapp.repository.AccountRepository;
import in.vishal.bankingapp.repository.CustomerRepository;
import in.vishal.bankingapp.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository; // Still needed for validation
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Account createAccount(Long customerId, String accountType, Double initialBalance) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        Account newAccount = new Account();
        newAccount.setCustomerId(customerId);
        newAccount.setAccountType(accountType);
        newAccount.setBalance(initialBalance);
        newAccount.setStatus("ACTIVE"); // Default status

        Account savedAccount = accountRepository.save(newAccount);

        // Record initial deposit as a transaction
        if (initialBalance > 0) {
            recordTransaction(savedAccount.getAccountId(), "Deposit", initialBalance);
        }
        return savedAccount;
    }

    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // --- ADD THIS METHOD to AccountService ---
    public List<Account> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }
    // --- END ADDITION ---


    @Transactional
    public Account deposit(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        if ("FROZEN".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalStateException("Account " + accountId + " is frozen. Deposit not allowed.");
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        recordTransaction(accountId, "Deposit", amount);
        return account;
    }

    @Transactional
    public Account withdraw(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        if ("FROZEN".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalStateException("Account " + accountId + " is frozen. Withdrawal not allowed.");
        }

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance in account: " + accountId);
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        recordTransaction(accountId, "Withdrawal", amount);
        return account;
    }

    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
    }

    private void recordTransaction(Long accountId, String type, Double amount) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Transactional
    public Account toggleAccountStatus(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        String newStatus = "FROZEN".equalsIgnoreCase(account.getStatus()) ? "ACTIVE" : "FROZEN";
        account.setStatus(newStatus);
        return accountRepository.save(account);
    }
}