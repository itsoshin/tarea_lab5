package com.example.tarea_lab5.Controller;

import org.springframework.stereotype.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InvoiceController {

    @GetMapping("/invoice")
    public String list() {
        return "invoice/list";
    }
}
