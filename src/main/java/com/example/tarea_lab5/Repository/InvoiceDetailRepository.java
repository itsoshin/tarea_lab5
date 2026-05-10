package com.example.tarea_lab5.Repository;

import com.example.tarea_lab5.Entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDetailRepository  extends JpaRepository<InvoiceDetail, Integer> {
}
