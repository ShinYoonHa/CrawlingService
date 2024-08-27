package com.crawl.Crawling.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeDto {
    private Long productId;
    private int price; //알림받기로 설정한 가격
    private boolean noticed;
}
