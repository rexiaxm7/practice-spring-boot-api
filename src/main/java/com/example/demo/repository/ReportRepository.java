package com.example.demo.repository;

import com.example.demo.bean.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("SELECT r FROM Report r WHERE r.user_id IN (SELECT u.id FROM User u  WHERE u.team_id = :teamId) AND r.month =:thisMonth")
    List<Report> findByTeamId(@Param("teamId") int teamId,@Param("thisMonth") int thisMonth);
}
