package com.crawl.Crawling.repository;

import com.crawl.Crawling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByTel(String tel);
    User findByNameAndTel(String name, String tel);
}
