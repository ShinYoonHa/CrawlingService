package com.crawl.Crawling.repository;

import com.crawl.Crawling.entity.Likes;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
    List<Likes> findByUser(User user);
}
