package com.crawl.Crawling.controller;

import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.RecentView;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.service.ProductService;
import com.crawl.Crawling.service.RecentViewService;
import com.crawl.Crawling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecentViewController {
    private final UserService userService;
    private final RecentViewService recentViewService;
    private final ProductService productService;

    @GetMapping(value = "/recentView")
    public String recentView(Principal principal, Model model) {
        String username = principal.getName(); //getName() 시 사용자 이메일이 넘어온다
        User user = userService.findByEmail(username); // UserService에서 사용자 정보를 가져오는 메서드
        //사용자 객체로 해당 사용자와 관련된 RecentView 객체들을 찾아옴
        List<RecentView> recentViewList = recentViewService.findAllByUser(user);
        List<Product> recentViewProducts = new ArrayList<>(); //최근 본 상품 리스트

        for(RecentView recentView : recentViewList) { //recentViewList에서 productId 로 상품 검색 후 List에 저장
            Product product = productService.findById(recentView.getProduct().getId());
            recentViewProducts.add(product);
        }
        model.addAttribute("products", recentViewProducts);

        return "recentView/recentViewList";
    }
}
