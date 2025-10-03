package vn.iotstar.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.Users;
import vn.iotstar.repository.UserRepository;

import java.util.Set;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("CustomUserDetailsService: Trying to authenticate: " + usernameOrEmail);
        
        Users user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> 
                new UsernameNotFoundException("User not found with username or email:" + usernameOrEmail));

        System.out.println("Found user: " + user.getUsername() + ", enabled: " + user.isEnabled());
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), // Dùng username thay vì email
            user.getPassword(),
            user.isEnabled(),
            true, // accountNonExpired
            true, // credentialsNonExpired  
            true, // accountNonLocked
            mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
    }
}