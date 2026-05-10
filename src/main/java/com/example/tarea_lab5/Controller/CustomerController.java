package com.example.tarea_lab5.Controller;

import com.example.tarea_lab5.Entity.Customer;
import com.example.tarea_lab5.Repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("")
    public String list(Model model) {

        model.addAttribute(
                "customers",
                customerRepository.findAll()
        );

        return "customer/list";
    }

    @GetMapping("/new")
    public String create(Model model) {

        model.addAttribute(
                "customer",
                new Customer()
        );

        return "customer/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model
    ) {

        Customer customer = customerRepository
                .findById(id)
                .orElseThrow();

        model.addAttribute(
                "customer",
                customer
        );

        return "customer/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute Customer customer,
            BindingResult bindingResult
    ) {

        if(customer.getDocumentType() != null) {

            if(customer.getDocumentType().equals("DNI")) {

                if(customer.getDocument() == null ||
                        !customer.getDocument().matches("\\d{8}")) {

                    bindingResult.rejectValue(
                            "document",
                            "error.document",
                            "DNI debe tener 8 dígitos"
                    );
                }
            }

            if(customer.getDocumentType().equals("RUC")) {

                if(customer.getDocument() == null ||
                        !customer.getDocument().matches("\\d{11}")) {

                    bindingResult.rejectValue(
                            "document",
                            "error.document",
                            "RUC debe tener 11 dígitos"
                    );
                }
            }
        }

        Customer existing = customerRepository
                .findByDocument(customer.getDocument());

        if(existing != null &&
                !existing.getId().equals(customer.getId())) {

            bindingResult.rejectValue(
                    "document",
                    "error.document",
                    "Documento ya registrado"
            );
        }

        if(bindingResult.hasErrors()) {
            return "customer/form";
        }

        customerRepository.save(customer);

        return "redirect:/customer";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        customerRepository.deleteById(id);

        return "redirect:/customer";
    }
}