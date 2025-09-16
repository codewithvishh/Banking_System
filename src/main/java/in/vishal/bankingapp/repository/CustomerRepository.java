package in.vishal.bankingapp.repository;

import in.vishal.bankingapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone);
    List<Customer> findByNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(String name, String phone);

    // --- RE-ADD THIS METHOD ---
    Optional<Customer> findByCustomerId(Long customerId);
}
    // This is needed because your CustomerUserDetailsService is explicitly calling it.
    // While findById(Long) from JpaRepository works on the primary key,
    // if your security context assumes a method named findByCustomerId specifically,
    // it's best to define it. Spring Data JPA will still map it to the primary key.
