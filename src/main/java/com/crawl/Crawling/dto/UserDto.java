package com.crawl.Crawling.dto;

import com.crawl.Crawling.constant.Social;
import com.crawl.Crawling.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class UserDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email //이메일 인증 여부를 Validation으로 확인함
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    //@Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요.")
    private String password;

    private String tel;

    private String gender;
    private Social social;

    private static ModelMapper modelMapper = new ModelMapper();

    public static UserDto of(User user) { //Entity와 DTO를 곧바로 연결
        return modelMapper.map(user, UserDto.class);
    }
}
