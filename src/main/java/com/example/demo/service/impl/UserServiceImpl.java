package com.example.demo.service.impl;


import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.module.Role;
import com.example.demo.module.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtService jwtService;

    public BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    @Override
    public User register(User user) {
        if(user.getRole()== Role.ADMIN||user.getUserEmail().endsWith("connectFit.com") ){
            throw new IllegalArgumentException(
                    "You are not authorize to create Admin");

        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }


    @Override
    public User updateUser(Long id,User updatedUser) {
        User existingUser=userRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("User with ID" +id+ "not found"));

        if(updatedUser.getUsername()!=null)existingUser.setUsername(updatedUser.getUsername());
        if(updatedUser.getRole()!=null)existingUser.setRole(updatedUser.getRole());
        if(updatedUser.getPassword()!=null){
            existingUser.setPassword(encoder.encode(updatedUser.getPassword()));
        }
        if(updatedUser.getUserEmail()!=null)existingUser.setUserEmail(updatedUser.getUserEmail());
        if(updatedUser.getIsPublic()!=null) existingUser.setIsPublic(updatedUser.getIsPublic());
        return  userRepo.save(existingUser);

    }

    @Override
    public String login(User user){
        Authentication authentication=authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserEmail(),user.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUserEmail());
        }


        return "fail";

    }

    @Override
    public User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("User with id - "+id+" not found"));
    }

    @Override
    public void deleteUser(Long userId){
        userRepo.deleteById(userId);
    }

}

