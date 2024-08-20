package com.crawl.Crawling.dto;

import com.crawl.Crawling.entity.PriceHistory;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Getter
@Setter
public class PriceHistoryDto {
    private Long id; //가격 이력 아이디
    private int price; //가격
    private LocalDate date; //가격 측정 날짜

    public static ModelMapper modelMapper = new ModelMapper();

    public static PriceHistoryDto of(PriceHistory priceHistory) {
        if(priceHistory == null) {
            return null;
        }
        //PriceHistory 정보를 PriceHistoryDto 채워서 반환
        return modelMapper.map(priceHistory, PriceHistoryDto.class);
    }
}
