package com.crawl.Crawling.repository;

import com.crawl.Crawling.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    Optional<Product> findById(Long productId);
}
