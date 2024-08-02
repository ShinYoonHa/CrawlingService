package com.crawl.Crawling.service;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CrawlingService {
    @Autowired
    private ProductService productService;
    @Autowired
    private PriceHistoryService priceHistoryService;

    private Elements product_list;

    public void crawl() throws IOException {

        String url = "https://www.coupang.com/np/categories/"+Category.APPLIANCE_DIGITAL.getCategory_id(); //가전/디지털 카테고리 첫 페이지
        System.out.println("데이터를 크롤링합니다");
        //url과 연결하여 페이지를 크롤링. 뒤에 붙은 건 크롤링 아닌 브라우저 검색처럼 보이기 위한 부분.
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Connection", "keep-alive")
                .get();

        product_list = doc.select("ul#productList li");
        List<Product> products = new ArrayList<>();

        for(int i=0; i<59; i++) {
            //상품 아이디
            Long id = Long.parseLong(product_list.get(i).attr("id"));
            //상품 썸네일 이미지
            String img_src = product_list.get(i).select("a dl dt img").attr("src");
            //상품명
            String name = product_list.get(i).select("a dl dd div.name").text();
            // 가격정보에 ',' 문자 제거 후 정수형 타입으로 변환
            String price_str = product_list.get(i)
                    .select("a dl dd div.price-area div.price-wrap div.price em.sale strong.price-value").text();
            if(price_str.equals("")) {
                //가격정보가 없는 상품 넘어가기 (ex. 반품)
                continue;
            }
            int price = Integer.parseInt(price_str.replace(",",""));
            
            Double rate = 0.0; //평점
            int rate_count = 0; //평점 개수
            //평점 조회함
            String rate_str = product_list.get(i)
                    .select("a dl dd div.other-info div.rating-star span.star em.rating").text();

            //평점이 없을 경우(null), 평점과 평점개수를 0으로 저장
            if(!rate_str.isEmpty()) {
                //문자열 평점을 실수 로 변환
                rate = Double.parseDouble(product_list.get(i)
                        .select("a dl dd div.other-info div.rating-star span.star em.rating").text());
                //평점 개수에 괄호 제거 후 정수 타입으로 변환
                String rate_count_str = product_list.get(i)
                        .select("a dl dd div.other-info > div.rating-star > span.rating-total-count").text();
                rate_count = Integer.parseInt(rate_count_str.replace("(","").replace(")",""));
            }
            Category category = Category.APPLIANCE_DIGITAL; //테스트 위한 카테고리


            Product product = new Product();
            product.setId(id);
            product.setImg(img_src);
            product.setName(name);
            product.setPrice(price);
            product.setRate(rate);
            product.setRate_count(rate_count);
            product.setCategory(category);

            products.add(product);

            System.out.println("상품 아이디: " + id);
            System.out.println("이미지 경로: " + img_src);
            System.out.println("상품명: " + name);
            System.out.println("가격: " + price);
            System.out.println("평점: " + rate);
            System.out.println("평점 개수: " + rate_count);
            System.out.println("카테고리: " + category.getCategory_value());
            System.out.println("====================================");
        }
        productService.saveAllProducts(products);
    }

    public void detailCrawl(Long item_id) throws IOException {
        //매개변수로 받은 id로 상품 조회
        Optional<Product> searchProduct = productService.getProduct(item_id);
        // 값이 없는 경우 예외 발생
        Product product = searchProduct.orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        String url = "https://www.coupang.com/vp/products/"+item_id; //상품 상세 페이지
        System.out.println("특정 상품 가격을 크롤링합니다");
        System.out.println("url: " + url);

        //url과 연결하여 페이지를 크롤링. 뒤에 붙은 건 크롤링 아닌 브라우저 검색처럼 보이기 위한 부분.
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Connection", "keep-alive")
                .get();

        //상품 상세 가격
        String detail_price = doc.select("span.total-price strong").getFirst().text();
        // 가격정보에 ',' 문자 제거 후 정수형 타입으로 변환
        int price = Integer.parseInt(detail_price.replace(",","").replace("원", ""));

        //검색한 상품의 가격변경
        product.setPrice(price);

        System.out.println("가격이 변경된 상품을 조회합니다.");
        System.out.println("상품 아이디: " + product.getId());
        System.out.println("상품명: " + product.getName());
        System.out.println("가격: " + product.getPrice());
        System.out.println("====================================");

        //해당 상품의 가격 이력 저장
        priceHistoryService.savePriceHistory(product, product.getPrice());

    }

}
