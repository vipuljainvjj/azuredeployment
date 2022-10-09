package com.vipul.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TextController {

    @Value("${vipul.jain}")
    String age;

    @GetMapping("/getRandomValue/{index}")
    public String getRandomValue(@PathVariable int index) {
        List<String> list = Arrays.asList("One", "Two", "Three", "Four");
        if (index <= list.size()) {
            return list.get(index) + ", " + age;
        }
        return "Value Does Not Exist";
    }

    @GetMapping("/")
    public String defaultEntry() {
        return "You are on Welcome Page.";
    }

}
