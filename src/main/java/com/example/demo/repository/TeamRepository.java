package com.example.demo.repository;

import com.example.demo.bean.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository  extends JpaRepository<Team, Integer> { }
