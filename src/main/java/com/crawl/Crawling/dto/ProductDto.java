package com.crawl.Crawling.dto;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.entity.Product;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class ProductDto {
    private Long id;            // 상품 아이디
    private String name;        // 상품명
    private int price;          // 가격
    private String img;         // 상품 이미지 경로
    private Double rate;        // 평점
    private int rateCount;      // 평점 개수
    @Enumerated(EnumType.STRING)
    private Category category;  // 카테고리

    private List<PriceHistoryDto> priceHistoryDto; //가격 이력 dto

    public static ModelMapper modelMapper = new ModelMapper();
    
    public static ProductDto of(Product product) {
        if(product == null) {
            return null;
        }
        //Product 정보를 ProductDto 채워서 반환
        ProductDto productDto = modelMapper.map(product, ProductDto.class);

        // Product의 priceHistories를 PriceHistoryDto로 변환하여 productDto에 저장
        List<PriceHistoryDto> priceHistoryDtos = product.getPriceHistories().stream()
                .map(PriceHistoryDto::of)
                .sorted(Comparator.comparing(PriceHistoryDto::getDate)) // 날짜 기준으로 정렬
                .collect(Collectors.toList());

        productDto.setPriceHistoryDto(priceHistoryDtos);

        //priceHistoryDto 값을 가진 ProductDto 반환
        return productDto;
    }
}