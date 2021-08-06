package com.example.demo.bean;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "team",schema = "public")
public class Team {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="name")
    @NotBlank(message = "{NotBlank.Team.name}")
    private String name;

    @Column(name="input_start_date")
    @NotNull(message = "{NotNull.Team.input_start_date}")
    private int input_start_date;

    @Column(name="alert_start_days")
    @NotNull(message = "{NotNull.Team.alert_start_days}")
    private int alert_start_days;

    @Column(name="sending_message_url")
    @NotNull(message = "{NotNull.Team.alert_start_days}")
    private String sending_message_url;
}
