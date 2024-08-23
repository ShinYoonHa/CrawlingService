package com.crawl.Crawling.config;

import com.crawl.Crawling.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Autowired
    private UserService userService;
    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/user/mypage/**", "/like/**").authenticated()
                .requestMatchers("/", "/images/**", "/crawl/**", "/category={category}",
                        "/product/**", "/user/**").permitAll()
                .anyRequest().authenticated()
        ).formLogin(formLogin -> formLogin
                .loginPage("/user/login")
                .successHandler(customLoginSuccessHandler) //성공하면 원래 페이지로 감
                .usernameParameter("email") //email 가지고 memberService로 감
                .failureUrl("/user/login/error") //실패 시 여기로 감
        ).logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
        )
        ;
        // CORS 설정 추가
        http.cors(withDefaults());

        return http.build();
    }
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
}
