package in.vishal.bankingapp.repository;

import in.vishal.bankingapp.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // This method is for case-sensitive username search.
    // Optional<Admin> findByUsername(String username); // If you still want this, keep it.

    // --- ADD/UPDATE THIS METHOD FOR CASE-INSENSITIVE SEARCH ---
    Optional<Admin> findByUsernameIgnoreCase(String username);
    // Spring Data JPA will automatically generate a query that ignores case for the username field.
    // This often translates to a query like WHERE UPPER(username) = UPPER(?) in SQL,
    // making the search consistent regardless of how the user typed it or how it was saved.
    // (Examples: findByNameContainingIgnoreCase, findByFirstNameIgnoreCase)
    // See: https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html
    // And: https://alexanderobregon.substack.com/p/handling-case-insensitive-searches
}