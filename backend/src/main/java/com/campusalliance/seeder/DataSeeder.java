package com.campusalliance.seeder;

import com.campusalliance.entity.Role;
import com.campusalliance.entity.User;
import com.campusalliance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@university.edu";
        
        // Auto-provision master admin if it doesn't exist
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .fullName("Master Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Seeded master admin account: " + adminEmail);
        }

        // Seed initial faculty accounts if they don't exist
        seedUserIfNotExists("dr.sharma@kiit.ac.in", "faculty123", "Dr. Rajesh Sharma", Role.FACULTY);
        seedUserIfNotExists("prof.das@kiit.ac.in", "faculty123", "Prof. Subhash Das", Role.FACULTY);
        seedUserIfNotExists("dr.mukherjee@kiit.ac.in", "faculty123", "Dr. Swati Mukherjee", Role.FACULTY);

        // Seed initial student accounts (23051800 to 23051806)
        seedUserIfNotExists("23051800@kiit.ac.in", "password123", "Aarav Patel", Role.STUDENT);
        seedUserIfNotExists("23051801@kiit.ac.in", "password123", "Ananya Roy", Role.STUDENT);
        seedUserIfNotExists("23051802@kiit.ac.in", "password123", "Rohan Sharma", Role.STUDENT);
        seedUserIfNotExists("23051803@kiit.ac.in", "password123", "Sneha Sen", Role.STUDENT);
        seedUserIfNotExists("23051804@kiit.ac.in", "password123", "Vikram Verma", Role.STUDENT);
        seedUserIfNotExists("23051805@kiit.ac.in", "password123", "Pooja Hegde", Role.STUDENT);
        seedUserIfNotExists("23051806@kiit.ac.in", "password123", "Devendra Mehta", Role.STUDENT);
    }

    private void seedUserIfNotExists(String email, String rawPassword, String fullName, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .role(role)
                    .build();
            userRepository.save(user);
        }
    }
}
