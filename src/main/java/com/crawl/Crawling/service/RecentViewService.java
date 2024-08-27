package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.RecentView;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.repository.RecentViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecentViewService {
    private static final int MAX_RECENT_VIEWS = 20; // 최대 최근본상품 수

    @Autowired
    private RecentViewRepository recentViewRepository;

    public List<RecentView> findAllByUser(User user) {
        return recentViewRepository.findAllByUserOrderByViewDateDesc(user); // 수정된 부분
    }

    public void addRecentView(User user, Product product) {
        //기존의 RecentView 존재하는지 확인
        RecentView existingView = recentViewRepository.findByUserAndProduct(user, product);
        if (existingView == null) { //기존에 없을 때만 추가
            List<RecentView> recentViews = recentViewRepository.findAllByUserOrderByViewDateDesc(user);
            
            //최대 크기 넘어가면 가장 오래된 RecentView 부터 삭제
            if(recentViews.size() >= MAX_RECENT_VIEWS) {
                RecentView oldestView = recentViews.get(0); //오래된 RecentView 조회
                recentViewRepository.delete(oldestView);
            }
            //새로운 RecentView 추가
            RecentView recentView = new RecentView(user, product);
            recentViewRepository.save(recentView);
        }
    }
}
