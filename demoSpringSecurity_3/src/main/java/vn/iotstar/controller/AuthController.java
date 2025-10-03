package vn.iotstar.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.Users;
import vn.iotstar.model.LoginDto;
import vn.iotstar.model.SignUpDto;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    @ResponseBody
    public void authenticateUser(@RequestBody LoginDto loginDto, HttpServletResponse response) throws IOException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDto.getUsernameOrEmail(),
                    loginDto.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            response.setContentType("text/plain; charset=UTF-8");
            response.setStatus(200);
            response.getWriter().write("User signed-in successfully!");
            response.getWriter().flush();
        } catch (Exception e) {
            response.setContentType("text/plain; charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("Authentication failed: " + e.getMessage());
            response.getWriter().flush();
        }
    }

    @PostMapping("/signup")
    @ResponseBody
    public void registerUser(@RequestBody SignUpDto signUpDto, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/plain; charset=UTF-8");
            
            // add check for username exists in DB
            if (userRepository.existsByUsername(signUpDto.getUsername())) {
                response.setStatus(400);
                response.getWriter().write("Username is already taken!");
                response.getWriter().flush();
                return;
            }

            // add check for email exists in DB
            if (userRepository.existsByEmail(signUpDto.getEmail())) {
                response.setStatus(400);
                response.getWriter().write("Email is already taken!");
                response.getWriter().flush();
                return;
            }

            // create user object
            Users user = new Users();
            user.setName(signUpDto.getName());
            user.setUsername(signUpDto.getUsername());
            user.setEmail(signUpDto.getEmail());
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

            // Find or create USER role
            Optional<Role> userRole = roleRepository.findByName("USER");
            Role roles;
            if (userRole.isPresent()) {
                roles = userRole.get();
            } else {
                // Create USER role if not exists
                roles = new Role();
                roles.setName("USER");
                roles = roleRepository.save(roles);
            }
            
            user.setRoles(Collections.singleton(roles));

            userRepository.save(user);

            response.setStatus(200);
            response.getWriter().write("User registered successfully");
            response.getWriter().flush();
            
        } catch (Exception e) {
            response.setContentType("text/plain; charset=UTF-8");
            response.setStatus(500);
            response.getWriter().write("Registration failed: " + e.getMessage());
            response.getWriter().flush();
        }
    }

    @PostMapping("/add-role")
    @ResponseBody
    public void addRoleToUser(@RequestParam String username, @RequestParam String roleName, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/plain; charset=UTF-8");
            
            // Tìm user
            Optional<Users> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                response.setStatus(404);
                response.getWriter().write("User not found: " + username);
                response.getWriter().flush();
                return;
            }
            
            Users user = userOpt.get();
            
            // Tìm hoặc tạo role
            Optional<Role> roleOpt = roleRepository.findByName(roleName);
            Role role;
            if (roleOpt.isPresent()) {
                role = roleOpt.get();
            } else {
                // Tạo role mới nếu chưa có
                role = new Role();
                role.setName(roleName);
                role = roleRepository.save(role);
            }
            
            // Thêm role vào user (không ghi đè role cũ)
            user.getRoles().add(role);
            userRepository.save(user);
            
            response.setStatus(200);
            response.getWriter().write("Added role " + roleName + " to user " + username + " successfully");
            response.getWriter().flush();
            
        } catch (Exception e) {
            response.setContentType("text/plain; charset=UTF-8");
            response.setStatus(500);
            response.getWriter().write("Failed to add role: " + e.getMessage());
            response.getWriter().flush();
        }
    }

    @GetMapping("/user-roles")
    @ResponseBody
    public void getUserRoles(@RequestParam String username, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/plain; charset=UTF-8");
            
            Optional<Users> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                response.setStatus(404);
                response.getWriter().write("User not found: " + username);
                response.getWriter().flush();
                return;
            }
            
            Users user = userOpt.get();
            StringBuilder roles = new StringBuilder();
            roles.append("User: ").append(user.getUsername()).append("\n");
            roles.append("Email: ").append(user.getEmail()).append("\n");
            roles.append("Roles: ");
            
            for (Role role : user.getRoles()) {
                roles.append(role.getName()).append(" ");
            }
            
            response.setStatus(200);
            response.getWriter().write(roles.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            response.setContentType("text/plain; charset=UTF-8");
            response.setStatus(500);
            response.getWriter().write("Failed to get user roles: " + e.getMessage());
            response.getWriter().flush();
        }
    }
}