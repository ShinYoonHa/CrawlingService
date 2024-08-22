package com.crawl.Crawling.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeRequestDto {
    private Long productId;
    private boolean liked;
}
