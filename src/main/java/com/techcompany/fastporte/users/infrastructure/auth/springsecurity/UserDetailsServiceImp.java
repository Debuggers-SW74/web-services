package com.techcompany.fastporte.users.infrastructure.auth.springsecurity;

import com.techcompany.fastporte.users.domain.model.aggregates.entities.User;
import com.techcompany.fastporte.users.domain.model.aggregates.entities.UserDetailsImp;
import com.techcompany.fastporte.users.infrastructure.persistence.jpa.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetailsImp loadUserByUsername(String username) {

        Optional<User> driver = userRepository.findByUsername(username);
        if (driver.isPresent()) {

            // Mapear roles a objetos GrantedAuthority
            List<GrantedAuthority> authorities = driver.get().getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                    .collect(Collectors.toList());

            return new UserDetailsImp(driver.get().getUsername(), driver.get().getPassword(), driver.get().getId(), authorities);
        }

        System.out.println("username: " + username + "Not found in users table");

        return new UserDetailsImp();

    }
}