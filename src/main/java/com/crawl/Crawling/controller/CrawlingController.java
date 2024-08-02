package com.crawl.Crawling.controller;

import com.crawl.Crawling.service.CrawlingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class CrawlingController {
    @GetMapping(value = "/crawl")
    public String crawl() throws IOException {
        CrawlingService crawlingService = new CrawlingService();
        return "crawl";
    }
}
