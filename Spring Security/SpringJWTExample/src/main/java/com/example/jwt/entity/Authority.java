package com.example.jwt.entity;

import com.example.jwt.model.AuthorityEnum;
import jakarta.persistence.*;

@Table(name = "authorities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Authorities
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorityEnum name;

    // Many authorities belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Foreign key column in authorities table
    private UserEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorityEnum getName() {
        return name;
    }

    public void setName(AuthorityEnum name) {
        this.name = name;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}