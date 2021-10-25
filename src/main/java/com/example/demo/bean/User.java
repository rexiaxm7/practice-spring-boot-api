package com.example.demo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "user",schema = "public")
@Component
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @Column(name="name")
    @NotBlank(message = "{NotBlank.User.name}")
    private String name;

    @Column(name="team_id")
    @NotNull(message = "{NotBlank.User.team_id}")
    private int team_id;

    @Column(name="email", unique=true)
    @NotBlank(message = "{NotBlank.User.email}")
    private String email;

    @JsonIgnore
    @Column(name="password")
    @NotBlank(message = "{NotBlank.User.password}")
    private String password;


    @Column(name = "admin", nullable = false)
    private Boolean admin;

    @ManyToOne()
    @JoinColumn(name="team_id",insertable = false ,updatable = false)
    private Team team;

}
