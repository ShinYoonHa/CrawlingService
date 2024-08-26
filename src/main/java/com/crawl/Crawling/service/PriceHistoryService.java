package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.PriceHistory;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.repository.PriceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PriceHistoryService {
    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    //받은 상품에 대한 가격을 저장
    public void savePriceHistory(Product product) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProduct(product);
        priceHistory.setPrice(product.getPrice());
        priceHistory.setDate(LocalDate.now()); //현재 날짜를 저장 yyyy-MM-dd
        priceHistoryRepository.save(priceHistory);
    }

    //상품에 대한 가격 이력 리스트 반환
    public List<PriceHistory> getPriceHistoryByProduct(Product product) {
        return priceHistoryRepository.findByProduct(product);
    }

    //모든 가격 이력 반환
    public List<PriceHistory> getAllPriceHistories() {
        return priceHistoryRepository.findAll();
    }
    //상품의 최고가 조회. 가격 내림차순 정렬 후 첫 번째 값
    public PriceHistory findMaxPriceByProduct(Product product) {
        return priceHistoryRepository.findTopByProductOrderByPriceDesc(product);
    }
    //상품의 최저가 조회. 가격 오름차순 정렬 후 첫 번째 값
    public PriceHistory findMinPriceByProduct(Product product) {
        return priceHistoryRepository.findTopByProductOrderByPriceAsc(product);
    }

}