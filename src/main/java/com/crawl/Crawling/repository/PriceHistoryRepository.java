package com.crawl.Crawling.repository;

import com.crawl.Crawling.entity.PriceHistory;
import com.crawl.Crawling.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    //상품 정보로 상품에 대한 가격이력 리스트 반환
    List<PriceHistory> findByProduct(Product product);
    PriceHistory findTopByProductOrderByPriceDesc(Product product);
    PriceHistory findTopByProductOrderByPriceAsc(Product product);
}
