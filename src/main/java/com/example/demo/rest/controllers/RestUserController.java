package com.example.demo.rest.controllers;

import com.example.demo.rest.bean.User;
import com.example.demo.rest.repository.UserRepository;
import com.example.demo.rest.service.RestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users")
public class RestUserController {

    private final RestUserService restUserService;

    @Autowired
    public RestUserController(RestUserService restUserService) {
        this.restUserService = restUserService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> userListGet() {
        return restUserService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Optional<User> userGet(@PathVariable("id") int id) {
        return restUserService.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public User userCreate(@RequestBody User user) {
        return restUserService.create(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public User userUpdate(@PathVariable("id") int id,@RequestBody User user) {
        user.setId(id);
        return restUserService.update(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void userDelete(@PathVariable("id") int id) {
       restUserService.delete(id);
    }
}
