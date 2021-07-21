package com.example.demo;

import com.example.demo.rest.validator.ConfirmUserIdValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConfirmUserIdValidatorTest {


    @Autowired
    ConfirmUserIdValidator confirmUserIdValidator;

    private ConstraintValidatorContext context;



    @ParameterizedTest
    @ValueSource(strings = { "5", "3", "4" })
    void ConfirmDate(String s) {

        assertTrue(confirmUserIdValidator.isValid(Integer.parseInt(s), context));
    }

}

