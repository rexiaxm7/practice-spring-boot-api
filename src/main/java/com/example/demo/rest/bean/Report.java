package com.example.demo.rest.bean;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "report",schema = "public")
public class Report {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="user_id")
    @NotNull(message = "ユーザーIDを入力してください")
    private int user_id;

    @Column(name="month")
    @Min(1)
    @Max(12)
    @NotNull(message = "月を入力してください")
    private int month;

    @Column(name = "content")
    @NotBlank(message = "内容を入力してください")
    private String content;
}
