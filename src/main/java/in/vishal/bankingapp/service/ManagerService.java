package in.vishal.bankingapp.service;


import in.vishal.bankingapp.model.Account;
import in.vishal.bankingapp.model.Customer;
import in.vishal.bankingapp.model.Transaction;
import in.vishal.bankingapp.repository.AccountRepository;
import in.vishal.bankingapp.repository.CustomerRepository;
import in.vishal.bankingapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getLargeTransactions(Double threshold) {
        return transactionRepository.findLargeTransactions(threshold);
    }

    public Object[] getLargeTransactionSummary(Double threshold) {
        return transactionRepository.getLargeTransactionSummary(threshold);
    }

    public Path exportCustomerReport() throws IOException {
        String userHome = System.getProperty("user.home");
        Path desktopPath = Paths.get(userHome, "Desktop");
        Path filePath = desktopPath.resolve("customers_report.csv");

        // Ensure directory exists
        if (!Files.exists(desktopPath)) {
            Files.createDirectories(desktopPath);
        }

        try (FileWriter fw = new FileWriter(filePath.toFile())) {
            fw.append("ID,Name,Phone,Address\n");
            for (Customer customer : customerRepository.findAll()) {
                fw.append(customer.getCustomerId().toString()).append(",")
                  .append(customer.getName()).append(",")
                  .append(customer.getPhone()).append(",")
                  .append(customer.getAddress()).append("\n");
            }
        }
        return filePath;
    }
}
