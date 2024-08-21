package com.crawl.Crawling.controller;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.dto.ProductDto;
import com.crawl.Crawling.dto.ProductSearchDto;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping(value = {"/category={category}/page={page}", "/category={category}"})
    public String productList(ProductSearchDto productSearchDto, Model model,
                               @PathVariable("category") String category, @PathVariable("page") Optional<Integer> page) {
        //page.isPresent() 값 있으면 page.get(), 없으면 0 반환. 페이지 당 사이즈 60개
        productSearchDto.setSearchCategory(category);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 60);
        Page<Product> products = productService.getCategoryPage(productSearchDto, pageable);

        model.addAttribute("categoryList", Category.values());
        model.addAttribute("products", products);
        model.addAttribute("productSearchDto", productSearchDto);
        model.addAttribute("maxPage", 10);

        return "product/productList"; //카테고리 상품 메인 페이지로 이동
    }

    @GetMapping(value = "/product/{productId}")
    public String productDetail(@PathVariable("productId") Long id, Model model) {
        ProductDto productDto = productService.getProduct(id);

        model.addAttribute("productDto", productDto);

        return "product/productDetail";
    }
}
