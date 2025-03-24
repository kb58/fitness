package com.example.demo.service.impl;


import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.module.Role;
import com.example.demo.module.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepo userRepo;


    public BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    @Override
    public List<User> getAllUsers(){
        List<User> users=userRepo.findAll();
        if(users.isEmpty()){
            throw new ResourceNotFoundException("No users found.");
        }
        return users;
    }

    @Override
    public String deleteUser(Long id) {
        try{
            userRepo.deleteById(id);
            return "User Deleted Successfully";
        }catch (Exception e){
            return "Unable to Delete the User";
        }
    }



    @Override
    public User createUser(User user) {
        if(user.getRole()== Role.ADMIN && !user.getUserEmail().endsWith("@connectFit.com")){
            throw new IllegalArgumentException(
                    "Admins must have an email ending with @connectFit.com");

        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override

    public  void bulkImportUsers(List<User> users){
        userRepo.saveAll(users);
    }



}
