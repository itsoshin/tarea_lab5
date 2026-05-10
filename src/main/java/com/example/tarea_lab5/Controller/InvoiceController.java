package com.example.tarea_lab5.Controller;

import com.example.tarea_lab5.Entity.Invoice;
import com.example.tarea_lab5.Entity.InvoiceDetail;
import com.example.tarea_lab5.Entity.Product;
import com.example.tarea_lab5.Repository.CustomerRepository;
import com.example.tarea_lab5.Repository.InvoiceRepository;
import com.example.tarea_lab5.Repository.ProductRepository;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public String list(Model model) {

        model.addAttribute("invoices", invoiceRepository.findAll());

        return "invoice/list";
    }

    @GetMapping("/new")
    public String create(Model model) {

        Invoice invoice = new Invoice();

        for(int i = 0; i < 3; i++) {
            invoice.getDetails().add(new InvoiceDetail());
        }

        model.addAttribute("invoice", invoice);
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("products", productRepository.findAll());

        return "invoice/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Invoice invoice, BindingResult bindingResult, Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        if(invoice.getDate() == null) {
            bindingResult.rejectValue(
                    "date",
                    "error.date",
                    "Ingrese una fecha"
            );
        }
        if(invoice.getDate() != null && invoice.getDate().isAfter(LocalDate.now())) {
            bindingResult.rejectValue(
                    "date",
                    "error.date",
                    "La fecha no puede ser futura"
            );
        }
        if(invoice.getType() != null && invoice.getCustomer() != null) {
            if(invoice.getType().equals("FACTURA") && !invoice.getCustomer().getDocumentType().equals("RUC")) {
                bindingResult.reject(
                        "error.type",
                        "Factura solo permite clientes RUC"
                );
            }
            if(invoice.getType().equals("BOLETA") && !invoice.getCustomer().getDocumentType().equals("DNI")) {
                bindingResult.reject(
                        "error.type",
                        "Boleta solo permite clientes DNI"
                );
            }
        }
        boolean hasProducts = false;

        Set<Integer> products = new HashSet<>();
        if(invoice.getDate() == null) {
            bindingResult.rejectValue(
                    "date",
                    "error.date",
                    "Ingrese una fecha"
            );
        }
        if(invoice.getDetails() != null) {
            for(InvoiceDetail d : invoice.getDetails()) {
                if(d.getProduct() == null || d.getProduct().getId() == null || d.getQuantity() == null || d.getQuantity() <= 0) {
                    continue;
                }

                hasProducts = true;
                Integer productId = d.getProduct().getId();

                if(products.contains(productId)) {
                    bindingResult.reject(
                            "error.repeat",
                            "Producto repetido"
                    );
                }

                products.add(productId);
                Product product = productRepository.findById(productId).orElseThrow();
                d.setProduct(product);

                if(d.getQuantity() > product.getStock()) {
                    bindingResult.reject(
                            "error.stock",
                            "Stock insuficiente para " + product.getName()
                    );
                }
                d.setInvoice(invoice);
                d.setPrice(product.getPrice());
                d.setSubtotal(d.getPrice() * d.getQuantity());
            }
        }
        if(!hasProducts) {
            bindingResult.reject(
                    "error.products",
                    "Debe seleccionar al menos un producto"
            );
        }
        if(bindingResult.hasErrors()) {
            return "invoice/form";
        }
        for(InvoiceDetail d : invoice.getDetails()) {
            if(d.getProduct() == null ||
                    d.getQuantity() == null ||
                    d.getQuantity() <= 0) {
                continue;
            }
            Product product = productRepository.findById(d.getProduct().getId()).orElseThrow();
            product.setStock(product.getStock() - d.getQuantity());
            productRepository.save(product);
        }
        invoice.getDetails().removeIf(d -> d.getProduct() == null || d.getProduct().getId() == null ||
                        d.getQuantity() == null ||
                        d.getQuantity() <= 0
        );
        invoiceRepository.save(invoice);
        return "redirect:/invoice";
    }
}
