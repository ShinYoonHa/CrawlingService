package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.Notice;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public void toggleNotice(User user, Product product, int price) { //product에 알림설정 toggle 메소드
        Notice notice = findByUserAndProduct(user, product);
        if(notice != null) { //사용자의 가격 알림 신청 이력이 db에 있을 경우
            noticeRepository.delete(notice); //삭제
        } else { //사용자가 상품을 좋아요 한적이 없을 경우
            Notice newNotice = new Notice();
            newNotice.setUser(user);
            newNotice.setProduct(product);
            newNotice.setPrice(price);

            noticeRepository.save(newNotice);
        }
    }
    //User와 Product로 좋아요 이력 찾기 (단일 반환값)
    public Notice findByUserAndProduct(User user, Product product) {
        try {
            Notice notice = noticeRepository.findByUserAndProduct(user, product);
            return notice;
        } catch (NullPointerException e) {
            return null;
        }
    }
    //User로 좋아요 이력 찾기 (다중 반환값)
    public List<Notice> findByUser(User user) {
        return noticeRepository.findByUser(user);
    }

    public void removeNotice(User user, Product product) {
        noticeRepository.deleteByUserAndProduct(user, product);
    }

    public List<Notice> getAllNotice() {
        return noticeRepository.findAll();
    }

}
