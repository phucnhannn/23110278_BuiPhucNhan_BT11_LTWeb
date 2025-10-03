package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.Users;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Tạo các role cần thiết nếu chưa tồn tại
        createRoleIfNotExists("USER");
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("EDITOR");
        createRoleIfNotExists("CREATOR");
        
        // Tạo user admin mặc định để test
        createDefaultAdminUser();
        
        System.out.println("✅ Data initialization completed - All roles and default admin user created!");
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            System.out.println("🔧 Created role: " + roleName);
        } else {
            System.out.println("✓ Role already exists: " + roleName);
        }
    }

    private void createDefaultAdminUser() {
        String adminUsername = "admin";
        String adminEmail = "admin@iotstar.vn";
        
        if (!userRepository.existsByUsername(adminUsername)) {
            Users adminUser = new Users();
            adminUser.setName("Administrator");
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEnabled(true);
            
            // Lấy các role từ database trong cùng transaction
            Set<Role> adminRoles = new HashSet<>();
            roleRepository.findByName("USER").ifPresent(adminRoles::add);
            roleRepository.findByName("ADMIN").ifPresent(adminRoles::add);
            roleRepository.findByName("EDITOR").ifPresent(adminRoles::add);
            roleRepository.findByName("CREATOR").ifPresent(adminRoles::add);
            
            adminUser.setRoles(adminRoles);
            userRepository.save(adminUser);
            
            System.out.println("🔧 Created default admin user:");
            System.out.println("   Username: " + adminUsername);
            System.out.println("   Email: " + adminEmail);
            System.out.println("   Password: admin123");
            System.out.println("   Roles: USER, ADMIN, EDITOR, CREATOR");
        } else {
            System.out.println("✓ Admin user already exists: " + adminUsername);
        }
    }
}