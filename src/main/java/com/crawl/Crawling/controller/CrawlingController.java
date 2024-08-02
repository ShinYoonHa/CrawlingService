package com.crawl.Crawling.controller;

import com.crawl.Crawling.service.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping(value = "/crawl")
@Controller
public class CrawlingController {
    @Autowired
    CrawlingService crawlingService;

    @GetMapping(value = "")
    public String crawl() {
        try {
            crawlingService.crawl();
            return "crawl";

        } catch(IOException e) {
            e.printStackTrace();
            return "크롤링 중 오류가 발생했습니다";
        }
    }

    @GetMapping(value = "/detailCrawl")
    public String detailCrawl() {
        try {
            crawlingService.detailCrawl(8052075526L);
            return "detailCrawl";

        } catch(IOException e) {
            e.printStackTrace();
            return "크롤링 중 오류가 발생했습니다";
        }
    }
}
