package com.crawl.Crawling.repository;

import com.crawl.Crawling.entity.Notice;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Notice findByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
    List<Notice> findByUser(User user);
}
