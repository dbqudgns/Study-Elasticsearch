package com.example.coupangapiserver.repository;

import com.example.coupangapiserver.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
