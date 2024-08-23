package com.crawl.Crawling.entity;

import com.crawl.Crawling.constant.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Product {
    @Id
    private Long id; //상품 아이디

    private String name; //상품명
    private int price; //가격
    private String img; //상품 이미지 경로
    private Double rate; //평점
    private int rate_count; //평점 개수

    @Enumerated(EnumType.STRING)
    private Category category; //카테고리

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PriceHistory> priceHistories; //가격이력

    // 연관관계 매핑
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Likes> likes; // 상품을 좋아요 누른 리스트

    // 연관관계 매핑
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RecentView> recentViews; //최근 본 리스트

}
