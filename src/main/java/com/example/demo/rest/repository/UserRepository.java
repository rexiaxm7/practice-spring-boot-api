package com.example.demo.rest.repository;

import com.example.demo.rest.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
