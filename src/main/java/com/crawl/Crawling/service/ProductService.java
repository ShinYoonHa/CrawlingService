package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public void saveAllProducts(List<Product> products) {
        for(Product product : products) {
            //중복 데이터 확인 및 처리
            Product existingProduct = productRepository.findById(product.getId()).orElse(null);
            if(existingProduct != null) {
                //기존 제품이 있을경우, 기존 제품에 대해 변경된 필드만 업데이트
                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setImg(product.getImg());
                existingProduct.setRate(product.getRate());
                existingProduct.setRate_count(product.getRate_count());
                productRepository.save(existingProduct);

                //가격 이력 저장
                priceHistoryService.savePriceHistory(existingProduct, product.getPrice());
            } else {
                //기존 제품이 없을 경우 새 제품 추가
                productRepository.save(product);
                //새 제품에 대한 가격 이력 추가
                priceHistoryService.savePriceHistory(product, product.getPrice());
            }
        }
    }

    //모든 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Optional<Product> getProduct(Long item_id) {
        return productRepository.findById(item_id);
    }

    //상품가격 업데이트 및 새로운 가격 이력 저장
    public void updateProductPrice(Long productId, int newPrice) {
        //상품 아이디로 상품 객체 검색
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        priceHistoryService.savePriceHistory(product, newPrice); // PriceHistoryService 사용

        // 현재 가격 정보 수정
        product.setPrice(newPrice);
        productRepository.save(product);
    }
}