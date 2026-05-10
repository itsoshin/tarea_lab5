package com.example.tarea_lab5.Controller;

import org.springframework.stereotype.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerController {

    @GetMapping("/customer")
    public String list() {
        return "customer/list";
    }
}
