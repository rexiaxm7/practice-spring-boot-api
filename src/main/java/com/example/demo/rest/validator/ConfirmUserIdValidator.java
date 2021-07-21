package com.example.demo.rest.validator;

import com.example.demo.rest.annotation.ConfirmUserId;
import com.example.demo.rest.bean.User;
import com.example.demo.rest.repository.UserRepository;
import com.example.demo.rest.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ConfirmUserIdValidator implements ConstraintValidator<ConfirmUserId, Integer> {


    private final UserService userService;

    public ConfirmUserIdValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(ConfirmUserId constraintAnnotation) {

    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        List<User> list = userService.findAll();

        for(int i =0;i<list.size(); i++){
            if(list.get(i).getId() == value){
                return  true;
            }
        }

        return false;
    }
}
