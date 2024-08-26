package com.crawl.Crawling.repository;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    Optional<Product> findById(Long productId);
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findSimilarProducts(@Param("category")Category category,
                                      @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice);
    List<Product> findByCategoryIn(List<Category> categoryList);
}
