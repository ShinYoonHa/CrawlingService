package com.crawl.Crawling.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RecentView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name= "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime viewDate; // 최근 본 날짜

    public RecentView(User user, Product product) {
        this.user = user;
        this.product = product;
        this.viewDate = LocalDateTime.now(); // 현재 시간으로 설정
    }
}
