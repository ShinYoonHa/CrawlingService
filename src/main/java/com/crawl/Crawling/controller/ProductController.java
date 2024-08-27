package com.crawl.Crawling.controller;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.dto.ProductDto;
import com.crawl.Crawling.dto.ProductSearchDto;
import com.crawl.Crawling.entity.*;
import com.crawl.Crawling.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final LikesService likesService;
    private final UserService userService;
    private final RecentViewService recentViewService;
    private final PriceHistoryService priceHistoryService;
    private final RecommendService recommendService;
    private final NoticeService noticeService;

    //카테고리별 상품 목록
    @GetMapping(value = {"/category={category}/page={page}", "/category={category}"})
    public String productList(ProductSearchDto productSearchDto, Model model,
                               @PathVariable("category") String category, @PathVariable("page") Optional<Integer> page,
                              Principal principal) {
        //page.isPresent() 값 있으면 page.get(), 없으면 0 반환. 페이지 당 사이즈 60개
        productSearchDto.setSearchCategory(category);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 60);
        Page<Product> products = productService.getCategoryPage(productSearchDto, pageable);

        List<RecentView> recentViewList = new ArrayList<>();
        if(principal != null) { //로그인 상태이면
            User user = userService.findByEmail(principal.getName());
            recentViewList = recentViewService.findAllByUser(user); //사용자가 최근 조회한 상품 리스트
        }

        model.addAttribute("categoryList", Category.values());
        model.addAttribute("products", products);
        model.addAttribute("productSearchDto", productSearchDto);
        model.addAttribute("maxPage", 10);
        model.addAttribute("recentViewList", recentViewList);

        return "product/productList"; //카테고리 상품 메인 페이지로 이동
    }

    //상품 상세 페이지
    @GetMapping(value = "/product/{productId}")
    public String productDetail(@PathVariable("productId") Long id, Model model, Principal principal) {
        ProductDto productDto = productService.getProductDto(id);
        boolean isLoggedIn = (principal != null); // 로그인 여부 확인
        //역대 최고&최저가 조회
        PriceHistory maxPriceHistory = priceHistoryService.findMaxPriceByProduct(productService.findById(id));
        PriceHistory minPriceHistory = priceHistoryService.findMinPriceByProduct(productService.findById(id));

        if(isLoggedIn) { //로그인된 사용자가 있을 경우
            User user = userService.findByEmail(principal.getName());
            Product product = productService.findById(id);
            Likes likes = likesService.findByUserAndProduct(user, product); //좋아요 이력 있는지 확인
            Notice notice = noticeService.findByUserAndProduct(user, product); //알림 설정 이력 존재 확인
            //좋아요 목록, 최근 조회 목록 기반으로 추천리스트 작성
            List<ProductDto> recommedList = recommendService.recommendProduct(user);

            recentViewService.addRecentView(user, product); //사용자와 상품으로 최근본 상품에 등록
            model.addAttribute("isLiked", likes != null);
            model.addAttribute("isNoticed", notice != null);
            model.addAttribute("recommendList", recommedList);
        } else {
            model.addAttribute("isLiked", false);
        }

        model.addAttribute("maxPrice", maxPriceHistory.getPrice());
        model.addAttribute("minPrice", minPriceHistory.getPrice());
        model.addAttribute("productDto", productDto);
        model.addAttribute("isLoggedIn", isLoggedIn); // 로그인 여부 추가

        return "product/productDetail";
    }

}
