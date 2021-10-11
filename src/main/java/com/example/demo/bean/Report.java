package com.example.demo.bean;

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
    @NotNull(message = "{NotNull.Report.user_id}")
    private int user_id;

    @Column(name="year")
    @NotNull(message = "{NotNull.Report.month}")
    private int year;

    @Column(name="month")
    @Min(1)
    @Max(12)
    @NotNull(message = "{NotNull.Report.month}")
    private int month;

    @Column(name = "content")
    @NotBlank(message = "{NotBlank.Report.content}")
    private String content;

}
