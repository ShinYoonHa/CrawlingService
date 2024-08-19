package com.crawl.Crawling.repository;

import com.crawl.Crawling.constant.Category;
import com.crawl.Crawling.dto.ProductSearchDto;
import com.crawl.Crawling.entity.Product;
import com.crawl.Crawling.entity.QProduct;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public ProductRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    // 검색 조건에 따른 BooleanExpression 메서드들
    // html-searchDto가 바인딩됨.
    // html select태그 내 th:field="*{필드명}" 의 필드명 = searchDto의 변수 이름,
    // 메소드들의 매개변수명 = searchDto 변수 이름
    private BooleanExpression searchCategoryEq(String searchCategory) {
        if(StringUtils.isEmpty(searchCategory)) {
            return null;
        }
        switch (searchCategory) {
            case "WOMAN_FASHION":
                return QProduct.product.category.eq(Category.valueOf("WOMAN_FASHION"));
            case "MAN_FASHION":
                return QProduct.product.category.eq(Category.valueOf("MAN_FASHION"));
            case "BEAUTY":
                return QProduct.product.category.eq(Category.valueOf("BEAUTY"));
            case "BABY_KIDS":
                return QProduct.product.category.eq(Category.valueOf("BABY_KIDS"));
            case "FOOD":
                return QProduct.product.category.eq(Category.valueOf("FOOD"));
            case "KITCHEN_ITEMS":
                return QProduct.product.category.eq(Category.valueOf("KITCHEN_ITEMS"));
            case "HOUSEHOLD_ITEMS":
                return QProduct.product.category.eq(Category.valueOf("HOUSEHOLD_ITEMS"));
            case "FURNITURE_INTERIOR":
                return QProduct.product.category.eq(Category.valueOf("FURNITURE_INTERIOR"));
            case "APPLIANCE_DIGITAL":
                return QProduct.product.category.eq(Category.valueOf("APPLIANCE_DIGITAL"));
            case "SPORTS_LEISURE":
                return QProduct.product.category.eq(Category.valueOf("SPORTS_LEISURE"));
            case "CAR_ITEMS":
                return QProduct.product.category.eq(Category.valueOf("CAR_ITEMS"));
            case "BOOK_ALBUM":
                return QProduct.product.category.eq(Category.valueOf("BOOK_ALBUM"));
            case "TOY_HOBBY":
                return QProduct.product.category.eq(Category.valueOf("TOY_HOBBY"));
            case "STATIONARY_OFFICE":
                return QProduct.product.category.eq(Category.valueOf("STATIONARY_OFFICE"));
            case "PET_ITEMS":
                return QProduct.product.category.eq(Category.valueOf("PET_ITEMS"));
            case "HEALTH_NUTRIENTS":
                return QProduct.product.category.eq(Category.valueOf("HEALTH_NUTRIENTS"));
            default:
                return null;
        }
    }
    private BooleanExpression searchByLike(String searchQuery) {
        if (StringUtils.isEmpty(searchQuery)) {
            return null;
        }
        return QProduct.product.name.containsIgnoreCase(searchQuery);
    }

    @Override
    public Page<Product> getCategoryPage(ProductSearchDto productSearchDto, Pageable pageable) {
        QueryResults<Product> results = queryFactory
                .selectFrom(QProduct.product)
                .where(
                        searchCategoryEq(productSearchDto.getSearchCategory()),
                        searchByLike(productSearchDto.getSearchQuery())
                )
                .orderBy(QProduct.product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        //메소드들에서 null 반환하면 에러 아니냐?! QueryDSL은 결과값이 null 이면 그냥 Pass 해버림
        //fetchResults - 쿼리 결과 List와 개수를 반환
        List<Product> products = results.getResults(); //쿼리 결과 리스트 반환
        long total = results.getTotal(); //전체 개수 반환

        return new PageImpl<>(products, pageable, total);
    }
}
