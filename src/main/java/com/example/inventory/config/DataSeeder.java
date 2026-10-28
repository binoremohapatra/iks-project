package com.example.inventory.config;

import com.example.inventory.model.Product;
import com.example.inventory.model.Role;
import com.example.inventory.model.User;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Runs once at startup: creates demo users and a few products if tables are empty. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN));
            userRepository.save(new User("user", passwordEncoder.encode("user123"), Role.USER));
        }
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Laptop", "14 inch, 16GB RAM", new BigDecimal("55000.00"), 25));
            productRepository.save(new Product("Wireless Mouse", "Bluetooth, silent click", new BigDecimal("799.00"), 120));
            productRepository.save(new Product("Mechanical Keyboard", "Blue switches", new BigDecimal("3499.50"), 40));
        }
    }
}
