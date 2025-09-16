package in.vishal.bankingapp.service.security;

import in.vishal.bankingapp.model.Manager;
import in.vishal.bankingapp.repository.ManagerRepository;
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
public class ManagerUserDetailsService implements UserDetailsService {

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerRepository.findByUsername(username) // Ensure findByUsername method exists in repo
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found with username: " + username));

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"));

        return new org.springframework.security.core.userdetails.User( // Spring Security's User
            manager.getUsername(),
            manager.getPassword(), // This should be the HASHED password from your database
            authorities
        );
    }
}