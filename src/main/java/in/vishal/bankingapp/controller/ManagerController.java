package in.vishal.bankingapp.controller;

import in.vishal.bankingapp.model.Account;
import in.vishal.bankingapp.model.Customer;
import in.vishal.bankingapp.model.Transaction;
import in.vishal.bankingapp.service.AccountService;
import in.vishal.bankingapp.service.CustomerService;
import in.vishal.bankingapp.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;
    @Autowired
    private CustomerService customerService; // For search, if needed
    @Autowired
    private AccountService accountService; // For toggle status

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> viewAllCustomers() {
        List<Customer> customers = managerService.getAllCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> viewAllAccounts() {
        List<Account> accounts = managerService.getAllAccounts();
        if (accounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> viewAllTransactions() {
        List<Transaction> transactions = managerService.getAllTransactions();
        if (transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/transactions/large")
    public ResponseEntity<Map<String, Object>> viewLargeTransactions(@RequestParam(defaultValue = "50000") Double threshold) {
        List<Transaction> largeTransactions = managerService.getLargeTransactions(threshold);
        Object[] summary = managerService.getLargeTransactionSummary(threshold);

        Map<String, Object> response = Map.of(
            "transactions", largeTransactions,
            "summary", Map.of("count", summary[0], "totalAmount", summary[1])
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customers/report/csv")
    public ResponseEntity<ByteArrayResource> exportCustomerReport() {
        try {
            Path filePath = managerService.exportCustomerReport();
            byte[] data = Files.readAllBytes(filePath);
            ByteArrayResource resource = new ByteArrayResource(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers_report.csv");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(data.length)
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
