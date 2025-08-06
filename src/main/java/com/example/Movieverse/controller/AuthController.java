package com.example.Movieverse.controller;
import ch.qos.logback.core.net.SMTPAppenderBase;
import com.example.Movieverse.Payload.JwtResponse;
import com.example.Movieverse.Payload.SignUpRequest;
import com.example.Movieverse.Payload.LoginRequest;

import com.example.Movieverse.model.User;
import com.example.Movieverse.repository.UserRepository;
import com.example.Movieverse.security.jwt.JwtUtils;
import com.example.Movieverse.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body("Error: Email is already in use!");
//        }
//
//        // Create new user's account
//        User user = new User(
//                signUpRequest.getUsername(),
//                signUpRequest.getEmail(),
//                passwordEncoder.encode(signUpRequest.getPassword()), // ← This is the correct line
//                "ROLE_USER"
//        );
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok("User registered successfully!");
//    }
@PostMapping("/signup")
public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
   try {
       System.out.println("Inside /signup controller");
       System.out.println("SIGNUP endpoint hit");

       System.out.println("Received signup request — Email: " + signUpRequest.getEmail());
       if (userRepository.existsByEmail(signUpRequest.getEmail())) {
           return ResponseEntity
                   .badRequest()
                   .body("Error: Email is already in use!");
       }

       // Create new user's account
       User user = new User(
               signUpRequest.getUsername(),
               signUpRequest.getEmail(),
               passwordEncoder.encode(signUpRequest.getPassword()),
               "ROLE_USER"

       );

       userRepository.save(user);
       // Authenticate user right after signup
       Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(), signUpRequest.getPassword())
       );

       SecurityContextHolder.getContext().setAuthentication(authentication);
       String jwt = jwtUtils.generateJwtToken(authentication);

       UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
       System.out.println("Authoritiessssssssssss: " + userDetails.getAuthorities());


       return ResponseEntity.ok(new JwtResponse(jwt,
               userDetails.getId(),
               userDetails.getUsername(),
               userDetails.getEmail(),
               userDetails.getAuthorities().stream()
                       .map(item -> item.getAuthority())
                       .collect(Collectors.toList())
       ));
   } catch (Exception e) {
       e.printStackTrace();
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("Exception occurred: " + e.getMessage());
   }

}
    // POST /auth/login
    @PostMapping("/login")

    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return ResponseEntity.ok(new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        userDetails.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList())
                ));

            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }

    }
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()") // optional but good for clarity
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("id", userDetails.getId());
        response.put("username", userDetails.getUsername());
        response.put("email", userDetails.getEmail());
        response.put("roles", userDetails.getAuthorities());

        return ResponseEntity.ok(response);
    }
}
