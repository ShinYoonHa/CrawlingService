package com.crawl.Crawling.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    //로그인 성공 시 원래페이지로 리디렉션 하기위한 커스텀 메소드
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication)
            throws IOException, ServletException {
        //이전 페이지로 리디렉션
        String redirectUrl = req.getParameter("redirect");
        if(redirectUrl != null && !redirectUrl.isEmpty()) {
            getRedirectStrategy().sendRedirect(req, res, redirectUrl);
        } else {
            super.onAuthenticationSuccess(req, res, authentication);
        }
    }
}
