package com.example.Movieverse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String username;

    @Column()
    private String email;

    @Column()
    private String password;
    private String role;

    public User(String username, String email, String encode, String roleUser) {

            this.username = username;
            this.email = email;
            this.password = encode;
            this.role = roleUser;

    }


    public String getRole() {
        return this.role;
    }
}
