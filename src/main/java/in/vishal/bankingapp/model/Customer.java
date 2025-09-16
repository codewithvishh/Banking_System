package in.vishal.bankingapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq_generator")
    @SequenceGenerator(name = "customer_seq_generator", sequenceName = "CUSTOMER_SEQ", allocationSize = 1)
    @Column(name = "customer_id")
    private Long customerId;

    private String name;
    private String phone;
    private String address;
    @Column(nullable = true)
    private String password;
}