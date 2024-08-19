package com.crawl.Crawling.repository;


import com.crawl.Crawling.dto.ProductSearchDto;
import com.crawl.Crawling.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    public Page<Product> getCategoryPage(ProductSearchDto productSearchDto, Pageable pageable);
}
