package com.crawl.Crawling.controller;

import com.crawl.Crawling.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping(value = "/crawl")
@Controller
public class CrawlingController {
    @Autowired
    ProductService productService;

    @GetMapping(value = "")
    public String crawl() {
        try {
            productService.saveAllProducts();
            return "crawl";

        } catch(IOException e) {
            e.printStackTrace();
            return "크롤링 중 오류가 발생했습니다";
        }
    }

    @GetMapping(value = "/update")
    public String detailCrawl() {
        try {
            //db에 저장된 모든 상품의 정보 업데이트
            productService.updateAllProduct();
            return "update";

        } catch(IOException e) {
            e.printStackTrace();
            return "크롤링 중 오류가 발생했습니다";
        }
    }
}
