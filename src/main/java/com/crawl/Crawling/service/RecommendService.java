package com.crawl.Crawling.service;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.dto.ProductDto;
import com.crawl.Crawling.entity.Likes;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.RecentView;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.repository.LikesRepository;
import com.crawl.Crawling.repository.ProductRepository;
import com.crawl.Crawling.repository.RecentViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RecentViewRepository recentViewRepository;
    private final LikesRepository likesRepository;
    private final ProductRepository productRepository;
    private final int PRICE_RANGE = 30000;

    public List<ProductDto> recommendProduct(User user) {
        // 사용자 최근 본 상품 및 좋아요 목록 조회
        List<Product> recentViewList = recentViewRepository.findAllByUserOrderByViewDateDesc(user)
                .stream()
                .map(RecentView::getProduct)
                .toList();
        List<Product> likedList = likesRepository.findByUser(user)
                .stream()
                .map(Likes::getProduct)
                .toList();

        // 카테고리별 가중치 계산
        Map<Category, Long> categoryWeights = recentViewList.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        // 가중치에 따라 추천 상품 리스트 생성
        List<Product> recommendList = new ArrayList<>();
        for (Map.Entry<Category, Long> entry : categoryWeights.entrySet()) {
            Category category = entry.getKey();
            Long weight = entry.getValue();

            // 각 카테고리에서 가중치만큼 상품을 가져옴
            List<Product> weightedProducts = productRepository.findByCategory(category)
                    .stream()
                    .filter(product -> !recentViewList.contains(product)
                            && !likedList.contains(product))
                    .sorted((p1, p2) -> Double.compare(p2.getRate(), p1.getRate())) // 평점 높은 순 정렬
                    .limit(weight) // 가중치에 따라 상품 개수 제한
                    .toList();

            recommendList.addAll(weightedProducts);
        }

        // 최근 본 상품과 유사한 상품 추가
        for (Product recentProduct : recentViewList) {
            List<Product> similarProductList = productRepository.findSimilarProducts(
                    recentProduct.getCategory(), recentProduct.getPrice() - PRICE_RANGE,
                    recentProduct.getPrice() + PRICE_RANGE);
            similarProductList.stream()
                    .filter(product -> !recentViewList.contains(product)
                            && !likedList.contains(product))
                    .forEach(recommendList::add);
        }

        // 중복 제거한 리스트 생성
        List<Product> distinctRecommendList = recommendList.stream()
                .distinct()
                .toList();

        // 최종 추천 리스트를 20개로 제한
        List<Product> finalRecommendList = distinctRecommendList.stream()
                .limit(20)
                .toList();

        // Entity를 DTO로 변환 후 반환
        return finalRecommendList.stream()
                .map(ProductDto::of)
                .collect(Collectors.toList());
    }
}