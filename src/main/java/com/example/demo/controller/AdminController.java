package com.example.demo.controller;


import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.module.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.impl.UserServiceImpl;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    AdminService adminService;



    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers(){
        try{
            List<User> users=adminService.getAllUsers();
            return ResponseEntity.ok(users);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching users: "+e.getMessage());
        }
    }




    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id){
        return adminService.deleteUser(id);
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createAdmin(@RequestBody User user){
        try{
            User createdUser=adminService.createUser(user);
            return ResponseEntity.ok(createdUser);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }






}
