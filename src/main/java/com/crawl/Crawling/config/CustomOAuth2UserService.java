package com.crawl.Crawling.config;

import com.crawl.Crawling.constant.Role;
import com.crawl.Crawling.constant.Social;
import com.crawl.Crawling.dto.UserDto;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired //findByEmail 해주는 repository
    private UserRepository userRepository;

    @Autowired //google 서버랑 세션을 연결해 놓음
    private HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        //서비스를 구분하는 문자열. ex) "naver"
        String socialName = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        //OAuth2 로그인 시 키 값. 구글 key : "sub", 네이버 key : "response", 카카오 key : "id"
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2 로그인을 통해 가져온 OAuth2User의 attribute를 담아주는 of 메소드.
        OAuthAttributes attributes = OAuthAttributes.of(socialName, userNameAttributeName
        ,oAuth2User.getAttributes()); //매개변수 들어가는 값을 of() -> ofXXX() 로 전달. 소셜 로그인 성공시킴
        User user = userRepository.findByEmail(attributes.getEmail());
        if(user == null) { //첫 소셜 로그인
            user = socialSave(attributes, socialName);
        }
        httpSession.setAttribute("user",  UserDto.of(user));
        //기본 객체 만들고 Attribute의 속성도 같이반환!
        return new DefaultOAuth2User(Collections.emptySet()
                , attributes.getAttributes()
                , attributes.getNameAttributeKey()
        );
    }
    private User socialSave(OAuthAttributes attributes, String socialName) { //저장. 이미 저장됐으면 update 처리
        //attrubute에 저장된 값으로 새로운 User 생성 후 저장
        User user = new User();
        user.setEmail(attributes.getEmail());
        user.setName(attributes.getName());
        user.setSocial(Social.valueOf(socialName.toUpperCase())); //소셜 이름 저장
        user.setRole(Role.ADMIN);
        return userRepository.save(user);

    }
}
