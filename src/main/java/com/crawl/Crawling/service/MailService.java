package com.crawl.Crawling.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.crawl.Crawling.controller.UserController.SIGNUP;

@Service
@RequiredArgsConstructor
public class MailService {
    //Bean 등록해둔 MailConfig를 emailsender 라는 이름으로 autowired
    private final JavaMailSender emailSender;
    private String signupCode;
    private String newPw;

    private MimeMessage createMessage(String to, int num) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();
        String msgg = "";
        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        if(num == SIGNUP) {
            message.setSubject("ShowMate 회원가입 이메일 인증");// 제목
            msgg += "<div style='margin:20px;'>";
            msgg += "<h1> 안녕하세요</h1>";
            msgg += "<h1> 가격 추적 서비스 ShowMate 입니다</h1>";
            msgg += "<br>";
            msgg += "<p>아래 코드를 복사해, 회원가입 화면에 입력해주세요</p>";
            msgg += "<br>";
            msgg += "<p>항상 당신의 꿈을 응원합니다. 감사합니다!</p>";
            msgg += "<br>";
            msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
            msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
            msgg += "<div style='font-size:130%'>";
            msgg += "CODE : <strong>";
            msgg += signupCode + "</strong><div><br/> "; // 메일에 인증번호 넣기
            msgg += "</div>";
        } else {
            message.setSubject("ShowMate 비밀번호 재설정");// 제목
            msgg += "<div style='margin:20px;'>";
            msgg += "<h1> 안녕하세요</h1>";
            msgg += "<h1> 가격 추적 서비스 ShowMate 입니다</h1>";
            msgg += "<br>";
            msgg += "<p>아래 임시비밀번호로, 다시 로그인해주세요</p>";
            msgg += "<br>";
            msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
            msgg += "<h3 style='color:blue;'>임시 비밀번호입니다.</h3>";
            msgg += "<div style='font-size:130%'>";
            msgg += "CODE : <strong>";
            msgg += newPw + "</strong><div><br/> "; // 메일에 새로 설정한 비밀번호 넣기
            msgg += "</div>";
        }
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("idc06012@naver.com", "ShowMate_Admin"));// 보내는 사람

        return message;
    }

    public String createKey(int num) {
        StringBuffer key = new StringBuffer();
        Random r = new Random();

        for(int i=0; i<8; i++) {
            int idx = r.nextInt(num); //0,1,2 중에 나오도록
            switch (idx) {
                case 0 :
                    key.append((char)(r.nextInt(26) +97)); //a~z
                    break;
                case 1:
                    key.append(r.nextInt(10)); //0~9
                    break;
                case 2:
                    key.append((char)(r.nextInt(26) +65)); //A~Z
                    break;
            }
        }
        return key.toString();
    }
    public String sendSimpleMessage(String to, int num) throws Exception {
        if (num != SIGNUP) {
            newPw = createKey(2); // 비밀번호 재설정 시마다 새로운 비밀번호 생성
        } else {
            signupCode = createKey(3); // 회원가입 코드도 새로 생성되도록
        }

        MimeMessage message = createMessage(to, num);
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        if(num == SIGNUP) { //회원가입 메시지일경우 회원가입 용 코드(대문자 포함)
            return signupCode;
        } else { //비밀번호 재설정 메시지일경우 비밀번호 용 코드(대문자 미포함)
            return newPw;
        }
    }
    public void sendLowPriceMail(String to, String productName, Long productId) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();
        String msgg = "";
        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        message.setSubject("ShowMate 가격 알림"); // 제목

        // HTML 메시지 작성
        msgg += "<div style='margin:20px; font-family:Verdana, Geneva, sans-serif;'>";
        msgg += "<h1 style='color:#7048cd;'>안녕하세요, ShowMate입니다!</h1>";
        msgg += "<p style='font-size:1.1rem;'>";
        msgg += "회원님이 설정하신 <strong>\"<span style='color:#ff0000;'>" + productName + "</span>\"</strong>의 가격이 ";
        msgg += "지정한 가격 이하로 낮아졌습니다!</p>";
        msgg += "<br>";
        msgg += "<p style='font-size:1rem;'>지금 바로 아래 링크를 클릭하여 확인해보세요:</p>";
        msgg += "<div style='text-align:center; margin:20px;'>";
        msgg += "<a href='https://www.coupang.com/vp/products/" + productId + "' ";
        msgg += "style='display:inline-block; padding:10px 20px; background-color:#7048cd; color:white; text-decoration:none; ";
        msgg += "font-size:1.2rem; border-radius:5px;'>";
        msgg += "상품 확인하기</a></div>";
        msgg += "<br>";
        msgg += "<p style='font-size:0.9rem; color:#555;'>감사합니다!</p>";
        msgg += "<br>";
        msgg += "<hr style='border:0; border-top:1px solid #ccc;'>";
        msgg += "<p style='font-size:0.8rem; text-align:center; color:#999;'>";
        msgg += "이 메일은 발신 전용이니 회신하지 마세요.</p></div>";

        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("idc06012@naver.com", "ShowMate_Admin"));// 보내는 사람

        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}
