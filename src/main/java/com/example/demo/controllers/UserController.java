package com.example.demo.controllers;

import com.example.demo.bean.User;
import com.example.demo.security.SimpleLoginUser;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> userListGet() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Optional<User> userGet(@PathVariable("id") int id) {
        return userService.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public User userUpdate(@PathVariable("id") int id,@RequestBody User user) {
        //TODO:認証情報からパスワードを取得したい
        Optional<User> myUser = userService.findOne(id);
        myUser.ifPresent( me -> {
            user.setPassword(me.getPassword());
            user.setId(id);
        });
        return userService.update(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void userDelete(@PathVariable("id") int id) {
       userService.delete(id);
    }
}
