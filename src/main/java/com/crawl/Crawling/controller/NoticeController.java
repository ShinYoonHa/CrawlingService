package com.crawl.Crawling.controller;

import com.crawl.Crawling.dto.NoticeDto;
import com.crawl.Crawling.entity.Notice;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.service.MailService;
import com.crawl.Crawling.service.NoticeService;
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
@RequestMapping(value = "/notice")
public class NoticeController {
    private final UserService userService;
    private final ProductService productService;
    private final NoticeService noticeService;
    private final MailService mailService;

    //마이페이지 내 알림 설정 리스트
    @GetMapping(value = "/noticeList")
    public String noticeList(Principal principal, Model model) {
        String username = principal.getName(); //getName() 시 사용자 이메일이 넘어온다
        User user = userService.findByEmail(username); // UserService에서 사용자 정보를 가져오는 메서드
        List<Notice> noticeList = noticeService.findByUser(user); //사용자가 설정한 알림들
        List<Map<String, Object>> noticedProductList = new ArrayList<>(); //알림 설정된 상품 리스트(front로 넘겨주기 위함)

        for(Notice notice : noticeList) { //likeList에 저장된 Likes에서 productId 로 상품 검색 후 List에 저장
            Map<String, Object> map = new HashMap<>();
            Product product = productService.findById(notice.getProduct().getId());
            map.put("product", product);
            map.put("noticePrice", notice.getPrice());
            noticedProductList.add(map);
        }

        model.addAttribute("products", noticedProductList);
        return "notice/noticeList";
    }

    //상품 상세 페이지에서 알림 설정 눌렀을 경우
    @PostMapping(value = "")
    public ResponseEntity<Map<String, String>> productNotice(@RequestBody NoticeDto noticeDto, Principal principal) {
        boolean isNoticed = noticeDto.isNoticed(); //ajax로 넘어온 true/false
        int price = noticeDto.getPrice();
        User user = userService.findByEmail(principal.getName()); //현재 로그인된 사용자의 아이디
        Product product = productService.findById(noticeDto.getProductId());

        noticeService.toggleNotice(user, product, price);

        Map<String, String> res = new HashMap<>();
        if(isNoticed) {
            res.put("message", "지정가 알림이 설정됩니다");
        } else {
            res.put("message", "지정가 알림을 취소합니다");
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/remove")
    public ResponseEntity<String> removeNotice(@RequestBody List<Long> productIds, Principal principal) {
        User user = userService.findByEmail(principal.getName()); // 현재 로그인된 사용자의 정보 가져오기
        for (Long productId : productIds) {
            Product product = productService.findById(productId);
            noticeService.removeNotice(user, product); // 등록된 알림 항목 제거 메서드 호출
        }
        return ResponseEntity.ok("삭제 성공");
    }
    @PostMapping(value = "/remove/{id}")
    public ResponseEntity<String> removeOneNotice(@PathVariable("id") Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName()); // 현재 로그인된 사용자의 정보 가져오기
        Product product = productService.findById(id);
        noticeService.removeNotice(user, product);
        
        return ResponseEntity.ok("삭제 성공");
    }

    @GetMapping(value = "/sendMail")
    public String sendMail() throws Exception {
        List<Notice> noticeList = noticeService.getAllNotice();
        
        for(Notice notice : noticeList) {
            int price = notice.getPrice(); //지정가
            Product product = productService.findById(notice.getProduct().getId());
            if(product.getPrice() <= price) { //상품의 현재가가 지정가 이하일 경우
                mailService.sendLowPriceMail(notice.getUser().getEmail(), product.getName(), product.getId());
            }
        }
        return "sendMail";
    }
}
