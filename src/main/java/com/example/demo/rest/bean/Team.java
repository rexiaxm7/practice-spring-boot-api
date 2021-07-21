package com.example.demo.rest.bean;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "team",schema = "public")
public class Team {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="name")
    @NotBlank(message = "チーム名を入力してください")
    private String name;
}
