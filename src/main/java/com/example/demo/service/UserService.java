package com.example.demo.service;

import com.example.demo.module.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User register(User user);
    String login(User user);
    User updateUser(Long id, User updatedUser);
    User getUser(Long id);
    void deleteUser(Long userId);
}
