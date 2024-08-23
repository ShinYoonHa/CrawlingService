package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.Likes;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;

    public void toggleLikes(User user, Product product) { //product에 좋아요 누름/취소 toggle 메소드
        Likes likes = findByUserAndProduct(user, product);
        if(likes != null) { //사용자가 상품을 좋아요한 이력이 db에 있을 경우
            likesRepository.delete(likes); //삭제
        } else { //사용자가 상품을 좋아요 한적이 없을 경우
            Likes newLikes = new Likes();
            newLikes.setUser(user);
            newLikes.setProduct(product);

            likesRepository.save(newLikes);
        }
    }
    //User와 Product로 좋아요 이력 찾기 (단일 반환값)
    public Likes findByUserAndProduct(User user, Product product) {
        try {
            Likes likes = likesRepository.findByUserAndProduct(user, product);
            return likes;
        } catch (NullPointerException e) {
            return null;
        }
    }
    //User로 좋아요 이력 찾기 (다중 반환값)
    public List<Likes> findByUser(User user) {
        return likesRepository.findByUser(user);
    }

    public void removeLike(User user, Product product) {
        likesRepository.deleteByUserAndProduct(user, product);
    }

}
