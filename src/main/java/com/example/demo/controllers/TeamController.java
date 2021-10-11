package com.example.demo.controllers;

import com.example.demo.bean.Team;
import com.example.demo.service.TeamService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Team> teamListGet() {
        return teamService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Optional<Team> teamGet(@PathVariable("id") int id) {
        return teamService.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Team teamCreate(@RequestBody Team team) {
        return teamService.create(team);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Team teamUpdate(@PathVariable("id") int id,@RequestBody Team team) {
        team.setId(id);
        return teamService.update(team);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void teamDelete(@PathVariable("id") int id) {
        teamService.delete(id);
    }
}
