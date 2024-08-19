package com.crawl.Crawling.controller;

import com.crawl.Crawling.dto.UserDto;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

//    String confirm = ""; //이메일 인증 시 보낼 코드
//    boolean confirmCheck = false; //이메일 인증 코드 일치하는지 체크

    @GetMapping(value = "/signup")
    public String signup(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "user/signupForm";
    }
    @PostMapping(value = "/signup")
    public String signup(@Valid UserDto userDto, BindingResult bindingResult, Model model) {
        //MemberFormDto 변수에 설정해놓은 조건 검사, BindingResult에 결과 저장
        if(bindingResult.hasErrors()) { //다시 회원가입 화면으로
            System.out.println("에러발생 1");
            System.out.println(bindingResult);
            return "user/signupForm";
        }
//        if(!confirmCheck) { //이메일 인증 되지 않았을 경우
//            model.addAttribute("errorMessage", "이메일을 인증 해주세요");
//            return "user/signupForm";
//        }
        try {
            //User객체 생성
            User user = User.createUser(userDto, passwordEncoder);
            userService.saveUser(user); //db에 저장
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            System.out.println("에러발생 2");
            return "user/signupForm";
        }
        return "redirect:/";
    }
    @GetMapping(value = "/login")
    public String loginMember() {
        return "user/loginForm";
    }
    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "user/loginForm";
    }
}
