package com.example.demo.rest.bean;

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
    @NotBlank(message = "名前を入力してください")
    private String name;

    @Column(name= "team_id")
    @NotNull(message = "年齢を入力してください")
    private int team_id;

}
