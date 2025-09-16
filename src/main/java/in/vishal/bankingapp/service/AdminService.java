package in.vishal.bankingapp.service;

import in.vishal.bankingapp.model.Admin;
import in.vishal.bankingapp.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // For hashing
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Injected from SecurityConfig

    public Admin createAdmin(String username, String rawPassword) {
        if (adminRepository.findByUsernameIgnoreCase(username).isPresent()) {
            throw new IllegalArgumentException("Admin username '" + username + "' already exists");
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword)); // Hash password
        return adminRepository.save(admin);
    }

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsernameIgnoreCase(username);
    }

    public boolean authenticateAdmin(String username, String rawPassword) {
        Optional<Admin> adminOptional = adminRepository.findByUsernameIgnoreCase(username);
        return adminOptional.map(admin -> passwordEncoder.matches(rawPassword, admin.getPassword()))
                            .orElse(false);
    }
}
