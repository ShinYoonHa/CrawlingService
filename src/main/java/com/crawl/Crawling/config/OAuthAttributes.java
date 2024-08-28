package com.crawl.Crawling.config;

import com.crawl.Crawling.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey; //kakao, google 로그인 분기시키는 key
    private String name;
    private String email;

    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey,
                           String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    // 서비스(kakao/google)를 구분해 알맞게 매핑 처리.
    // registrationId - OAuth2 로그인을 처리한 서비스 명("google","kakao","naver"..)
    // userNameAttributeName - 서비스의 map의 키값. {google="sub", kakao="id", naver="response"}
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if(registrationId.equals("kakao")) {
            return ofKakao(userNameAttributeName, attributes);
        } else if(registrationId.equals("naver")){
            return ofNaver(userNameAttributeName, attributes);
        } else {
            return ofGoogle(userNameAttributeName, attributes);
        }
    }
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        //로그인에 필요한 속성 추출은 시스템(kakao, google...) 마다 다름
        //구글은 받아오는 attribures 값이 email 로 구분 가능한 하나의 json 객체. {..., email: ...,}
        //바로 attribute를 OAuthAttributes 생성자에 넣어준다
        return new OAuthAttributes(attributes, "email",
                (String) attributes.get("name"),
                (String) attributes.get("email"));
    }
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        //카카오로 받은 데이터에서 계정 정보가 담긴 kakao_account 값을 꺼냄
        Map<String, Object> kakao_account = (Map<String, Object>)attributes.get("kakao_account");
        //profile(nickname, image, url...등) 정보가 담긴 값을 꺼냄
        Map<String, Object> profile = (Map<String, Object>)kakao_account.get("profile");

        //카카오는 받아오는 attribures 값이 제일 복잡함. kakao_account 항목에서 다시 profile 객체를 빼내고
        //kakao_account에서 email 추출, profile에서 nickname 추출.
        return new OAuthAttributes(kakao_account, "email",
                (String) profile.get("nickname"),
                (String) kakao_account.get("email"));
    }
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        // 네이버에서 받은 데이터에서 프로필 정보. 담긴 response 값을 꺼낸다.
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        //네이버는 받아오는 attributes 값이 json 안에 response 객체가 있는 형태 {..., response: {email: ..., ...,}}
        //attribute가 아닌 속에 email 값을 지닌 response 값을 생성자에 넣어줌.
        return new OAuthAttributes(response, "email",
                (String) response.get("name"),
                (String) response.get("email")
        );
    }
}
