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

//    //모든 가격 이력 업데이트
//    public void updateAllHistory() throws IOException {
//        // 모든 상품 조회
//        List<Product> products = productService.getAllProducts();
//
//        for (Product product : products) {
//            // 상품의 상세 페이지를 크롤링하여 최신 가격을 가져오는 메서드 호출
//            int latestPrice = crawlingService.crawlLatestPrice(product.getId());
//
//            // 가격 히스토리에 저장
//            savePriceHistory(product, latestPrice);
//        }
//    }
}