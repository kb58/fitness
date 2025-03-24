package com.example.demo.controller;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.module.User;
import com.example.demo.repository.UserPrincipal;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String greet(){
        return "Welcome to Socio";
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try{
            User createdUser=userService.register(user);
            return ResponseEntity.ok(createdUser);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/auth/login")
    public String login(@RequestBody User user){
        return userService.login(user);
    }


    @PutMapping("/updateUser/{id}")
    public User updateUser(@PathVariable Long id,@RequestBody User updatedUser){
        return userService.updateUser(id,updatedUser);
    }


    @GetMapping("/getUser/{id}")
    public ResponseEntity<? > getUser(@PathVariable Long id){

        try{
            User user= userService.getUser(id);
            return ResponseEntity.ok(user);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching users: "+e.getMessage());
        }
    }

    @DeleteMapping("/Delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserPrincipal userPrincipal){
        try{
            User user=userPrincipal.getUser();
            userService.deleteUser(user.getId());
            return ResponseEntity.ok(user.getUsername() + " Delete successfully");
        }
        catch (Exception e){
            return (ResponseEntity<?>) ResponseEntity.internalServerError();
        }
    }
}

