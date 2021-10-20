package com.example.demo.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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

    @ManyToOne
    @JoinColumn(name="team_id")
    private Team team;

}
