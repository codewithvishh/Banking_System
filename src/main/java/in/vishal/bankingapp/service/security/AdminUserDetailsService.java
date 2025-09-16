package in.vishal.bankingapp.service.security;

import in.vishal.bankingapp.model.Admin;
import in.vishal.bankingapp.repository.AdminRepository;
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
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // --- ENSURE THIS CALL MATCHES THE REPOSITORY METHOD ---
        Admin admin = adminRepository.findByUsernameIgnoreCase(username) // Using the case-insensitive method
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new org.springframework.security.core.userdetails.User(
            admin.getUsername(),
            admin.getPassword(),
            authorities
        );
    }
}