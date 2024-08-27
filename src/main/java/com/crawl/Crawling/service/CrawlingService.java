package com.crawl.Crawling.service;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.dto.ProductDto;
import com.crawl.Crawling.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlingService {

    //각 카테고리의 1~10 페이지에 있는 상품 리스트 크롤링
    public List<Product> crawl() throws IOException {

        List<Product> products = new ArrayList<>();
        System.out.println("데이터를 크롤링합니다");

        //Category 항목별로 크롤링 실행
        for(Category category_value : Category.values()){
            for(int i=1; i<=10; i++) {
                String url = "https://www.coupang.com/np/categories/"
                        + category_value.getCategory_id() + "?page=" + i;
                System.out.println("크롤링할 url: " + url);

                //주어진 url을 크롤링하고 Document로 반환
                Document doc = getDocByUrl(url);
                //Document 내에 상품 리스트를 가져옴
                Elements product_list = doc.select("ul#productList li");
                //상품리스트와 현재 카테고리로 Product 객체를 채운 뒤 리스트에 저장
                fillProductList(product_list, category_value, products);
            }
        }
        //List<Product>를 반환
        return products;

    }

    //특정 상품의 변경된 최신 정보 크롤링
    public ProductDto crawlLatestProductStatus(ProductDto productDto) throws IOException {
        String url = "https://www.coupang.com/vp/products/" + productDto.getId(); // 상품 상세 페이지
        Document doc = getDocByUrl(url);
        
        //상세페이지 정보가 없으면 null 반환
        if (doc.select("span.total-price strong").isEmpty()) {return null;}

        // 가격정보에 ',' 문자 제거 후 정수형 타입으로 변환
        String detail_price_str = doc.select("span.total-price strong").getFirst().text();

        //가격
        int detail_price = Integer.parseInt(detail_price_str.replace(",", "")
                .replace("원", ""));

        Double detail_rate = 0.0; //평점
        int detail_rate_count = 0; //평점 개수
        //평점 조회함
        String rate_str = doc.select("span.rating-star-num").attr("style"); //"90.0%"
        //평점이 없을 경우(null), 평점과 평점개수를 0으로 저장
        if (!rate_str.isEmpty()) {
            //문자열 평점을 실수 타입으로 변환
            rate_str = rate_str.replace("width: ", "").replace("%;", "").trim();
            detail_rate = Double.parseDouble(rate_str) / 20.0; //10.0은 0.5, 90.0은 4.5와 같이 score 형식으로 변환
            //평점 개수에 괄호 제거 후 정수 타입으로 변환
            String rate_count_str = doc.select("span.count").text(); //"4,889개 상품평"
            detail_rate_count = Integer.parseInt(rate_count_str.replace("개 상품평", "").replace(",",""));
        }
        productDto.setPrice(detail_price);
        productDto.setRate(detail_rate);
        productDto.setRate_count(detail_rate_count);

        return productDto;
    }
    
    
    //받은 url에 대한 Document를 생성 후 반환.
    public Document getDocByUrl(String url) throws IOException {
        //url과 연결하여 페이지를 크롤링. 뒤에 붙은 건 크롤링 아닌 브라우저 검색처럼 보이기 위한 부분.
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Connection", "keep-alive")
                .get();
        return doc;
    }

    //상품정보를 가진 Elements와 카테고리 값, 채워진 상품을 받아 리스트에 저장
    //Call by Reference - 리스트 객체(주소값)를 인자로 받아 수정하면 기존 리스트에 반영.
    public void fillProductList(Elements product_list, Category category_value, List<Product> products) {
        for (int i = 0; i < product_list.size(); i++) { //한 페이지 당 상품 60개

            //상품 아이디
            Long id = Long.parseLong(product_list.get(i).attr("id"));

            //상품 썸네일 이미지
            String img_src = product_list.get(i).select("a dl dt img").attr("src");
            //상품명
            String name = product_list.get(i).select("a dl dd div.name").text();
            // 가격정보에 ',' 문자 제거 후 정수형 타입으로 변환
            String price_str = product_list.get(i).select("a dl dd div.price-area div.price-wrap" +
                    " div.price em.sale strong.price-value").text();
            //상품 가격정보가 없는 경우 다음 상품으로 넘어가기 (ex. 반품)
            if (price_str.equals("")) {continue;}
            int price = Integer.parseInt(price_str.replace(",", ""));

            Double rate = 0.0; //평점
            int rate_count = 0; //평점 개수
            //평점 조회함
            String rate_str = product_list.get(i).select("a dl dd div.other-info" +
                    " div.rating-star span.star em.rating").text();

            //평점이 없을 경우(null), 평점과 평점개수를 0으로 저장
            if (!rate_str.isEmpty()) {
                //문자열 평점을 실수 타입으로 변환
                rate = Double.parseDouble(rate_str);
                //평점 개수에 괄호 제거 후 정수 타입으로 변환
                String rate_count_str = product_list.get(i).select("a dl dd div.other-info" +
                        " div.rating-star span.rating-total-count").text();
                rate_count = Integer.parseInt(rate_count_str.replace("(", "")
                        .replace(")", ""));
            }
            Product product = new Product();
            product.setId(id);
            product.setImg(img_src);
            product.setName(name);
            product.setPrice(price);
            product.setRate(rate);
            product.setRate_count(rate_count);
            product.setCategory(Category.valueOf(category_value.name())); //카테고리의 영문명 저장

            products.add(product);
        }
    }

}
