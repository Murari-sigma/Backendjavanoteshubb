package com.murari.javanoteshubb.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/java/introduction")
    public String introduction() {
        return "introduction";
    }

}
