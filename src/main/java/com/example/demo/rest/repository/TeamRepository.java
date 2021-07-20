package com.example.demo.rest.repository;

import com.example.demo.rest.bean.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository  extends JpaRepository<Team, Integer> { }
