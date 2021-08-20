package com.example.demo.bean;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "submission",schema = "public")
public class Submission {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="team_id")
    @NotNull(message = "{NotNull.Submission.team_id}")
    private int team_id;

    @Column(name="year")
    @NotNull(message = "{NotNull.Submission.year}")
    private int year;

    @Column(name="month")
    @Min(1)
    @Max(12)
    @NotNull(message = "{NotNull.Submission.month}")
    private int month;


}
