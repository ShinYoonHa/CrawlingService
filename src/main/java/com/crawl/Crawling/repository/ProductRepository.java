package com.crawl.Crawling.repository;

import com.crawl.Crawling.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
//    @Query("SELECT p from Product p WHERE p.category = :category")
//    Page<Product> findAllByCategory(String category, Pageable pageable);
//    @Query("SELECT p from Product p WHERE p.name LIKE %:keyword% AND " +
//            "p.category = :category")
//    Page<Product> findByCategoryAndKeyword(String category, String keyword, Pageable pageable);
}
