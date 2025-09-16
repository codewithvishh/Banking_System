package in.vishal.bankingapp.repository;

import in.vishal.bankingapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.amount > ?1 ORDER BY t.amount DESC")
    List<Transaction> findLargeTransactions(Double amountThreshold);

    @Query("SELECT COUNT(t) AS count, SUM(t.amount) AS total FROM Transaction t WHERE t.amount > ?1")
    Object[] getLargeTransactionSummary(Double amountThreshold);
}
