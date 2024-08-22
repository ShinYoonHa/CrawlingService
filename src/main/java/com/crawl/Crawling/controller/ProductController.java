package com.crawl.Crawling.controller;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.dto.LikeRequestDto;
import com.crawl.Crawling.dto.ProductDto;
import com.crawl.Crawling.dto.ProductSearchDto;
import com.crawl.Crawling.entity.Likes;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.service.LikesService;
import com.crawl.Crawling.service.ProductService;
import com.crawl.Crawling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final LikesService likesService;
    private final UserService userService;

    @GetMapping(value = {"/category={category}/page={page}", "/category={category}"})
    public String productList(ProductSearchDto productSearchDto, Model model,
                               @PathVariable("category") String category, @PathVariable("page") Optional<Integer> page) {
        //page.isPresent() 값 있으면 page.get(), 없으면 0 반환. 페이지 당 사이즈 60개
        productSearchDto.setSearchCategory(category);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 60);
        Page<Product> products = productService.getCategoryPage(productSearchDto, pageable);

        model.addAttribute("categoryList", Category.values());
        model.addAttribute("products", products);
        model.addAttribute("productSearchDto", productSearchDto);
        model.addAttribute("maxPage", 10);

        return "product/productList"; //카테고리 상품 메인 페이지로 이동
    }

    @GetMapping(value = "/product/{productId}")
    public String productDetail(@PathVariable("productId") Long id, Model model, Principal principal) {
        ProductDto productDto = productService.getProduct(id);
        User user = userService.findByEmail(principal.getName());
        Likes likes = likesService.findByUserAndProduct(user, productService.findById(id));
        boolean isLiked = (likes != null);

        model.addAttribute("productDto", productDto);
        model.addAttribute("isLiked", isLiked);

        return "product/productDetail";
    }
    @PostMapping(value = "/product/like")
    public ResponseEntity<Map<String, String>> productLike(@RequestBody LikeRequestDto likeRequestDto, Principal principal) {
        boolean isLike = likeRequestDto.isLiked(); //ajax로 넘어온 true/false
        User user = userService.findByEmail(principal.getName()); //현재 로그인된 사용자의 아이디
        Product product = productService.findById(likeRequestDto.getProductId());

        likesService.toggleLikes(user, product);

        Map<String, String> res = new HashMap<>();
        if(isLike) {
            res.put("message", "상품을 좋아요한 상품에 추가합니다");
        }
        return ResponseEntity.ok(res);
    }
}
