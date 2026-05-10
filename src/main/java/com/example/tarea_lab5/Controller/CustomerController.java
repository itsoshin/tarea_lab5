package com.example.tarea_lab5.Controller;

import com.example.tarea_lab5.Entity.Customer;
import com.example.tarea_lab5.Repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customer/list";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("customer", new Customer());

        return "customer/form";
    }

    @GetMapping("/edit")
    public String edit(@ModelAttribute("customer") Customer customer, Model model, @RequestParam("id") int id) {

        Optional<Customer> optCustomer = customerRepository.findById(id);
        if (optCustomer.isPresent()) {
            customer = optCustomer.get();
            model.addAttribute("customer", customer);
            return "customer/form";
        } else {
            return "redirect:/customer";
        }
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Customer customer, BindingResult bindingResult) {
        if(customer.getDocumentType() != null) {
            if(customer.getDocumentType().equals("DNI")) {
                if(customer.getDocument() == null || !customer.getDocument().matches("\\d{8}")) {
                    bindingResult.rejectValue(
                            "document",
                            "error.document",
                            "El DNI debe tener 8 dígitos"
                    );
                }
            }
            if(customer.getDocumentType().equals("RUC")) {
                if(customer.getDocument() == null || !customer.getDocument().matches("\\d{11}")) {
                    bindingResult.rejectValue(
                            "document",
                            "error.document",
                            "El RUC debe tener 11 dígitos"
                    );
                }
            }
        }

        Customer existing = customerRepository.findByDocument(customer.getDocument());

        if(existing != null && !existing.getId().equals(customer.getId())) {
            bindingResult.rejectValue(
                    "document",
                    "error.document",
                    "Documento ya registrado");
        }

        if(bindingResult.hasErrors()) {
            return "customer/form";
        }

        customerRepository.save(customer);
        return "redirect:/customer";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id, RedirectAttributes attr) {

        customerRepository.deleteById(id);

        attr.addFlashAttribute("msg",
                "Cliente eliminado");

        return "redirect:/customer";
    }
}