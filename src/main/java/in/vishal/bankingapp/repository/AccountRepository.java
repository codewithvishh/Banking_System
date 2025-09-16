 package in.vishal.bankingapp.repository;

import in.vishal.bankingapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // --- NEW IMPORT ---
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Optional<Account> findByCustomerId(Long customerId); // This only returns ONE account.
    // --- NEW CODE START ---
    List<Account> findByCustomerId(Long customerId); // Returns ALL accounts for a customer
    // --- NEW CODE END ---
}