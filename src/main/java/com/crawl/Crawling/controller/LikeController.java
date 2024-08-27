package com.crawl.Crawling.controller;

import com.crawl.Crawling.dto.LikeDto;
import com.crawl.Crawling.entity.Likes;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.service.LikesService;
import com.crawl.Crawling.service.ProductService;
import com.crawl.Crawling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/like")
public class LikeController {
    private final UserService userService;
    private final ProductService productService;
    private final LikesService likesService;

    //마이페이지 내 좋아요 상품목록 리스트
    @GetMapping(value = "/likeList")
    public String likeList(Principal principal, Model model) {
        String username = principal.getName(); //getName() 시 사용자 이메일이 넘어온다
        User user = userService.findByEmail(username); // UserService에서 사용자 정보를 가져오는 메서드
        List<Likes> likeList = likesService.findByUser(user);
        List<Product> likedProductList = new ArrayList<>(); //좋아요한 상품 리스트

        for(Likes like : likeList) { //likeList에 저장된 Likes에서 productId 로 상품 검색 후 List에 저장
            Product product = productService.findById(like.getProduct().getId());
            likedProductList.add(product);
        }
        model.addAttribute("products", likedProductList);
        return "like/likeList";
    }

    //상품 상세 페이지에서 좋아요 눌렀을 경우
    @PostMapping(value = "")
    public ResponseEntity<Map<String, String>> productLike(@RequestBody LikeDto likeRequestDto, Principal principal) {
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

    @PostMapping(value = "/remove")
    public ResponseEntity<String> removeLikes(@RequestBody List<Long> productIds, Principal principal) {
        User user = userService.findByEmail(principal.getName()); // 현재 로그인된 사용자의 정보 가져오기
        for (Long productId : productIds) {
            Product product = productService.findById(productId);
            likesService.removeLike(user, product); // LikesService에서 좋아요 제거 메서드 호출
        }
        return ResponseEntity.ok("삭제 성공");
    }

}
