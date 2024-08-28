package com.crawl.Crawling.controller;

import com.crawl.Crawling.constant.Social;
import com.crawl.Crawling.dto.PasswordDto;
import com.crawl.Crawling.dto.UserDto;
import com.crawl.Crawling.entity.User;
import com.crawl.Crawling.service.MailService;
import com.crawl.Crawling.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    public static final int SIGNUP = 3;
    public static final int RESET_PW = 2;

    String confirm = ""; //이메일 인증 시 보낼 코드
    boolean confirmCheck = false; //이메일 인증 코드 일치하는지 체크

    @GetMapping(value = "/signup")
    public String signup(Model model) {
        confirmCheck = false; //회원가입화면 렌더링 시 코드 확인 여부 초기화
        model.addAttribute("userDto", new UserDto());
        return "user/signupForm";
    }
    @PostMapping(value = "/signup")
    public String signup(@Valid UserDto userDto, BindingResult bindingResult, Model model) {
        //MemberFormDto 변수에 설정해놓은 조건 검사, BindingResult에 결과 저장
        if(bindingResult.hasErrors()) { //다시 회원가입 화면으로
            return "user/signupForm";
        }
        if(!confirmCheck) { //이메일 인증 되지 않았을 경우
            model.addAttribute("errorMessage", "이메일을 인증 해주세요");
            return "user/signupForm";
        }
        try {
            //User객체 생성
            User user = User.createUser(userDto, passwordEncoder);
            userService.saveUser(user); //db에 저장
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/signupForm";
        }
        return "user/loginForm";
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<String> deleteUser(Principal principal) {

        userService.deleteUser(principal.getName());
        return ResponseEntity.ok("삭제 성공");
    }

    @PostMapping(value = "/mailSend/{email}")
    @ResponseBody
    public ResponseEntity emailConfirm(@PathVariable("email") String email) throws Exception{
        confirm = mailService.sendSimpleMessage(email, SIGNUP);
        return ResponseEntity.ok("인증 메일을 보냈습니다."); //ajax로 전송
    }

    @GetMapping(value = "/codeCheck/{code}")
    @ResponseBody
    public ResponseEntity codeConfirm(@PathVariable("code") String code) throws Exception {
        if(confirm.equals(code)) { //보낸 인증코드와 입력된 인증코드가 일치하는지
            confirmCheck = true;
            return ResponseEntity.ok("인증 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 코드를 올바르게 입력해주세요.");
        }
    }

    @GetMapping(value = "/login")
    public String login() {
        return "user/loginForm";
    }
    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "user/loginForm";
    }

    @GetMapping(value = "/find-email")
    public String findEmail() {
        return "user/findEmail";
    }
    @PostMapping(value = "/find-email")
    @ResponseBody
    public ResponseEntity<Map<String, String>> findEmail(@RequestBody Map<String, String> req) {
        String name = req.get("name");
        String tel = req.get("tel");
        String pw = req.get("pw");

        User user = userService.findByNameAndTelAndPw(name, tel, pw);

        Map<String, String> res = new HashMap<>();
        if (user != null) {
            res.put("message", "가입된 이메일: " + user.getEmail());
            return ResponseEntity.ok(res); // 200 OK
        } else {
            res.put("message", "해당 정보로 가입된 사용자를 찾을 수 없습니다");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res); // 404 Not Found
        }
    }

    @GetMapping(value = "/reset-password")
    public String resetPassword() {
        return "user/resetPassword";
    }
    @PostMapping(value = "/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> req) throws Exception {
        String email = req.get("email");
        String name = req.get("name");
        String tel = req.get("tel");

        User user = userService.findByEmailAndNameAndTel(email, name, tel);
        Map<String, String> res = new HashMap<>();
        if(user != null) {
            String newPw = mailService.sendSimpleMessage(email, RESET_PW);
            userService.updateUserPassword(user, newPw);
            return ResponseEntity.ok(res); // 200 OK
        } else {
            res.put("message", "임시 비밀번호 설정에 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res); // 404 Not Found
        }
    }

    @GetMapping(value = "/mypage")
    public String mypage(Principal principal, Model model) {
        if(principal == null) {
            return "redirect:/user/login";
        }
        User user = userService.findByEmail(principal.getName());
        boolean isSocial = false; //소셜 로그인인가?
        if(!(user.getSocial().equals(Social.LOCAL))) {
            isSocial = true;
        }

        model.addAttribute("isSocial", isSocial);
        return "user/mypage";
    }

    // 회원정보 수정 페이지로 이동
    @GetMapping(value = "/edit")
    public String editForm(Principal principal, Model model) {
        if(principal == null) {
            return "redirect:/user/login";
        }
        // 사용자 정보를 가져와서 모델에 추가
        String username = principal.getName(); //getName() 시 사용자 이메일이 넘어온다
        User user = userService.findByEmail(username); // UserService에서 사용자 정보를 가져오는 메서드

        model.addAttribute("user", user); // 기존정보 미리 채우려고 user 정보 보내줌
        return "user/editForm"; // 회원정보 수정 페이지로 이동
    }
    @PostMapping(value = "/edit")
    public String editForm(@Valid UserDto userDto, BindingResult bindingResult, Model model) {
        //MemberFormDto 변수에 설정해놓은 조건 검사, BindingResult에 결과 저장
        if(bindingResult.hasErrors()) { //다시 회원가입 화면으로
            return "user/editForm";
        }
        try {
            userService.updateUser(userDto); //userDto에 담긴 정보로 회원정보 수정
            return "redirect:/user/logout";
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/editForm";
        }
    }
    @PostMapping(value = "/confirm-password")
    public ResponseEntity<String> confirmPassword(@RequestBody PasswordDto passwordDto, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        if(userService.checkPassword(user, passwordDto.getPassword())) {
            return ResponseEntity.ok("성공");
        } else {
            return new ResponseEntity<String>("비밀번호 불일치", HttpStatus.BAD_REQUEST);
        }
    }


}
