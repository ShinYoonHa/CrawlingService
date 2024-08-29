package com.crawl.Crawling.controller;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.dto.ProductDto;
import com.crawl.Crawling.entity.*;
import com.crawl.Crawling.service.ProductService;
import com.crawl.Crawling.service.RecommendService;
import com.crawl.Crawling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;
    private final RecommendService recommendService;
    private final ProductService productService;

    @GetMapping(value = "/")
    public String main(Model model, Principal principal) {
        // 카테고리별 가격 하락률이 높은 상품을 가져옴
        Map<Category, List<Product>> priceDropProductsByCategory = productService.getTopPriceDropProductsByCategory(20);
        boolean isLoggedIn = (principal != null); // 로그인 여부 확인

        if(isLoggedIn) { //로그인된 사용자가 있을 경우
            User user = userService.findByEmail(principal.getName());
            //좋아요 목록, 최근 조회 목록 기반으로 추천리스트 작성
            List<ProductDto> recommedList = recommendService.recommendProduct(user);
            model.addAttribute("recommendList", recommedList);
        }

        model.addAttribute("priceDropProductsByCategory", priceDropProductsByCategory);
        return "main";
    }
}
