package com.example.tarea_lab5.Controller;

import com.example.tarea_lab5.Entity.Product;
import com.example.tarea_lab5.Repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.validation.Valid;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public String list(Model model) {

        model.addAttribute(
                "products",
                productRepository.findAll()
        );

        return "product/list";
    }

    @GetMapping("/new")
    public String create(Model model) {

        model.addAttribute(
                "product",
                new Product()
        );

        return "product/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model
    ) {

        Product product = productRepository
                .findById(id)
                .orElseThrow();

        model.addAttribute(
                "product",
                product
        );

        return "product/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute Product product,
            BindingResult bindingResult
    ) {

        Product existing = productRepository
                .findByName(product.getName());

        if(existing != null &&
                !existing.getId().equals(product.getId())) {

            bindingResult.rejectValue(
                    "name",
                    "error.name",
                    "Nombre duplicado"
            );
        }

        if(bindingResult.hasErrors()) {
            return "product/form";
        }

        productRepository.save(product);

        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        productRepository.deleteById(id);

        return "redirect:/product";
    }

    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatch() {
        return "redirect:/product/new?error=number";
    }
}