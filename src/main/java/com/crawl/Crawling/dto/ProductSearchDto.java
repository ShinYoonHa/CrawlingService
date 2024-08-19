package com.crawl.Crawling.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchDto {
    private String searchCategory; //카테고리
    private String searchQuery = ""; //검색어
}
