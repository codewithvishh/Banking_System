package in.vishal.bankingapp.service;

import in.vishal.bankingapp.model.Customer;
import in.vishal.bankingapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Customer registerCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().isEmpty() ||
            customer.getPhone() == null || customer.getPhone().isEmpty() ||
            customer.getAddress() == null || customer.getAddress().isEmpty() ||
            customer.getPassword() == null || customer.getPassword().isEmpty()) {
            throw new IllegalArgumentException("All customer fields (including password) are required!");
        }
        if (!customer.getPhone().matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain only digits!");
        }
        if (customerRepository.findByPhone(customer.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered!");
        }

        String hashedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(hashedPassword);

        return customerRepository.save(customer);
    }

    // --- NEW CODE START ---
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId); // Correctly uses findById as customerId is the primary key
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> searchCustomers(String searchTerm) {
        // Your existing search method is good
        return customerRepository.findByNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(searchTerm, searchTerm);
    }
    // --- NEW CODE END ---

    public Customer updateCustomer(Long customerId, Customer updatedCustomer) {
        return customerRepository.findById(customerId).map(customer -> {
            if (updatedCustomer.getName() != null && !updatedCustomer.getName().isEmpty()) {
                customer.setName(updatedCustomer.getName());
            }
            if (updatedCustomer.getPhone() != null && !updatedCustomer.getPhone().isEmpty()) {
                // You might want to add a check here if the new phone number already exists for another customer
                customer.setPhone(updatedCustomer.getPhone());
            }
            if (updatedCustomer.getAddress() != null && !updatedCustomer.getAddress().isEmpty()) {
                customer.setAddress(updatedCustomer.getAddress());
            }
            if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isEmpty()) {
                customer.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
            }
            return customerRepository.save(customer);
        }).orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
    }
}