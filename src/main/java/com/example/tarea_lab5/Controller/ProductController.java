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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product/list";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("product", new Product());

        return "product/form";
    }

    @GetMapping("/edit")
    public String edit(@ModelAttribute("product") Product product, Model model, @RequestParam("id") int id) {

        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            product = optProduct.get();
            model.addAttribute("product", product);
            return "product/form";
        } else {
            return "redirect:/product";
        }
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Product product, BindingResult bindingResult) {
        Product existing = productRepository.findByName(product.getName());
        if(existing != null && !existing.getId().equals(product.getId())) {
            bindingResult.rejectValue(
                    "name",
                    "error.name",
                    "Ya existe ese nombre de producto"
            );
        }

        if(bindingResult.hasErrors()) {
            return "product/form";
        }

        productRepository.save(product);

        return "redirect:/product";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id, RedirectAttributes attr) {

        productRepository.deleteById(id);
        attr.addFlashAttribute(
                "msg",
                "Producto eliminado");

        return "redirect:/product";
    }
}