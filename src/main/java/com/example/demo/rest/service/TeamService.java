package com.example.demo.rest.service;
import com.example.demo.rest.bean.Team;
import com.example.demo.rest.repository.TeamRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Optional<Team> findOne(Integer id) {
        return teamRepository.findById(id);
    }

    public Team create(Team team) {
        return teamRepository.save(team);
    }

    public Team update(Team team) {
        return teamRepository.save(team);
    }

    public void delete(Integer id) {
        teamRepository.deleteById(id);
    }
}
