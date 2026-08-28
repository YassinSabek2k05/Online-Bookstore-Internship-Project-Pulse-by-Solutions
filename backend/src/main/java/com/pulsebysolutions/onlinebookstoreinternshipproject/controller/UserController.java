package com.pulsebysolutions.onlinebookstoreinternshipproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {
    @GetMapping("/test")
    public String test() {
        return "User controller is working!";
    }
}
