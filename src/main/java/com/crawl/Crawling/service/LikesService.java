package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.Likes;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;

    public void toggleLikes(User user, Product product) { //product에 좋아요 누름/취소 toggle 메소드
        Likes likes = findByUserAndProduct(user, product);
        if(likes != null) { //user가 product에 좋아요 눌러서 db에 likes 값이 있을 경우
            likesRepository.delete(likes); //삭제
        } else { //db에 해당 product에 대한 user의 likes가 없을 경우
            Likes newLikes = new Likes();
            newLikes.setUser(user);
            newLikes.setProduct(product);

            likesRepository.save(newLikes);
        }
    }
    public Likes findByUserAndProduct(User user, Product product) {
        try {
            Likes likes = likesRepository.findByUserAndProduct(user, product);
            return likes;
        } catch (NullPointerException e) {
            return null;
        }
    }

}
