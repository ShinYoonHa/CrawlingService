package com.crawl.Crawling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    @GetMapping(value = "/login")
    public String login() {
        return "user/loginForm";
    }
    @GetMapping(value = "/signup")
    public String signup() {
        return "user/signupForm";
    }
}
