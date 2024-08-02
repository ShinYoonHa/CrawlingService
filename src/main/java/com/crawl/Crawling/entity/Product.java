package com.crawl.Crawling.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PriceHistory> priceHistories;
}
