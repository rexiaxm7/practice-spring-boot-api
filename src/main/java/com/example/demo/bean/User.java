package com.example.demo.bean;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "user",schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @Column(name="name")
    @NotBlank(message = "{NotBlank.User.name}")
    private String name;

    @Column(name= "team_id")
    @NotNull(message = "{NotNull.User.team_id}")
    private int team_id;

}
