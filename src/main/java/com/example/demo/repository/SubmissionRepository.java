package com.example.demo.repository;

import com.example.demo.bean.Report;
import com.example.demo.bean.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    @Query("SELECT count(s) FROM Submission s WHERE s.team_id =:teamId AND s.year = :thisYear AND s.month = :thisMonth")
    int countBySubmission(@Param("teamId") int teamId,@Param("thisYear") int thisYear, @Param("thisMonth") int thisMonth);
}
