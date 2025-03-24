package com.example.demo.service;


import com.example.demo.module.User;

import java.util.List;


public interface AdminService {
    List<User> getAllUsers();
    String deleteUser(Long id);

    User createUser(User User);
    void bulkImportUsers(List<User> users);

}
