package com.example.demo.repository;

import com.example.demo.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 月報未登録者のIDを取得する
    @Query("SELECT u.id FROM User u WHERE u.id NOT IN (SELECT r.user_id FROM Report r WHERE r.year = :thisYear AND r.month = :thisMonth) AND u.team_id = :teamId")
    List<Integer> checkSubmission(@Param("thisYear") int thisYear,@Param("thisMonth") int thisMonth, @Param("teamId") int teamId);
}
