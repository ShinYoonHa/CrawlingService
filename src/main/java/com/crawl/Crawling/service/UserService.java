package com.crawl.Crawling.service;

import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public User saveUser(User user) {
        checkDuplicateUser(user); //@Transactional 이기에 예외시 작동 안함
        return userRepository.save(user); //db에 저장
    }
    public User findByNameAndTel(String name, String tel) {
        return userRepository.findByNameAndTel(name, tel);
    }
    //중복된 User 있는지 확인하는 메소드
    private void checkDuplicateUser(User user) {
        User findUser = userRepository.findByEmail(user.getEmail());

        if (findUser != null) { //값이 있으면
            throw new IllegalStateException("이미 가입된 사용자입니다."); //예외발생!
        }
        findUser = userRepository.findByTel(user.getTel());

        if (findUser != null) { //값이 있으면
            throw new IllegalStateException("이미 가입된 휴대폰 번호입니다."); //예외발생!
        }
    }

    //UserDetails - 스프링 시큐리티에서 회원의 정보를 담기 위해서 사용하는 인터페이스
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException(email);
        }
        //빌더패턴. 여기서 User - springframework 객체
        return org.springframework.security.core.userdetails.User.builder().username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }
}
