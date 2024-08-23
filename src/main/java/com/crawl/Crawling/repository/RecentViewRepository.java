package com.crawl.Crawling.repository;


import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.RecentView;
import com.crawl.Crawling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentViewRepository extends JpaRepository<RecentView, Long> {
    List<RecentView> findAllByUser(User user);
    RecentView findByUserAndProduct(User user, Product product);

}
