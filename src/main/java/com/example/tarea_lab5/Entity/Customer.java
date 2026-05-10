package com.example.tarea_lab5.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 11)
    private String document;

    @Column(nullable = false, length = 10)
    private String documentType;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<Invoice> invoices;
}
