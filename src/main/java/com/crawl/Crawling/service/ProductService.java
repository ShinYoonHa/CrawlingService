package com.crawl.Crawling.service;

import com.crawl.Crawling.dto.ProductDto;
import com.crawl.Crawling.dto.ProductSearchDto;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
//@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PriceHistoryService priceHistoryService;
    @Autowired
    private CrawlingService crawlingService;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    //인자로 받은 List<Product>를 원래있는 상품인지 확인 후 저장
    public void saveAllProducts() throws IOException {
        List<Product> products = crawlingService.crawl();

        for(Product product : products) {
            //인자로 받은 리스트를 순환 -> 중복 데이터 확인 및 처리
            Product existingProduct = productRepository.findById(product.getId()).orElse(null);
            if(existingProduct != null) {
                //기존 제품이 있을경우, 기존 제품에 대해 변경된 필드 업데이트
                existingProduct.setPrice(product.getPrice()); //가격 갱신
                existingProduct.setRate(product.getRate()); //평점 갱신
                existingProduct.setRate_count(product.getRate_count()); //평점 수 갱신
                //영속성 컨텍스트에서 관리되지만 db에 변경사항을 저장 및 추가 시 명시적으로 save 실행.
                productRepository.save(existingProduct);

                //가격 이력 갱신
                priceHistoryService.savePriceHistory(existingProduct);
            } else {
                //기존 제품이 없을 경우(새 제품) 그대로 추가
                productRepository.save(product);
                //새 제품에 대한 가격 이력 추가
                priceHistoryService.savePriceHistory(product);
            }
        }
    }

    //모든 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    //특정 상품 조회
    public ProductDto getProductDto(Long item_id) {
        Product product =  productRepository.findById(item_id)
                .orElseThrow(() -> new EntityNotFoundException("해당 id를 가진 상품이 없습니다"));
        //Product를 ProductDto로 변환
        return ProductDto.of(product);
    }
    public Product findById(Long id) {
        return productRepository.findById(id).get();
    }

    //저장된 모든 상품에 대한 정보 업데이트
    public void updateAllProduct() throws IOException {
        List<Product> products = getAllProducts(); //db에 저장된 모든 상품 조회
        System.out.println("업데이트 할 전체상품 개수: " + products.size());

        for(Product product : products) {
            //모든 상품에 대해 정보 업데이트 (가격, 평점, 평점 개수)
            Product findProduct = productRepository.findById(product.getId()).orElse(null);
            //Product 정보를 dto에 채움.
            ProductDto productDto = ProductDto.of(findProduct);
            //정보를 직접 변경하기에 dto에 정보 넣고, dto를 통해 entity에 최신 값 갱신
            if(crawlingService.crawlLatestProductStatus(productDto) == null) {
                continue; //기존 db에 있었으나, 현재 상세정보가 없어진 상품인 경우 넘어감
            }

            findProduct.setPrice(productDto.getPrice());
            findProduct.setRate(productDto.getRate());
            findProduct.setRate_count(productDto.getRate_count());
            //영속성 컨텍스트에서 관리되지만 db에 변경사항을 저장 및 추가 시 명시적으로 save 실행.
            productRepository.save(findProduct);

            //가격 이력 갱신
            priceHistoryService.savePriceHistory(findProduct);
        }
    }

    @Transactional(readOnly = true) //조회용 쿼리임을 명시
    public Page<Product> getCategoryPage(ProductSearchDto productSearchDto, Pageable pageable) {
        return productRepository.getCategoryPage(productSearchDto, pageable);
    }
}