package in.vishal.bankingapp.controller;

import in.vishal.bankingapp.model.Account;
import in.vishal.bankingapp.model.Admin;
import in.vishal.bankingapp.model.Customer;
import in.vishal.bankingapp.service.AccountService;
import in.vishal.bankingapp.service.AdminService;
import in.vishal.bankingapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // --- NEW IMPORT ---
import java.util.Map;
import java.util.Optional; // --- NEW IMPORT ---

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerService customerService;

    // This endpoint should ideally be secured, often only accessible by other admins
    @PostMapping("/create-admin")
    public ResponseEntity<Admin> createAdmin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        try {
            Admin newAdmin = adminService.createAdmin(username, password);
            return new ResponseEntity<>(newAdmin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Username already exists
        }
    }

    // Existing customer registration can be used, or create a specific admin-driven one
    @PostMapping("/customers/register")
    public ResponseEntity<Customer> registerCustomer(@RequestBody Customer customer) {
        try {
            Customer registeredCustomer = customerService.registerCustomer(customer);
            return new ResponseEntity<>(registeredCustomer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/accounts/create")
    public ResponseEntity<Account> createAccountForCustomer(@RequestBody Map<String, Object> request) {
        Long customerId = Long.valueOf(request.get("customerId").toString());
        String accountType = (String) request.get("accountType");
        Double initialBalance = Double.valueOf(request.get("initialBalance").toString());
        try {
            Account newAccount = accountService.createAccount(customerId, accountType, initialBalance);
            return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> modifyCustomerDetails(@PathVariable Long id, @RequestBody Customer customerDetails) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // --- NEW CODE START: Admin Retrieval Endpoints ---

    // 1. Get a specific customer by ID
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        Optional<Customer> customerOptional = customerService.getCustomerById(customerId);
        return customerOptional.map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
                               .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 2. Get all customers
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content if list is empty
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // 3. Search customers by name or phone
    @GetMapping("/customers/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String searchTerm) {
        List<Customer> customers = customerService.searchCustomers(searchTerm);
        if (customers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // 4. Get a specific account by ID
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long accountId) {
        Optional<Account> accountOptional = accountService.getAccountById(accountId);
        return accountOptional.map(account -> new ResponseEntity<>(account, HttpStatus.OK))
                              .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 5. Get all accounts
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        if (accounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // 6. Get all accounts for a specific customer
    @GetMapping("/customers/{customerId}/accounts")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        if (accounts.isEmpty()) {
            // Check if the customer actually exists before returning NO_CONTENT vs NOT_FOUND
            if (customerService.getCustomerById(customerId).isPresent()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer not found
            }
        }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // --- NEW CODE END: Admin Retrieval Endpoints ---
}