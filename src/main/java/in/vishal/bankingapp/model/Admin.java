package in.vishal.bankingapp.model;

import jakarta.persistence.*; // For Spring Boot 3+
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins") // Assuming your admin table is named 'admins'
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Or Sequence if Oracle uses it
    private Long id; // Primary key for Admin entity

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Stored hashed

    // If your table uses an auto-incrementing ID that's not 'id', adjust @Id and @Column
    // For instance, if your table has ADMIN_ID, you'd use:
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "admin_id")
    // private Long adminId;
    // And then update the references in AdminRepository
}

