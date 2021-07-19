package com.example.demo.rest.bean;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user",schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @Column(name="name")
    private String name;

    @Column(name="age")
    private int age;

}
