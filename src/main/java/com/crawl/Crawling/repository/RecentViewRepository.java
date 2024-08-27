package com.crawl.Crawling.repository;


import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.RecentView;
import com.crawl.Crawling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentViewRepository extends JpaRepository<RecentView, Long> {
    List<RecentView> findAllByUserOrderByViewDateDesc(User user); //특정 사용자가 최근 본 목록 날짜 순으로 내림차순
    RecentView findByUserAndProduct(User user, Product product);

}
