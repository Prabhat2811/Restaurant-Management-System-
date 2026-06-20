package com.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login.html";   // resolves to templates/login.html (Thymeleaf)
                          // or static/login.html if you're serving static files
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
