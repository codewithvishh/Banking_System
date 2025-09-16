package in.vishal.bankingapp.service.security;

import in.vishal.bankingapp.model.Customer;
import in.vishal.bankingapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String customerIdString) throws UsernameNotFoundException {
        Long customerId;
        try {
            customerId = Long.valueOf(customerIdString); // Assuming customerId is the username
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid customer ID format: " + customerIdString);
        }

        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with ID: " + customerIdString));

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        // --- NEW: Retrieve the HASHED password from the Customer entity ---
        return new org.springframework.security.core.userdetails.User(
            customer.getCustomerId().toString(), // Username must be a String
            customer.getPassword(), // This is the HASHED password retrieved from the database
            authorities
        );
        // -----------------------------------------------------------------
    }
}