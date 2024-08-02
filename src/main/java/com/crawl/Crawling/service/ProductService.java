package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.PriceHistory;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceHistoryService priceHistoryService;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    //상품가격 업데이트 및 새로운 가격 이력 저장
    public void updateProductPrice(Long productId, int newPrice) {
        //상품 아이디로 상품 객체 검색
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        // 새로운 가격 정보를 History에 추가
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProduct(product);
        priceHistory.setPrice(newPrice);
        priceHistory.setDate(LocalDate.now());
        priceHistoryService.savePriceHistory(priceHistory); // PriceHistoryService 사용

        // 현재 가격 정보 수정
        product.setPrice(newPrice);
        productRepository.save(product);
    }
}