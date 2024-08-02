package com.crawl.Crawling.constant;

public enum Category {
    WOMAN_FASHION(187069L, "여성패션"),
    MAN_FASHION(186764L, "남성패션"),
    BEAUTY(176522L, "뷰티"),
    BABY_KIDS(221934L, "출산/유아동"),
    FOOD(194276L, "식품"),
    KITCHEN_ITEMS(185669L, "주방용품"),
    HOUSEHOLD_ITEMS(115673L, "생활용품"),
    FURNITURE_INTERIOR(184555L, "가구/인테리어"),
    APPLIANCE_DIGITAL(178255L,"가전/디지털"),
    SPORTS_LEISURE(317778L, "스포츠/레저"),
    CAR_ITEMS(184060L, "자동차용품"),
    BOOK_ALBUM(317777L, "도서/음반"),
    TOY_HOBBY(317779L, "완구/취미"),
    STATIONARY_OFFICE(177295L, "문구/오피스"),
    PET_ITEMS(115674L, "애완동물용품"),
    HEALTH_NUTRIENTS(305798L, "헬스/건강식품")
    ;

    private Long category_id;
    private String category_value;

    Category(Long category_id, String category_value) {
        this.category_id = category_id;
        this.category_value = category_value;
    }
    //카테고리 id 반환
    public Long getCategory_id() {
        return category_id;
    }
    //카테고리 한글명 반환
    public String getCategory_value() {
        return category_value;
    }
    //Category.name() - 열거 객체명 출력: FOOD
}
