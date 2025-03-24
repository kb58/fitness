package com.example.demo.repository;


import com.example.demo.module.Role;
import com.example.demo.module.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitilze implements CommandLineRunner {
    @Autowired

    private UserRepo userRepo;

    @Override
    public void run(String... args) throws Exception {

        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);
        if (userRepo.findByUserEmail("admin@connectFit.com") == null) {
            User admin = new User();
            admin.setUserEmail("admin@connectFit.com");
            admin.setUsername("admin1");
            admin.setPassword(encoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
        }
    }
}
