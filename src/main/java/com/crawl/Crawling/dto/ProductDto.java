package com.crawl.Crawling.dto;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.entity.Product;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ProductDto {
    private Long id;            // 상품 아이디
    private String name;        // 상품명
    private int price;          // 가격
    private String img;         // 상품 이미지 경로
    private Double rate;        // 평점
    private int rateCount;      // 평점 개수
    private Category category;  // 카테고리

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", img='" + img + '\'' +
                ", rate=" + rate +
                ", rateCount=" + rateCount +
                ", category=" + category +
                '}';
    }

    public static ModelMapper modelMapper = new ModelMapper();
    
    public static ProductDto of(Product product) {
        if(product == null) {
            return null;
        }
        //Product 정보를 ProductDto 채워서 반환
        return modelMapper.map(product, ProductDto.class);
    }
}